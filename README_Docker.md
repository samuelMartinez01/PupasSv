# PupasSv - Dockerización

## 📁 Estructura del Proyecto

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

## 🚀 Pasos para Dockerizar

### ⚠️ Prerequisito IMPORTANTE
**Tu frontend debe estar corriendo en `localhost:3000` antes de ejecutar Docker Compose**

### 1. Preparar el entorno
```bash
# 1. Asegúrate de que tu frontend esté corriendo
# En otro terminal o repositorio:
# npm start (o el comando que uses para tu frontend)
# Verificar: http://localhost:3000

# 2. Construir el WAR del backend
mvn clean package

# 3. Verificar que tienes el script SQL
ls tipicos_tpi135_2025.sql
```

### 2. Ejecutar con Docker Compose
```bash
# Un solo comando para todo
docker-compose up -d --build
```

### 3. Para desarrollo (con logs visibles)
```bash
docker-compose up --build
```

## 📋 Orden de Ejecución

La configuración garantiza este orden:

1. **🗄️ PostgreSQL** se levanta y inicializa con tu script
2. **🔍 Frontend Checker** verifica que tu frontend esté en puerto 3000
3. **⚙️ Backend (Open Liberty)** se levanta solo después de confirmar que todo está listo

Si el frontend no está corriendo, verás un error claro y el backend no se iniciará.

## 📋 Qué hace la configuración

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

## 📊 Comandos Útiles

```bash
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

# Detener y eliminar volúmenes (⚠️ elimina datos)
docker-compose down -v

# Reconstruir solo la aplicación
docker-compose build --no-cache pupas-backend
```

## 🌍 Acceso

- **Aplicación**: http://localhost:9080
- **PostgreSQL**: localhost:5432
- **Logs de Liberty**: Volumen `liberty_logs`
- **Datos de PostgreSQL**: Volumen `postgres_data`

## 🐛 Troubleshooting

### Si el verificador de frontend falla:
```bash
# Ver logs específicos
docker-compose logs frontend-checker

# Verificar manualmente si tu frontend está corriendo
curl http://localhost:3000

# Si tu frontend usa una ruta específica para health check:
# Modifica el docker-compose.yml en la línea del curl
```

### Si la aplicación no conecta a la BD:
1. Verificar que PostgreSQL esté corriendo: `docker-compose logs postgres`
2. Verificar que el script SQL se ejecutó: `docker-compose logs postgres | grep init`
3. Verificar conexión desde el contenedor de Liberty:
   ```bash
   docker exec -it pupas_backend /bin/bash
   # Dentro del contenedor, verificar conectividad
   ```

### Si hay errores de permisos:
```bash
# Verificar permisos del script
chmod +x deploy.sh
chmod 644 tipicos_tpi135_2025.sql
```

### Para reiniciar completamente:
```bash
docker-compose down -v  # ⚠️ Elimina todos los datos
docker-compose up -d --build  # Reconstruir todo

//para pruebas e2e mvn test -Dtags=e2e
```