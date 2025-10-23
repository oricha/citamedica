import { Button } from "@/components/ui/button";
import { ArrowRight, CheckCircle2 } from "lucide-react";
import heroMockup from "@/assets/hero-mockup.jpg";

export function Hero() {
  return (
    <section className="relative overflow-hidden pt-20 pb-32 lg:pt-32 lg:pb-40">
      {/* Background gradient */}
      <div className="absolute inset-0 gradient-hero opacity-10" />
      
      <div className="container relative mx-auto px-4">
        <div className="flex flex-col items-center text-center">
          {/* Badge */}
          {/* Main heading */}
          <h1 className="mb-6 max-w-4xl text-balance">
            La gestión de citas de tu clínica, <span className="text-primary">simplificada</span>
          </h1>

          {/* Subtitle */}
          <p className="mb-10 max-w-2xl text-lg text-muted-foreground md:text-xl">
            Centraliza tus citas, pacientes e historiales clínicos en una plataforma intuitiva. 
            Menos administración, más tiempo para tus pacientes.
          </p>

          {/* CTA Buttons */}
          <div className="mb-12 flex flex-col gap-4 sm:flex-row">
            <Button size="lg" variant="cta" className="group">
              Empieza tu prueba gratuita de 14 días
              <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
            </Button>
            <Button size="lg" variant="outline">
              Ver Demo en Vivo
            </Button>
          </div>

          {/* Trust indicators */}
          <div className="mb-16 flex flex-wrap items-center justify-center gap-6 text-sm text-muted-foreground">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="h-4 w-4 text-secondary" />
              <span>Sin tarjeta de crédito</span>
            </div>
            <div className="flex items-center gap-2">
              <CheckCircle2 className="h-4 w-4 text-secondary" />
              <span>Configuración en 5 minutos</span>
            </div>
            <div className="flex items-center gap-2">
              <CheckCircle2 className="h-4 w-4 text-secondary" />
              <span>Soporte en español</span>
            </div>
          </div>

          {/* Hero Image */}
          <div className="relative w-full max-w-6xl">
            <div className="relative overflow-hidden rounded-xl border border-border bg-card shadow-2xl">
              <img
                src={heroMockup}
                alt="AgendaMed Dashboard y App Móvil"
                className="w-full h-auto"
              />
            </div>
            {/* Decorative elements */}
            <div className="absolute -top-4 -right-4 h-72 w-72 rounded-full bg-primary/20 blur-3xl" />
            <div className="absolute -bottom-4 -left-4 h-72 w-72 rounded-full bg-secondary/20 blur-3xl" />
          </div>
        </div>
      </div>
    </section>
  );
}
