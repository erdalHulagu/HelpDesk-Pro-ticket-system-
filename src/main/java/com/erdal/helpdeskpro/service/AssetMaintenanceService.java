
package com.erdal.helpdeskpro.service;

import com.erdal.helpdeskpro.domain.AssetMaintenance;
import com.erdal.helpdeskpro.enums.AssetMaintenanceStatus;

import java.util.List;

public interface AssetMaintenanceService {

    void createMaintenance(AssetMaintenance maintenance);

    AssetMaintenance getMaintenanceById(Long id);

    List<AssetMaintenance> getAllMaintenance();

    List<AssetMaintenance> getMaintenanceByAssetId(Long assetId);

    List<AssetMaintenance> getMaintenanceByStatus(
            AssetMaintenanceStatus status
    );

    void updateMaintenanceStatus(
            Long id,
            AssetMaintenanceStatus status
    );
    
    List<AssetMaintenance> getMaintenanceByReporterId(Long userId);
}
