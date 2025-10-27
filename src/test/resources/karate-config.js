function fn() {
  var env = karate.env || 'local';
  var config = { env: env };
  config.baseUrl = karate.properties['baseUrl'] || 'http://localhost:8080';
  karate.configure('ssl', true);
  return config;
}
