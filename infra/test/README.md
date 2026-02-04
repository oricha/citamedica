# Entorno test (Servidor Linux + Dokploy)

Este entorno genera un `docker-compose.yml` y lo despliega por SSH en tu servidor Linux.

## Uso

```bash
cd infra/test
cp terraform.tfvars.example terraform.tfvars
terraform init
terraform apply
```

## Requisitos

- Servidor Linux con Docker y Docker Compose.
- Acceso SSH por llave privada.
- Imágenes publicadas para `backend` y `landing`.
