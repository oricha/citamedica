# Entorno prod (AWS ECS Fargate)

Este entorno crea infraestructura base en AWS y despliega backend, landing y cal.com en ECS Fargate.

## Uso

```bash
cd infra/prod
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform apply
```

## Requisitos

- Credenciales AWS configuradas.
- Imágenes disponibles (recomendado en ECR) para backend y landing.
- Base de datos externa (por ejemplo RDS) para backend y cal.com.

## Nota

Esta es una base funcional. Para produccion completa se recomienda agregar ALB, HTTPS (ACM), dominio (Route53), WAF y autoscaling.
