package com.example.karate.support;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import com.example.sample.api.DetailResource;
import com.example.sample.api.mapper.BusinessExceptionMapper;
import com.example.sample.api.mapper.InvalidRequestExceptionMapper;
import com.example.sample.api.mapper.UnhandledExceptionMapper;
import com.example.sample.dto.DetailRowView;
import com.example.sample.model.Status;
import com.example.sample.repository.DetailRepository;
import com.example.sample.service.DetailService;
import com.example.sample.service.DetailServiceImpl;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jsonb.JsonBindingFeature;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.ws.rs.core.UriBuilder;

/**
 * Bootstraps an embedded Jersey server that exposes the same REST resources as
 * the production application. The server is used exclusively by the Karate
 * regression tests.
 */
public final class KarateTestServer {

    private final EntityManagerFactory entityManagerFactory;

    private HttpServer server;

    private URI baseUri;

    public KarateTestServer() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("jakartaee-sample-test");
        TestDataManager.configure(entityManagerFactory);
    }

    public URI start() {
        if (server != null) {
            return Objects.requireNonNull(baseUri, "Server already running without base URI");
        }

        TestDataManager.reset();

        final ResourceConfig config = new ResourceConfig()
                .register(JsonBindingFeature.class)
                .register(DetailResource.class)
                .register(BusinessExceptionMapper.class)
                .register(InvalidRequestExceptionMapper.class)
                .register(UnhandledExceptionMapper.class)
                .register(new DetailServiceBinder(entityManagerFactory));

        final int port = findAvailablePort();
        baseUri = UriBuilder.fromUri("http://127.0.0.1/")
                .port(port)
                .path("api")
                .build();

        server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
        return baseUri;
    }

    public URI getBaseUri() {
        return Objects.requireNonNull(baseUri, "Server has not been started yet");
    }

    public void stop() {
        if (server != null) {
            server.shutdownNow();
            server = null;
        }
        if (entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }

    private static int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to allocate HTTP port", ex);
        }
    }

    private static final class DetailServiceBinder extends AbstractBinder {

        private final EntityManagerFactory emf;

        private DetailServiceBinder(final EntityManagerFactory emf) {
            this.emf = emf;
        }

        @Override
        protected void configure() {
            bindFactory(new DetailServiceFactory(emf))
                    .to(DetailService.class)
                    .in(RequestScoped.class);
        }
    }

    private static final class DetailServiceFactory implements Factory<DetailService> {

        private final EntityManagerFactory emf;

        private DetailServiceFactory(final EntityManagerFactory emf) {
            this.emf = emf;
        }

        @Override
        public DetailService provide() {
            final EntityManager entityManager = emf.createEntityManager();
            return new ManagedDetailService(entityManager);
        }

        @Override
        public void dispose(final DetailService instance) {
            if (instance instanceof ManagedDetailService managed) {
                managed.close();
            }
        }
    }

    private static final class ManagedDetailService extends DetailServiceImpl implements AutoCloseable {

        private final EntityManager entityManager;

        private ManagedDetailService(final EntityManager entityManager) {
            super(new DetailRepository(entityManager));
            this.entityManager = entityManager;
        }

        @Override
        public List<DetailRowView> getListForLoginUser(
                final String userId, final Status status) {
            return execute(() -> super.getListForLoginUser(userId, status));
        }

        @Override
        public void apply(final List<Long> selectedDetailIds, final String userId) {
            execute(() -> {
                super.apply(selectedDetailIds, userId);
                return null;
            });
        }

        private <T> T execute(final Supplier<T> action) {
            final EntityTransaction transaction = entityManager.getTransaction();
            boolean started = false;
            if (!transaction.isActive()) {
                transaction.begin();
                started = true;
            }
            try {
                final T result = action.get();
                if (started) {
                    transaction.commit();
                }
                return result;
            } catch (RuntimeException ex) {
                if (started && transaction.isActive()) {
                    transaction.rollback();
                }
                throw ex;
            }
        }

        @Override
        public void close() {
            final EntityTransaction transaction = entityManager.getTransaction();
            if (transaction.isActive()) {
                transaction.rollback();
            }
            entityManager.close();
        }
    }
}
