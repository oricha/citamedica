import { Suspense } from 'react';

async function getUpcoming() {
  // Server component fetch to internal API
  const res = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL || ''}/api/doctor/bookings`, { cache: 'no-store' }).catch(() => null);
  if (!res || !res.ok) {
    // fallback to mock
    const now = new Date();
    return [
      {
        id: '1',
        title: 'Consulta de control',
        startTime: new Date(now.getTime() + 60 * 60 * 1000).toISOString(),
        patientName: 'Juan Pérez',
        status: 'CONFIRMED',
      },
    ];
  }
  const data = await res.json();
  return data?.data || [];
}

export default async function DoctorDashboardPage() {
  const appointments = await getUpcoming();
  const today = new Date().toDateString();
  const todayAppointments = appointments.filter((a: any) => new Date(a.startTime).toDateString() === today);

  return (
    <div className="space-y-6">
      <section className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="rounded bg-gray-900 p-4 border border-gray-800">
          <p className="text-sm text-gray-400">Citas hoy</p>
          <p className="text-2xl font-semibold">{todayAppointments.length}</p>
        </div>
        <div className="rounded bg-gray-900 p-4 border border-gray-800">
          <p className="text-sm text-gray-400">Próximas (7 días)</p>
          <p className="text-2xl font-semibold">{appointments.length}</p>
        </div>
        <div className="rounded bg-gray-900 p-4 border border-gray-800">
          <p className="text-sm text-gray-400">Completadas</p>
          <p className="text-2xl font-semibold">-</p>
        </div>
      </section>

      <section className="rounded bg-gray-900 p-4 border border-gray-800">
        <h2 className="text-lg font-semibold mb-3">Próximas citas</h2>
        <Suspense fallback={<div>Cargando...</div>}>
          <AppointmentsList initial={appointments} />
        </Suspense>
      </section>
    </div>
  );
}

"use client";
import { useMemo, useState } from 'react';

function AppointmentsList({ initial }: { initial: any[] }) {
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('');
  const [selected, setSelected] = useState<any | null>(null);

  const filtered = useMemo(() => {
    return initial.filter((a) => {
      const matchQ = query ? (a.patientName || '').toLowerCase().includes(query.toLowerCase()) : true;
      const matchS = status ? (a.status || '').toLowerCase() === status.toLowerCase() : true;
      return matchQ && matchS;
    });
  }, [initial, query, status]);

  return (
    <div>
      <div className="mb-3 flex gap-2">
        <input
          placeholder="Buscar por paciente..."
          className="rounded bg-gray-800 border border-gray-700 px-3 py-2 text-sm flex-1"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
        <select
          className="rounded bg-gray-800 border border-gray-700 px-3 py-2 text-sm"
          value={status}
          onChange={(e) => setStatus(e.target.value)}
        >
          <option value="">Todas</option>
          <option value="CONFIRMED">Confirmadas</option>
          <option value="PENDING">Pendientes</option>
          <option value="CANCELLED">Canceladas</option>
        </select>
      </div>

      <div className="divide-y divide-gray-800">
        {filtered.map((a: any) => (
          <button
            key={a.id}
            onClick={() => setSelected(a)}
            className="w-full text-left py-3 flex items-center justify-between hover:bg-gray-800/60 rounded px-2"
          >
            <div>
              <p className="font-medium">{a.title || 'Consulta'}</p>
              <p className="text-sm text-gray-400">{new Date(a.startTime).toLocaleString()} — {a.patientName || 'Paciente'}</p>
            </div>
            <span className="text-xs rounded px-2 py-1 border border-gray-700 text-gray-300">{a.status}</span>
          </button>
        ))}
        {!filtered.length && <p className="text-sm text-gray-400">No hay citas que coincidan.</p>}
      </div>

      {selected && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center p-4" onClick={() => setSelected(null)}>
          <div className="bg-gray-900 border border-gray-700 rounded p-4 max-w-md w-full" onClick={(e) => e.stopPropagation()}>
            <h3 className="text-lg font-semibold mb-2">Detalle de cita</h3>
            <p className="text-sm text-gray-300">Paciente: {selected.patientName || 'Paciente'}</p>
            <p className="text-sm text-gray-300">Fecha: {new Date(selected.startTime).toLocaleString()}</p>
            <p className="text-sm text-gray-300">Estado: {selected.status}</p>
            <div className="mt-4 text-right">
              <button className="rounded bg-gray-800 border border-gray-700 px-3 py-2 text-sm" onClick={() => setSelected(null)}>Cerrar</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
