package com.erdal.helpdeskpro.repository.dao;

import com.erdal.helpdeskpro.domain.dtos.AssetOverviewDTO;
import com.erdal.helpdeskpro.enums.AssetStatus;
import com.erdal.helpdeskpro.repository.AssetOverviewRepository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class AssetOverviewDAO implements AssetOverviewRepository {

    private final SessionFactory sessionFactory;

    public AssetOverviewDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public AssetOverviewDTO getOverview() {

        try (Session session = sessionFactory.openSession()) {

            Object[] result = (Object[]) session.createQuery(
                    "SELECT " +
                    "COUNT(a.id), " +
                    "SUM(CASE WHEN a.status = :assigned THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN a.status = :available THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN a.status = :maintenance THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN a.status = :retired THEN 1 ELSE 0 END), " +
                    "SUM(CASE WHEN a.status = :lost THEN 1 ELSE 0 END) " +
                    "FROM Asset a",
                    Object[].class
            )
            .setParameter("assigned", AssetStatus.ASSIGNED)
            .setParameter("available", AssetStatus.AVAILABLE)
            .setParameter("maintenance", AssetStatus.MAINTENANCE)
            .setParameter("retired", AssetStatus.RETIRED)
            .setParameter("lost", AssetStatus.LOST)
            .uniqueResult();

            return new AssetOverviewDTO(
                    ((Number) result[0]).longValue(),
                    ((Number) result[1]).longValue(),
                    ((Number) result[2]).longValue(),
                    ((Number) result[3]).longValue(),
                    ((Number) result[4]).longValue(),
                    ((Number) result[5]).longValue()
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to retrieve asset overview.", e);
        }
    }
}