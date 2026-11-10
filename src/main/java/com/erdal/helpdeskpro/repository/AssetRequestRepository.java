
package com.erdal.helpdeskpro.repository;

import com.erdal.helpdeskpro.domain.request.AssetRequest;
import com.erdal.helpdeskpro.enums.AssetRequestStatus;

import java.util.List;

public interface AssetRequestRepository {

    void save(AssetRequest request);

    AssetRequest findById(Long id);

    List<AssetRequest> findAll();

    List<AssetRequest> findAllByUserId(Long userId);

    List<AssetRequest> findAllByStatus(AssetRequestStatus status);

    void reviewRequest(
            Long requestId,
            AssetRequestStatus status,
            Long reviewerId
    );

    void cancelRequest(Long requestId, Long userId);

    void fulfillRequest(Long requestId, Long assetId);
}
