# TuneUp Back‑End Service

A Spring Boot–based back‑end for TuneUp. Provides REST APIs for all resource interactions.

---

##  Prerequisites

* **Java 17+**
* **Gradle 7+**
* **Docker & Docker Compose** *(optional)*
* **`tuneup-cloud-key.json`**

---

## ⚙ Configuration & Conventions

* All environment‑specific overrides live in `src/main/resources/application-*.yml`.
* **Service Account Key**: must be named `tuneup-cloud-key.json` in the src/main/resources directory (and is in `.gitignore`).
* **Flyway Migrations**:

    * On startup, Flyway will run all `db/migration/*.sql` scripts.
    * To get a production‑only schema (no sample data), remove `V2__base_data.sql` before launch.

---

##  Google Cloud Credentials

1. Go to GCP Console → IAM & Admin → Service Accounts (For project Marking the admin account logins have been submitted with the src files as has the required json file)
2. Select (or create) the SA your app needs
3. Under **Keys**, **Add Key → Create new key (JSON)**
4. Download and rename to `tuneup-cloud-key.json`
5. Move into the project root:

   ```bash
   mv ~/Downloads/my-key.json ./tuneup-cloud-key.json
   ```
6. **Export** for local runs:

   ```bash
   export GOOGLE_APPLICATION_CREDENTIALS="$(pwd)/tuneup-cloud-key.json"
   ```

---

##  Database Setup

By default the app uses SQL Server. Update the connection in your `application-*.yml` or via env vars:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://<HOST>:<PORT>;databaseName=<DB_NAME>
    username: <USER>
    password: <PASSWORD>
```

Or set env vars:

```bash
export SPRING_DATASOURCE_URL="jdbc:sqlserver://localhost:1433;databaseName=tuneup"
export SPRING_DATASOURCE_USERNAME=sa
export SPRING_DATASOURCE_PASSWORD=YourStrong!Passw0rd
```

*Flyway will automatically migrate schema and seed data on each launch.*

---

## Running Locally (without Docker)

1. Ensure `tuneup-cloud-key.json` is in the project root and `GOOGLE_APPLICATION_CREDENTIALS` is exported.
2. Configure your database connection (see **Database Setup**).
3. Launch via Gradle or simply run the TuneupApplication.Java file:

   ```bash
   ./gradlew bootRun
   ```

---

##  Running Locally (with Docker)

`docker-compose.test.yml` can spin up both the back‑end (test profile) and a SQL Server instance:

```bash
docker-compose -f docker-compose.test.yml up --build
```

---

##  Building & Running Dev Docker Image

```bash
# Build the image
docker build -t tuneup-service:dev .

# Run the container (override DB and creds as needed)
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:sqlserver://host.docker.internal:1433;databaseName=tuneup" \
  -e SPRING_DATASOURCE_USERNAME=sa \
  -e SPRING_DATASOURCE_PASSWORD=YourStrong!Passw0rd \
  -e GOOGLE_APPLICATION_CREDENTIALS="/opt/tuneup-cloud-key.json" \
  -v $(pwd)/tuneup-cloud-key.json:/opt/tuneup-cloud-key.json \
  --name tuneup-dev \
  tuneup-service:dev
```

---

##  Security Considerations

* **Least Privilege**: Grant the SA only the roles it needs (e.g. Pub/Sub Publisher, Storage Viewer).
* **Key Rotation**: Regularly delete old JSON keys in GCP IAM.
* **Protect Local Secrets**: Treat `tuneup-cloud-key.json` like any other credential—never commit or email it.

---

##  Further Reading

* [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
* [Docker & Docker Compose](https://docs.docker.com/compose/)
* [Flyway Migrations](https://flywaydb.org/documentation/)

---

##  Contributing

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/xyz`)
3. Commit your changes & push (`git push origin feature/xyz`)
4. Open a Pull Request
5. Ensure green build and request review from Repo Admin


