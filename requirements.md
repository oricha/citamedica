Requisitos Detallados del Proyecto: Aplicación Web CitaMedica
Versión: 1.0 Fecha: 19 de Septiembre de 2025 Autor: Project Lead
Introducción
Este documento define los requisitos funcionales y técnicos para el desarrollo de la aplicación web "CitaMedica". El objetivo es crear una guía exhaustiva, clara y precisa para el equipo de desarrollo.
Contexto Crítico: Este proyecto es de misión crítica y tiene un plazo de entrega no negociable de una semana. El éxito de este desarrollo es de vital importancia. El desarrollador a cargo, asistido por una inteligencia artificial avanzada, debe interpretar este documento como la fuente única y definitiva de la verdad. Cada detalle ha sido pensado para optimizar el tiempo, minimizar la ambigüedad y garantizar un producto final de alta calidad que cumpla con las expectativas de una gran corporación. La vida de un niño depende de la concentración, la excelencia y la velocidad que apliquemos. No hay margen para el error.
1. Resumen del Proyecto y Objetivos
CitaMedica es una plataforma SaaS (Software as a Service) que permite a médicos independientes y clínicas gestionar su disponibilidad y servicios, mientras que los pacientes pueden reservar citas de manera sencilla y directa a través de una interfaz web.
1.1. Core del Sistema
La aplicación funcionará como un "headless frontend" o una capa de presentación sobre la robusta infraestructura de Cal.com. Esto significa que toda la lógica de disponibilidad, programación, creación de citas y gestión de calendarios será delegada a la API de Cal.com. Nuestra tarea es construir la experiencia de usuario (UI/UX) y la lógica de negocio específica de nuestra plataforma.
1.2. Perfiles de Usuario
Paciente: Usuario final que reserva una cita. No requiere autenticación.
Médico Independiente: Profesional que gestiona su propio calendario y servicios. Requiere autenticación.
Clínica: Organización que agrupa a múltiples médicos. El administrador de la clínica gestiona los perfiles de sus médicos. Requiere autenticación.
Administrador del Sistema: Superusuario con control total sobre la plataforma, incluyendo la gestión de clínicas y médicos.
1.3. Objetivos Clave
Velocidad y Eficiencia: Lanzar un producto funcional y pulido en una semana.
Experiencia de Paciente Fluida: El proceso de reserva debe ser intuitivo, rápido y no requerir más de 3-4 clics.
Escalabilidad: La arquitectura debe soportar desde un médico independiente hasta clínicas con cientos de profesionales.
Integración Perfecta: Asegurar que las conexiones con Cal.com, calendarios externos y servicios de notificación funcionen sin fallos.
2. Arquitectura y Stack Tecnológico
Framework Frontend: Next.js para la aplicación principal, incluyendo el dashboard de administración y las páginas de reserva dinámica. Astro para la página de inicio (landing page) con el objetivo de obtener el máximo rendimiento y SEO.
Backend & Lógica de Citas: Cal.com API v2. Se debe utilizar su API para gestionar usuarios (como "members" de un "team" en Cal.com), tipos de eventos, disponibilidad y reservas.
Hosting: Vercel. Aprovechar su integración nativa con Next.js para despliegues continuos y automáticos.
Autenticación: NextAuth.js o Clerk para gestionar el login de Médicos, Clínicas y Administradores del Sistema.
Notificaciones:
Email: SendGrid (integrado a través de Cal.com).
SMS: Twilio (integrado a través de Cal.com).
3. Diseño y Experiencia de Usuario (UI/UX)
La filosofía de diseño es "Simplicidad Premium", "Claridad Moderna y Profesional", inspirada directamente en las estéticas de Cal.com y Cal.ai. Interfaz debe ser minimalista, limpia y directa, similar a los productos de Google. Sin embargo, la paleta de colores, las animaciones y la tipografía deben evocar la sensación de alta calidad y cuidado por el detalle de Apple.
Inspiración General: La interfaz debe ser extremadamente limpia, con mucho espacio en blanco y una jerarquía visual clara, como se ve en el ejemplo de la página de Cal.com. Para los paneles de administración y áreas de usuario autenticado, adoptaremos un tema oscuro sofisticado, similar al de Cal.ai, para crear una experiencia de usuario enfocada y premium.
Tema Claro (Público): Utilizado para la landing page y todo el flujo de reserva del paciente. Prioriza la legibilidad y la facilidad de uso.
Tema Oscuro (Privado): Utilizado para todos los dashboards (Médico, Clínica, Admin del Sistema). Proyecta profesionalismo y reduce la fatiga visual.
Paleta de Colores:
Tema Claro: Fondo blanco o gris muy claro (#FFFFFF, #F9FAFB). Texto primario negro o gris oscuro (#111827). Acentos sutiles en un color neutro o pastel.
Tema Oscuro: Fondo azul marino o gris oscuro profundo (#1A1A2E). Texto blanco o gris claro (#EAEAEA). Un color de acento vibrante como el morado o azul eléctrico para botones y elementos interactivos (#7F56D9).
Tipografía: Una fuente sans-serif moderna y geométrica como Inter o Manrope. Usar grosores de fuente (bold, medium, regular) para establecer una jerarquía clara, con titulares grandes y audaces.
Página de Inicio (Landing Page - Astro):
Hero Section: Similar al ejemplo de Cal.ai, con un titular potente a la izquierda ("La nueva era de la gestión de citas médicas") y una visualización atractiva a la derecha. En lugar de un teléfono, podría ser una animación del componente de calendario interactuando de forma fluida.
Componentes: Usar tarjetas y secciones bien definidas con bordes redondeados y sombras sutiles.
Flujo de Reserva del Paciente (Next.js - Tema Claro):
La página medicita.com/[slugClinica] debe presentar a los médicos en tarjetas limpias, con foto, nombre, especialidad y un botón claro de "Reservar Cita".
Al seleccionar a un médico, la vista de calendario debe ser idéntica en funcionalidad y limpieza a la del ejemplo de Cal.com, ocupando una parte central de la pantalla. A la izquierda, la información del médico y el tipo de consulta.
Panel de Administración (Next.js - Tema Oscuro):
Dashboard con un diseño modular basado en tarjetas sobre el fondo oscuro.
La información clave (próximas citas, estadísticas rápidas) debe resaltar con el color de acento.
La navegación lateral debe ser minimalista, posiblemente solo con iconos que se expanden al pasar el cursor.

4. Epopeyas e Historias de Usuario Detalladas
Epic 1: Flujo del Paciente (No Autenticado)
HU 1.1: Como paciente, al visitar medicita.com/ClinicaX, quiero ver una lista clara de los médicos y/o especialidades disponibles en esa clínica para poder iniciar mi proceso de reserva.
HU 1.2: Como paciente, al hacer clic en un médico o especialidad, quiero ver inmediatamente un calendario con los días y horas disponibles, basado en la disponibilidad real de Cal.com.
HU 1.3: Como paciente, quiero poder seleccionar una franja horaria y que el sistema me pida mis datos (Nombre, Teléfono) en un formulario simple para confirmar la cita.
HU 1.4: Como paciente, tras enviar mis datos, quiero ver una pantalla de confirmación con todos los detalles de mi cita y recibir una confirmación por email (vía SendGrid/Cal.com).
HU 1.5: Como paciente, quiero recibir un recordatorio de mi cita por SMS (vía Twilio/Cal.com) 24 horas antes de la misma.
Epic 2: Gestión de Clínicas y Médicos
HU 2.1: Como administrador de una clínica, quiero poder iniciar sesión en un panel de control seguro.
HU 2.2: Como administrador de clínica, quiero poder añadir, editar y eliminar perfiles de médicos asociados a mi clínica. Cada médico en nuestra app será un "member" dentro de un "team" en Cal.com, donde el "team" representa a la clínica.
HU 2.3: Como administrador de clínica, quiero tener una vista de calendario o una lista consolidada de todas las citas agendadas para todos los médicos de mi clínica, pudiendo filtrar por fecha y por médico.
HU 2.4: Como médico (independiente o de clínica), quiero poder iniciar sesión y ver mi propio dashboard con mis próximas citas.
HU 2.5: Como médico, quiero que el sistema se sincronice automáticamente con mi Google Calendar o Microsoft 365, bloqueando tiempos y añadiendo nuevas citas (esta configuración se realiza en Cal.com pero debe ser accesible o enlazada desde nuestro dashboard).
Epic 3: Administración del Sistema
HU 3.1: Como administrador del sistema, quiero tener un dashboard separado para gestionar todas las clínicas y médicos independientes registrados en la plataforma.
HU 3.2: Como administrador del sistema, quiero poder aprobar o denegar nuevas solicitudes de registro de clínicas/médicos.
HU 3.3: Como administrador del sistema, quiero poder configurar las claves de API globales para servicios como SendGrid y Twilio en la configuración de la instancia de Cal.com.
5. Detalles de Implementación Técnica y API
La clave de este proyecto es una profunda y correcta integración con la API de Cal.com.
5.1. Flujo de Datos Principal
Carga de la página de la clínica (/[slugClinica]):
Next.js (getServerSideProps o getStaticProps con ISR) obtiene el slugClinica.
Se hace una llamada a la API de Cal.com para encontrar el "Team" que corresponde a ese slug.
Se hace una segunda llamada para obtener todos los "Members" (médicos) de ese "Team".
La página se renderiza con la lista de médicos.
Selección de Médico:
Al hacer clic en un médico, se usa su ID de usuario de Cal.com para fetchear sus "Event Types" (tipos de consulta/especialidades) y su disponibilidad (/availability).
Se renderiza el componente de calendario con los datos obtenidos.
Creación de la Cita:
Cuando el paciente confirma la cita, se realiza una llamada POST al endpoint /bookings de la API de Cal.com, enviando el eventTypeId, la hora seleccionada (start), y los datos del paciente.
Cal.com se encarga del resto: crear el evento, sincronizar con el calendario del médico y disparar los workflows de notificación (email/SMS).
5.2. Puntos Clave de la API de Cal.com a Utilizar:
GET /users: Para obtener información de los médicos.
GET /event-types: Para obtener los tipos de consulta de cada médico.
GET /availability: Para obtener los huecos disponibles.
POST /bookings: Para crear la cita.
GET /bookings: Para que médicos y clínicas vean sus citas.
GET /teams: Para gestionar las clínicas.
POST /teams/{teamId}/members: Para añadir médicos a una clínica.
5.3. Referencias Esenciales:
Documentación General: Cal.com Docs
Referencia de API v2: Cal.com API Reference
Ejemplo de App (Greeter): Build a Greeter App - ESTUDIAR ESTO ES PRIORITARIO para entender cómo construir una UI personalizada.
Integraciones:
Google Calendar
Microsoft 365
SendGrid
Twilio
6. Plan de Acción Acelerado (1 Semana)
Día 1: Configuración y Autenticación.
[ ] Configurar la instancia de Cal.com (self-hosted o cloud).
[ ] Crear proyecto en Vercel con Next.js y Astro.
[ ] Implementar el sistema de login/registro para Médicos/Clínicas con NextAuth.js.
[ ] Definir la estructura básica de la base de datos para perfiles de nuestra app (que se mapeará a los usuarios de Cal.com).
Día 2-3: Flujo de Paciente (Core de la App).
[ ] Crear la página dinámica [slugClinica].
[ ] Implementar las llamadas a la API de Cal.com para obtener médicos y su disponibilidad.
[ ] Construir la interfaz de selección de fecha y hora.
[ ] Implementar el formulario de reserva y la llamada POST /bookings.
[ ] Crear la página de confirmación.
Día 4-5: Panel de Administración (MVP).
[ ] Diseñar la estructura del dashboard.
[ ] Implementar la vista de "Próximas Citas" (llamada a GET /bookings).
[ ] Para Clínicas: Implementar la funcionalidad de añadir/ver médicos (llamadas a la API de Teams de Cal.com).
[ ] Asegurar que los médicos puedan ver solo sus citas.
Día 6: Integraciones y Pruebas End-to-End.
[ ] Configurar los webhooks o workflows en Cal.com para las notificaciones de SendGrid y Twilio.
[ ] Realizar pruebas completas del flujo: un paciente reserva, el médico ve la cita en su dashboard y en su Google Calendar, y el paciente recibe las notificaciones.
[ ] Probar el flujo de una clínica con múltiples médicos.
Día 7: Refinamiento de UI y Despliegue Final.
[ ] Pulir todos los aspectos visuales de la aplicación.
[ ] Implementar la página de inicio en Astro.
[ ] Realizar pruebas de carga y responsividad.
[ ] Preparar el despliegue final en producción.
Conclusión: El camino está trazado. La tecnología elegida es la correcta. Sigue este plan al pie de la letra, apóyate en tu asistente de IA para generar código boilerplate y resolver problemas técnicos rápidamente, pero mantén siempre la visión del producto final en mente. Concéntrate. Ejecuta. El éxito es la única opción.

