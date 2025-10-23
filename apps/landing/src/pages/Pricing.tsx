import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/Footer";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Check, X } from "lucide-react";
import { Link } from "react-router-dom";

const plans = [
  {
    name: "Profesional",
    description: "Perfecto para médicos independientes",
    price: "29",
    period: "/mes",
    features: [
      { text: "1 agenda profesional", included: true },
      { text: "Hasta 100 pacientes", included: true },
      { text: "Historial clínico básico", included: true },
      { text: "App móvil iOS y Android", included: true },
      { text: "Soporte por email", included: true },
      { text: "Múltiples ubicaciones", included: false },
      { text: "Exportación de datos", included: false },
      { text: "Integración con sistemas", included: false },
      { text: "Gestor de cuenta dedicado", included: false },
    ],
  },
  {
    name: "Clínica",
    description: "Ideal para equipos y múltiples profesionales",
    price: "79",
    period: "/mes",
    popular: true,
    features: [
      { text: "Hasta 5 agendas", included: true },
      { text: "Pacientes ilimitados", included: true },
      { text: "Historial clínico completo", included: true },
      { text: "App móvil iOS y Android", included: true },
      { text: "Múltiples ubicaciones", included: true },
      { text: "Exportación de datos", included: true },
      { text: "Soporte prioritario 24/7", included: true },
      { text: "Personalización de marca", included: true },
      { text: "Integración con sistemas", included: false },
    ],
  },
  {
    name: "Hospital",
    description: "Solución empresarial personalizada",
    price: "Contacto",
    period: "",
    features: [
      { text: "Agendas ilimitadas", included: true },
      { text: "Pacientes ilimitados", included: true },
      { text: "Personalización completa", included: true },
      { text: "Integración con HIS/EMR", included: true },
      { text: "API completa", included: true },
      { text: "Formación del equipo", included: true },
      { text: "Gestor de cuenta dedicado", included: true },
      { text: "SLA garantizado 99.9%", included: true },
      { text: "Soporte on-site opcional", included: true },
    ],
  },
];

const faqs = [
  {
    question: "¿Puedo cambiar de plan en cualquier momento?",
    answer: "Sí, puedes actualizar o degradar tu plan cuando lo necesites. Los cambios se aplican inmediatamente y ajustamos la facturación de forma proporcional.",
  },
  {
    question: "¿Qué incluye la prueba gratuita?",
    answer: "La prueba gratuita de 14 días incluye acceso completo al plan Clínica sin necesidad de tarjeta de crédito. Podrás probar todas las funciones antes de decidir.",
  },
  {
    question: "¿Los datos de mis pacientes están seguros?",
    answer: "Absolutamente. Cumplimos con GDPR y encriptamos todos los datos en tránsito y en reposo. Nuestros servidores están en Europa y realizamos backups diarios.",
  },
  {
    question: "¿Puedo importar mis pacientes existentes?",
    answer: "Sí, ofrecemos herramientas de importación para Excel, CSV y también podemos ayudarte a migrar desde otros sistemas de gestión.",
  },
  {
    question: "¿Hay costes ocultos o límites de uso?",
    answer: "No. El precio que ves es el precio que pagas. Sin límites de almacenamiento, sin cobros por SMS, sin sorpresas en la factura.",
  },
  {
    question: "¿Ofrecen descuentos para pagos anuales?",
    answer: "Sí, ofrecemos un 20% de descuento si pagas anualmente. Contacta con nuestro equipo de ventas para más detalles.",
  },
];

export default function PricingPage() {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <main>
        {/* Hero Section */}
        <section className="pt-20 pb-16 lg:pt-32 lg:pb-24">
          <div className="container mx-auto px-4">
            <div className="text-center max-w-3xl mx-auto">
              <h1 className="mb-6">
                Precios transparentes para cada tipo de práctica
              </h1>
              <p className="text-xl text-muted-foreground mb-8">
                Empieza con 14 días de prueba gratuita. Sin tarjeta de crédito. Sin compromiso.
              </p>
              
              {/* Toggle Annual/Monthly */}
              <div className="inline-flex items-center gap-3 p-1 rounded-full bg-muted">
                <button className="px-6 py-2 rounded-full bg-primary text-primary-foreground font-medium transition-smooth">
                  Mensual
                </button>
                <button className="px-6 py-2 rounded-full font-medium text-muted-foreground transition-smooth hover:text-foreground">
                  Anual <span className="text-secondary text-sm ml-1">(-20%)</span>
                </button>
              </div>
            </div>
          </div>
        </section>

        {/* Pricing Cards */}
        <section className="pb-24">
          <div className="container mx-auto px-4">
            <div className="grid gap-8 lg:grid-cols-3 lg:gap-6 max-w-7xl mx-auto">
              {plans.map((plan) => (
                <Card
                  key={plan.name}
                  className={`relative flex flex-col transition-smooth ${
                    plan.popular
                      ? "border-2 border-primary shadow-lg lg:scale-105"
                      : "border-2 hover:border-primary/20 hover:shadow-xl"
                  }`}
                >
                  {plan.popular && (
                    <div className="absolute -top-4 left-1/2 -translate-x-1/2">
                      <div className="rounded-full gradient-cta px-6 py-1.5 text-sm font-semibold text-white shadow-lg">
                        Más Popular
                      </div>
                    </div>
                  )}

                  <CardHeader className="pb-8">
                    <CardTitle className="text-2xl">{plan.name}</CardTitle>
                    <CardDescription className="text-base">{plan.description}</CardDescription>
                    
                    <div className="pt-4">
                      {plan.price === "Contacto" ? (
                        <div className="text-4xl font-bold text-foreground">A medida</div>
                      ) : (
                        <div>
                          <span className="text-5xl font-bold text-foreground">${plan.price}</span>
                          <span className="text-lg text-muted-foreground">{plan.period}</span>
                        </div>
                      )}
                    </div>
                  </CardHeader>

                  <CardContent className="flex-1">
                    <ul className="space-y-3">
                      {plan.features.map((feature, i) => (
                        <li key={i} className="flex items-start gap-3">
                          {feature.included ? (
                            <Check className="h-5 w-5 text-secondary flex-shrink-0 mt-0.5" />
                          ) : (
                            <X className="h-5 w-5 text-muted-foreground/40 flex-shrink-0 mt-0.5" />
                          )}
                          <span className={feature.included ? "text-foreground" : "text-muted-foreground/60"}>
                            {feature.text}
                          </span>
                        </li>
                      ))}
                    </ul>
                  </CardContent>

                  <CardFooter>
                    <Button
                      variant={plan.popular ? "cta" : "outline"}
                      className="w-full"
                      size="lg"
                      asChild
                    >
                      <Link to="#demo">
                        {plan.price === "Contacto" ? "Contactar Ventas" : "Empezar Prueba Gratuita"}
                      </Link>
                    </Button>
                  </CardFooter>
                </Card>
              ))}
            </div>
          </div>
        </section>

        {/* Features Comparison Table */}
        <section className="py-24 bg-muted/30">
          <div className="container mx-auto px-4">
            <div className="text-center mb-16">
              <h2 className="mb-4">Todas las funcionalidades, comparadas</h2>
              <p className="text-lg text-muted-foreground">
                Encuentra el plan perfecto para tu práctica médica
              </p>
            </div>

            <div className="max-w-5xl mx-auto overflow-x-auto">
              <table className="w-full border-collapse">
                <thead>
                  <tr className="border-b-2 border-border">
                    <th className="text-left py-4 px-4 font-semibold">Funcionalidad</th>
                    <th className="text-center py-4 px-4 font-semibold">Profesional</th>
                    <th className="text-center py-4 px-4 font-semibold">Clínica</th>
                    <th className="text-center py-4 px-4 font-semibold">Hospital</th>
                  </tr>
                </thead>
                <tbody>
                  {[
                    { name: "Agendas", pro: "1", clinic: "5", hospital: "Ilimitadas" },
                    { name: "Pacientes", pro: "100", clinic: "Ilimitados", hospital: "Ilimitados" },
                    { name: "Usuarios", pro: "1", clinic: "5", hospital: "Ilimitados" },
                    { name: "Ubicaciones", pro: "1", clinic: "3", hospital: "Ilimitadas" },
                    { name: "Almacenamiento", pro: "5 GB", clinic: "50 GB", hospital: "Ilimitado" },
                  ].map((row) => (
                    <tr key={row.name} className="border-b border-border">
                      <td className="py-4 px-4 text-muted-foreground">{row.name}</td>
                      <td className="py-4 px-4 text-center">{row.pro}</td>
                      <td className="py-4 px-4 text-center font-medium">{row.clinic}</td>
                      <td className="py-4 px-4 text-center">{row.hospital}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </section>

        {/* FAQ Section */}
        <section className="py-24">
          <div className="container mx-auto px-4">
            <div className="max-w-3xl mx-auto">
              <h2 className="mb-12 text-center">Preguntas frecuentes</h2>
              
              <div className="space-y-6">
                {faqs.map((faq, index) => (
                  <Card key={index}>
                    <CardHeader>
                      <CardTitle className="text-xl">{faq.question}</CardTitle>
                    </CardHeader>
                    <CardContent>
                      <p className="text-muted-foreground leading-relaxed">{faq.answer}</p>
                    </CardContent>
                  </Card>
                ))}
              </div>

              <div className="mt-12 text-center">
                <p className="text-muted-foreground mb-4">
                  ¿Tienes más preguntas? Estamos aquí para ayudarte.
                </p>
                <Button variant="outline" size="lg" asChild>
                  <Link to="#contact">Contactar con Soporte</Link>
                </Button>
              </div>
            </div>
          </div>
        </section>

        {/* Final CTA */}
        <section className="py-24 bg-muted/30">
          <div className="container mx-auto px-4">
            <div className="max-w-3xl mx-auto text-center">
              <h2 className="mb-6">¿Listo para transformar tu clínica?</h2>
              <p className="text-xl text-muted-foreground mb-8">
                Únete a cientos de profesionales que ya confían en AgendaMed
              </p>
              <Button size="lg" variant="cta">
                Empezar Prueba Gratuita de 14 Días
              </Button>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}
