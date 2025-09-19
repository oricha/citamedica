import AvailabilityCalendar from '@/components/AvailabilityCalendar';
import BookingForm from '@/components/BookingForm';
import { CalcomApiService } from '@/services/calcom';
import { Suspense } from 'react';

export const dynamic = 'force-dynamic';

async function getDoctorAndAvailability(doctorSlug: string) {
  const baseUrl = process.env.CALCOM_BASE_URL || '';
  const apiKey = process.env.CALCOM_API_KEY || '';

  if (!baseUrl || !apiKey) {
    // mock
    const now = new Date();
    const slots = Array.from({ length: 10 }).map((_, i) => {
      const start = new Date(now.getTime() + (i + 1) * 60 * 60 * 1000);
      const end = new Date(start.getTime() + 30 * 60 * 1000);
      return { start: start.toISOString(), end: end.toISOString(), available: true };
    });
    return {
      doctor: { slug: doctorSlug, name: `Dr(a). ${doctorSlug}`, specialty: 'Medicina General' },
      eventTypeId: 1,
      availability: slots,
    };
  }

  const cal = new CalcomApiService(baseUrl, apiKey);
  // Find user by slug: in Cal.com, slug aligns with username typically
  const users = await cal.getUsers();
  const user = users.find((u: any) => u.username === doctorSlug);
  if (!user) throw new Error('Doctor no encontrado');
  const eventTypes = await cal.getEventTypes(String(user.id));
  const eventTypeId = eventTypes?.[0]?.id || 1;
  const from = new Date();
  const to = new Date();
  to.setDate(from.getDate() + 14);
  const availability = await cal.getAvailability(String(user.id), from.toISOString(), to.toISOString());
  return {
    doctor: { slug: doctorSlug, name: user.name, specialty: user.bio || 'Medicina General', avatar: user.avatar },
    eventTypeId,
    availability,
  };
}

export default async function DoctorPage({ params }: { params: { clinic: string; doctor: string } }) {
  const { clinic, doctor } = params;
  const data = await getDoctorAndAvailability(doctor);

  return (
    <main className="mx-auto max-w-5xl p-6 grid gap-6 md:grid-cols-3">
      <section className="md:col-span-1 bg-white rounded border p-4">
        <div className="flex items-center gap-3">
          <img
            src={data.doctor.avatar || `https://api.dicebear.com/8.x/initials/svg?seed=${encodeURIComponent(data.doctor.name)}`}
            alt={data.doctor.name}
            className="h-12 w-12 rounded-full"
          />
          <div>
            <h2 className="text-xl font-semibold text-gray-900">{data.doctor.name}</h2>
            <p className="text-sm text-gray-600">{data.doctor.specialty}</p>
          </div>
        </div>
        <p className="text-xs text-gray-500 mt-2">Selecciona una franja horaria en el calendario para continuar.</p>
      </section>
      <section className="md:col-span-2">
        <Suspense fallback={<div>Cargando disponibilidad...</div>}>
          {/* Client interactivity to pick a slot and show form */}
          {/* A tiny client wrapper here to manage selection */}
          {/* Inline client component to avoid extra files */}
          {/* eslint-disable-next-line @next/next/no-sync-scripts */}
          {/* We keep it simple and controlled */}
          <CalendarAndForm
            clinicSlug={clinic}
            doctor={data.doctor}
            eventTypeId={data.eventTypeId}
            availability={data.availability as any}
          />
        </Suspense>
      </section>
    </main>
  );
}

"use client";
import { useState } from 'react';

function CalendarAndForm({ clinicSlug, doctor, eventTypeId, availability }: { clinicSlug: string; doctor: any; eventTypeId: number; availability: any[] }) {
  const [selected, setSelected] = useState<{ start: string; end: string } | null>(null);
  return (
    <div className="space-y-6">
      <AvailabilityCalendar availability={availability} onSelect={setSelected} />
      <BookingForm clinicSlug={clinicSlug} doctor={doctor} eventTypeId={eventTypeId} selectedSlot={selected} />
    </div>
  );
}
