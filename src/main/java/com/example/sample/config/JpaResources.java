package com.example.sample.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * JPAのエンティティマネージャーをCDI経由で提供する設定クラスです。
 */
@ApplicationScoped
public class JpaResources {

    @PersistenceContext
    private EntityManager containerManagedEm;

    /**
     * コンテナ管理のエンティティマネージャーをアプリ内で利用できるように公開します。
     *
     * @return 共有の {@link EntityManager}
     */
    @Produces
    public EntityManager produceEntityManager() {
        return containerManagedEm;
    }
}
