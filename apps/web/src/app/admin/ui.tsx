"use client";
import { useEffect, useState } from 'react';

export default function AdminClient() {
  const [registrations, setRegistrations] = useState<any[]>([]);
  const [clinics, setClinics] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    (async () => {
      const regs = await fetch('/api/admin/pending-registrations').then((r) => r.json()).catch(() => ({ data: [] }));
      const cls = await fetch('/api/admin/clinics').then((r) => r.json()).catch(() => ({ data: [] }));
      setRegistrations(regs?.data || []);
      setClinics(cls?.data || []);
    })();
  }, []);

  async function approve(id: string) {
    setLoading(true);
    try {
      await fetch('/api/admin/approve-registration', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ registrationId: id }),
      });
      setRegistrations((prev) => prev.filter((r) => r.id !== id));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-8">
      <section className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="rounded bg-gray-900 p-4 border border-gray-800">
          <p className="text-sm text-gray-400">Clínicas</p>
          <p className="text-2xl font-semibold">{clinics.length}</p>
        </div>
        <div className="rounded bg-gray-900 p-4 border border-gray-800">
          <p className="text-sm text-gray-400">Pendientes</p>
          <p className="text-2xl font-semibold">{registrations.length}</p>
        </div>
        <div className="rounded bg-gray-900 p-4 border border-gray-800">
          <p className="text-sm text-gray-400">Médicos</p>
          <p className="text-2xl font-semibold">{clinics.reduce((acc, c) => acc + (c.doctors?.length || 0), 0)}</p>
        </div>
      </section>
      <section className="rounded bg-gray-900 p-4 border border-gray-800">
        <h2 className="text-lg font-semibold mb-4">Solicitudes pendientes</h2>
        <ul className="divide-y divide-gray-800">
          {registrations.map((r) => (
            <li key={r.id} className="py-3 flex items-center justify-between">
              <div>
                <p className="font-medium">{r.type === 'CLINIC' ? 'Clínica' : 'Médico'} — {r.name}</p>
                <p className="text-sm text-gray-400">{r.email}</p>
              </div>
              <button disabled={loading} onClick={() => approve(r.id)} className="rounded bg-green-600 hover:bg-green-700 px-3 py-2 text-sm disabled:opacity-50">Aprobar</button>
            </li>
          ))}
          {!registrations.length && <p className="text-sm text-gray-400">No hay solicitudes pendientes.</p>}
        </ul>
      </section>

      <section className="rounded bg-gray-900 p-4 border border-gray-800">
        <h2 className="text-lg font-semibold mb-4">Clínicas</h2>
        <ul className="divide-y divide-gray-800">
          {clinics.map((c) => (
            <li key={c.id} className="py-3 flex items-center justify-between">
              <div>
                <p className="font-medium">{c.name}</p>
                <p className="text-sm text-gray-400">Médicos: {c.doctors?.length || 0}</p>
              </div>
            </li>
          ))}
          {!clinics.length && <p className="text-sm text-gray-400">No hay clínicas registradas.</p>}
        </ul>
      </section>
    </div>
  );
}
