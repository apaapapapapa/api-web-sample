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

public class WebPageSimulation extends Simulation {

    // Gatling実行時に -DtargetBaseUrl=... で上書き可能。指定がない場合はローカル環境を想定。
    private final String targetBaseUrl = System.getProperty("targetBaseUrl", "http://localhost:8080");

    // すべてのリクエストで共通利用するHTTP設定（ベースURLやヘッダーなど）。
    private final HttpProtocolBuilder httpProtocol = http
        .baseUrl(targetBaseUrl)
        .inferHtmlResources()
        .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .acceptEncodingHeader("gzip, deflate")
        .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

    // シナリオ定義：トップページにアクセスし、HTTP 200が返ることを確認。
    private final ScenarioBuilder browseScenario = scenario("Browse Home Page")
        .exec(
            http("Open Home")
                .get("/")
                .check(status().is(200))
        );

    public WebPageSimulation() {
        // 30秒かけて徐々にアクセス数を増やし、その後60秒は一定負荷を維持。
        setUp(
            browseScenario.injectOpen(
                rampUsersPerSec(1).to(10).during(Duration.ofSeconds(30)),
                constantUsersPerSec(10).during(Duration.ofSeconds(60))
            )
        )
            .protocols(httpProtocol)
            // SLA違反を早期に気づけるよう、成功率と応答時間の閾値を定義。
            .assertions(
                global().successfulRequests().percent().gt(99.0),
                forAll().responseTime().percentile3().lt(800)
            );
    }
}
