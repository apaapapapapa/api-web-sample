Feature: Template for API integration testing with Karate

  # Remove the @ignore tag when the endpoint and assertions are ready.
  @ignore
  Scenario: Verify health endpoint responds successfully
    Given url baseUrl + '/health'
    When method get
    Then status 200
