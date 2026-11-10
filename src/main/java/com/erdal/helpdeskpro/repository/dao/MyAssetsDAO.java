package com.erdal.helpdeskpro.repository.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.domain.AssetAssignment;
import com.erdal.helpdeskpro.domain.response.MyAssetResponse;
import com.erdal.helpdeskpro.repository.MyAssetsRepository;

public class MyAssetsDAO implements MyAssetsRepository {

    private final SessionFactory sessionFactory;

    public MyAssetsDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<MyAssetResponse> findMyAssets(Long userId) {

        try (Session session = sessionFactory.openSession()) {

            List<AssetAssignment> assignments = session.createQuery(
                    "FROM AssetAssignment aa " +
                    "JOIN FETCH aa.asset " +
                    "WHERE aa.user.id = :userId " +
                    "AND aa.returnedAt IS NULL " +
                    "ORDER BY aa.assignedAt DESC",
                    AssetAssignment.class
            )
            .setParameter("userId", userId)
            .getResultList();

            List<MyAssetResponse> responses = new ArrayList<>();

            for (AssetAssignment assignment : assignments) {

                Asset asset = assignment.getAsset();

                MyAssetResponse response = new MyAssetResponse();

                response.setAssignmentId(assignment.getId());
                response.setAssetId(asset.getId());

                response.setAssetTag(asset.getAssetTag());
                response.setName(asset.getName());
                response.setCategory(asset.getCategory());

                response.setBrand(asset.getBrand());
                response.setModel(asset.getModel());
                response.setSerialNumber(asset.getSerialNumber());

                response.setStatus(asset.getStatus());

                response.setPurchaseDate(asset.getPurchaseDate());
                response.setWarrantyExpiry(asset.getWarrantyExpiry());

                response.setAssignedAt(assignment.getAssignedAt());
                response.setNotes(assignment.getNotes());

                responses.add(response);
            }

            return responses;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve employee assets.", e
            );
        }
    }
}