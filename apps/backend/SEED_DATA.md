# Seed Data - CitaMedica

## Descripción

El servicio `SeedDataService` carga automáticamente datos de prueba en la base de datos cuando la aplicación se inicia por primera vez. Este servicio es **idempotente**, lo que significa que puede ejecutarse múltiples veces sin duplicar datos.

## Datos Creados

El script de seed crea los siguientes datos de ejemplo:

### 1 Clínica
- **Nombre**: Clínica Demo CitaMedica
- **Slug**: clinica-demo
- **Dirección**: Calle Principal 123, Madrid, 28001
- **Teléfono**: +34 912 345 678
- **Cal Team ID**: demo-team-id

### 2 Médicos
1. **Dr. María García López**
   - Especialidad: Cardiología
   - Email: maria.garcia@clinicademo.com
   - Teléfono: +34 612 345 678
   - Cal Username: dr-maria-garcia
   - Estado: Activo

2. **Dr. Juan Martínez Ruiz**
   - Especialidad: Medicina General
   - Email: juan.martinez@clinicademo.com
   - Teléfono: +34 623 456 789
   - Cal Username: dr-juan-martinez
   - Estado: Activo

### 3 Pacientes
1. **Ana Rodríguez Sánchez**
   - Email: ana.rodriguez@email.com
   - Teléfono: +34 634 567 890
   - Fecha de nacimiento: 15/03/1985
   - Seguro: Sanitas Básico

2. **Carlos Fernández López**
   - Email: carlos.fernandez@email.com
   - Teléfono: +34 645 678 901
   - Fecha de nacimiento: 22/07/1978
   - Seguro: Adeslas Premium

3. **Laura Gómez Martín**
   - Email: laura.gomez@email.com
   - Teléfono: +34 656 789 012
   - Fecha de nacimiento: 08/11/1992
   - Seguro: DKV Salud

### 4 Citas de Ejemplo
1. **Cita Pasada (Completada)**
   - Médico: Dr. María García López
   - Paciente: Ana Rodríguez Sánchez
   - Tipo: Consulta de Cardiología
   - Fecha: Hace 7 días
   - Estado: COMPLETED
   - Notas: Revisión rutinaria. Paciente en buen estado de salud.

2. **Cita de Hoy (Programada)**
   - Médico: Dr. Juan Martínez Ruiz
   - Paciente: Carlos Fernández López
   - Tipo: Consulta General
   - Fecha: Hoy a las 15:00
   - Estado: SCHEDULED
   - Notas: Primera consulta. Revisión general.

3. **Cita Futura (Programada)**
   - Médico: Dr. María García López
   - Paciente: Laura Gómez Martín
   - Tipo: Electrocardiograma
   - Fecha: En 3 días a las 11:30
   - Estado: SCHEDULED
   - Notas: Electrocardiograma de control.

4. **Cita Futura (Programada)**
   - Médico: Dr. Juan Martínez Ruiz
   - Paciente: Ana Rodríguez Sánchez
   - Tipo: Seguimiento
   - Fecha: En 14 días a las 09:00
   - Estado: SCHEDULED
   - Notas: Seguimiento de tratamiento.

## Uso

### Ejecución Automática

El servicio se ejecuta automáticamente cuando la aplicación Spring Boot se inicia, **solo si no existen datos previos** (verifica la existencia de la clínica "clinica-demo").

### Ejecución Manual con Make

Para cargar los datos de prueba manualmente:

```bash
make seed
```

Este comando:
1. Reinicia el servicio backend
2. El servicio detecta que no hay datos y los carga automáticamente
3. Muestra un resumen de los datos creados

### Verificación

Para verificar que los datos se cargaron correctamente:

```bash
# Ver logs del backend
make backend-logs

# Conectar a la base de datos
make db-shell

# Dentro de psql, ejecutar:
SELECT * FROM clinic;
SELECT * FROM doctor;
SELECT * FROM patient;
SELECT * FROM appointment;
```

### Consultar vía API

También puedes verificar los datos usando los endpoints REST:

```bash
# Obtener médicos de la clínica
curl http://localhost:8080/api/v1/doctors?clinicId=1

# Obtener citas de un médico
curl http://localhost:8080/api/v1/appointments?doctorId=1&date=2025-10-27
```

## Idempotencia

El script es **idempotente**, lo que significa que:
- Si los datos ya existen, no se crean duplicados
- Verifica la existencia de la clínica "clinica-demo" antes de crear datos
- Puede ejecutarse múltiples veces de forma segura
- Registra en los logs si los datos ya existen

## Logs

El servicio genera logs detallados durante la ejecución:

```
INFO  - Starting seed data process...
INFO  - Created clinic: Clínica Demo CitaMedica (ID: 1)
INFO  - Created 2 doctors
INFO  - Created 3 patients
INFO  - Created 4 appointments
INFO  - Seed data process completed successfully!
INFO  - Summary: 1 clinic, 2 doctors, 3 patients, 4 appointments
```

Si los datos ya existen:

```
INFO  - Starting seed data process...
INFO  - Seed data already exists. Skipping seed process.
```

## Desactivar en Producción

El servicio está configurado con `@Profile("!test")`, lo que significa que:
- Se ejecuta en todos los perfiles excepto en `test`
- Para desactivarlo en producción, puedes:
  1. Usar el perfil `prod` y modificar la anotación a `@Profile("dev")`
  2. O eliminar/comentar el servicio antes del despliegue

## Resetear Datos

Para eliminar todos los datos y volver a cargarlos:

```bash
# Opción 1: Resetear base de datos completa
make db-reset

# Opción 2: Limpiar volúmenes y reiniciar
make clean
make dev
```

## Personalización

Para modificar los datos de prueba, edita el archivo:
```
apps/backend/src/main/java/com/citamedica/backend/service/SeedDataService.java
```

Puedes personalizar:
- Nombres y datos de la clínica
- Información de médicos y especialidades
- Datos de pacientes
- Fechas y tipos de citas