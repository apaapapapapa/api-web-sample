package com.example.sample.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Jakarta RESTのエンドポイントを有効化するためのエントリポイントです。
 * アプリ全体のベースパスを「/api」にまとめています。
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // no-op
}
