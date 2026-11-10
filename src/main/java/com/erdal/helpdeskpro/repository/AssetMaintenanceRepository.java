
package com.erdal.helpdeskpro.repository;

import com.erdal.helpdeskpro.domain.AssetMaintenance;
import com.erdal.helpdeskpro.enums.AssetMaintenanceStatus;

import java.util.List;

public interface AssetMaintenanceRepository {

    void save(AssetMaintenance maintenance);

    AssetMaintenance findById(Long id);

    List<AssetMaintenance> findAll();

    List<AssetMaintenance> findAllByAssetId(Long assetId);

    List<AssetMaintenance> findAllByStatus(
            AssetMaintenanceStatus status
    );

    // Çalışanın kendi arıza ve bakım kayıtları.
    List<AssetMaintenance> findAllByReporterId(Long userId);

    void updateStatus(Long id, AssetMaintenanceStatus status);
}
