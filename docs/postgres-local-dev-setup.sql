-- PostgreSQL local para CitaMedica (DEV). Ejecutar como superusuario, p. ej.:
--   psql -U postgres -f docs/postgres-local-dev-setup.sql

CREATE USER citamedica WITH PASSWORD 'citamedica123';
CREATE DATABASE citamedica OWNER citamedica;

\c citamedica

-- PostgreSQL 15+: permisos por defecto en public suelen requerir explícito para Flyway/DDL
GRANT ALL ON SCHEMA public TO citamedica;
