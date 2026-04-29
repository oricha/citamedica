# Infraestructura Terraform de CitaMedica

Esta carpeta contiene 3 entornos:

- `local`: levanta los contenedores actuales en Docker local usando Terraform.
- `test`: despliega en un servidor Linux (Dokploy) por SSH con Docker Compose.
- `prod`: despliegue base en AWS ECS Fargate para backend, landing y cal.com.

## Flujo recomendado

1. Entra al entorno (`cd infra/local`, `infra/test` o `infra/prod`).
2. Crea tu archivo `terraform.tfvars` desde `terraform.tfvars.example`.
3. Ejecuta:

```bash
terraform init
terraform plan
terraform apply
```

> Nota: en `prod` se asume base de datos administrada externa (por ejemplo RDS), no PostgreSQL en contenedor.
