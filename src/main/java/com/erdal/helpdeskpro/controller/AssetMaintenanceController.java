
package com.erdal.helpdeskpro.controller;

import com.erdal.helpdeskpro.domain.AssetMaintenance;
import com.erdal.helpdeskpro.enums.AssetMaintenanceStatus;
import com.erdal.helpdeskpro.service.AssetMaintenanceService;

import java.util.List;

public class AssetMaintenanceController {

	private final AssetMaintenanceService maintenanceService;

	public AssetMaintenanceController(AssetMaintenanceService maintenanceService) {
		this.maintenanceService = maintenanceService;
	}

	public void createMaintenance(AssetMaintenance maintenance) {
		maintenanceService.createMaintenance(maintenance);
	}

	public AssetMaintenance getMaintenanceById(Long id) {
		return maintenanceService.getMaintenanceById(id);
	}

	public List<AssetMaintenance> getAllMaintenance() {
		return maintenanceService.getAllMaintenance();
	}

	public List<AssetMaintenance> getMaintenanceByAssetId(Long assetId) {
		return maintenanceService.getMaintenanceByAssetId(assetId);
	}

	public List<AssetMaintenance> getMaintenanceByStatus(AssetMaintenanceStatus status) {
		return maintenanceService.getMaintenanceByStatus(status);
	}

	public void updateMaintenanceStatus(Long id, AssetMaintenanceStatus status) {
		maintenanceService.updateMaintenanceStatus(id, status);
	}

	public List<AssetMaintenance> getMaintenanceByReporterId(Long userId) {
		return maintenanceService.getMaintenanceByReporterId(userId);
	}
}
