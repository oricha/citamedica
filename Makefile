.PHONY: help dev down logs clean seed test

help:
	@echo "CitaMedica Development Commands"
	@echo "  make dev     - Start all services"
	@echo "  make down    - Stop all services"
	@echo "  make logs    - View logs"
	@echo "  make clean   - Clean volumes"
	@echo "  make seed    - Load seed data"
	@echo "  make test    - Run tests"

dev:
	docker-compose up -d
	@echo "Services starting..."
	@echo "Landing: http://localhost:3001"
	@echo "Backend: http://localhost:8080"
	@echo "Cal.com: http://localhost:3000"

down:
	docker-compose down

logs:
	docker-compose logs -f

clean:
	docker-compose down -v
	@echo "Volumes cleaned"

seed:
	@echo "Loading seed data..."
	docker-compose exec backend-api java -jar seed.jar
	@echo "Seed data loaded"

test:
	cd apps/backend && ./gradlew test
	cd apps/landing && npm test