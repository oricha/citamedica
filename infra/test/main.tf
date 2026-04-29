terraform {
  required_version = ">= 1.5.0"

  required_providers {
    local = {
      source  = "hashicorp/local"
      version = "~> 2.5"
    }
    null = {
      source  = "hashicorp/null"
      version = "~> 3.2"
    }
  }
}

locals {
  rendered_compose = templatefile("${path.module}/templates/docker-compose.test.yml.tftpl", {
    backend_image           = var.backend_image
    landing_image           = var.landing_image
    calcom_image            = var.calcom_image
    postgres_clinic_pass    = var.postgres_clinic_password
    postgres_cal_pass       = var.postgres_cal_password
    calcom_api_url          = var.calcom_api_url
    calcom_api_key          = var.calcom_api_key
    calcom_webhook_secret   = var.calcom_webhook_secret
    jwt_secret              = var.jwt_secret
    notifications_enabled   = var.notifications_enabled
    nextauth_secret         = var.nextauth_secret
    calendso_encryption_key = var.calendso_encryption_key
    app_public_url          = var.app_public_url
    calcom_public_url       = var.calcom_public_url
  })
}

resource "local_file" "compose_file" {
  filename = "${path.module}/docker-compose.generated.yml"
  content  = local.rendered_compose
}

resource "null_resource" "deploy_dokploy_host" {
  triggers = {
    compose_sha = sha256(local.rendered_compose)
    deploy_path = var.deploy_path
  }

  connection {
    type        = "ssh"
    host        = var.ssh_host
    user        = var.ssh_user
    private_key = file(var.ssh_private_key_path)
    port        = var.ssh_port
  }

  provisioner "remote-exec" {
    inline = [
      "mkdir -p ${var.deploy_path}",
      "mkdir -p ${var.deploy_path}/data/postgres-clinic",
      "mkdir -p ${var.deploy_path}/data/postgres-cal"
    ]
  }

  provisioner "file" {
    source      = local_file.compose_file.filename
    destination = "${var.deploy_path}/docker-compose.yml"
  }

  provisioner "remote-exec" {
    inline = [
      "cd ${var.deploy_path}",
      "docker compose pull",
      "docker compose up -d"
    ]
  }
}
