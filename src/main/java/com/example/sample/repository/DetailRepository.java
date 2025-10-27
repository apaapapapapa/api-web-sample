package com.example.sample.repository;

import java.util.List;

import com.example.sample.model.Detail;
import com.example.sample.model.Status;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;

/**
 * 明細テーブルへアクセスするためのリポジトリです。
 * JPQLを用いてユーザーごとの明細検索や更新を行います。
 */
@ApplicationScoped
@NoArgsConstructor // Weldの制約で引数なしコンストラクタも必要
public class DetailRepository {

    private EntityManager em;

    @Inject
    public DetailRepository(EntityManager em) {
        this.em = em;
    }
    
    private static final String USER_ID_PARAM = "userId";

    /**
     * 指定したユーザーが所有する明細をすべて取得します。
     *
     * @param userId 所有者のユーザーID
     * @return 明細のリスト（降順）
     */
    public List<Detail> findByUserId(final String userId) {
        requireUserId(userId);
        return em.createQuery(
                "SELECT d FROM Detail d WHERE d.ownerUserId = :userId ORDER BY d.detailId DESC",
                Detail.class)
            .setParameter(USER_ID_PARAM, userId)
            .getResultList();
    }

    /**
     * ユーザーと状態を条件に明細を検索します。
     * 状態が未指定の場合はユーザー条件のみで検索します。
     *
     * @param userId 所有者のユーザーID
     * @param status 絞り込み対象の状態（null可）
     * @return 条件に一致する明細のリスト
     */
    public List<Detail> findByUserIdAndStatus(final String userId, final Status status) {
        requireUserId(userId);
        if (status == null) {
            return findByUserId(userId);
        }
        return em.createQuery(
                "SELECT d FROM Detail d WHERE d.ownerUserId = :userId AND d.status = :status ORDER BY d.detailId DESC",
                Detail.class)
            .setParameter(USER_ID_PARAM, userId)
            .setParameter("status", status)
            .getResultList();
    }

    /**
     * 指定したID群の中で、ユーザーが所有している明細だけを取得します。
     *
     * @param ids 確認したい明細IDのリスト
     * @param userId 所有者のユーザーID
     * @return 条件に合致する明細のリスト
     */
    public List<Detail> findByDetailIdInAndOwnerUserId(final List<Long> ids, final String userId) {
        requireUserId(userId);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return em.createQuery(
                "SELECT d FROM Detail d WHERE d.detailId IN :ids AND d.ownerUserId = :userId",
                Detail.class)
            .setParameter("ids", ids)
            .setParameter(USER_ID_PARAM, userId)
            .getResultList();
    }

    /**
     * 明細をロックし、申請中の状態へ更新します。
     *
     * @param detailId 対象の明細ID
     * @param userId 操作しているユーザーID
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void lockAndMarkRequested(final Long detailId, final String userId) {
        requireUserId(userId);
        final Detail managed = em.find(Detail.class, detailId, LockModeType.PESSIMISTIC_WRITE);
        if (managed == null || !userId.equals(managed.getOwnerUserId())) {
            throw new IllegalArgumentException("対象が存在しないか、ユーザー不一致: id=" + detailId);
        }
        managed.setStatus(Status.REQUESTED);
        em.flush();
    }

    /**
     * ユーザーIDのバリデーションを行います。
     *
     * @param userId チェック対象のユーザーID
     */
    private static void requireUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId is required");
        }
    }
}
