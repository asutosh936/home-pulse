# Home Maintenance Assistant (HMA)

The Home Maintenance Assistant (HMA) is an agentic application built with Spring Boot and Spring AI. It allows users to log maintenance tasks via natural language. The AI agent interprets the input, identifies the maintenance task and the item involved, and interacts with an H2 database to persist the records.

## Features
- **Natural Language Interaction**: Log task details (e.g. *"replaced HEPA filter in my Dyson vacuum today"*) using a single endpoint.
- **Agentic Tool Calling**: Automatically parses item names, actions, and notes from conversational input and saves them to the database.
- **Stateful Conversation Memory**: Holds context using Spring AI's chat memory advisor.
- **H2 Database Integration**: In-memory database storing all logged tasks.
- **H2 Console Access**: Inspect logged data through a web interface.

---

## Prerequisites
- **Java**: JDK 17 (or newer)
- **Build Tool**: Apache Maven (v3.9+)

---

## Configuration

The application properties are defined in `src/main/resources/application.properties`. 

### Key Properties
- **Server Port**: `8080`
- **Servlet Context Path**: `/home-pulse`
- **Spring AI Anthropic Model**: `claude-sonnet-4-6`

To run the application, you need to configure your **Anthropic API Key** as an environment variable:
```bash
export ANTHROPIC_API_KEY="your-anthropic-api-key-here"
```

---

## Running the Application

To boot up the application, run:
```bash
mvn spring-boot:run
```

---

## REST API Usage

The main conversation loop is exposed at the `/api/chat` endpoint.

### Endpoint Details
* **Method**: `POST`
* **URL**: `http://localhost:8080/home-pulse/api/chat`
* **Content-Type**: `application/json`

### Request Body
```json
{
  "message": "Hey, I just replaced the HEPA filter in my Dyson vacuum today.",
  "conversationId": "my-session-id"
}
```
*Note: `conversationId` is optional. If not provided, it defaults to `"default-session"`.*

### Response Body
```json
{
  "response": "Successfully logged maintenance: MaintenanceLog{id=1, itemName='Dyson vacuum', action='replaced HEPA filter', date=2026-05-24, notes='null'}"
}
```

### Example `curl` command
```bash
curl -X POST http://localhost:8080/home-pulse/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hey, I just replaced the HEPA filter in my Dyson vacuum today."}'
```

---

## Database Access (H2 Console)

You can view the tables and check entries directly via the H2 Web Console:
- **Console URL**: [http://localhost:8080/home-pulse/h2-console](http://localhost:8080/home-pulse/h2-console)
- **JDBC URL**: `jdbc:h2:mem:hmadb`
- **Username**: `sa`
- **Password**: `password`

---

## Running Tests

To run the automated test suite (which verifies context loads, database tool invocation, and web endpoint routing):
```bash
mvn test
```
