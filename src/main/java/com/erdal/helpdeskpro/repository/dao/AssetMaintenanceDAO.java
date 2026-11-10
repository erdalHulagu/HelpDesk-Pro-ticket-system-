
package com.erdal.helpdeskpro.repository.dao;

import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.domain.AssetMaintenance;
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

            maintenance.setAsset(asset);

            if (maintenance.getStatus() == null) {
                maintenance.setStatus(AssetMaintenanceStatus.OPEN);
            }

            if (maintenance.getStatus()
                    == AssetMaintenanceStatus.IN_PROGRESS) {

                maintenance.setStartedAt(LocalDateTime.now());
                asset.setStatus(AssetStatus.MAINTENANCE);
            }

            if (maintenance.getStatus()
                    == AssetMaintenanceStatus.COMPLETED) {

                maintenance.setCompletedAt(LocalDateTime.now());
                asset.setStatus(AssetStatus.AVAILABLE);
            }

            session.persist(maintenance);

            transaction.commit();

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

            AssetMaintenance maintenance = session.get(
                    AssetMaintenance.class,
                    id
            );

            if (maintenance == null) {
                throw new IllegalArgumentException(
                        "Maintenance record not found."
                );
            }

            Asset asset = maintenance.getAsset();

            if (status == AssetMaintenanceStatus.IN_PROGRESS) {

                if (maintenance.getStartedAt() == null) {
                    maintenance.setStartedAt(LocalDateTime.now());
                }

                asset.setStatus(AssetStatus.MAINTENANCE);
            }

            if (status == AssetMaintenanceStatus.COMPLETED) {

                maintenance.setCompletedAt(LocalDateTime.now());

                if (asset.getStatus() == AssetStatus.MAINTENANCE) {
                    asset.setStatus(AssetStatus.AVAILABLE);
                }
            }

            if (status == AssetMaintenanceStatus.CANCELLED) {

                if (asset.getStatus() == AssetStatus.MAINTENANCE) {
                    asset.setStatus(AssetStatus.AVAILABLE);
                }
            }

            maintenance.setStatus(status);

            transaction.commit();

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
