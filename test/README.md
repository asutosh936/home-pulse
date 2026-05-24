HMA Functional Tests (Karate)
=================================

Prerequisites
- Java 17+
- Maven 3.6+
- The Home Maintenance Assistant API running (default at `http://localhost:8080/home-pulse`)

Run all functional tests (from repository root)

```bash
mvn -f test/pom.xml test
```

Override the base URL (useful for running against deployed instances)

```bash
mvn -Dkarate.baseUrl=http://my-host:8080/home-pulse -f test/pom.xml test
```

Helper script
- `test/run-karate-with-wait.sh`: waits for the API to be reachable and then runs the Karate suite. Example:

```bash
# wait up to 120s for the API then run tests
./test/run-karate-with-wait.sh http://localhost:8080/home-pulse 120
```

Run tests from your IDE
- Run the JUnit test class `com.homepulse.hma.karate.RunKarateTests` in `test/src/test/java`.

Where tests live
- Features: `test/src/test/resources/features` (e.g. `chat-api.feature`)
- Config: `test/src/test/resources/karate-config.js`

Notes
- The feature file `chat-api.feature` contains scenarios that log tasks, check duplicates, validate schema, and exercise multi-turn sessions. Ensure the API under test is reachable at the configured base URL before running tests.
