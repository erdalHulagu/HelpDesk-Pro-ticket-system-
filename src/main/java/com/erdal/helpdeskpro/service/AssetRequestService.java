
package com.erdal.helpdeskpro.service;

import com.erdal.helpdeskpro.domain.request.AssetRequest;
import com.erdal.helpdeskpro.enums.AssetRequestStatus;

import java.util.List;

public interface AssetRequestService {

    void createRequest(AssetRequest request);

    AssetRequest getRequestById(Long id);

    List<AssetRequest> getAllRequests();

    List<AssetRequest> getRequestsByUserId(Long userId);

    List<AssetRequest> getRequestsByStatus(AssetRequestStatus status);

    void reviewRequest(
            Long requestId,
            AssetRequestStatus status,
            Long reviewerId
    );

    void cancelRequest(Long requestId, Long userId);

    void fulfillRequest(Long requestId, Long assetId);
}
