
package com.erdal.helpdeskpro.repository.dao;

import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.domain.AssetAssignment;
import com.erdal.helpdeskpro.enums.AssetStatus;
import com.erdal.helpdeskpro.repository.AssetAssignmentRepository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class AssetAssignmentDAO implements AssetAssignmentRepository {

    private final SessionFactory sessionFactory;

    public AssetAssignmentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(AssetAssignment assignment) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            if (assignment.getId() == null) {

                Asset asset = session.get(
                        Asset.class,
                        assignment.getAsset().getId()
                );

                if (asset == null) {
                    throw new IllegalArgumentException("Asset not found.");
                }

                if (asset.getStatus() != AssetStatus.AVAILABLE) {
                    throw new IllegalArgumentException(
                            "Asset is not available for assignment."
                    );
                }

                AssetAssignment activeAssignment =
                        findActiveByAssetId(session, asset.getId());

                if (activeAssignment != null) {
                    throw new IllegalArgumentException(
                            "Asset already has an active assignment."
                    );
                }

                assignment.setAsset(asset);
                asset.setStatus(AssetStatus.ASSIGNED);

                session.persist(assignment);

            } else {
                session.merge(assignment);
            }

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "Asset assignment could not be saved.",
                    e
            );
        }
    }

    @Override
    public AssetAssignment findById(Long id) {

        try (Session session = sessionFactory.openSession()) {
            return session.get(AssetAssignment.class, id);
        }
    }

    @Override
    public List<AssetAssignment> findAll() {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "FROM AssetAssignment a ORDER BY a.id DESC",
                    AssetAssignment.class
            ).list();
        }
    }

    @Override
    public List<AssetAssignment> findAllByUserId(Long userId) {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "FROM AssetAssignment a WHERE a.user.id = :userId " +
                    "ORDER BY a.id DESC",
                    AssetAssignment.class
            )
            .setParameter("userId", userId)
            .list();
        }
    }

    @Override
    public List<AssetAssignment> findAllByAssetId(Long assetId) {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "FROM AssetAssignment a WHERE a.asset.id = :assetId " +
                    "ORDER BY a.id DESC",
                    AssetAssignment.class
            )
            .setParameter("assetId", assetId)
            .list();
        }
    }

    @Override
    public AssetAssignment findActiveByAssetId(Long assetId) {

        try (Session session = sessionFactory.openSession()) {
            return findActiveByAssetId(session, assetId);
        }
    }

    private AssetAssignment findActiveByAssetId(
            Session session,
            Long assetId
    ) {

        return session.createQuery(
                "FROM AssetAssignment a " +
                "WHERE a.asset.id = :assetId " +
                "AND a.returnedAt IS NULL",
                AssetAssignment.class
        )
        .setParameter("assetId", assetId)
        .uniqueResult();
    }

    @Override
    public void returnAsset(Long assignmentId) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            AssetAssignment assignment = session.get(
                    AssetAssignment.class,
                    assignmentId
            );

            if (assignment == null) {
                throw new IllegalArgumentException(
                        "Asset assignment not found."
                );
            }

            if (assignment.getReturnedAt() != null) {
                throw new IllegalArgumentException(
                        "This asset has already been returned."
                );
            }

            assignment.setReturnedAt(LocalDateTime.now());

            Asset asset = assignment.getAsset();
            asset.setStatus(AssetStatus.AVAILABLE);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "Asset could not be returned.",
                    e
            );
        }
    }
}
