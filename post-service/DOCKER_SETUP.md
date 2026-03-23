# Run PostgreSQL with Docker for `post-service`

## 1) Start PostgreSQL container
From `post-service` folder:

```bash
docker compose up -d
```

This creates a PostgreSQL database with:
- Database: `postsDB`
- Username: `postgres`
- Password: `postgres`
- Port: `5432`

## 2) Run your Spring Boot app
In a new terminal:

```bash
./mvnw spring-boot:run
```

The app will connect using values in `application.yaml`.

## 3) Optional: override credentials without editing YAML

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postsDB
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
./mvnw spring-boot:run
```

## 4) Stop DB container

```bash
docker compose down
```

To also remove saved DB data volume:

```bash
docker compose down -v
```
