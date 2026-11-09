
package com.erdal.helpdeskpro.repository.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.enums.AssetStatus;
import com.erdal.helpdeskpro.exception.BadRequestExeption;
import com.erdal.helpdeskpro.repository.AssetRepository;

public class AssetDAO implements AssetRepository {

    private final SessionFactory sessionFactory;

    public AssetDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(Asset asset) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            Asset existingAsset = session.createQuery(
                "FROM Asset a WHERE a.assetTag = :assetTag",
                Asset.class
            )
            .setParameter("assetTag", asset.getAssetTag())
            .uniqueResult();

            if (existingAsset != null &&
                !existingAsset.getId().equals(asset.getId())) {

                throw new IllegalArgumentException(
                    "Asset tag already exists."
                );
            }

            if (asset.getId() == null) {
                session.persist(asset);
            } else {
                session.merge(asset);
            }

            transaction.commit();

        } catch (RuntimeException e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public Asset findById(Long id) {

        try (Session session = sessionFactory.openSession()) {
            return session.get(Asset.class, id);
        }
    }

    @Override
    public Asset findByAssetTag(String assetTag) {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                "FROM Asset a WHERE a.assetTag = :assetTag",
                Asset.class
            )
            .setParameter("assetTag", assetTag)
            .uniqueResult();
        }
    }

    @Override
    public List<Asset> findAll() {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                "FROM Asset a ORDER BY a.id DESC",
                Asset.class
            ).list();
        }
    }

    @Override
    public List<Asset> findAllByStatus(AssetStatus status) {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                "FROM Asset a WHERE a.status = :status ORDER BY a.id DESC",
                Asset.class
            )
            .setParameter("status", status)
            .list();
        }
    }

    @Override
    public void deleteById(Long id) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            Asset asset = session.get(Asset.class, id);

            if (asset == null) {
                throw new IllegalArgumentException(
                    "Asset not found."
                );
            }

            // Keep the asset history; do not physically delete assets.
            asset.setStatus(AssetStatus.RETIRED);

            transaction.commit();

        } catch (RuntimeException e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }
}
