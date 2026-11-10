
package com.erdal.helpdeskpro.service.impl;

import com.erdal.helpdeskpro.domain.request.AssetRequest;
import com.erdal.helpdeskpro.enums.AssetRequestStatus;
import com.erdal.helpdeskpro.repository.AssetRequestRepository;
import com.erdal.helpdeskpro.service.AssetRequestService;

import java.util.List;

public class AssetRequestServiceImpl implements AssetRequestService {

    private final AssetRequestRepository requestRepository;

    public AssetRequestServiceImpl(
            AssetRequestRepository requestRepository
    ) {
        this.requestRepository = requestRepository;
    }

    @Override
    public void createRequest(AssetRequest request) {
        requestRepository.save(request);
    }

    @Override
    public AssetRequest getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    @Override
    public List<AssetRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    public List<AssetRequest> getRequestsByUserId(Long userId) {
        return requestRepository.findAllByUserId(userId);
    }

    @Override
    public List<AssetRequest> getRequestsByStatus(
            AssetRequestStatus status
    ) {
        return requestRepository.findAllByStatus(status);
    }

    @Override
    public void reviewRequest(
            Long requestId,
            AssetRequestStatus status,
            Long reviewerId
    ) {
        requestRepository.reviewRequest(
                requestId,
                status,
                reviewerId
        );
    }

    @Override
    public void cancelRequest(Long requestId, Long userId) {
        requestRepository.cancelRequest(requestId, userId);
    }

    @Override
    public void fulfillRequest(Long requestId, Long assetId) {
        requestRepository.fulfillRequest(requestId, assetId);
    }
}
