export default function ClinicAdminLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-gray-950 text-gray-100">
      <div className="flex">
        <aside className="w-64 hidden md:block bg-gray-900 border-r border-gray-800 p-4">
          <h2 className="text-lg font-semibold mb-6">Clínica</h2>
          <nav className="space-y-2">
            <a className="block rounded px-3 py-2 hover:bg-gray-800" href="/clinic-admin">General</a>
          </nav>
        </aside>
        <main className="flex-1 p-6">
          <header className="mb-6">
            <h1 className="text-2xl font-bold">Administrador de Clínica</h1>
          </header>
          {children}
        </main>
      </div>
    </div>
  );
}

