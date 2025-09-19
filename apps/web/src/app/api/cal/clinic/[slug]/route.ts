import { NextResponse } from 'next/server';
import { CalcomApiService } from '@/services/calcom';

export async function GET(_: Request, { params }: { params: { slug: string } }) {
  try {
    const baseUrl = process.env.CALCOM_BASE_URL || '';
    const apiKey = process.env.CALCOM_API_KEY || '';
    if (!baseUrl || !apiKey) {
      return NextResponse.json({
        clinic: { slug: params.slug, name: `Clínica ${params.slug}` },
        doctors: [
          { slug: 'dr-garcia', name: 'Dra. García', specialty: 'Pediatría' },
          { slug: 'dr-perez', name: 'Dr. Pérez', specialty: 'Cardiología' },
        ],
      });
    }

    const cal = new CalcomApiService(baseUrl, apiKey);
    const team = await cal.getTeam(params.slug);
    const members = await cal.getTeamMembers(String(team.id));
    const doctors = members.map((m) => ({
      slug: m.user.username,
      name: m.user.name,
      specialty: m.user.bio || 'Medicina General',
      avatar: m.user.avatar,
    }));
    return NextResponse.json({ clinic: { slug: params.slug, name: team.name }, doctors });
  } catch (e: any) {
    return NextResponse.json({ error: e?.message || 'Error fetching clinic' }, { status: 500 });
  }
}
