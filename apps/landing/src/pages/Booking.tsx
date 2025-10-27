import { useEffect } from "react";
import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

const Booking = () => {
  useEffect(() => {
    // Load Cal.com embed script
    const script = document.createElement("script");
    script.src = "https://app.cal.com/embed/embed.js";
    script.async = true;
    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  }, []);

  const calcomUrl = import.meta.env.VITE_CALCOM_URL || "http://localhost:3000";

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      
      <main className="flex-1 container mx-auto px-4 py-12">
        <div className="max-w-4xl mx-auto">
          <div className="text-center mb-8">
            <h1 className="text-4xl font-bold mb-4">Reserva tu Cita</h1>
            <p className="text-xl text-muted-foreground">
              Selecciona el médico y horario que mejor se adapte a tus necesidades
            </p>
          </div>

          <Card className="shadow-lg">
            <CardHeader>
              <CardTitle>Sistema de Reservas</CardTitle>
              <CardDescription>
                Elige tu especialista y agenda tu cita de forma rápida y sencilla
              </CardDescription>
            </CardHeader>
            <CardContent>
              {/* Cal.com Embed */}
              <div 
                className="cal-embed"
                data-cal-link={`${calcomUrl}/team/citamedica`}
                data-cal-config='{"layout":"month_view","theme":"light"}'
                style={{ width: "100%", height: "100%", overflow: "scroll" }}
              />
            </CardContent>
          </Card>

          <div className="mt-8 grid md:grid-cols-3 gap-6">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">📅 Fácil y Rápido</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground">
                  Reserva tu cita en menos de 2 minutos
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg">🔔 Recordatorios</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground">
                  Recibe notificaciones antes de tu cita
                </p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="text-lg">✅ Confirmación Instantánea</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-sm text-muted-foreground">
                  Tu cita queda confirmada al instante
                </p>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default Booking;