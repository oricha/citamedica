export default function BookingConfirmationPage({ searchParams }: { searchParams: { uid?: string; clinic?: string; doctor?: string } }) {
  return (
    <main className="mx-auto max-w-2xl p-6">
      <div className="rounded border bg-white p-6 shadow-sm">
        <h1 className="text-2xl font-bold text-gray-900">Reserva confirmada</h1>
        <p className="text-gray-600 mt-2">Hemos registrado tu cita correctamente.</p>
        {searchParams?.uid && (
          <p className="text-sm text-gray-500 mt-2">Código de reserva: <span className="font-mono">{searchParams.uid}</span></p>
        )}
        <div className="mt-6">
          <a href={`/${searchParams?.clinic || ''}`} className="text-blue-600 hover:underline">Volver a la clínica</a>
        </div>
      </div>
    </main>
  );
}

