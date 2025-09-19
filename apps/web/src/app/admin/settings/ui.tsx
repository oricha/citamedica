"use client";
import { useEffect, useState } from 'react';

export default function SettingsClient() {
  const [values, setValues] = useState<any>({
    CALCOM_BASE_URL: '',
    CALCOM_API_KEY: '',
    SENDGRID_API_KEY: '',
    TWILIO_ACCOUNT_SID: '',
    TWILIO_AUTH_TOKEN: '',
    TWILIO_PHONE_NUMBER: '',
  });
  const [message, setMessage] = useState('');

  useEffect(() => {
    (async () => {
      const res = await fetch('/api/admin/config');
      if (res.ok) {
        const data = await res.json();
        setValues((v: any) => ({ ...v, ...(data?.data || {}) }));
      }
    })();
  }, []);

  async function save() {
    const res = await fetch('/api/admin/config', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(values),
    });
    setMessage(res.ok ? 'Configuración guardada.' : 'No se pudo guardar.');
  }

  function set(key: string, val: string) {
    setValues((v: any) => ({ ...v, [key]: val }));
  }

  return (
    <div className="space-y-6">
      <div className="rounded bg-gray-900 p-4 border border-gray-800">
        <h2 className="text-lg font-semibold mb-4">Claves de API</h2>
        <div className="grid gap-3">
          {Object.keys(values).map((k) => (
            <div key={k}>
              <label className="block text-sm text-gray-300 mb-1">{k}</label>
              <input
                type={k.includes('KEY') || k.includes('TOKEN') ? 'password' : 'text'}
                className="w-full rounded bg-gray-800 border border-gray-700 px-3 py-2 text-sm"
                value={values[k] || ''}
                onChange={(e) => set(k, e.target.value)}
              />
            </div>
          ))}
        </div>
        <div className="mt-4">
          <button onClick={save} className="rounded bg-blue-600 hover:bg-blue-700 px-4 py-2 text-sm">Guardar</button>
          {message && <span className="ml-3 text-sm text-gray-400">{message}</span>}
        </div>
      </div>
      <p className="text-xs text-gray-500">Nota: Estas claves se guardan en la base de datos para referencia del sistema. Aún debes configurar variables de entorno del runtime para que los servicios hagan efecto.</p>
    </div>
  );
}

