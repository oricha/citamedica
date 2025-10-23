import { useState } from "react";
import { Link } from "react-router-dom";
import { ChevronDown, Menu, X, Calendar, Users, FileText, Smartphone, Building2, Hospital, UserCircle, CreditCard, BookOpen } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
} from "@/components/ui/navigation-menu";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { cn } from "@/lib/utils";

const productLinks = [
  {
    title: "Agenda Inteligente",
    description: "Gestión de calendario, citas y tipos de tratamiento",
    icon: Calendar,
    href: "#",
  },
  {
    title: "Gestión de Pacientes",
    description: "Base de datos de pacientes y búsqueda avanzada",
    icon: Users,
    href: "#",
  },
  {
    title: "Historial Clínico Digital",
    description: "Notas por visita y datos del paciente",
    icon: FileText,
    href: "#",
  },
  {
    title: "App Móvil",
    description: "Accede desde cualquier lugar",
    icon: Smartphone,
    href: "#",
  },
];

const solutionLinks = [
  {
    title: "Para Clínicas Pequeñas",
    description: "Soluciones adaptadas a tu tamaño",
    icon: Building2,
    href: "#",
  },
  {
    title: "Para Hospitales",
    description: "Escalable para organizaciones grandes",
    icon: Hospital,
    href: "#",
  },
  {
    title: "Para Médicos Independientes",
    description: "Todo lo que necesitas para empezar",
    icon: UserCircle,
    href: "#",
  },
];

export function Navbar() {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <nav className="sticky top-0 z-50 w-full border-b border-border bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="container mx-auto px-4">
        <div className="flex h-16 items-center justify-between">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary">
              <Calendar className="h-5 w-5 text-primary-foreground" />
            </div>
            <span className="text-xl font-bold text-foreground">AgendaMed</span>
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden items-center space-x-6 lg:flex">
            <NavigationMenu>
              <NavigationMenuList>
                <NavigationMenuItem>
                  <NavigationMenuTrigger className="text-sm font-medium">
                    Producto
                  </NavigationMenuTrigger>
                  <NavigationMenuContent>
                    <ul className="grid w-[600px] gap-3 p-4 md:grid-cols-2">
                      {productLinks.map((item) => (
                        <ListItem
                          key={item.title}
                          title={item.title}
                          href={item.href}
                          icon={item.icon}
                        >
                          {item.description}
                        </ListItem>
                      ))}
                    </ul>
                  </NavigationMenuContent>
                </NavigationMenuItem>

                <NavigationMenuItem>
                  <NavigationMenuTrigger className="text-sm font-medium">
                    Soluciones
                  </NavigationMenuTrigger>
                  <NavigationMenuContent>
                    <ul className="grid w-[500px] gap-3 p-4">
                      {solutionLinks.map((item) => (
                        <ListItem
                          key={item.title}
                          title={item.title}
                          href={item.href}
                          icon={item.icon}
                        >
                          {item.description}
                        </ListItem>
                      ))}
                    </ul>
                  </NavigationMenuContent>
                </NavigationMenuItem>

                <NavigationMenuItem>
                  <Link
                    to="/precios"
                    className="inline-flex h-10 w-max items-center justify-center rounded-md bg-background px-4 py-2 text-sm font-medium transition-smooth hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground focus:outline-none disabled:pointer-events-none disabled:opacity-50"
                  >
                    Precios
                  </Link>
                </NavigationMenuItem>

                <NavigationMenuItem>
                  <Link
                    to="/blog"
                    className="inline-flex h-10 w-max items-center justify-center rounded-md bg-background px-4 py-2 text-sm font-medium transition-smooth hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground focus:outline-none disabled:pointer-events-none disabled:opacity-50"
                  >
                    Blog
                  </Link>
                </NavigationMenuItem>
              </NavigationMenuList>
            </NavigationMenu>
          </div>

          {/* CTA Buttons */}
          <div className="hidden items-center space-x-3 lg:flex">
            <Button variant="ghost" asChild>
              <Link to="#login">Iniciar Sesión</Link>
            </Button>
            <Button variant="default" asChild>
              <Link to="#demo">Solicitar una Demo</Link>
            </Button>
          </div>

          {/* Mobile Menu */}
          <Sheet open={mobileOpen} onOpenChange={setMobileOpen}>
            <SheetTrigger asChild className="lg:hidden">
              <Button variant="ghost" size="icon">
                <Menu className="h-5 w-5" />
              </Button>
            </SheetTrigger>
            <SheetContent side="right" className="w-[300px] sm:w-[400px]">
              <div className="flex flex-col space-y-4 py-4">
                <Link to="/" className="flex items-center space-x-2">
                  <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary">
                    <Calendar className="h-5 w-5 text-primary-foreground" />
                  </div>
                  <span className="text-xl font-bold">AgendaMed</span>
                </Link>
                
                <div className="space-y-3 pt-4">
                  <div className="space-y-2">
                    <p className="font-semibold text-sm text-muted-foreground">Producto</p>
                    {productLinks.map((item) => (
                      <Link
                        key={item.title}
                        to={item.href}
                        className="flex items-start space-x-3 rounded-md p-2 hover:bg-accent"
                        onClick={() => setMobileOpen(false)}
                      >
                        <item.icon className="h-5 w-5 text-primary mt-0.5" />
                        <div>
                          <p className="font-medium text-sm">{item.title}</p>
                          <p className="text-xs text-muted-foreground">{item.description}</p>
                        </div>
                      </Link>
                    ))}
                  </div>

                  <div className="space-y-2 pt-4">
                    <p className="font-semibold text-sm text-muted-foreground">Soluciones</p>
                    {solutionLinks.map((item) => (
                      <Link
                        key={item.title}
                        to={item.href}
                        className="flex items-start space-x-3 rounded-md p-2 hover:bg-accent"
                        onClick={() => setMobileOpen(false)}
                      >
                        <item.icon className="h-5 w-5 text-primary mt-0.5" />
                        <div>
                          <p className="font-medium text-sm">{item.title}</p>
                          <p className="text-xs text-muted-foreground">{item.description}</p>
                        </div>
                      </Link>
                    ))}
                  </div>

                  <div className="flex flex-col space-y-2 pt-4 border-t">
                    <Button variant="ghost" asChild className="justify-start">
                      <Link to="/precios" onClick={() => setMobileOpen(false)}>
                        <CreditCard className="mr-2 h-4 w-4" />
                        Precios
                      </Link>
                    </Button>
                    <Button variant="ghost" asChild className="justify-start">
                      <Link to="/blog" onClick={() => setMobileOpen(false)}>
                        <BookOpen className="mr-2 h-4 w-4" />
                        Blog
                      </Link>
                    </Button>
                    <Button variant="ghost" asChild className="justify-start">
                      <Link to="/contacto" onClick={() => setMobileOpen(false)}>
                        <BookOpen className="mr-2 h-4 w-4" />
                        Contacto
                      </Link>
                    </Button>
                  </div>

                  <div className="flex flex-col space-y-2 pt-4 border-t">
                    <Button variant="outline" asChild>
                      <Link to="#login" onClick={() => setMobileOpen(false)}>Iniciar Sesión</Link>
                    </Button>
                    <Button variant="default" asChild>
                      <Link to="#demo" onClick={() => setMobileOpen(false)}>Solicitar una Demo</Link>
                    </Button>
                  </div>
                </div>
              </div>
            </SheetContent>
          </Sheet>
        </div>
      </div>
    </nav>
  );
}

const ListItem = ({
  className,
  title,
  children,
  icon: Icon,
  href,
  ...props
}: {
  className?: string;
  title: string;
  children: React.ReactNode;
  icon?: any;
  href: string;
}) => {
  return (
    <li>
      <NavigationMenuLink asChild>
        <a
          href={href}
          className={cn(
            "block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-smooth hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground",
            className
          )}
          {...props}
        >
          <div className="flex items-start space-x-3">
            {Icon && <Icon className="h-5 w-5 text-primary mt-0.5 flex-shrink-0" />}
            <div>
              <div className="text-sm font-medium leading-none mb-1">{title}</div>
              <p className="line-clamp-2 text-sm leading-snug text-muted-foreground">
                {children}
              </p>
            </div>
          </div>
        </a>
      </NavigationMenuLink>
    </li>
  );
};
