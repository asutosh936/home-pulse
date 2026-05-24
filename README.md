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
  "message": "I replaced the HVAC filter 95 days ago.",
  "conversationId": "reminder-session"
}
```
*Note: `conversationId` is optional. If not provided, it defaults to `"default-session"`.*

### Response Body
```json
{
  "response": "Successfully logged maintenance: MaintenanceLog{id=1, itemName='HVAC', action='replaced filter', date=2026-05-24, notes='...' }"
}
```

### Example `curl` command
```bash
curl -X POST http://localhost:8080/home-pulse/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"I replaced the HVAC filter 95 days ago.","conversationId":"reminder-session"}'
```

---

## Database Access (H2 Console)

You can view the tables and check entries directly via the H2 Web Console:
- **Console URL**: [http://localhost:8080/home-pulse/h2-console](http://localhost:8080/home-pulse/h2-console)
- **JDBC URL**: `jdbc:h2:mem:hmadb`
- **Username**: `sa`
- **Password**: `password`

---

## Proactive Notification System

HearthKeeper now includes a proactive reminder flow for upcoming home maintenance tasks.

### How it works
- `MaintenanceManager` runs on a configurable cron schedule via `hma.notification.cron`.
- By default, it triggers every day at 9:00 AM using `0 0 9 * * ?`.
- `MaintenanceContextService` summarizes recent maintenance logs and user history.
- The AI agent evaluates the context and calls `NotificationService.sendNotification(String message)` when a reminder is needed.
- `NotificationService` logs the notification and stores it in the H2 database for later review.

### Configuration
Add or update the user history preference in `src/main/resources/application.properties`:
```properties
hma.user-history=The user prefers morning reminders and is currently busy with home projects.
```
Add or override the scheduler cron expression to control how often the proactive review runs:
```properties
hma.notification.cron=0 0 9 * * ?
```
This helps the agent choose the right tone and timing for proactive reminders.

### Validation
1. Start the application:
```bash
mvn spring-boot:run
```
2. Use the `/api/chat` endpoint to log maintenance tasks, especially recurring items like filters or batteries.
3. Confirm older maintenance entries exist in the H2 console or logs.
4. Check application logs for the daily review and any lines prefixed with `Proactive notification:`.

For faster local validation, override the scheduler to run every minute by setting:
```properties
hma.notification.cron=0 * * * * ?
```
Then restart the application and watch the logs for repeated review cycles.

### Notification endpoints
- `GET http://localhost:8080/home-pulse/api/notifications` — returns all sent proactive reminders.
- `POST http://localhost:8080/home-pulse/api/admin/maintenance-review` — triggers the maintenance review immediately.

### Single validation via curl
1. Log a recurring maintenance task:
```bash
curl -X POST http://localhost:8080/home-pulse/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"I replaced the HVAC filter 95 days ago.","conversationId":"reminder-session"}'
```
2. Trigger the review immediately:
```bash
curl -X POST http://localhost:8080/home-pulse/api/admin/maintenance-review
```
3. Verify the notification list:
```bash
curl http://localhost:8080/home-pulse/api/notifications
```

### Sample reminder scenario
1. Log a recurring maintenance task:
```bash
curl -X POST http://localhost:8080/home-pulse/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"I replaced the HVAC filter 95 days ago.","conversationId":"reminder-session"}'
```
2. Wait for the scheduler to run at 9:00 AM (or adjust the cron expression for local testing).
3. Look for a console log similar to:
```text
Proactive notification: I know you've been busy, but your HVAC filter is due for a replacement soon.
```
4. If the system decides no reminder is needed, you should still see the daily review log entry.

### Validation via Maven test
Run the focused notification persistence test:
```bash
mvn -Dtest=NotificationPersistenceIntegrationTest test
```

For the full notification flow, run:
```bash
mvn -Dtest=NotificationServiceTest,NotificationPersistenceIntegrationTest,MaintenanceManagerTest test
```

---

## Running Tests

To run the automated test suite (which verifies context loads, database tool invocation, and web endpoint routing):
```bash
mvn test
```
