import DoctorCard from '@/components/DoctorCard';
import { CalcomApiService } from '@/services/calcom';

export const dynamic = 'force-dynamic';

async function getClinicAndDoctors(slug: string) {
  const baseUrl = process.env.CALCOM_BASE_URL || '';
  const apiKey = process.env.CALCOM_API_KEY || '';

  // Fallback to mock when config is missing (useful in restricted environments)
  if (!baseUrl || !apiKey) {
    return {
      clinic: { slug, name: `Clínica ${slug}` },
      doctors: [
        { slug: 'dr-garcia', name: 'Dra. García', specialty: 'Pediatría' },
        { slug: 'dr-perez', name: 'Dr. Pérez', specialty: 'Cardiología' },
      ],
    };
  }

  const cal = new CalcomApiService(baseUrl, apiKey);
  // Assuming team slug is the clinic slug
  const team = await cal.getTeam(slug);
  const members = await cal.getTeamMembers(String(team.id));
  const doctors = members.map((m) => ({
    slug: m.user.username,
    name: m.user.name,
    specialty: m.user.bio || 'Medicina General',
    avatar: m.user.avatar,
  }));
  return { clinic: { slug, name: team.name }, doctors };
}

export default async function ClinicPage({ params }: { params: { clinic: string } }) {
  const { clinic, doctors } = await getClinicAndDoctors(params.clinic);
  return (
    <main className="mx-auto max-w-4xl p-6">
      <h1 className="text-2xl font-bold text-gray-900 mb-2">{clinic.name}</h1>
      <p className="text-gray-600 mb-6">Selecciona un médico para reservar tu cita.</p>
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {doctors.map((d) => (
          <DoctorCard key={d.slug} clinicSlug={clinic.slug} doctor={d as any} />
        ))}
      </div>
    </main>
  );
}
