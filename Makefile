.PHONY: help dev dev-docker backend-local down logs clean seed test build status health restart \
        backend-logs landing-logs calcom-logs db-logs db-logs-clinic rebuild rebuild-docker \
        test-backend test-frontend db-shell db-shell-local db-migrate db-reset \
        install format lint

COMPOSE ?= docker-compose
# Incluye servicios del perfil docker-db al hacer down/clean para detener backend y postgres-clinic si estaban activos
COMPOSE_ALL := $(COMPOSE) --profile docker-db

# Default target
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "CitaMedica - Comandos disponibles:"
	@echo ""
	@echo "  make dev          - Cal.com + landing + Postgres Cal (DEV: Postgres clínica en localhost)"
	@echo "  make dev-docker   - Stack completo en Docker (postgres-clinic + backend-api + resto)"
	@echo "  make backend-local - Backend Spring con Gradle (.env → Postgres local :5432)"
	@echo "  make down         - Detener todos los servicios"
	@echo "  make logs         - Ver logs en tiempo real de todos los servicios"
	@echo "  make clean        - Limpiar volúmenes y detener servicios"
	@echo "  make seed         - Cargar datos de prueba (reinicia backend Docker o indicaciones para local)"
	@echo "  make test         - Ejecutar tests"
	@echo "  make build        - Construir imágenes Docker"
	@echo "  make status       - Ver estado de los servicios"
	@echo "  make health       - Verificar salud de los servicios"
	@echo "  make restart      - Reiniciar contenedores en ejecución"
	@echo ""
	@echo "Base de datos:"
	@echo "  make db-shell        - psql a postgres-clinic (requiere make dev-docker)"
	@echo "  make db-shell-local  - psql a Postgres local (usa .env)"
	@echo "  make db-migrate      - Flyway desde Gradle (usa .env / valores por defecto)"
	@echo "  make db-logs         - Logs de postgres-cal (Cal.com)"
	@echo "  make db-logs-clinic  - Logs de postgres-clinic (requiere dev-docker)"
	@echo ""
	@echo "Logs específicos:"
	@echo "  make backend-logs - Ver logs del backend (requiere make dev-docker)"
	@echo "  make landing-logs - Ver logs del landing"
	@echo "  make calcom-logs  - Ver logs de Cal.com"
	@echo ""

dev: ## Cal.com + landing + BD Cal (Postgres clínica en el host para DEV)
	@echo "🚀 Iniciando servicios (sin postgres-clinic ni backend en Docker)..."
	$(COMPOSE) up -d
	@echo ""
	@echo "⏳ Esperando a que los servicios estén listos..."
	@sleep 15
	@echo ""
	@echo "✅ Servicios Docker disponibles:"
	@echo "  📄 Landing:  http://localhost:3001"
	@echo "  📅 Cal.com:  http://localhost:3000"
	@echo ""
	@echo "🔧 Backend NO está en Docker. Con Postgres local en :5432:"
	@echo "     make backend-local"
	@echo ""
	@echo "     (Stack todo en Docker: make dev-docker)"
	@echo ""
	@echo "💡 Usa 'make logs' para ver los logs en tiempo real"

dev-docker: ## Stack completo incluyendo postgres-clinic y backend-api en Docker
	@echo "🚀 Iniciando todos los servicios (perfil docker-db)..."
	$(COMPOSE) --profile docker-db up -d
	@echo ""
	@echo "⏳ Esperando a que los servicios estén listos..."
	@sleep 15
	@echo ""
	@echo "✅ Servicios disponibles:"
	@echo "  📄 Landing:  http://localhost:3001"
	@echo "  🔧 Backend:  http://localhost:8080"
	@echo "  📅 Cal.com:  http://localhost:3000"
	@echo "  💚 Health:   http://localhost:8080/actuator/health"
	@echo ""

backend-local: ## Ejecutar backend con Gradle (carga .env si existe)
	@echo "🔧 Arrancando backend con Postgres local (según .env)..."
	@set -a; [ -f .env ] && . ./.env; set +a; \
	cd apps/backend && ./gradlew bootRun

down: ## Detener todos los servicios
	@echo "🛑 Deteniendo servicios..."
	$(COMPOSE_ALL) down
	@echo "✅ Servicios detenidos"

logs: ## Ver logs en tiempo real
	$(COMPOSE_ALL) logs -f

backend-logs: ## Ver logs del backend en Docker
	$(COMPOSE) --profile docker-db logs -f backend-api

landing-logs: ## Ver logs del landing
	$(COMPOSE) logs -f landing

calcom-logs: ## Ver logs de Cal.com
	$(COMPOSE) logs -f calcom

db-logs: ## Ver logs de postgres-cal (BD de Cal.com)
	$(COMPOSE) logs -f postgres-cal

db-logs-clinic: ## Ver logs de postgres-clinic (requiere perfil docker-db)
	$(COMPOSE) --profile docker-db logs -f postgres-clinic

clean: ## Limpiar volúmenes y detener servicios
	@echo "🧹 Limpiando volúmenes y deteniendo servicios..."
	$(COMPOSE_ALL) down -v
	@echo "✅ Limpieza completada"

build: ## Construir imágenes Docker
	@echo "🔨 Construyendo imágenes Docker..."
	@HTTP_PROXY= HTTPS_PROXY= http_proxy= https_proxy= NO_PROXY=* no_proxy=* DOCKER_BUILDKIT=1 $(COMPOSE) build --no-cache || (echo "❌ Error al construir. Intentando sin --no-cache..." && HTTP_PROXY= HTTPS_PROXY= http_proxy= https_proxy= NO_PROXY=* no_proxy=* $(COMPOSE) build)
	@echo "✅ Imágenes construidas"

rebuild: ## Reconstruir servicios por defecto (sin backend Docker)
	@echo "🔄 Reconstruyendo servicios..."
	$(COMPOSE) down
	$(COMPOSE) build --no-cache
	$(COMPOSE) up -d
	@echo "✅ Servicios reconstruidos y reiniciados"

rebuild-docker: ## Reconstruir stack completo con perfil docker-db
	@echo "🔄 Reconstruyendo stack Docker completo..."
	$(COMPOSE) --profile docker-db down
	$(COMPOSE) build --no-cache
	$(COMPOSE) --profile docker-db up -d
	@echo "✅ Stack con docker-db reconstruido"

restart: ## Reiniciar contenedores en ejecución
	@echo "🔄 Reiniciando servicios..."
	$(COMPOSE_ALL) restart
	@echo "✅ Servicios reiniciados"

status: ## Ver estado de los servicios
	@echo "📊 Estado de los servicios:"
	@echo ""
	@SERVICES=$$($(COMPOSE_ALL) ps 2>/dev/null | tail -n +2 | grep -v "^$$" | wc -l | tr -d ' '); \
	if [ "$$SERVICES" -gt "0" ]; then \
		$(COMPOSE_ALL) ps; \
		echo ""; \
		echo "📈 Resumen:"; \
		$(COMPOSE_ALL) ps --format "table {{.Service}}\t{{.Status}}\t{{.Ports}}" | tail -n +2 | while read line; do \
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
		echo "💡 Para iniciar: make dev (o make dev-docker)"; \
	fi
	@echo ""
	@echo "📦 Imágenes Docker disponibles:"
	@docker images --format "  {{.Repository}}:{{.Tag}}\t({{.Size}})" | grep -E "(citamedica|calcom)" | head -5 || echo "  No se encontraron imágenes de CitaMedica"
	@echo ""
	@echo "💡 Comandos útiles:"
	@echo "  make dev     - Solo Cal.com + landing (+ Postgres local para la API)"
	@echo "  make dev-docker - Todo en Docker"
	@echo "  make logs    - Ver logs"

health: ## Verificar salud de los servicios
	@echo "💚 Verificando salud de los servicios..."
	@echo ""
	@echo "Backend (local o Docker):"
	@curl -s http://localhost:8080/actuator/health | jq '.' || echo "❌ Backend no disponible en :8080"
	@echo ""
	@echo "Landing Health:"
	@curl -s http://localhost:3001/health || echo "❌ Landing no disponible"
	@echo ""

seed: ## Cargar datos de prueba (SeedDataService al arrancar)
	@echo "🌱 Datos de prueba (SeedDataService)..."
	@if $(COMPOSE) --profile docker-db ps -q backend-api 2>/dev/null | grep -q .; then \
		echo "Reiniciando backend en Docker..."; \
		$(COMPOSE) --profile docker-db restart backend-api; \
		echo "⏳ Esperando al backend..."; \
		sleep 10; \
		echo "✅ Reinicio enviado"; \
	else \
		echo "No hay contenedor backend-api."; \
		echo "Si usas make backend-local, detén el proceso (Ctrl+C) y vuelve a ejecutar make backend-local."; \
		echo "(El seed solo corre al arrancar si la BD está vacía; si ya hay datos, no se vuelve a cargar.)"; \
	fi
	@echo ""
	@echo "💡 Con backend Docker: make backend-logs"

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

db-shell: ## psql a postgres-clinic en Docker
	$(COMPOSE) --profile docker-db exec postgres-clinic psql -U citamedica -d citamedica

db-shell-local: ## psql (host/puerto/BD derivados de SPRING_DATASOURCE_URL en .env)
	@set -a; [ -f .env ] && . ./.env; set +a; \
	u="$${SPRING_DATASOURCE_USERNAME:-citamedica}"; \
	p="$${SPRING_DATASOURCE_PASSWORD:-citamedica123}"; \
	url="$${SPRING_DATASOURCE_URL:-jdbc:postgresql://localhost:5432/citamedica}"; \
	rest=$${url#jdbc:postgresql://}; \
	hostport=$${rest%%/*}; \
	db=$${rest#*/}; db=$${db%%\?*}; \
	host=$${hostport%%:*}; \
	port=$${hostport##*:}; \
	if [ "$$host" = "$$port" ] || [ -z "$$port" ]; then port=5432; fi; \
	PGPASSWORD="$$p" psql -h "$$host" -p "$$port" -U "$$u" -d "$$db"

db-migrate: ## Ejecutar migraciones Flyway con Gradle (lee SPRING_DATASOURCE_* del entorno / .env)
	@echo "🔄 Ejecutando migraciones Flyway..."
	@set -a; [ -f .env ] && . ./.env; set +a; \
	cd apps/backend && ./gradlew flywayMigrate

db-reset: ## Borrar datos de postgres-clinic + recrear (no afecta volúmenes de Cal.com)
	@echo "⚠️  Se eliminará el volumen de postgres-clinic y se recrearán clínica + backend Docker."
	@read -p "¿Continuar? [y/N] " -n 1 -r; \
	echo; \
	if [[ $$REPLY =~ ^[Yy]$$ ]]; then \
		$(COMPOSE) --profile docker-db stop postgres-clinic backend-api 2>/dev/null; \
		$(COMPOSE) --profile docker-db rm -f postgres-clinic backend-api 2>/dev/null; \
		for v in $$(docker volume ls -q | grep -E '(^|_)postgres-clinic-data$$' || true); do \
			echo "Eliminando volumen $$v..."; docker volume rm $$v 2>/dev/null || true; \
		done; \
		$(COMPOSE) --profile docker-db up -d postgres-clinic; \
		sleep 5; \
		$(COMPOSE) --profile docker-db up -d backend-api; \
		echo "✅ postgres-clinic y backend-api recreados."; \
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
