# Portfolio Backend API

A modern, secure, and scalable REST API for portfolio websites built with Spring Boot 3.

## Features

- **Authentication & Authorization**: JWT-based authentication with refresh tokens
- **Portfolio Management**: CRUD operations for Projects, Skills, Experiences
- **Search**: Full-text search for projects by title/description
- **Analytics**: View count tracking for projects
- **Contact Form**: Rate-limited contact form (3 req/hour) with email notifications
- **File Upload**: Cloudinary integration for image uploads
- **API Documentation**: Swagger/OpenAPI 3.0
- **Caching**: Spring Cache for improved performance (projects, skills, experiences)
- **Request Logging**: Automatic request/response logging with timing
- **Monitoring**: Spring Boot Actuator health endpoints

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- MapStruct (DTO mapping)
- Cloudinary (image storage)
- Bucket4j (rate limiting)

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Docker (optional)

### Local Development

1. **Clone and setup environment:**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

2. **Run with Docker Compose (recommended):**
   ```bash
   docker-compose up -d
   ```

3. **Or run manually:**
   ```bash
   # Start PostgreSQL first
   mvn clean package
   java -jar target/portfolio-backend-1.0.0.jar
   ```

### API Endpoints

| Endpoint | Description | Auth |
|----------|-------------|------|
| `POST /api/auth/login` | Admin login | Public |
| `GET /api/projects` | List all active projects | Public |
| `GET /api/projects/featured` | Get featured projects | Public |
| `GET /api/projects/search?keyword=` | Search projects | Public |
| `GET /api/projects/{id}` | Get project by ID (tracks view) | Public |
| `GET /api/skills` | List all visible skills | Public |
| `GET /api/experiences` | List all experiences | Public |
| `POST /api/contact` | Submit contact form | Public (rate limited) |
| `GET /api/actuator/health` | Health check | Public |
| `GET /api/swagger-ui.html` | API documentation | Public |

## Default Admin Credentials

On first startup, a default admin account is created:
- **Email**: `admin@portfolio.com`
- **Password**: `Admin@123`

**Important**: Change the password immediately after first login!

## Caching Strategy

Public read endpoints are cached with Caffeine (in-memory) to improve performance:
- `projects` - All projects list
- `featuredProjects` - Featured projects only
- `project` - Individual project by ID
- `skills` - Skills list (with category key)
- `experiences` - Experiences list (with type key)

Cache is automatically cleared on any create/update/delete operation using `@CacheEvict`.

## Environment Variables

See `.env.example` for all available configuration options.

## License

MIT
