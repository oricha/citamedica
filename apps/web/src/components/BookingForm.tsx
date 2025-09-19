"use client";
import { useState } from 'react';

export interface BookingFormProps {
  clinicSlug: string;
  doctor: {
    slug: string;
    name: string;
    id?: string;
  };
  eventTypeId: number;
  selectedSlot: { start: string; end: string } | null;
}

export default function BookingForm({ clinicSlug, doctor, eventTypeId, selectedSlot }: BookingFormProps) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [phone, setPhone] = useState('');
  const [notes, setNotes] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!selectedSlot) return;
    setLoading(true);
    setError('');
    try {
      const res = await fetch('/api/bookings', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          eventTypeId,
          start: selectedSlot.start,
          responses: {
            name,
            email,
            phone,
            notes,
          },
        }),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data?.error || 'No se pudo crear la reserva');
      window.location.href = `/bookings/confirmation?uid=${encodeURIComponent(data?.data?.uid || '')}&clinic=${encodeURIComponent(clinicSlug)}&doctor=${encodeURIComponent(doctor.slug)}`;
    } catch (err: any) {
      setError(err.message || 'Error al crear la reserva');
    } finally {
      setLoading(false);
    }
  }

  if (!selectedSlot) return null;

  return (
    <form onSubmit={handleSubmit} className="space-y-4 bg-white p-4 rounded border">
      <h4 className="text-lg font-semibold text-gray-900">Confirmar reserva con {doctor.name}</h4>
      <p className="text-sm text-gray-600">{new Date(selectedSlot.start).toLocaleString()}</p>
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
        <input className="w-full rounded border px-3 py-2" required value={name} onChange={(e) => setName(e.target.value)} />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
        <input type="email" className="w-full rounded border px-3 py-2" required value={email} onChange={(e) => setEmail(e.target.value)} />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Teléfono</label>
        <input className="w-full rounded border px-3 py-2" required value={phone} onChange={(e) => setPhone(e.target.value)} />
      </div>
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">Notas (opcional)</label>
        <textarea className="w-full rounded border px-3 py-2" rows={3} value={notes} onChange={(e) => setNotes(e.target.value)} />
      </div>
      <button disabled={loading} className="rounded bg-blue-600 text-white px-4 py-2 hover:bg-blue-700 disabled:opacity-50">
        {loading ? 'Reservando...' : 'Confirmar reserva'}
      </button>
    </form>
  );
}

