package com.example.karate;

import com.intuit.karate.junit5.Karate;

/**
 * Integration test runner for Karate features. The runner collects all feature
 * files from the classpath under the {@code karate} resource folder while
 * ignoring any scenario that is explicitly tagged with {@code @ignore}.
 */
public class KarateIT {

    @Karate.Test
    Karate runKarateFeatures() {
        return Karate.run("classpath:karate").tags("~@ignore");
    }
}
