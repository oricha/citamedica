import { NextResponse } from 'next/server';
import { CalcomApiService } from '@/services/calcom';

export async function GET(req: Request) {
  try {
    const { searchParams } = new URL(req.url);
    const userId = searchParams.get('userId');
    if (!userId) return NextResponse.json({ error: 'Missing userId' }, { status: 400 });
    const baseUrl = process.env.CALCOM_BASE_URL || '';
    const apiKey = process.env.CALCOM_API_KEY || '';
    if (!baseUrl || !apiKey) {
      return NextResponse.json({ eventTypes: [{ id: 1, title: 'Consulta', length: 30 }] });
    }
    const cal = new CalcomApiService(baseUrl, apiKey);
    const eventTypes = await cal.getEventTypes(userId);
    return NextResponse.json({ eventTypes });
  } catch (e: any) {
    return NextResponse.json({ error: e?.message || 'Error fetching event types' }, { status: 500 });
  }
}
