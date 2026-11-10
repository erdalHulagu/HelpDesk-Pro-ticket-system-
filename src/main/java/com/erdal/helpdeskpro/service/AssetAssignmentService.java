
package com.erdal.helpdeskpro.service;

import com.erdal.helpdeskpro.domain.AssetAssignment;
import java.util.List;

public interface AssetAssignmentService {

    void assignAsset(AssetAssignment assignment);

    AssetAssignment getAssignmentById(Long id);

    List<AssetAssignment> getAllAssignments();

    List<AssetAssignment> getAssignmentsByUserId(Long userId);

    List<AssetAssignment> getAssignmentsByAssetId(Long assetId);

    AssetAssignment getActiveAssignmentByAssetId(Long assetId);

    void returnAsset(Long assignmentId);
}
