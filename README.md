# Innovatech — Sistema de Microservicios (EFT JVY0101)

Sistema empresarial distribuido basado en microservicios para gestión de proyectos, recursos, indicadores y mensajería, desarrollado como Evaluación Final Transversal de la asignatura JVY0101 (Java: Diseño y Construcción de Soluciones Nativas en Nube), Duoc UC.

## Arquitectura

El sistema está compuesto por 4 microservicios independientes construidos en Spring Boot, desplegados con Docker y Docker Swarm, con persistencia en PostgreSQL y MongoDB, integración con AWS SQS y Lambda para procesamiento asíncrono, y un pipeline de CI/CD con GitHub Actions que despliega automáticamente a una instancia EC2.

| Microservicio | Puerto | Función | Base de datos |
|---|---|---|---|
| `ms-project` | 8081 | Gestión de proyectos (CRUD) | PostgreSQL |
| `ms-resources` | 8082 | Gestión de recursos, publica eventos en SQS | PostgreSQL |
| `ms-analytics` | 8084 | Indicadores y métricas | PostgreSQL |
| `ms-collaboration` | 8083 | Mensajería entre usuarios | MongoDB |

### Relación de entidades (JPA)
`ms-project` implementa una relación real `@OneToMany` / `@ManyToOne` entre `Proyecto` y `Tarea` (un proyecto tiene muchas tareas, cada tarea pertenece a un proyecto).

## Stack tecnológico
- **Backend:** Java 21, Spring Boot 3.x, Spring Data JPA, Maven
- **Bases de datos:** PostgreSQL 16, MongoDB 7
- **Contenedores:** Docker, Docker Compose, Docker Swarm
- **CI/CD:** GitHub Actions
- **Cloud:** AWS (EC2, SQS, Lambda, API Gateway) vía AWS Academy Learner Lab
- **Registro de imágenes:** Docker Hub

## Estructura del repositorio
```
innovatech-microservices/
├── ms-project/          # Gestión de proyectos
├── ms-resources/        # Gestión de recursos + productor SQS
├── ms-analytics/        # Indicadores
├── ms-collaboration/    # Mensajería (MongoDB)
├── docker-compose.yml   # Orquestación local/EC2
├── docker-stack.yml     # Despliegue en Docker Swarm
├── init-db.sql          # Inicialización de bases de datos
└── .github/workflows/ci.yml  # Pipeline CI/CD
```

## Cómo correr el proyecto localmente

Requisitos: Docker Desktop, Docker Compose, Java 21, Maven.

```bash
# Compilar cada microservicio
cd ms-project && mvn clean package -DskipTests && cd ..
cd ms-resources && mvn clean package -DskipTests && cd ..
cd ms-analytics && mvn clean package -DskipTests && cd ..
cd ms-collaboration && mvn clean package -DskipTests && cd ..

# Levantar todo con Docker Compose
docker-compose up -d
```

Los endpoints quedan disponibles en `http://localhost:<puerto>`.

### Ejemplo de uso (ms-project)
```
GET    /proyectos          → listar todos los proyectos
GET    /proyectos/{id}     → obtener un proyecto
POST   /proyectos          → crear un proyecto
PUT    /proyectos/{id}     → actualizar un proyecto
DELETE /proyectos/{id}     → eliminar un proyecto
POST   /proyectos/{id}/tareas → agregar una tarea a un proyecto
```

## Docker Swarm
El archivo `docker-stack.yml` define el despliegue en modo Swarm, incluyendo réplicas configurables por servicio (`ms-collaboration` desplegado con 3 réplicas para demostrar escalabilidad).

```bash
docker swarm init
docker stack deploy -c docker-stack.yml innovatech
```

## Pipeline CI/CD
El workflow en `.github/workflows/ci.yml` se ejecuta en cada push y consta de 3 etapas:
1. **build** — compila y empaqueta los 4 microservicios con Maven (matrix build).
2. **push-docker** — (solo en `main`) construye la imagen Docker de `ms-project` y la sube a Docker Hub.
3. **deploy** — (solo en `main`) se conecta por SSH a la instancia EC2 y despliega la imagen actualizada automáticamente.

## Infraestructura en la nube (AWS)
- **EC2:** instancia `t3.micro` corriendo Docker, con `ms-project` y PostgreSQL desplegados y accesibles públicamente.
- **SQS:** cola `innovatech-sqs`, recibe eventos publicados por `ms-resources` al crear un recurso.
- **Lambda:** función `innovatech-lambda`, consume mensajes de la cola SQS y también puede invocarse directamente vía API Gateway.
- **API Gateway:** expone un endpoint HTTP (`POST /notificaciones`) que invoca la Lambda directamente, sin pasar por la cola.

> Nota: por tratarse de un entorno AWS Academy Learner Lab, las credenciales y la IP pública del EC2 son temporales y cambian en cada sesión del laboratorio.

## Uso de Inteligencia Artificial
Se utilizó Claude (Anthropic) como herramienta de apoyo técnico durante el desarrollo: depuración de errores de despliegue, configuración de Docker/Docker Swarm/CI-CD, redacción de este README y orientación general en la integración de servicios AWS (SQS, Lambda, API Gateway). Todo el código, las decisiones de diseño y las pruebas fueron implementadas, revisadas y validadas por el estudiante. Las conclusiones, justificaciones técnicas y reflexiones individuales del informe fueron elaboradas sin apoyo de IA, conforme a lo indicado en la pauta de evaluación.

## Autor
Proyecto desarrollado para la asignatura JVY0101, Duoc UC.

Prueba CI/CD 07/11/2026 04:56:40

Pipeline deploy fix
