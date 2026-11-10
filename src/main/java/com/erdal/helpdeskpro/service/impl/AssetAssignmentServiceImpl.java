
package com.erdal.helpdeskpro.service.impl;

import com.erdal.helpdeskpro.domain.AssetAssignment;
import com.erdal.helpdeskpro.repository.AssetAssignmentRepository;
import com.erdal.helpdeskpro.service.AssetAssignmentService;

import java.util.List;

public class AssetAssignmentServiceImpl
        implements AssetAssignmentService {

    private final AssetAssignmentRepository assignmentRepository;

    public AssetAssignmentServiceImpl(
            AssetAssignmentRepository assignmentRepository
    ) {
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public void assignAsset(AssetAssignment assignment) {
        assignmentRepository.save(assignment);
    }

    @Override
    public AssetAssignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    @Override
    public List<AssetAssignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    @Override
    public List<AssetAssignment> getAssignmentsByUserId(Long userId) {
        return assignmentRepository.findAllByUserId(userId);
    }

    @Override
    public List<AssetAssignment> getAssignmentsByAssetId(Long assetId) {
        return assignmentRepository.findAllByAssetId(assetId);
    }

    @Override
    public AssetAssignment getActiveAssignmentByAssetId(Long assetId) {
        return assignmentRepository.findActiveByAssetId(assetId);
    }

    @Override
    public void returnAsset(Long assignmentId) {
        assignmentRepository.returnAsset(assignmentId);
    }
}
