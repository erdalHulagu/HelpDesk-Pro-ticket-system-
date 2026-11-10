
package com.erdal.helpdeskpro.repository;

import com.erdal.helpdeskpro.domain.AssetAssignment;
import java.util.List;

public interface AssetAssignmentRepository {

    void save(AssetAssignment assignment);

    AssetAssignment findById(Long id);

    List<AssetAssignment> findAll();

    List<AssetAssignment> findAllByUserId(Long userId);

    List<AssetAssignment> findAllByAssetId(Long assetId);

    AssetAssignment findActiveByAssetId(Long assetId);

    void returnAsset(Long assignmentId);
}
