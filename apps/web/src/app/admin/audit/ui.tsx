"use client";
import { useEffect, useState } from 'react';

export default function AuditClient() {
  const [logs, setLogs] = useState<any[]>([]);
  useEffect(() => {
    (async () => {
      const res = await fetch('/api/admin/audit');
      if (res.ok) {
        const data = await res.json();
        setLogs(data?.data || []);
      }
    })();
  }, []);

  return (
    <div className="rounded bg-gray-900 p-4 border border-gray-800">
      <h2 className="text-lg font-semibold mb-4">Registro de auditoría</h2>
      <ul className="divide-y divide-gray-800">
        {logs.map((l) => (
          <li key={l.id} className="py-3">
            <p className="text-sm text-gray-300">{l.action} — <span className="text-gray-500">{new Date(l.createdAt).toLocaleString()}</span></p>
            <pre className="text-xs text-gray-500 mt-1 overflow-auto">{JSON.stringify(l.metadata || {}, null, 2)}</pre>
          </li>
        ))}
        {!logs.length && <p className="text-sm text-gray-400">No hay eventos de auditoría.</p>}
      </ul>
    </div>
  );
}

