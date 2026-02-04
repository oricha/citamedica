output "local_urls" {
  description = "URLs locales de los servicios"
  value = {
    landing = "http://localhost:3001"
    backend = "http://localhost:8080"
    calcom  = "http://localhost:3000"
  }
}
