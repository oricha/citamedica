import { Star, Quote } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { AnimatedSection } from "@/components/AnimatedSection";

const testimonials = [
  {
    name: "Dr. Ana Martínez",
    role: "Dermatóloga",
    clinic: "Clínica Dermis",
    content: "AgendaMed ha transformado la forma en que gestionamos nuestras 5 consultas. Antes perdíamos horas en llamadas y papeles. Ahora todo está centralizado y accesible desde cualquier lugar.",
    rating: 5,
  },
  {
    name: "Carlos Ruiz",
    role: "Director Administrativo",
    clinic: "Centro Médico San Rafael",
    content: "La integración fue increíblemente rápida. En menos de una semana teníamos todo el equipo operando con AgendaMed. El soporte técnico en español fue fundamental.",
    rating: 5,
  },
  {
    name: "Dra. Isabel Torres",
    role: "Médico de Familia",
    clinic: "Consultorio Privado",
    content: "Como médico independiente, necesitaba algo simple pero potente. AgendaMed me da todo lo que necesito sin complicaciones. La app móvil es perfecta para revisar mi agenda entre consultas.",
    rating: 5,
  },
];

export function SocialProof() {
  return (
    <section className="py-24 lg:py-32">
      <div className="container mx-auto px-4">
        {/* Section Header */}
        <div className="mb-16 text-center">
          <h2 className="mb-4">
            Con la confianza de cientos de profesionales de la salud
          </h2>
          <p className="mx-auto max-w-2xl text-lg text-muted-foreground">
            Descubre por qué médicos y clínicas eligen AgendaMed para gestionar sus consultas
          </p>
        </div>

        {/* Testimonials Grid */}
        <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
          {testimonials.map((testimonial, index) => (
            <AnimatedSection key={index} delay={index * 100}>
              <Card className="border-2 transition-smooth hover:shadow-xl hover:border-primary/20">
              <CardContent className="p-6">
                {/* Quote Icon */}
                <Quote className="mb-4 h-8 w-8 text-primary/40" />

                {/* Stars */}
                <div className="mb-4 flex gap-1">
                  {Array.from({ length: testimonial.rating }).map((_, i) => (
                    <Star key={i} className="h-4 w-4 fill-secondary text-secondary" />
                  ))}
                </div>

                {/* Content */}
                <p className="mb-6 text-foreground leading-relaxed">
                  "{testimonial.content}"
                </p>

                {/* Author */}
                <div className="border-t pt-4">
                  <p className="font-semibold text-foreground">{testimonial.name}</p>
                  <p className="text-sm text-muted-foreground">{testimonial.role}</p>
                  <p className="text-sm text-primary">{testimonial.clinic}</p>
                </div>
              </CardContent>
            </Card>
            </AnimatedSection>
          ))}
        </div>

        {/* Stats */}
        <div className="mt-20 grid gap-8 md:grid-cols-3">
          <div className="text-center">
            <div className="mb-2 text-4xl font-bold text-primary lg:text-5xl">500+</div>
            <p className="text-muted-foreground">Clínicas y consultorios</p>
          </div>
          <div className="text-center">
            <div className="mb-2 text-4xl font-bold text-primary lg:text-5xl">50K+</div>
            <p className="text-muted-foreground">Citas gestionadas al mes</p>
          </div>
          <div className="text-center">
            <div className="mb-2 text-4xl font-bold text-primary lg:text-5xl">4.9/5</div>
            <p className="text-muted-foreground">Valoración promedio</p>
          </div>
        </div>
      </div>
    </section>
  );
}
