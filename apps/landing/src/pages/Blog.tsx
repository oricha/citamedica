import { Navbar } from "@/components/Navbar";
import { Footer } from "@/components/Footer";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Calendar, Clock, User } from "lucide-react";

const posts = [
  {
    title: "Cómo digitalizar tu consulta médica en 5 pasos",
    excerpt: "Descubre las mejores prácticas para llevar tu clínica a la era digital sin complicaciones.",
    category: "Guías",
    author: "Dr. Carlos Ruiz",
    date: "15 de Enero, 2025",
    readTime: "8 min",
    image: "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800&q=80",
  },
  {
    title: "GDPR y datos de salud: Todo lo que necesitas saber",
    excerpt: "Una guía completa sobre el cumplimiento normativo en la gestión de datos médicos.",
    category: "Legal",
    author: "Ana Martínez",
    date: "12 de Enero, 2025",
    readTime: "12 min",
    image: "https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=800&q=80",
  },
  {
    title: "10 funcionalidades que tu sistema de gestión debe tener",
    excerpt: "Las características esenciales que no pueden faltar en un buen software médico.",
    category: "Tecnología",
    author: "Isabel Torres",
    date: "8 de Enero, 2025",
    readTime: "6 min",
    image: "https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?w=800&q=80",
  },
  {
    title: "Mejorando la experiencia del paciente con tecnología",
    excerpt: "Cómo la digitalización puede mejorar la satisfacción y fidelización de tus pacientes.",
    category: "Experiencia",
    author: "Dr. Javier López",
    date: "5 de Enero, 2025",
    readTime: "7 min",
    image: "https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=800&q=80",
  },
  {
    title: "Casos de éxito: Clínicas que transformaron su gestión",
    excerpt: "Historias reales de clínicas que mejoraron su eficiencia con AgendaMed.",
    category: "Casos de Éxito",
    author: "Equipo AgendaMed",
    date: "2 de Enero, 2025",
    readTime: "10 min",
    image: "https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=800&q=80",
  },
  {
    title: "Tendencias en HealthTech para 2025",
    excerpt: "Las innovaciones tecnológicas que están revolucionando el sector salud este año.",
    category: "Tendencias",
    author: "María González",
    date: "28 de Diciembre, 2024",
    readTime: "9 min",
    image: "https://images.unsplash.com/photo-1551076805-e1869033e561?w=800&q=80",
  },
];

const categories = ["Todos", "Guías", "Tecnología", "Legal", "Experiencia", "Casos de Éxito", "Tendencias"];

export default function BlogPage() {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      
      <main>
        {/* Hero Section */}
        <section className="pt-20 pb-16 lg:pt-32 lg:pb-24 bg-muted/30">
          <div className="container mx-auto px-4">
            <div className="text-center max-w-3xl mx-auto">
              <h1 className="mb-6">Blog de AgendaMed</h1>
              <p className="text-xl text-muted-foreground">
                Recursos, guías y mejores prácticas para profesionales de la salud
              </p>
            </div>
          </div>
        </section>

        {/* Categories Filter */}
        <section className="py-8 border-b border-border">
          <div className="container mx-auto px-4">
            <div className="flex flex-wrap gap-2 justify-center">
              {categories.map((category) => (
                <Badge
                  key={category}
                  variant={category === "Todos" ? "default" : "outline"}
                  className="cursor-pointer px-4 py-2 text-sm transition-smooth hover:bg-primary hover:text-primary-foreground"
                >
                  {category}
                </Badge>
              ))}
            </div>
          </div>
        </section>

        {/* Featured Post */}
        <section className="py-16">
          <div className="container mx-auto px-4">
            <div className="max-w-6xl mx-auto">
              <Card className="overflow-hidden border-2 hover:border-primary/20 transition-smooth">
                <div className="grid md:grid-cols-2 gap-0">
                  <div className="relative h-64 md:h-auto overflow-hidden">
                    <img
                      src={posts[0].image}
                      alt={posts[0].title}
                      className="w-full h-full object-cover transition-smooth hover:scale-105"
                    />
                    <Badge className="absolute top-4 left-4 bg-primary">
                      Destacado
                    </Badge>
                  </div>
                  <CardHeader className="p-8">
                    <div className="mb-4">
                      <Badge variant="outline">{posts[0].category}</Badge>
                    </div>
                    <CardTitle className="text-3xl mb-4">{posts[0].title}</CardTitle>
                    <CardDescription className="text-base mb-6 leading-relaxed">
                      {posts[0].excerpt}
                    </CardDescription>
                    <div className="flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
                      <div className="flex items-center gap-2">
                        <User className="h-4 w-4" />
                        {posts[0].author}
                      </div>
                      <div className="flex items-center gap-2">
                        <Calendar className="h-4 w-4" />
                        {posts[0].date}
                      </div>
                      <div className="flex items-center gap-2">
                        <Clock className="h-4 w-4" />
                        {posts[0].readTime}
                      </div>
                    </div>
                  </CardHeader>
                </div>
              </Card>
            </div>
          </div>
        </section>

        {/* Blog Posts Grid */}
        <section className="pb-24">
          <div className="container mx-auto px-4">
            <div className="max-w-6xl mx-auto">
              <h2 className="mb-12">Últimas publicaciones</h2>
              
              <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3">
                {posts.slice(1).map((post, index) => (
                  <Card
                    key={index}
                    className="overflow-hidden border-2 hover:border-primary/20 transition-smooth hover:shadow-xl cursor-pointer group"
                  >
                    <div className="relative h-48 overflow-hidden">
                      <img
                        src={post.image}
                        alt={post.title}
                        className="w-full h-full object-cover transition-smooth group-hover:scale-105"
                      />
                    </div>
                    <CardHeader>
                      <div className="mb-3">
                        <Badge variant="outline">{post.category}</Badge>
                      </div>
                      <CardTitle className="text-xl mb-3 line-clamp-2 group-hover:text-primary transition-smooth">
                        {post.title}
                      </CardTitle>
                      <CardDescription className="line-clamp-3">
                        {post.excerpt}
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <div className="flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                        <div className="flex items-center gap-1">
                          <User className="h-3 w-3" />
                          {post.author}
                        </div>
                        <div className="flex items-center gap-1">
                          <Clock className="h-3 w-3" />
                          {post.readTime}
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </div>
          </div>
        </section>

        {/* Newsletter */}
        <section className="py-24 bg-muted/30">
          <div className="container mx-auto px-4">
            <div className="max-w-2xl mx-auto text-center">
              <h2 className="mb-4">Suscríbete a nuestro newsletter</h2>
              <p className="text-lg text-muted-foreground mb-8">
                Recibe las últimas novedades, guías y consejos directamente en tu email
              </p>
              <div className="flex gap-3 max-w-md mx-auto">
                <input
                  type="email"
                  placeholder="tu@email.com"
                  className="flex-1 rounded-md border border-input bg-background px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
                />
                <button className="px-6 py-3 rounded-md gradient-cta text-white font-medium transition-smooth hover:opacity-90">
                  Suscribir
                </button>
              </div>
              <p className="text-xs text-muted-foreground mt-4">
                Sin spam. Puedes cancelar en cualquier momento.
              </p>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  );
}
