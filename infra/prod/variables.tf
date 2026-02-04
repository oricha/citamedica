variable "project_name" {
  description = "Nombre del proyecto"
  type        = string
  default     = "citamedica"
}

variable "environment" {
  description = "Nombre del entorno"
  type        = string
  default     = "prod"
}

variable "aws_region" {
  description = "Region AWS"
  type        = string
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "CIDR para VPC"
  type        = string
  default     = "10.40.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "Subredes publicas para ECS"
  type        = list(string)
  default     = ["10.40.1.0/24", "10.40.2.0/24"]
}

variable "allowed_ingress_cidrs" {
  description = "CIDRs permitidos para exponer servicios"
  type        = list(string)
  default     = ["0.0.0.0/0"]
}

variable "fargate_cpu" {
  description = "CPU por tarea Fargate"
  type        = number
  default     = 512
}

variable "fargate_memory" {
  description = "Memoria por tarea Fargate"
  type        = number
  default     = 1024
}

variable "backend_image" {
  description = "Imagen backend (idealmente ECR)"
  type        = string
}

variable "landing_image" {
  description = "Imagen landing (idealmente ECR)"
  type        = string
}

variable "calcom_image" {
  description = "Imagen de Cal.com"
  type        = string
  default     = "calcom/cal.com:latest"
}

variable "backend_database_url" {
  description = "JDBC URL para backend"
  type        = string
}

variable "backend_database_user" {
  description = "Usuario DB backend"
  type        = string
}

variable "backend_database_password" {
  description = "Password DB backend"
  type        = string
  sensitive   = true
}

variable "calcom_database_url" {
  description = "DATABASE_URL para Cal.com"
  type        = string
}

variable "calcom_database_direct_url" {
  description = "DATABASE_DIRECT_URL para Cal.com"
  type        = string
}

variable "calcom_api_url" {
  description = "API URL de Cal.com para backend"
  type        = string
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
  description = "NEXTAUTH_SECRET de Cal.com"
  type        = string
  sensitive   = true
}

variable "calendso_encryption_key" {
  description = "CALENDSO_ENCRYPTION_KEY de Cal.com"
  type        = string
  sensitive   = true
}

variable "backend_public_url" {
  description = "URL publica del backend"
  type        = string
}

variable "calcom_public_url" {
  description = "URL publica de Cal.com"
  type        = string
}
