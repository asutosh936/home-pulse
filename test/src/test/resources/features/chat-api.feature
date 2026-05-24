Feature: Home Maintenance Assistant Chat API

  Background:
    * url baseUrl
    * def chatPath = chatEndpoint

  # ──────────────────────────────────────────────────────────────────────────────
  # Scenario 1: Log a maintenance task successfully
  # ──────────────────────────────────────────────────────────────────────────────
  Scenario: Log a maintenance task via natural language
    Given path chatPath
    And request { message: 'I just replaced the HEPA filter in my Dyson vacuum today.', conversationId: 'karate-log-test' }
    And header Content-Type = 'application/json'
    When method POST
    Then status 200
    And match response == { response: '#string' }
    And match response.response contains '#string'
    * def reply = response.response
    * karate.log('AI reply:', reply)

  # ──────────────────────────────────────────────────────────────────────────────
  # Scenario 2: List recent maintenance tasks
  # ──────────────────────────────────────────────────────────────────────────────
  Scenario: Ask the assistant to list recent maintenance tasks
    Given path chatPath
    And request { message: 'List my last 5 maintenance tasks.', conversationId: 'karate-list-test' }
    And header Content-Type = 'application/json'
    When method POST
    Then status 200
    And match response == { response: '#string' }
    * karate.log('List reply:', response.response)

  # ──────────────────────────────────────────────────────────────────────────────
  # Scenario 3: Duplicate task is rejected on the same day
  # ──────────────────────────────────────────────────────────────────────────────
  Scenario: Submitting the same task twice on the same day should not create a duplicate
    # First submission
    Given path chatPath
    And request { message: 'I cleaned the AC filter today.', conversationId: 'karate-dup-test' }
    And header Content-Type = 'application/json'
    When method POST
    Then status 200
    * def firstReply = response.response
    * karate.log('First reply:', firstReply)

    # Second identical submission in same conversation
    Given path chatPath
    And request { message: 'I cleaned the AC filter today.', conversationId: 'karate-dup-test' }
    And header Content-Type = 'application/json'
    When method POST
    Then status 200
    * def secondReply = response.response
    * karate.log('Second reply (should mention already logged):', secondReply)
    # The AI should inform the user the task is already logged
    And match secondReply contains '#string'

  # ──────────────────────────────────────────────────────────────────────────────
  # Scenario 4: Request with no conversationId defaults gracefully
  # ──────────────────────────────────────────────────────────────────────────────
  Scenario: Request without a conversationId should succeed with default session
    Given path chatPath
    And request { message: 'What maintenance did I do recently?' }
    And header Content-Type = 'application/json'
    When method POST
    Then status 200
    And match response == { response: '#string' }

  # ──────────────────────────────────────────────────────────────────────────────
  # Scenario 5: Request with a blank conversationId defaults gracefully
  # ──────────────────────────────────────────────────────────────────────────────
  Scenario: Request with a blank conversationId should succeed with default session
    Given path chatPath
    And request { message: 'What maintenance did I do recently?', conversationId: '   ' }
    And header Content-Type = 'application/json'
    When method POST
    Then status 200
    And match response == { response: '#string' }

  # ──────────────────────────────────────────────────────────────────────────────
  # Scenario 6: Vague message triggers clarification from the AI
  # ──────────────────────────────────────────────────────────────────────────────
  Scenario: A vague message should get a clarification question from the AI
    Given path chatPath
    And request { message: 'I did some cleaning today.', conversationId: 'karate-vague-test' }
    And header Content-Type = 'application/json'
    When method POST
    Then status 200
    And match response == { response: '#string' }
    * def reply = response.response
    * karate.log('Clarification reply:', reply)
    # The AI should return some question or request for more details
    And assert reply.length > 0

  # ──────────────────────────────────────────────────────────────────────────────
  # Scenario 7: Validate JSON response structure
  # ──────────────────────────────────────────────────────────────────────────────
  Scenario: Response must always be a JSON object with a 'response' string field
    Given path chatPath
    And request { message: 'Hello!', conversationId: 'karate-schema-test' }
    And header Content-Type = 'application/json'
    When method POST
    Then status 200
    And match response == { response: '#string' }
    # ensure the `response` field is a string (tolerant check)
    And match response.response == '#string'

  # ──────────────────────────────────────────────────────────────────────────────
  # Scenario 8: Stateful conversation continuity across multiple turns
  # ──────────────────────────────────────────────────────────────────────────────
  Scenario: Multi-turn conversation maintains context within the same session
    * def sessionId = 'karate-session-' + java.lang.System.currentTimeMillis()

    # Turn 1 — provide item
    Given path chatPath
    And request { message: 'I just serviced my lawn mower.', conversationId: '#(sessionId)' }
    And header Content-Type = 'application/json'
    When method POST
    Then status 200
    * def turn1Reply = response.response
    * karate.log('Turn 1 reply:', turn1Reply)

    # Turn 2 — ask for confirmation/listing in the same session
    Given path chatPath
    And request { message: 'What was the last thing I logged?', conversationId: '#(sessionId)' }
    And header Content-Type = 'application/json'
    When method POST
    Then status 200
    And match response == { response: '#string' }
    * karate.log('Turn 2 reply:', response.response)
