
package com.erdal.helpdeskpro.controller;

import com.erdal.helpdeskpro.domain.AssetAssignment;
import com.erdal.helpdeskpro.service.AssetAssignmentService;

import java.util.List;

public class AssetAssignmentController {

    private final AssetAssignmentService assignmentService;

    public AssetAssignmentController(
            AssetAssignmentService assignmentService
    ) {
        this.assignmentService = assignmentService;
    }

    public void assignAsset(AssetAssignment assignment) {
        assignmentService.assignAsset(assignment);
    }

    public AssetAssignment getAssignmentById(Long id) {
        return assignmentService.getAssignmentById(id);
    }

    public List<AssetAssignment> getAllAssignments() {
        return assignmentService.getAllAssignments();
    }

    public List<AssetAssignment> getAssignmentsByUserId(Long userId) {
        return assignmentService.getAssignmentsByUserId(userId);
    }

    public List<AssetAssignment> getAssignmentsByAssetId(Long assetId) {
        return assignmentService.getAssignmentsByAssetId(assetId);
    }

    public AssetAssignment getActiveAssignmentByAssetId(Long assetId) {
        return assignmentService.getActiveAssignmentByAssetId(assetId);
    }

    public void returnAsset(Long assignmentId) {
        assignmentService.returnAsset(assignmentId);
    }
}
