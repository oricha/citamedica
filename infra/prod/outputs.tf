output "ecs_cluster_name" {
  description = "Nombre del cluster ECS"
  value       = aws_ecs_cluster.main.name
}

output "ecs_service_names" {
  description = "Servicios desplegados en ECS"
  value       = [for svc in aws_ecs_service.service : svc.name]
}

output "public_subnet_ids" {
  description = "Subnets publicas usadas por ECS"
  value       = [for s in aws_subnet.public : s.id]
}
