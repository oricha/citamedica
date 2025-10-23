import { Button } from "@/components/ui/button";
import { ArrowRight } from "lucide-react";

export function FinalCTA() {
  return (
    <section className="relative overflow-hidden py-24 lg:py-32">
      {/* Background gradient */}
      <div className="absolute inset-0 gradient-hero opacity-10" />

      <div className="container relative mx-auto px-4">
        <div className="flex flex-col items-center text-center">
          <h2 className="mb-6 max-w-3xl">
            Dedica más tiempo a lo que realmente importa
          </h2>
          <p className="mb-10 max-w-2xl text-lg text-muted-foreground md:text-xl">
            Únete a AgendaMed y digitaliza tu consulta hoy mismo. 
            Empieza con nuestra prueba gratuita de 14 días.
          </p>

          <div className="flex flex-col gap-4 sm:flex-row">
            <Button size="lg" variant="cta" className="group">
              Solicitar una Demo Gratuita
              <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
            </Button>
            <Button size="lg" variant="outline">
              Hablar con Ventas
            </Button>
          </div>

          <p className="mt-6 text-sm text-muted-foreground">
            Sin compromiso. Cancela en cualquier momento.
          </p>
        </div>
      </div>
    </section>
  );
}
