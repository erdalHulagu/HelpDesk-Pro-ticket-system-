
package com.erdal.helpdeskpro.repository.dao;

import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.domain.AssetAssignment;
import com.erdal.helpdeskpro.domain.request.AssetRequest;
import com.erdal.helpdeskpro.domain.User;
import com.erdal.helpdeskpro.enums.AssetRequestStatus;
import com.erdal.helpdeskpro.enums.AssetStatus;
import com.erdal.helpdeskpro.repository.AssetRequestRepository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public class AssetRequestDAO implements AssetRequestRepository {

    private final SessionFactory sessionFactory;

    public AssetRequestDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(AssetRequest request) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            if (request.getUser() == null
                    || request.getUser().getId() == null) {
                throw new IllegalArgumentException("User is required.");
            }

            User user = session.get(
                    User.class,
                    request.getUser().getId()
            );

            if (user == null) {
                throw new IllegalArgumentException("User not found.");
            }

            request.setUser(user);
            request.setId(null);
            request.setStatus(AssetRequestStatus.PENDING);
            request.setReviewedBy(null);
            request.setReviewedAt(null);
            request.setAsset(null);

            session.persist(request);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "Asset request could not be created.",
                    e
            );
        }
    }

    @Override
    public AssetRequest findById(Long id) {

        try (Session session = sessionFactory.openSession()) {
            return session.get(AssetRequest.class, id);
        }
    }

    @Override
    public List<AssetRequest> findAll() {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "FROM AssetRequest r ORDER BY r.id DESC",
                    AssetRequest.class
            ).list();
        }
    }

    @Override
    public List<AssetRequest> findAllByUserId(Long userId) {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "FROM AssetRequest r " +
                    "WHERE r.user.id = :userId ORDER BY r.id DESC",
                    AssetRequest.class
            )
            .setParameter("userId", userId)
            .list();
        }
    }

    @Override
    public List<AssetRequest> findAllByStatus(
            AssetRequestStatus status
    ) {

        try (Session session = sessionFactory.openSession()) {

            return session.createQuery(
                    "FROM AssetRequest r " +
                    "WHERE r.status = :status ORDER BY r.id DESC",
                    AssetRequest.class
            )
            .setParameter("status", status)
            .list();
        }
    }

    @Override
    public void reviewRequest(
            Long requestId,
            AssetRequestStatus status,
            Long reviewerId
    ) {

        if (status != AssetRequestStatus.APPROVED
                && status != AssetRequestStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Review status must be APPROVED or REJECTED."
            );
        }

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            AssetRequest request = session.get(
                    AssetRequest.class,
                    requestId
            );

            if (request == null) {
                throw new IllegalArgumentException(
                        "Asset request not found."
                );
            }

            if (request.getStatus() != AssetRequestStatus.PENDING) {
                throw new IllegalArgumentException(
                        "Only pending requests can be reviewed."
                );
            }

            User reviewer = session.get(User.class, reviewerId);

            if (reviewer == null) {
                throw new IllegalArgumentException(
                        "Reviewer not found."
                );
            }

            request.setStatus(status);
            request.setReviewedBy(reviewer);
            request.setReviewedAt(LocalDateTime.now());

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "Asset request could not be reviewed.",
                    e
            );
        }
    }

    @Override
    public void cancelRequest(Long requestId, Long userId) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            AssetRequest request = session.get(
                    AssetRequest.class,
                    requestId
            );

            if (request == null) {
                throw new IllegalArgumentException(
                        "Asset request not found."
                );
            }

            if (request.getUser() == null
                    || !request.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException(
                        "You can only cancel your own request."
                );
            }

            if (request.getStatus() != AssetRequestStatus.PENDING) {
                throw new IllegalArgumentException(
                        "Only pending requests can be cancelled."
                );
            }

            request.setStatus(AssetRequestStatus.CANCELLED);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            throw new RuntimeException(
                    "Asset request could not be cancelled.",
                    e
            );
        }
    }


@Override
public void fulfillRequest(Long requestId, Long assetId) {

    Transaction transaction = null;

    try (Session session = sessionFactory.openSession()) {

        transaction = session.beginTransaction();

        AssetRequest request = session.get(
                AssetRequest.class,
                requestId
        );

        if (request == null) {
            throw new IllegalArgumentException("Asset request not found.");
        }

        if (request.getStatus() != AssetRequestStatus.APPROVED) {
            throw new IllegalArgumentException(
                    "Only approved requests can be fulfilled."
            );
        }

        Asset asset = session.get(Asset.class, assetId);

        if (asset == null) {
            throw new IllegalArgumentException("Asset not found.");
        }

        if (asset.getCategory() != request.getCategory()) {
            throw new IllegalArgumentException(
                    "Asset category does not match the request."
            );
        }

        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                    "Asset is not available."
            );
        }

        if (request.getReviewedBy() == null) {
            throw new IllegalArgumentException(
                    "Approved request has no reviewer."
            );
        }

        AssetAssignment assignment = new AssetAssignment();

        assignment.setAsset(asset);
        assignment.setUser(request.getUser());
        assignment.setAssignedBy(request.getReviewedBy());
        assignment.setAssignedAt(LocalDateTime.now());

        asset.setStatus(AssetStatus.ASSIGNED);

        request.setAsset(asset);
        request.setStatus(AssetRequestStatus.FULFILLED);

        session.persist(assignment);

        transaction.commit();

    } catch (Exception e) {

        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }

        throw new RuntimeException(
                "Asset request could not be fulfilled.",
                e
        );
    }
}

}
