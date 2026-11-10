
package com.erdal.helpdeskpro.service.impl;

import com.erdal.helpdeskpro.domain.AssetMaintenance;
import com.erdal.helpdeskpro.enums.AssetMaintenanceStatus;
import com.erdal.helpdeskpro.repository.AssetMaintenanceRepository;
import com.erdal.helpdeskpro.service.AssetMaintenanceService;

import java.util.List;

public class AssetMaintenanceServiceImpl
        implements AssetMaintenanceService {

    private final AssetMaintenanceRepository maintenanceRepository;

    public AssetMaintenanceServiceImpl(
            AssetMaintenanceRepository maintenanceRepository
    ) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @Override
    public void createMaintenance(AssetMaintenance maintenance) {
        maintenanceRepository.save(maintenance);
    }

    @Override
    public AssetMaintenance getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id);
    }

    @Override
    public List<AssetMaintenance> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }

    @Override
    public List<AssetMaintenance> getMaintenanceByAssetId(Long assetId) {
        return maintenanceRepository.findAllByAssetId(assetId);
    }

    @Override
    public List<AssetMaintenance> getMaintenanceByStatus(
            AssetMaintenanceStatus status
    ) {
        return maintenanceRepository.findAllByStatus(status);
    }

    @Override
    public void updateMaintenanceStatus(
            Long id,
            AssetMaintenanceStatus status
    ) {
        maintenanceRepository.updateStatus(id, status);
    }
}
