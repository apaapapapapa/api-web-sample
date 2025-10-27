package com.example.sample.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activates Jakarta REST endpoints under the /api base path.
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // no-op
}
