import { Check } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Link } from "react-router-dom";

const plans = [
  {
    name: "Profesional",
    description: "Para médicos independientes",
    price: "29",
    features: [
      "1 agenda profesional",
      "Hasta 100 pacientes",
      "Historial clínico básico",
      "App móvil iOS y Android",
      "Soporte por email",
    ],
  },
  {
    name: "Clínica",
    description: "Para equipos y múltiples agendas",
    price: "79",
    popular: true,
    features: [
      "Hasta 5 agendas",
      "Pacientes ilimitados",
      "Historial clínico completo",
      "App móvil iOS y Android",
      "Múltiples ubicaciones",
      "Exportación de datos",
      "Soporte prioritario",
    ],
  },
  {
    name: "Hospital",
    description: "Soluciones personalizadas",
    price: "Contacto",
    features: [
      "Agendas ilimitadas",
      "Pacientes ilimitados",
      "Personalización completa",
      "Integración con sistemas existentes",
      "Formación del equipo",
      "Gestor de cuenta dedicado",
      "SLA garantizado",
    ],
  },
];

export function Pricing() {
  return (
    <section id="precios" className="py-24 lg:py-32 bg-muted/30">
      <div className="container mx-auto px-4">
        {/* Section Header */}
        <div className="mb-16 text-center">
          <h2 className="mb-4">Planes para cada tipo de práctica</h2>
          <p className="mx-auto max-w-2xl text-lg text-muted-foreground">
            Empieza con 14 días de prueba gratuita. Sin tarjeta de crédito requerida.
          </p>
        </div>

        {/* Pricing Cards */}
        <div className="grid gap-8 lg:grid-cols-3 lg:gap-6">
          {plans.map((plan) => (
            <Card
              key={plan.name}
              className={`relative flex flex-col transition-smooth hover:shadow-xl ${
                plan.popular
                  ? "border-2 border-primary shadow-lg lg:scale-105"
                  : "border-2 hover:border-primary/20"
              }`}
            >
              {plan.popular && (
                <div className="absolute -top-4 left-1/2 -translate-x-1/2">
                  <div className="rounded-full bg-primary px-4 py-1 text-sm font-semibold text-primary-foreground">
                    Más Popular
                  </div>
                </div>
              )}

              <CardHeader>
                <CardTitle className="text-2xl">{plan.name}</CardTitle>
                <CardDescription className="text-base">{plan.description}</CardDescription>
              </CardHeader>

              <CardContent className="flex-1">
                <div className="mb-6">
                  {plan.price === "Contacto" ? (
                    <div className="text-3xl font-bold text-foreground">Contacto</div>
                  ) : (
                    <div>
                      <span className="text-4xl font-bold text-foreground">${plan.price}</span>
                      <span className="text-muted-foreground">/mes</span>
                    </div>
                  )}
                </div>

                <ul className="space-y-3">
                  {plan.features.map((feature) => (
                    <li key={feature} className="flex items-start gap-3">
                      <Check className="h-5 w-5 text-secondary flex-shrink-0 mt-0.5" />
                      <span className="text-muted-foreground">{feature}</span>
                    </li>
                  ))}
                </ul>
              </CardContent>

              <CardFooter>
                <Button
                  variant={plan.popular ? "cta" : "outline"}
                  className="w-full"
                  size="lg"
                >
                  {plan.price === "Contacto" ? "Contactar Ventas" : "Empezar Prueba Gratuita"}
                </Button>
              </CardFooter>
            </Card>
          ))}
        </div>

        {/* Additional Info */}
        <div className="mt-12 text-center">
          <p className="text-muted-foreground">
            ¿Necesitas más información?{" "}
            <Link to="/precios" className="font-medium text-primary hover:underline">
              Ver todos los planes y características
            </Link>
          </p>
        </div>
      </div>
    </section>
  );
}
