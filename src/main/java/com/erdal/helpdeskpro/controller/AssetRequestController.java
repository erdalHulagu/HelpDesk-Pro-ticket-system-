
package com.erdal.helpdeskpro.controller;

import com.erdal.helpdeskpro.domain.request.AssetRequest;
import com.erdal.helpdeskpro.enums.AssetRequestStatus;
import com.erdal.helpdeskpro.service.AssetRequestService;

import java.util.List;

public class AssetRequestController {

    private final AssetRequestService requestService;

    public AssetRequestController(
            AssetRequestService requestService
    ) {
        this.requestService = requestService;
    }

    public void createRequest(AssetRequest request) {
        requestService.createRequest(request);
    }

    public AssetRequest getRequestById(Long id) {
        return requestService.getRequestById(id);
    }

    public List<AssetRequest> getAllRequests() {
        return requestService.getAllRequests();
    }

    public List<AssetRequest> getRequestsByUserId(Long userId) {
        return requestService.getRequestsByUserId(userId);
    }

    public List<AssetRequest> getRequestsByStatus(
            AssetRequestStatus status
    ) {
        return requestService.getRequestsByStatus(status);
    }

    public void reviewRequest(
            Long requestId,
            AssetRequestStatus status,
            Long reviewerId
    ) {
        requestService.reviewRequest(
                requestId,
                status,
                reviewerId
        );
    }

    public void cancelRequest(Long requestId, Long userId) {
        requestService.cancelRequest(requestId, userId);
    }

    public void fulfillRequest(Long requestId, Long assetId) {
        requestService.fulfillRequest(requestId, assetId);
    }
}
