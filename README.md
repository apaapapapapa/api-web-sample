mvn -P wildfly-bootable-jar wildfly-jar:dev-watch

mvn clean install -P wildfly-bootable-jar

## Playwright smoke tests
- Disabled by default. Maven passes `-Dplaywright.enabled=false`, so the tests skip unless you override it.
- Provide a reachable application URL before enabling: `mvn test -Dplaywright.enabled=true -Dplaywright.baseUrl=http://localhost:8080`.
- Keep `-Dplaywright.headless=true` if you are running in CI or another environment without a display.
