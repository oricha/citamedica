# Entorno local (Terraform + Docker)

Este entorno levanta los mismos contenedores de `docker-compose.yml` directamente con Terraform.

## Uso

```bash
cd infra/local
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform apply
```

Este módulo levanta **Postgres clínica y backend en Docker**. Para desarrollo con **Postgres en el host**, usa el `docker-compose.yml` de la raíz del repo con `make dev` (sin perfil `docker-db`) y `make backend-local`.

## Requisitos

- Docker corriendo localmente.
- Terraform >= 1.5.
