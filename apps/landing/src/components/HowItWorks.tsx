import { Settings, CalendarCheck, UserCheck } from "lucide-react";

const steps = [
  {
    number: "01",
    icon: Settings,
    title: "Configura tu Agenda",
    description: "Define tus horarios, ubicaciones y tipos de tratamiento en minutos.",
  },
  {
    number: "02",
    icon: CalendarCheck,
    title: "Gestiona tus Citas",
    description: "Recibe reservas o créalas manualmente. El calendario se actualiza en tiempo real.",
  },
  {
    number: "03",
    icon: UserCheck,
    title: "Atiende a tus Pacientes",
    description: "Consulta su historial, añade notas y gestiona su seguimiento con facilidad.",
  },
];

export function HowItWorks() {
  return (
    <section className="py-24 lg:py-32 bg-muted/30">
      <div className="container mx-auto px-4">
        {/* Section Header */}
        <div className="mb-16 text-center">
          <h2 className="mb-4">Cómo funciona</h2>
          <p className="mx-auto max-w-2xl text-lg text-muted-foreground">
            En tres simples pasos, transforma la gestión de tu clínica
          </p>
        </div>

        {/* Steps */}
        <div className="grid gap-8 md:grid-cols-3 lg:gap-12">
          {steps.map((step, index) => (
            <div
              key={step.number}
              className="relative flex flex-col items-center text-center"
            >
              {/* Connector Line (hidden on mobile, shown on md+) */}
              {index < steps.length - 1 && (
                <div className="absolute top-16 left-1/2 hidden h-0.5 w-full bg-border md:block" />
              )}

              {/* Icon Circle */}
              <div className="relative mb-6 flex h-32 w-32 items-center justify-center rounded-full bg-primary/10 border-4 border-background shadow-lg">
                <step.icon className="h-14 w-14 text-primary" />
                <div className="absolute -top-2 -right-2 flex h-12 w-12 items-center justify-center rounded-full bg-primary text-primary-foreground font-bold text-lg shadow-lg">
                  {step.number}
                </div>
              </div>

              {/* Content */}
              <h3 className="mb-3 text-xl font-bold">{step.title}</h3>
              <p className="text-muted-foreground">{step.description}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
