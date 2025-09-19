import { NextResponse } from 'next/server';
import { getServerSession } from 'next-auth/next';
import { authOptions } from '../../auth/[...nextauth]/route';
import { CalcomApiService } from '@/services/calcom';

export async function GET() {
  try {
    const session = await getServerSession(authOptions);
    if (!session) return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });

    const baseUrl = process.env.CALCOM_BASE_URL || '';
    const apiKey = process.env.CALCOM_API_KEY || '';
    if (!baseUrl || !apiKey) {
      // mock data
      const now = new Date();
      return NextResponse.json({
        data: [
          {
            id: '1',
            title: 'Consulta de control',
            startTime: new Date(now.getTime() + 3600000).toISOString(),
            patientName: 'Juan Pérez',
            status: 'CONFIRMED',
          },
        ],
      });
    }

    // Use calcomUserId from session
    // @ts-ignore
    const calcomUserId = session.user?.calcomUserId as string | null;
    if (!calcomUserId) return NextResponse.json({ data: [] });

    const cal = new CalcomApiService(baseUrl, apiKey);
    const now = new Date();
    const in7 = new Date(now.getTime() + 7 * 24 * 3600 * 1000);
    const bookings = await cal.getBookings(String(calcomUserId), {
      dateFrom: now.toISOString(),
      dateTo: in7.toISOString(),
      status: 'ACCEPTED',
    } as any);

    const mapped = bookings.map((b: any) => ({
      id: String(b.id),
      title: b.title || 'Consulta',
      startTime: b.startTime,
      patientName: b.attendees?.[0]?.name || b.attendees?.[0]?.email || 'Paciente',
      status: b.status,
    }));
    return NextResponse.json({ data: mapped });
  } catch (e: any) {
    return NextResponse.json({ error: e?.message || 'Error fetching bookings' }, { status: 500 });
  }
}
