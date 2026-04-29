variable "backend_image_name" {
  description = "Nombre de la imagen Docker local del backend"
  type        = string
  default     = "citamedica/backend:local"
}

variable "landing_image_name" {
  description = "Nombre de la imagen Docker local de landing"
  type        = string
  default     = "citamedica/landing:local"
}

variable "calcom_image" {
  description = "Imagen de Cal.com"
  type        = string
  default     = "calcom/cal.com:latest"
}

variable "postgres_clinic_password" {
  description = "Password de PostgreSQL para CitaMedica"
  type        = string
  sensitive   = true
  default     = "citamedica123"
}

variable "postgres_cal_password" {
  description = "Password de PostgreSQL para Cal.com"
  type        = string
  sensitive   = true
  default     = "calcom123"
}

variable "calcom_api_key" {
  description = "API key de Cal.com usada por backend"
  type        = string
  default     = ""
  sensitive   = true
}

variable "calcom_webhook_secret" {
  description = "Webhook secret compartido entre backend y Cal.com"
  type        = string
  default     = "dev-webhook-secret-change-in-production"
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret del backend"
  type        = string
  default     = "dev-secret-key-change-in-production"
  sensitive   = true
}

variable "notifications_enabled" {
  description = "Activa o desactiva notificaciones en backend"
  type        = string
  default     = "false"
}

variable "nextauth_secret" {
  description = "NEXTAUTH_SECRET de Cal.com"
  type        = string
  default     = "change-this-to-a-random-string-in-production"
  sensitive   = true
}

variable "calendso_encryption_key" {
  description = "CALENDSO_ENCRYPTION_KEY de Cal.com"
  type        = string
  default     = "change-this-to-a-32-char-random-string"
  sensitive   = true
}
