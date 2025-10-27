package com.example.karate;

import java.net.URI;

import com.example.karate.support.KarateTestServer;
import com.intuit.karate.junit5.Karate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * Integration test runner for Karate features. The runner collects all feature
 * files from the classpath under the {@code karate} resource folder while
 * ignoring any scenario that is explicitly tagged with {@code @ignore}.
 */
public class KarateIT {

    private static KarateTestServer server;

    @BeforeAll
    static void startServer() {
        server = new KarateTestServer();
        final URI baseUri = server.start();
        System.setProperty("baseUrl", baseUri.toString());
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Karate.Test
    Karate runKarateFeatures() {
        return Karate.run("classpath:karate").tags("~@ignore");
    }
}
