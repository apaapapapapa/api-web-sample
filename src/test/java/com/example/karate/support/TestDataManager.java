package com.example.karate.support;

import java.util.Objects;

import com.example.sample.model.Detail;
import com.example.sample.model.Status;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

/**
 * Utility that resets the in-memory database to a deterministic state before
 * each Karate scenario. The helper is wired from {@link KarateTestServer} and
 * invoked directly from the feature files.
 */
public final class TestDataManager {

    private static EntityManagerFactory entityManagerFactory;

    private TestDataManager() {
    }

    static void configure(final EntityManagerFactory emf) {
        entityManagerFactory = Objects.requireNonNull(emf, "EntityManagerFactory is required");
    }

    /**
     * Clears all persisted details and inserts a small deterministic dataset
     * that mirrors the scenarios covered by the regression tests.
     */
    public static synchronized void reset() {
        final EntityManagerFactory emf = Objects.requireNonNull(entityManagerFactory,
                "TestDataManager has not been initialised");
        final EntityManager entityManager = emf.createEntityManager();
        final EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.createQuery("DELETE FROM Detail").executeUpdate();

            persistDetail(entityManager, "交通費精算", Status.DRAFT, "user1");
            persistDetail(entityManager, "備品購入", Status.REQUESTED, "user1");
            persistDetail(entityManager, "会議費", Status.APPROVED, "user2");
            persistDetail(entityManager, "出張費", Status.DRAFT, "user1");

            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }

    private static void persistDetail(final EntityManager entityManager, final String title,
            final Status status, final String ownerUserId) {
        final Detail detail = new Detail();
        detail.setTitle(title);
        detail.setStatus(status);
        detail.setOwnerUserId(ownerUserId);
        entityManager.persist(detail);
    }
}
