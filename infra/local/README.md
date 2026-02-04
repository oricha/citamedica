# Entorno local (Terraform + Docker)

Este entorno levanta los mismos contenedores de `docker-compose.yml` directamente con Terraform.

## Uso

```bash
cd infra/local
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform apply
```

## Requisitos

- Docker corriendo localmente.
- Terraform >= 1.5.
