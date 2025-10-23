import { Calendar, Users, FileText, Smartphone } from "lucide-react";
import { AnimatedSection } from "@/components/AnimatedSection";
import dashboardCalendar from "@/assets/dashboard-calendar.jpg";
import patientList from "@/assets/patient-list.jpg";
import patientHistory from "@/assets/patient-history.jpg";

const features = [
  {
    icon: Calendar,
    title: "Tu calendario, bajo control",
    description:
      "Gestiona la agenda de múltiples profesionales y ubicaciones. Crea, edita y visualiza citas por tipo de tratamiento (Primera Visita, Revisión, Laser Co2, etc.), asigna colores y duraciones.",
    image: dashboardCalendar,
  },
  {
    icon: Users,
    title: "Todos tus pacientes, en un solo lugar",
    description:
      "Accede a una lista completa de tus pacientes. Busca al instante por nombre, apellidos o teléfono. Importa tus contactos existentes con un clic.",
    image: patientList,
  },
  {
    icon: FileText,
    title: "Conoce a tu paciente antes de la visita",
    description:
      "Consulta el historial completo de visitas, detalles del paciente (DNI, email, teléfono, aseguradora) y añade notas privadas después de cada consulta.",
    image: patientHistory,
  },
  {
    icon: Smartphone,
    title: "Gestiona tu clínica desde cualquier lugar",
    description:
      "Accede a toda tu información desde nuestro potente panel web o desde nuestra aplicación nativa para iOS y Android. Tu clínica, en tu bolsillo.",
    image: dashboardCalendar,
  },
];

export function Features() {
  return (
    <section className="py-24 lg:py-32">
      <div className="container mx-auto px-4">
        {/* Section Header */}
        <div className="mb-16 text-center">
          <h2 className="mb-4">
            Todo lo que necesitas para gestionar tu clínica
          </h2>
          <p className="mx-auto max-w-2xl text-lg text-muted-foreground">
            Una plataforma completa diseñada específicamente para profesionales de la salud
          </p>
        </div>

        {/* Features Grid */}
        <div className="space-y-32">
          {features.map((feature, index) => (
            <AnimatedSection
              key={feature.title}
              delay={index * 100}
            >
              <div
                className={`grid gap-12 lg:grid-cols-2 lg:gap-16 items-center ${
                  index % 2 === 1 ? "lg:grid-flow-dense" : ""
                }`}
              >
              {/* Content */}
              <div className={index % 2 === 1 ? "lg:col-start-2" : ""}>
                <div className="mb-6 inline-flex h-14 w-14 items-center justify-center rounded-xl bg-primary/10">
                  <feature.icon className="h-7 w-7 text-primary" />
                </div>
                <h3 className="mb-4">{feature.title}</h3>
                <p className="text-lg text-muted-foreground leading-relaxed">
                  {feature.description}
                </p>
              </div>

              {/* Image */}
              <div className={index % 2 === 1 ? "lg:col-start-1 lg:row-start-1" : ""}>
                <div className="relative overflow-hidden rounded-xl border border-border bg-card shadow-xl transition-smooth hover:shadow-2xl">
                  <img
                    src={feature.image}
                    alt={feature.title}
                    className="w-full h-auto"
                  />
                </div>
              </div>
              </div>
            </AnimatedSection>
          ))}
        </div>
      </div>
    </section>
  );
}
