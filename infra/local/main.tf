terraform {
  required_version = ">= 1.5.0"

  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {}

resource "docker_network" "citamedica" {
  name = "citamedica-network"
}

resource "docker_volume" "postgres_clinic_data" {
  name = "postgres-clinic-data"
}

resource "docker_volume" "postgres_cal_data" {
  name = "postgres-cal-data"
}

resource "docker_image" "postgres" {
  name = "postgres:16-alpine"
}

resource "docker_image" "calcom" {
  name = var.calcom_image
}

resource "docker_image" "backend" {
  name = var.backend_image_name

  build {
    context    = "${path.module}/../../apps/backend"
    dockerfile = "Dockerfile"
  }
}

resource "docker_image" "landing" {
  name = var.landing_image_name

  build {
    context    = "${path.module}/../../apps/landing"
    dockerfile = "Dockerfile"
  }
}

resource "docker_container" "postgres_clinic" {
  name  = "citamedica-postgres"
  image = docker_image.postgres.image_id

  env = [
    "POSTGRES_DB=citamedica",
    "POSTGRES_USER=citamedica",
    "POSTGRES_PASSWORD=${var.postgres_clinic_password}",
  ]

  ports {
    internal = 5432
    external = 5432
  }

  volumes {
    volume_name    = docker_volume.postgres_clinic_data.name
    container_path = "/var/lib/postgresql/data"
  }

  networks_advanced {
    name = docker_network.citamedica.name
  }

  restart = "unless-stopped"
}

resource "docker_container" "postgres_cal" {
  name  = "calcom-postgres"
  image = docker_image.postgres.image_id

  env = [
    "POSTGRES_DB=calcom",
    "POSTGRES_USER=calcom",
    "POSTGRES_PASSWORD=${var.postgres_cal_password}",
  ]

  ports {
    internal = 5432
    external = 5433
  }

  volumes {
    volume_name    = docker_volume.postgres_cal_data.name
    container_path = "/var/lib/postgresql/data"
  }

  networks_advanced {
    name = docker_network.citamedica.name
  }

  restart = "unless-stopped"
}

resource "docker_container" "backend" {
  name  = "citamedica-backend"
  image = docker_image.backend.image_id

  env = [
    "SPRING_PROFILES_ACTIVE=dev",
    "SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-clinic:5432/citamedica",
    "SPRING_DATASOURCE_USERNAME=citamedica",
    "SPRING_DATASOURCE_PASSWORD=${var.postgres_clinic_password}",
    "CALCOM_API_URL=http://calcom:3000/api/v2",
    "CALCOM_API_KEY=${var.calcom_api_key}",
    "CALCOM_WEBHOOK_SECRET=${var.calcom_webhook_secret}",
    "JWT_SECRET=${var.jwt_secret}",
    "NOTIFICATIONS_ENABLED=${var.notifications_enabled}",
  ]

  ports {
    internal = 8080
    external = 8080
  }

  networks_advanced {
    name    = docker_network.citamedica.name
    aliases = ["backend-api"]
  }

  depends_on = [docker_container.postgres_clinic]
  restart    = "unless-stopped"
}

resource "docker_container" "landing" {
  name  = "citamedica-landing"
  image = docker_image.landing.image_id

  env = [
    "NEXT_PUBLIC_API_URL=http://localhost:8080",
    "NEXT_PUBLIC_CALCOM_URL=http://localhost:3000",
  ]

  ports {
    internal = 3001
    external = 3001
  }

  networks_advanced {
    name = docker_network.citamedica.name
  }

  restart = "unless-stopped"
}

resource "docker_container" "calcom" {
  name  = "citamedica-calcom"
  image = docker_image.calcom.image_id

  env = [
    "DATABASE_URL=postgresql://calcom:${var.postgres_cal_password}@postgres-cal:5432/calcom",
    "DATABASE_DIRECT_URL=postgresql://calcom:${var.postgres_cal_password}@postgres-cal:5432/calcom",
    "NEXTAUTH_SECRET=${var.nextauth_secret}",
    "CALENDSO_ENCRYPTION_KEY=${var.calendso_encryption_key}",
    "NEXT_PUBLIC_WEBAPP_URL=http://localhost:3000",
    "NEXT_PUBLIC_API_V2_URL=http://localhost:3000/api/v2",
    "WEBHOOK_SECRET=${var.calcom_webhook_secret}",
    "SKIP_DB_MIGRATION=false",
    "SKIP_ENV_VALIDATION=true",
  ]

  ports {
    internal = 3000
    external = 3000
  }

  networks_advanced {
    name    = docker_network.citamedica.name
    aliases = ["calcom"]
  }

  depends_on = [docker_container.postgres_cal]
  restart    = "unless-stopped"
}
