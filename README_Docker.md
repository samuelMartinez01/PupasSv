# PupasSv - Dockerización

## estructura
```
proyecto-raiz/
├── src/
├── target/
│   └── PupasSv-1.0-SNAPSHOT.war    # Generado por Maven
├── Dockerfile
├── docker-compose.yml
├── server.xml
├── .dockerignore
├── tipicos_tpi135_2025.sql        # Tu script de BD (se monta directamente)
└── pom.xml
```

### IMPORTANTE
**Tu frontend debe estar corriendo en `localhost:3000` antes de ejecutar Docker Compose**

### 1. Preparar el entorno

# 2. Construir el WAR del backend
mvn clean package

# 3. Verificar que tienes el script SQL
ls tipicos_tpi135_2025.sql

### 2. Ejecutar con Docker Compose
docker-compose up -d --build
```

### 3. Para desarrollo (con logs visibles)
```bash
docker-compose up --build

La configuración garantiza este orden:

1. **🗄️ PostgreSQL** se levanta y inicializa con tu script
2. **🔍 Frontend Checker** verifica que tu frontend esté en puerto 3000
3. **⚙️ Backend (Open Liberty)** se levanta solo después de confirmar que todo está listo

Si el frontend no está corriendo, verás un error claro y el backend no se iniciará.

## pasos de lo que hace este doker

### PostgreSQL
- **Crea** la base de datos `tipicos_tpi135`
- **Ejecuta** automáticamente `tipicos_tpi135_2025.sql` al primer inicio
- **Expone** el puerto 5432
- **Persiste** los datos en un volumen Docker

### Frontend Checker
- **Verifica** que tu frontend esté corriendo en `localhost:3000`
- **Hace hasta 30 intentos** (2.5 minutos) esperando el frontend
- **Bloquea** el inicio del backend si el frontend no está disponible

### Open Liberty
- **Despliega** el WAR en `/dropins`
- **Configura** conexión a PostgreSQL usando variables de entorno
- **Expone** los puertos 9080 y 9443
- **Espera** a que PostgreSQL y Frontend estén listos antes de iniciar

## 🔧 Variables de Entorno

El servidor puede configurarse con estas variables:

```yaml
DB_HOST: postgres          # Nombre del servicio PostgreSQL
DB_NAME: tipicos_tpi135   # Nombre de la base de datos
DB_PORT: 5432             # Puerto de PostgreSQL
DB_USER: postgres         # Usuario de la base de datos
DB_PASSWORD: abc123       # Contraseña de la base de datos
```

# Construir y ejecutar todo
docker-compose up -d --build

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs del backend
docker-compose logs -f pupas-backend

# Ver logs de PostgreSQL
docker-compose logs -f postgres

# Ver logs del verificador de frontend
docker-compose logs frontend-checker

# Estado de los servicios
docker-compose ps

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Reconstruir solo la aplicación
docker-compose build --no-cache pupas-backend.