output "dokploy_server" {
  description = "Servidor donde se desplego test"
  value       = var.ssh_host
}

output "compose_path" {
  description = "Ruta remota del docker-compose.yml"
  value       = "${var.deploy_path}/docker-compose.yml"
}
