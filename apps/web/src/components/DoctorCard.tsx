import Link from 'next/link';

export interface DoctorCardProps {
  clinicSlug: string;
  doctor: {
    slug: string;
    name: string;
    specialty: string;
    avatar?: string;
    bio?: string;
  };
}

export default function DoctorCard({ clinicSlug, doctor }: DoctorCardProps) {
  return (
    <div className="flex items-center rounded-lg border p-4 shadow-sm hover:shadow-md transition bg-white">
      <img
        src={doctor.avatar || `https://api.dicebear.com/8.x/initials/svg?seed=${encodeURIComponent(doctor.name)}`}
        alt={doctor.name}
        className="h-14 w-14 rounded-full object-cover mr-4"
      />
      <div className="flex-1">
        <h3 className="text-lg font-semibold text-gray-900">{doctor.name}</h3>
        <p className="text-sm text-gray-600">{doctor.specialty}</p>
      </div>
      <Link
        href={`/${clinicSlug}/${doctor.slug}`}
        className="ml-4 inline-flex items-center rounded-md bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
      >
        Reservar cita
      </Link>
    </div>
  );
}

