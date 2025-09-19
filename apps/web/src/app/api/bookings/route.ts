import { NextResponse } from 'next/server';
import { CalcomApiService } from '@/services/calcom';

export async function POST(req: Request) {
  try {
    const body = await req.json();
    const { eventTypeId, start, responses } = body || {};
    if (!eventTypeId || !start || !responses?.name || !responses?.email) {
      return NextResponse.json({ error: 'Missing required fields' }, { status: 400 });
    }

    const baseUrl = process.env.CALCOM_BASE_URL || '';
    const apiKey = process.env.CALCOM_API_KEY || '';
    if (!baseUrl || !apiKey) {
      // mock booking response
      return NextResponse.json({ success: true, data: { uid: `mock_${Date.now()}` } });
    }

    const cal = new CalcomApiService(baseUrl, apiKey);
    const booking = await cal.createBooking({ eventTypeId, start, responses });
    return NextResponse.json({ success: true, data: booking });
  } catch (e: any) {
    return NextResponse.json({ success: false, error: e?.message || 'Error creating booking' }, { status: 500 });
  }
}
