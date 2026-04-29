variable "ssh_host" {
  description = "IP o dominio del servidor Linux (Dokploy)"
  type        = string
}

variable "ssh_user" {
  description = "Usuario SSH del servidor"
  type        = string
  default     = "root"
}

variable "ssh_port" {
  description = "Puerto SSH"
  type        = number
  default     = 22
}

variable "ssh_private_key_path" {
  description = "Ruta a la llave privada SSH"
  type        = string
}

variable "deploy_path" {
  description = "Directorio remoto donde se desplegara Docker Compose"
  type        = string
  default     = "/opt/citamedica"
}

variable "backend_image" {
  description = "Imagen publicada del backend"
  type        = string
}

variable "landing_image" {
  description = "Imagen publicada de landing"
  type        = string
}

variable "calcom_image" {
  description = "Imagen de Cal.com"
  type        = string
  default     = "calcom/cal.com:latest"
}

variable "postgres_clinic_password" {
  description = "Password PostgreSQL app"
  type        = string
  sensitive   = true
}

variable "postgres_cal_password" {
  description = "Password PostgreSQL Cal.com"
  type        = string
  sensitive   = true
}

variable "calcom_api_url" {
  description = "URL API Cal.com usada por backend"
  type        = string
  default     = "http://calcom:3000/api/v2"
}

variable "calcom_api_key" {
  description = "API key de Cal.com"
  type        = string
  default     = ""
  sensitive   = true
}

variable "calcom_webhook_secret" {
  description = "Webhook secret compartido"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret backend"
  type        = string
  sensitive   = true
}

variable "notifications_enabled" {
  description = "Activa notificaciones backend"
  type        = string
  default     = "false"
}

variable "nextauth_secret" {
  description = "NEXTAUTH_SECRET Cal.com"
  type        = string
  sensitive   = true
}

variable "calendso_encryption_key" {
  description = "CALENDSO_ENCRYPTION_KEY Cal.com"
  type        = string
  sensitive   = true
}

variable "app_public_url" {
  description = "URL publica del backend para landing"
  type        = string
  default     = "http://localhost:8080"
}

variable "calcom_public_url" {
  description = "URL publica de Cal.com"
  type        = string
  default     = "http://localhost:3000"
}
