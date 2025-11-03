package com.example.sample.performance;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.forAll;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * REST API 向けのシンプルな Gatling シミュレーションです。
 * GET とエラーケースのバリデーションを高頻度で繰り返し、
 * API の応答時間と成功率を素早く確認することを目的としています。
 */
public class ApiLoadSimulation extends Simulation {

    private final String targetBaseUrl = System.getProperty("targetBaseUrl", "http://localhost:8080");
    private final HttpProtocolBuilder httpProtocol = http
        .baseUrl(resolveApiBase(targetBaseUrl))
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .acceptEncodingHeader("gzip, deflate")
        .userAgentHeader("Gatling/Java API load test");

    private final ScenarioBuilder listDetailsScenario = scenario("API - List details")
        .exec(
            http("GET /api/details (draft)")
                .get("/details")
                .queryParam("userId", "user1")
                .queryParam("status", "draft")
                .check(status().is(200))
        )
        .pause(Duration.ofMillis(500));

    private final ScenarioBuilder invalidStatusScenario = scenario("API - Reject unknown status")
        .exec(
            http("GET /api/details (unknown status)")
                .get("/details")
                .queryParam("userId", "user1")
                .queryParam("status", "unknown")
                .check(status().is(400))
        );

    public ApiLoadSimulation() {
        setUp(
            listDetailsScenario.injectOpen(
                rampUsersPerSec(1).to(15).during(Duration.ofSeconds(45)),
                constantUsersPerSec(15).during(Duration.ofSeconds(60))
            ),
            invalidStatusScenario.injectOpen(
                constantUsersPerSec(5).during(Duration.ofSeconds(60))
            )
        )
            .protocols(httpProtocol)
            .assertions(
                global().successfulRequests().percent().gt(99.0),
                forAll().responseTime().percentile3().lt(800)
            );
    }

    private static String resolveApiBase(final String rawBaseUrl) {
        if (rawBaseUrl == null || rawBaseUrl.isBlank()) {
            return "http://localhost:8080/api";
        }
        final String trimmed = rawBaseUrl.endsWith("/") ? rawBaseUrl.substring(0, rawBaseUrl.length() - 1) : rawBaseUrl;
        return trimmed.endsWith("/api") ? trimmed : trimmed + "/api";
    }
}

