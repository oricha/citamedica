import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/Footer";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Mail, Phone, MapPin, Clock } from "lucide-react";

const contactInfo = [
  {
    icon: Mail,
    title: "Email",
    content: "hola@agendamed.com",
    description: "Respuesta en 24h",
  },
  {
    icon: Phone,
    title: "Teléfono",
    content: "+34 900 123 456",
    description: "Lun-Vie 9:00-18:00",
  },
  {
    icon: MapPin,
    title: "Oficina",
    content: "Madrid, España",
    description: "Calle Ejemplo 123",
  },
  {
    icon: Clock,
    title: "Horario",
    content: "9:00 - 18:00 CET",
    description: "Lunes a Viernes",
  },
];

export default function ContactPage() {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <main>
        {/* Hero Section */}
        <section className="pt-20 pb-16 lg:pt-32 lg:pb-24 bg-muted/30">
          <div className="container mx-auto px-4">
            <div className="text-center max-w-3xl mx-auto">
              <h1 className="mb-6">Hablemos de tu clínica</h1>
              <p className="text-xl text-muted-foreground">
                Estamos aquí para ayudarte a transformar la gestión de tu práctica médica. 
                Contacta con nosotros y descubre cómo AgendaMed puede simplificar tu día a día.
              </p>
            </div>
          </div>
        </section>

        {/* Contact Info Cards */}
        <section className="py-16">
          <div className="container mx-auto px-4">
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4 max-w-6xl mx-auto">
              {contactInfo.map((info, index) => (
                <Card key={index} className="border-2 hover:border-primary/20 transition-smooth">
                  <CardHeader className="text-center">
                    <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full bg-primary/10">
                      <info.icon className="h-7 w-7 text-primary" />
                    </div>
                    <CardTitle className="text-lg">{info.title}</CardTitle>
                  </CardHeader>
                  <CardContent className="text-center">
                    <p className="font-semibold text-foreground mb-1">{info.content}</p>
                    <p className="text-sm text-muted-foreground">{info.description}</p>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </section>

        {/* Contact Form */}
        <section className="pb-24">
          <div className="container mx-auto px-4">
            <div className="max-w-4xl mx-auto">
              <div className="grid gap-12 lg:grid-cols-2">
                {/* Form */}
                <Card className="border-2">
                  <CardHeader>
                    <CardTitle className="text-2xl">Envíanos un mensaje</CardTitle>
                    <CardDescription>
                      Completa el formulario y nos pondremos en contacto contigo lo antes posible
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <form className="space-y-6">
                      <div className="grid gap-4 md:grid-cols-2">
                        <div className="space-y-2">
                          <Label htmlFor="firstName">Nombre *</Label>
                          <Input id="firstName" placeholder="Juan" required />
                        </div>
                        <div className="space-y-2">
                          <Label htmlFor="lastName">Apellidos *</Label>
                          <Input id="lastName" placeholder="García" required />
                        </div>
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor="email">Email *</Label>
                        <Input id="email" type="email" placeholder="juan@clinica.com" required />
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor="phone">Teléfono</Label>
                        <Input id="phone" type="tel" placeholder="+34 600 000 000" />
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor="clinicName">Nombre de la clínica</Label>
                        <Input id="clinicName" placeholder="Clínica San Rafael" />
                      </div>

                      <div className="space-y-2">
                        <Label htmlFor="message">Mensaje *</Label>
                        <Textarea
                          id="message"
                          placeholder="Cuéntanos cómo podemos ayudarte..."
                          className="min-h-32"
                          required
                        />
                      </div>

                      <Button type="submit" size="lg" variant="cta" className="w-full">
                        Enviar Mensaje
                      </Button>

                      <p className="text-xs text-muted-foreground text-center">
                        Al enviar este formulario, aceptas nuestra{" "}
                        <a href="#" className="text-primary hover:underline">
                          Política de Privacidad
                        </a>
                      </p>
                    </form>
                  </CardContent>
                </Card>

                {/* Additional Info */}
                <div className="space-y-8">
                  <Card className="border-2 bg-muted/30">
                    <CardHeader>
                      <CardTitle className="text-xl">¿Prefieres una demo en vivo?</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <p className="text-muted-foreground">
                        Agenda una videollamada con nuestro equipo y te mostraremos AgendaMed en acción. 
                        Personalizada para tu tipo de clínica.
                      </p>
                      <Button variant="outline" size="lg" className="w-full">
                        Agendar Demo
                      </Button>
                    </CardContent>
                  </Card>

                  <Card className="border-2">
                    <CardHeader>
                      <CardTitle className="text-xl">Soporte técnico</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                      <p className="text-muted-foreground">
                        ¿Ya eres cliente? Accede a nuestro centro de ayuda o contacta directamente 
                        con soporte técnico.
                      </p>
                      <div className="space-y-2">
                        <Button variant="outline" className="w-full">
                          Centro de Ayuda
                        </Button>
                        <Button variant="outline" className="w-full">
                          Soporte Técnico
                        </Button>
                      </div>
                    </CardContent>
                  </Card>

                  <Card className="border-2 gradient-hero text-white">
                    <CardHeader>
                      <CardTitle className="text-xl text-white">Ventas corporativas</CardTitle>
                    </CardHeader>
                    <CardContent>
                      <p className="text-white/90 mb-4">
                        ¿Gestionas múltiples clínicas o un hospital? Nuestro equipo de ventas 
                        enterprise puede ayudarte.
                      </p>
                      <Button variant="outline" className="w-full bg-white text-primary hover:bg-white/90">
                        Contactar Ventas
                      </Button>
                    </CardContent>
                  </Card>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* Map Section (Placeholder) */}
        <section className="py-24 bg-muted/30">
          <div className="container mx-auto px-4">
            <div className="max-w-6xl mx-auto">
              <div className="aspect-video rounded-xl overflow-hidden border-2 border-border bg-muted flex items-center justify-center">
                <div className="text-center">
                  <MapPin className="h-16 w-16 text-primary mx-auto mb-4" />
                  <p className="text-lg font-semibold text-foreground">Nuestras oficinas en Madrid</p>
                  <p className="text-muted-foreground">Calle Ejemplo 123, 28001 Madrid</p>
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}
