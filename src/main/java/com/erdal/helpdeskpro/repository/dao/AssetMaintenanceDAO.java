
package com.erdal.helpdeskpro.repository.dao;

import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.domain.AssetAssignment;
import com.erdal.helpdeskpro.domain.AssetMaintenance;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.AssetMaintenanceStatus;
import com.erdal.helpdeskpro.enums.AssetStatus;
import com.erdal.helpdeskpro.repository.AssetMaintenanceRepository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class AssetMaintenanceDAO implements AssetMaintenanceRepository {

    private final SessionFactory sessionFactory;

    public AssetMaintenanceDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(AssetMaintenance maintenance) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            if (maintenance == null
                    || maintenance.getAsset() == null
                    || maintenance.getAsset().getId() == null) {
                throw new IllegalArgumentException("Asset is required.");
            }

            if (maintenance.getReportedBy() == null
                    || maintenance.getReportedBy().getId() == null) {
                throw new IllegalArgumentException(
                        "Reporting user is required."
                );
            }

            Asset asset = session.get(
                    Asset.class,
                    maintenance.getAsset().getId()
            );

            if (asset == null) {
                throw new IllegalArgumentException("Asset not found.");
            }

            if (asset.getStatus() == AssetStatus.RETIRED
                    || asset.getStatus() == AssetStatus.LOST) {
                throw new IllegalArgumentException(
                        "Retired or lost assets cannot enter maintenance."
                );
            }

            // Employee yalnızca kendisine atanmış cihaz için
            // arıza bildirimi oluşturabilir.
            User reporter = session.get(
                    User.class,
                    maintenance.getReportedBy().getId()
            );

            if (reporter == null) {
                throw new IllegalArgumentException(
                        "Reporting user not found."
                );
            }

            String role = reporter.getRole().name();

            boolean isStaff =
                    "ADMIN".equals(role)
                    || "IT_SUPPORT".equals(role);

            if (!isStaff) {

                Long activeAssignmentCount =
                        session.createQuery(
                                "SELECT COUNT(aa.id) " +
                                "FROM AssetAssignment aa " +
                                "WHERE aa.asset.id = :assetId " +
                                "AND aa.user.id = :userId " +
                                "AND aa.returnedAt IS NULL",
                                Long.class
                        )
                        .setParameter("assetId", asset.getId())
                        .setParameter("userId", reporter.getId())
                        .uniqueResult();

                if (activeAssignmentCount == null
                        || activeAssignmentCount == 0) {
                    throw new IllegalArgumentException(
                            "You can only report issues for assets assigned to you."
                    );
                }
            }

            // Aynı cihazda başka açık veya devam eden bakım var mı?
            Long activeMaintenanceCount =
                    session.createQuery(
                            "SELECT COUNT(m.id) " +
                            "FROM AssetMaintenance m " +
                            "WHERE m.asset.id = :assetId " +
                            "AND m.status IN (:openStatus, :progressStatus)",
                            Long.class
                    )
                    .setParameter("assetId", asset.getId())
                    .setParameter(
                            "openStatus",
                            AssetMaintenanceStatus.OPEN
                    )
                    .setParameter(
                            "progressStatus",
                            AssetMaintenanceStatus.IN_PROGRESS
                    )
                    .uniqueResult();

            if (activeMaintenanceCount != null
                    && activeMaintenanceCount > 0) {
                throw new IllegalArgumentException(
                        "This asset already has an active maintenance record."
                );
            }

            maintenance.setAsset(asset);
            maintenance.setReportedBy(reporter);

            if (maintenance.getStatus() == null) {
                maintenance.setStatus(AssetMaintenanceStatus.OPEN);
            }

            if (maintenance.getStatus() != AssetMaintenanceStatus.OPEN
                    && maintenance.getStatus()
                    != AssetMaintenanceStatus.IN_PROGRESS) {
                throw new IllegalArgumentException(
                        "New maintenance must be OPEN or IN_PROGRESS."
                );
            }

            // OPEN ve IN_PROGRESS durumlarında cihaz kullanıma kapatılır.
            asset.setStatus(AssetStatus.MAINTENANCE);

            if (maintenance.getStatus()
                    == AssetMaintenanceStatus.IN_PROGRESS) {
                maintenance.setStartedAt(LocalDateTime.now());
            }

            session.persist(maintenance);

            transaction.commit();

        } catch (IllegalArgumentException e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "Maintenance record could not be saved.",
                    e
            );
        }
    }

    @Override
    public AssetMaintenance findById(Long id) {

        try (Session session = sessionFactory.openSession()) {
            return session.get(AssetMaintenance.class, id);
        }
    }

    @Override
    public List<AssetMaintenance> findAll() {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "FROM AssetMaintenance m ORDER BY m.id DESC",
                    AssetMaintenance.class
            ).list();
        }
    }

    @Override
    public List<AssetMaintenance> findAllByAssetId(Long assetId) {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "FROM AssetMaintenance m " +
                    "WHERE m.asset.id = :assetId ORDER BY m.id DESC",
                    AssetMaintenance.class
            )
            .setParameter("assetId", assetId)
            .list();
        }
    }

    @Override
    public List<AssetMaintenance> findAllByStatus(
            AssetMaintenanceStatus status
    ) {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "FROM AssetMaintenance m " +
                    "WHERE m.status = :status ORDER BY m.id DESC",
                    AssetMaintenance.class
            )
            .setParameter("status", status)
            .list();
        }
    }

    @Override
    public void updateStatus(
            Long id,
            AssetMaintenanceStatus status
    ) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            if (status == null) {
                throw new IllegalArgumentException(
                        "Maintenance status is required."
                );
            }

            AssetMaintenance maintenance = session.get(
                    AssetMaintenance.class,
                    id
            );

            if (maintenance == null) {
                throw new IllegalArgumentException(
                        "Maintenance record not found."
                );
            }

            Asset asset = session.get(
                    Asset.class,
                    maintenance.getAsset().getId()
            );

            if (asset == null) {
                throw new IllegalArgumentException("Asset not found.");
            }

            if (asset.getStatus() == AssetStatus.RETIRED
                    || asset.getStatus() == AssetStatus.LOST) {
                throw new IllegalArgumentException(
                        "Retired or lost assets cannot be maintained."
                );
            }

            if (status == AssetMaintenanceStatus.OPEN) {

                maintenance.setStartedAt(null);
                maintenance.setCompletedAt(null);
                asset.setStatus(AssetStatus.MAINTENANCE);

            } else if (status == AssetMaintenanceStatus.IN_PROGRESS) {

                if (maintenance.getStartedAt() == null) {
                    maintenance.setStartedAt(LocalDateTime.now());
                }

                maintenance.setCompletedAt(null);
                asset.setStatus(AssetStatus.MAINTENANCE);

            } else if (status == AssetMaintenanceStatus.COMPLETED
                    || status == AssetMaintenanceStatus.CANCELLED) {

                if (status == AssetMaintenanceStatus.COMPLETED) {
                    maintenance.setCompletedAt(LocalDateTime.now());
                }

                // Başka aktif bakım kaydı varsa cihaz bakımda kalır.
                Long otherActiveMaintenanceCount =
                        session.createQuery(
                                "SELECT COUNT(m.id) " +
                                "FROM AssetMaintenance m " +
                                "WHERE m.asset.id = :assetId " +
                                "AND m.id != :maintenanceId " +
                                "AND m.status IN (:openStatus, :progressStatus)",
                                Long.class
                        )
                        .setParameter("assetId", asset.getId())
                        .setParameter("maintenanceId", maintenance.getId())
                        .setParameter(
                                "openStatus",
                                AssetMaintenanceStatus.OPEN
                        )
                        .setParameter(
                                "progressStatus",
                                AssetMaintenanceStatus.IN_PROGRESS
                        )
                        .uniqueResult();

                if (otherActiveMaintenanceCount != null
                        && otherActiveMaintenanceCount > 0) {

                    asset.setStatus(AssetStatus.MAINTENANCE);

                } else {

                    // Aktif zimmet varsa ASSIGNED, yoksa AVAILABLE.
                    Long activeAssignmentCount =
                            session.createQuery(
                                    "SELECT COUNT(aa.id) " +
                                    "FROM AssetAssignment aa " +
                                    "WHERE aa.asset.id = :assetId " +
                                    "AND aa.returnedAt IS NULL",
                                    Long.class
                            )
                            .setParameter("assetId", asset.getId())
                            .uniqueResult();

                    if (activeAssignmentCount != null
                            && activeAssignmentCount > 0) {
                        asset.setStatus(AssetStatus.ASSIGNED);
                    } else {
                        asset.setStatus(AssetStatus.AVAILABLE);
                    }
                }
            }

            maintenance.setStatus(status);

            transaction.commit();

        } catch (IllegalArgumentException e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "Maintenance status could not be updated.",
                    e
            );
        }
    }
}
