import { NextResponse } from 'next/server';
import { CalcomApiService } from '@/services/calcom';

export async function GET(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const userId = searchParams.get('userId');
    const dateFrom = searchParams.get('dateFrom');
    const dateTo = searchParams.get('dateTo');
    if (!userId || !dateFrom || !dateTo) {
      return NextResponse.json({ error: 'Missing userId/dateFrom/dateTo' }, { status: 400 });
    }
    const baseUrl = process.env.CALCOM_BASE_URL || '';
    const apiKey = process.env.CALCOM_API_KEY || '';
    if (!baseUrl || !apiKey) {
      // mock simple slots
      const start = new Date(dateFrom);
      const slots = Array.from({ length: 6 }).map((_, i) => {
        const s = new Date(start.getTime() + (i + 1) * 60 * 60 * 1000);
        const e = new Date(s.getTime() + 30 * 60 * 1000);
        return { start: s.toISOString(), end: e.toISOString(), available: true };
      });
      return NextResponse.json({ availability: slots });
    }
    const cal = new CalcomApiService(baseUrl, apiKey);
    const availability = await cal.getAvailability(userId, dateFrom, dateTo);
    return NextResponse.json({ availability });
  } catch (e: any) {
    return NextResponse.json({ error: e?.message || 'Error fetching availability' }, { status: 500 });
  }
}
