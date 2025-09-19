"use client";
import { useEffect, useMemo, useState } from 'react';

interface Doctor {
  id: string;
  name: string;
  email?: string;
}

interface Appointment {
  id: string;
  doctorId: string;
  doctorName: string;
  startTime: string;
  patientName: string;
}

export default function ClinicAdminClient() {
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [newDoctorEmail, setNewDoctorEmail] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    (async () => {
      const [d, a] = await Promise.all([
        fetch('/api/clinic/doctors').then((r) => r.json()).catch(() => ({ data: [] })),
        fetch('/api/clinic/appointments').then((r) => r.json()).catch(() => ({ data: [] })),
      ]);
      setDoctors(d?.data || []);
      setAppointments(a?.data || []);
    })();
  }, []);

  async function addDoctor() {
    if (!newDoctorEmail) return;
    setLoading(true);
    try {
      const res = await fetch('/api/clinic/doctors', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: newDoctorEmail }),
      });
      const data = await res.json();
      if (res.ok) {
        setDoctors((p) => [...p, data.data]);
        setNewDoctorEmail('');
      }
    } finally {
      setLoading(false);
    }
  }

  const [doctorFilter, setDoctorFilter] = useState('');
  const [dateFilter, setDateFilter] = useState('');

  const filteredAppointments = useMemo(() => {
    return appointments.filter((a) => {
      const matchDoctor = doctorFilter ? a.doctorId === doctorFilter : true;
      const matchDate = dateFilter ? new Date(a.startTime).toISOString().slice(0, 10) === dateFilter : true;
      return matchDoctor && matchDate;
    });
  }, [appointments, doctorFilter, dateFilter]);

  return (
    <div className="space-y-8">
      <section className="rounded bg-gray-900 p-4 border border-gray-800">
        <h2 className="text-lg font-semibold mb-4">Gestión de médicos</h2>
        <div className="flex gap-2 mb-4">
          <input
            placeholder="Email del médico"
            className="rounded bg-gray-800 border border-gray-700 px-3 py-2 text-sm flex-1"
            value={newDoctorEmail}
            onChange={(e) => setNewDoctorEmail(e.target.value)}
          />
          <button disabled={loading} onClick={addDoctor} className="rounded bg-blue-600 hover:bg-blue-700 px-4 py-2 text-sm disabled:opacity-50">
            Añadir
          </button>
        </div>
        <ul className="divide-y divide-gray-800">
          {doctors.map((d) => (
            <li key={d.id} className="py-3 flex items-center justify-between">
              <div>
                <p className="font-medium">{d.name}</p>
                <p className="text-sm text-gray-400">{d.email}</p>
              </div>
              <div className="flex gap-2">
                <button
                  className="rounded bg-gray-800 border border-gray-700 px-3 py-2 text-sm"
                  onClick={() => {
                    const name = prompt('Nuevo nombre', d.name) || d.name;
                    setDoctors((prev) => prev.map((x) => (x.id === d.id ? { ...x, name } : x)));
                  }}
                >
                  Editar
                </button>
                <button
                  className="rounded bg-red-600 hover:bg-red-700 px-3 py-2 text-sm"
                  onClick={() => setDoctors((prev) => prev.filter((x) => x.id !== d.id))}
                >
                  Eliminar
                </button>
              </div>
            </li>
          ))}
          {!doctors.length && <p className="text-sm text-gray-400">No hay médicos registrados aún.</p>}
        </ul>
      </section>

      <section className="rounded bg-gray-900 p-4 border border-gray-800">
        <h2 className="text-lg font-semibold mb-4">Citas de la clínica</h2>
        <div className="flex gap-2 mb-4">
          <select className="rounded bg-gray-800 border border-gray-700 px-3 py-2 text-sm" value={doctorFilter} onChange={(e) => setDoctorFilter(e.target.value)}>
            <option value="">Todos los médicos</option>
            {doctors.map((d) => (
              <option key={d.id} value={d.id}>{d.name}</option>
            ))}
          </select>
          <input type="date" className="rounded bg-gray-800 border border-gray-700 px-3 py-2 text-sm" value={dateFilter} onChange={(e) => setDateFilter(e.target.value)} />
        </div>
        <div className="divide-y divide-gray-800">
          {filteredAppointments.map((a) => (
            <div key={a.id} className="py-3 flex items-center justify-between">
              <div>
                <p className="font-medium">{a.doctorName}</p>
                <p className="text-sm text-gray-400">{new Date(a.startTime).toLocaleString()} — {a.patientName}</p>
              </div>
            </div>
          ))}
          {!filteredAppointments.length && <p className="text-sm text-gray-400">No hay citas para los filtros seleccionados.</p>}
        </div>
      </section>
    </div>
  );
}
