
import { PrismaClient } from '@prisma/client';
import { NextResponse } from 'next/server';

const prisma = new PrismaClient();

export async function POST(req: Request) {
  try {
    const {
      clinicName,
      clinicSlug,
      clinicDescription,
      adminName,
      adminEmail,
      adminPassword,
    } = await req.json();

    if (!clinicName || !clinicSlug || !adminName || !adminEmail || !adminPassword) {
      return NextResponse.json({ message: 'Missing fields' }, { status: 400 });
    }

    const existingUser = await prisma.user.findUnique({
      where: { email: adminEmail },
    });

    if (existingUser) {
      return NextResponse.json({ message: 'Admin email already exists' }, { status: 409 });
    }

    const existingClinic = await prisma.clinic.findUnique({
        where: { slug: clinicSlug },
    });

    if (existingClinic) {
        return NextResponse.json({ message: 'Clinic slug already exists' }, { status: 409 });
    }

    await prisma.pendingRegistration.create({
      data: {
        type: 'CLINIC',
        name: clinicName,
        email: adminEmail,
        data: {
          clinicName,
          clinicSlug,
          clinicDescription,
          adminName,
          adminEmail,
          adminPassword,
        },
      },
    });

    return NextResponse.json({ message: 'Clinic registration pending approval' }, { status: 201 });
  } catch (error) {
    console.error('Clinic registration error:', error);
    return NextResponse.json({ message: 'Something went wrong' }, { status: 500 });
  }
}
