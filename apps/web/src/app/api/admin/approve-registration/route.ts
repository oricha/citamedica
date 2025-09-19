
import { PrismaClient } from '@prisma/client';
import { NextResponse } from 'next/server';
import { CalcomApiService } from '@/services/calcom';
import bcrypt from 'bcryptjs';
import { getServerSession } from 'next-auth/next';
import { authOptions } from '../auth/[...nextauth]/route';

const prisma = new PrismaClient();
const calcom = new CalcomApiService(process.env.CALCOM_BASE_URL!, process.env.CALCOM_API_KEY!)

export async function POST(req: Request) {
  try {
    const session = await getServerSession(authOptions);

    if (!session || session.user.role !== 'SYSTEM_ADMIN') {
      return NextResponse.json({ message: 'Unauthorized' }, { status: 401 });
    }

    const { registrationId } = await req.json();

    if (!registrationId) {
      return NextResponse.json({ message: 'Missing registrationId' }, { status: 400 });
    }

    const registration = await prisma.pendingRegistration.findUnique({
      where: { id: registrationId },
    });

    if (!registration || registration.status !== 'PENDING') {
      return NextResponse.json({ message: 'Invalid registration or not pending' }, { status: 400 });
    }

    if (registration.type === 'CLINIC') {
        const { clinicName, clinicSlug, clinicDescription, adminName, adminEmail, adminPassword } = registration.data as any;

        const hashedPassword = await bcrypt.hash(adminPassword, 10);

        const user = await prisma.user.create({
            data: {
                name: adminName,
                email: adminEmail,
                password: hashedPassword,
                role: 'CLINIC_ADMIN',
            },
        });

        const team = await calcom.createTeam({ name: clinicName, slug: clinicSlug });

        const clinic = await prisma.clinic.create({
            data: {
                name: clinicName,
                slug: clinicSlug,
                description: clinicDescription,
                calcomTeamId: team.id.toString(),
                adminUserId: user.id,
            },
        });

        await prisma.pendingRegistration.update({
            where: { id: registrationId },
            data: { status: 'APPROVED', approvedAt: new Date() },
        });

        // audit log
        await prisma.auditLog.create({
          data: {
            actorUserId: String(session.user.id),
            action: 'APPROVE_CLINIC',
            metadata: { clinicId: clinic.id, teamId: team.id, registrationId },
          }
        });

        return NextResponse.json({ message: 'Clinic approved successfully' }, { status: 200 });

    } else if (registration.type === 'DOCTOR') {
        const { name, email, password, specialty, bio } = registration.data as any;

        // 1. Create user in Cal.com
        const calcomUser = await calcom.createUser({
            email,
            name,
            username: email.split('@')[0], // Cal.com requires a username
        });

        // 2. Hash password for local DB
        const hashedPassword = await bcrypt.hash(password, 10);

        // 3. Create local User
        const user = await prisma.user.create({
            data: {
                name,
                email,
                password: hashedPassword,
                role: 'DOCTOR',
                calcomUserId: calcomUser.id.toString(),
            },
        });

        // 4. Create local Doctor profile
        await prisma.doctor.create({
            data: {
                name,
                slug: email.split('@')[0], // Using email prefix as slug for now
                specialty,
                bio,
                calcomUserId: calcomUser.id.toString(),
                isIndependent: true,
            },
        });

        // 5. Update pending registration status
        await prisma.pendingRegistration.update({
            where: { id: registrationId },
            data: { status: 'APPROVED', approvedAt: new Date() },
        });

        // audit log
        await prisma.auditLog.create({
          data: {
            actorUserId: String(session.user.id),
            action: 'APPROVE_DOCTOR',
            metadata: { userId: user.id, calcomUserId: calcomUser.id, registrationId },
          }
        });

        return NextResponse.json({ message: 'Doctor approved successfully' }, { status: 200 });
    }

    return NextResponse.json({ message: 'Invalid registration type' }, { status: 400 });

  } catch (error) {
    console.error('Approval error:', error);
    return NextResponse.json({ message: 'Something went wrong' }, { status: 500 });
  }
}
