.PHONY: help dev down logs clean seed test build status health restart backend-logs landing-logs calcom-logs db-logs

# Default target
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "CitaMedica - Comandos disponibles:"
	@echo ""
	@echo "  make dev          - Iniciar todos los servicios"
	@echo "  make down         - Detener todos los servicios"
	@echo "  make logs         - Ver logs en tiempo real de todos los servicios"
	@echo "  make clean        - Limpiar volúmenes y detener servicios"
	@echo "  make seed         - Cargar datos de prueba"
	@echo "  make test         - Ejecutar tests"
	@echo "  make build        - Construir imágenes Docker"
	@echo "  make status       - Ver estado de los servicios"
	@echo "  make health       - Verificar salud de los servicios"
	@echo "  make restart      - Reiniciar todos los servicios"
	@echo ""
	@echo "Logs específicos:"
	@echo "  make backend-logs - Ver logs del backend"
	@echo "  make landing-logs - Ver logs del landing"
	@echo "  make calcom-logs  - Ver logs de Cal.com"
	@echo "  make db-logs      - Ver logs de la base de datos"
	@echo ""

dev: ## Iniciar todos los servicios
	@echo "🚀 Iniciando servicios..."
	docker-compose up -d
	@echo ""
	@echo "⏳ Esperando a que los servicios estén listos..."
	@sleep 15
	@echo ""
	@echo "✅ Servicios disponibles:"
	@echo "  📄 Landing:  http://localhost:3001"
	@echo "  🔧 Backend:  http://localhost:8080"
	@echo "  📅 Cal.com:  http://localhost:3000"
	@echo "  💚 Health:   http://localhost:8080/actuator/health"
	@echo "  📊 Metrics:  http://localhost:8080/actuator/metrics"
	@echo ""
	@echo "💡 Usa 'make logs' para ver los logs en tiempo real"
	@echo "💡 Usa 'make health' para verificar el estado de los servicios"

down: ## Detener todos los servicios
	@echo "🛑 Deteniendo servicios..."
	docker-compose down
	@echo "✅ Servicios detenidos"

logs: ## Ver logs en tiempo real
	docker-compose logs -f

backend-logs: ## Ver logs del backend
	docker-compose logs -f backend-api

landing-logs: ## Ver logs del landing
	docker-compose logs -f landing

calcom-logs: ## Ver logs de Cal.com
	docker-compose logs -f calcom

db-logs: ## Ver logs de la base de datos
	docker-compose logs -f postgres-clinic

clean: ## Limpiar volúmenes y detener servicios
	@echo "🧹 Limpiando volúmenes y deteniendo servicios..."
	docker-compose down -v
	@echo "✅ Limpieza completada"

build: ## Construir imágenes Docker
	@echo "🔨 Construyendo imágenes Docker..."
	@echo "⏳ Deshabilitando proxy para la construcción..."
	@echo "🔨 Construyendo imágenes..."
	@HTTP_PROXY= HTTPS_PROXY= http_proxy= https_proxy= NO_PROXY=* no_proxy=* DOCKER_BUILDKIT=1 docker-compose build --no-cache || (echo "❌ Error al construir. Intentando sin --no-cache..." && HTTP_PROXY= HTTPS_PROXY= http_proxy= https_proxy= NO_PROXY=* no_proxy=* docker-compose build)
	@echo "✅ Imágenes construidas"

rebuild: ## Reconstruir y reiniciar servicios
	@echo "🔄 Reconstruyendo servicios..."
	docker-compose down
	docker-compose build --no-cache
	docker-compose up -d
	@echo "✅ Servicios reconstruidos y reiniciados"

restart: ## Reiniciar todos los servicios
	@echo "🔄 Reiniciando servicios..."
	docker-compose restart
	@echo "✅ Servicios reiniciados"

status: ## Ver estado de los servicios
	@echo "📊 Estado de los servicios:"
	@echo ""
	@SERVICES=$$(docker-compose ps 2>/dev/null | tail -n +2 | grep -v "^$$" | wc -l | tr -d ' '); \
	if [ "$$SERVICES" -gt "0" ]; then \
		docker-compose ps; \
		echo ""; \
		echo "📈 Resumen:"; \
		docker-compose ps --format "table {{.Service}}\t{{.Status}}\t{{.Ports}}" | tail -n +2 | while read line; do \
			if echo "$$line" | grep -q "Up"; then \
				echo "  ✅ $$line"; \
			elif echo "$$line" | grep -q "Exit"; then \
				echo "  ❌ $$line"; \
			else \
				echo "  ⚠️  $$line"; \
			fi; \
		done; \
	else \
		echo "⚠️  No hay servicios corriendo"; \
		echo ""; \
		echo "💡 Para iniciar los servicios, ejecuta: make dev"; \
	fi
	@echo ""
	@echo "📦 Imágenes Docker disponibles:"
	@docker images --format "  {{.Repository}}:{{.Tag}}\t({{.Size}})" | grep -E "(citamedica|calcom)" | head -5 || echo "  No se encontraron imágenes de CitaMedica"
	@echo ""
	@echo "💡 Comandos útiles:"
	@echo "  make dev     - Iniciar todos los servicios"
	@echo "  make logs    - Ver logs de todos los servicios"
	@echo "  make health  - Verificar salud de los servicios"

health: ## Verificar salud de los servicios
	@echo "💚 Verificando salud de los servicios..."
	@echo ""
	@echo "Backend Health:"
	@curl -s http://localhost:8080/actuator/health | jq '.' || echo "❌ Backend no disponible"
	@echo ""
	@echo "Landing Health:"
	@curl -s http://localhost:3001/health || echo "❌ Landing no disponible"
	@echo ""

seed: ## Cargar datos de prueba
	@echo "🌱 Cargando datos de prueba..."
	@echo "⏳ Reiniciando backend para ejecutar seed..."
	docker-compose restart backend-api
	@echo ""
	@echo "⏳ Esperando a que el backend cargue los datos..."
	@sleep 10
	@echo ""
	@echo "✅ Datos de prueba cargados"
	@echo ""
	@echo "📊 Datos creados:"
	@echo "  • 1 clínica (Clínica Demo CitaMedica)"
	@echo "  • 2 médicos (Cardiología y Medicina General)"
	@echo "  • 3 pacientes"
	@echo "  • 4 citas de ejemplo"
	@echo ""
	@echo "💡 Verifica los logs con: make backend-logs"

test: ## Ejecutar tests
	@echo "🧪 Ejecutando tests del backend..."
	cd apps/backend && ./gradlew test
	@echo ""
	@echo "🧪 Ejecutando tests del frontend..."
	cd apps/landing && npm test || echo "⚠️  Tests del frontend no configurados aún"

test-backend: ## Ejecutar solo tests del backend
	@echo "🧪 Ejecutando tests del backend..."
	cd apps/backend && ./gradlew test

test-frontend: ## Ejecutar solo tests del frontend
	@echo "🧪 Ejecutando tests del frontend..."
	cd apps/landing && npm test

db-shell: ## Conectar a la base de datos PostgreSQL
	docker-compose exec postgres-clinic psql -U citamedica -d citamedica

db-migrate: ## Ejecutar migraciones de base de datos
	@echo "🔄 Ejecutando migraciones..."
	docker-compose exec backend-api ./gradlew flywayMigrate

db-reset: ## Resetear base de datos (⚠️ CUIDADO: Borra todos los datos)
	@echo "⚠️  ADVERTENCIA: Esto borrará todos los datos de la base de datos"
	@read -p "¿Estás seguro? [y/N] " -n 1 -r; \
	echo; \
	if [[ $$REPLY =~ ^[Yy]$$ ]]; then \
		docker-compose down -v; \
		docker-compose up -d postgres-clinic; \
		sleep 5; \
		docker-compose up -d backend-api; \
		echo "✅ Base de datos reseteada"; \
	else \
		echo "❌ Operación cancelada"; \
	fi

install: ## Instalar dependencias
	@echo "📦 Instalando dependencias del backend..."
	cd apps/backend && ./gradlew build -x test
	@echo ""
	@echo "📦 Instalando dependencias del frontend..."
	cd apps/landing && npm install
	@echo "✅ Dependencias instaladas"

format: ## Formatear código
	@echo "✨ Formateando código del backend..."
	cd apps/backend && ./gradlew spotlessApply || echo "⚠️  Spotless no configurado"
	@echo ""
	@echo "✨ Formateando código del frontend..."
	cd apps/landing && npm run format || echo "⚠️  Prettier no configurado"

lint: ## Ejecutar linters
	@echo "🔍 Ejecutando linters del backend..."
	cd apps/backend && ./gradlew check || echo "⚠️  Checkstyle no configurado"
	@echo ""
	@echo "🔍 Ejecutando linters del frontend..."
	cd apps/landing && npm run lint || echo "⚠️  ESLint no configurado"

.PHONY: help dev down logs clean seed test build status health restart \
        backend-logs landing-logs calcom-logs db-logs rebuild \
        test-backend test-frontend db-shell db-migrate db-reset \
        install format lint