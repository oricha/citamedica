import { NextResponse } from 'next/server';
import { getServerSession } from 'next-auth/next';
import { authOptions } from '../../auth/[...nextauth]/route';
import { PrismaClient } from '@prisma/client';
import { CalcomApiService } from '@/services/calcom';

const prisma = new PrismaClient();

export async function GET() {
  try {
    const session = await getServerSession(authOptions);
    if (!session || session.user.role !== 'CLINIC_ADMIN') {
      return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }
    const clinic = await prisma.clinic.findFirst({ where: { adminUserId: String(session.user.id) } });
    const baseUrl = process.env.CALCOM_BASE_URL || '';
    const apiKey = process.env.CALCOM_API_KEY || '';
    if (!clinic || !clinic.calcomTeamId || !baseUrl || !apiKey) {
      // mock consolidated
      const now = new Date();
      const data = [
        { id: 'a1', doctorId: 'd1', doctorName: 'Dra. García', startTime: new Date(now.getTime() + 3600000).toISOString(), patientName: 'Juan Pérez' },
        { id: 'a2', doctorId: 'd2', doctorName: 'Dr. Pérez', startTime: new Date(now.getTime() + 7200000).toISOString(), patientName: 'María López' },
      ];
      return NextResponse.json({ data });
    }

    const cal = new CalcomApiService(baseUrl, apiKey);
    const members = await cal.getTeamMembers(String(clinic.calcomTeamId));
    const now = new Date();
    const in7 = new Date(now.getTime() + 7 * 24 * 3600 * 1000);
    const all: any[] = [];
    for (const m of members) {
      const bookings = await cal.getBookings(String(m.user.id), {
        dateFrom: now.toISOString(),
        dateTo: in7.toISOString(),
        status: 'ACCEPTED',
      } as any);
      for (const b of bookings) {
        all.push({
          id: String(b.id),
          doctorId: String(m.user.id),
          doctorName: m.user.name,
          startTime: b.startTime,
          patientName: b.attendees?.[0]?.name || b.attendees?.[0]?.email || 'Paciente',
        });
      }
    }
    // order by time
    all.sort((a, b) => +new Date(a.startTime) - +new Date(b.startTime));
    return NextResponse.json({ data: all });
  } catch (e: any) {
    return NextResponse.json({ error: e?.message || 'Error fetching appointments' }, { status: 500 });
  }
}
