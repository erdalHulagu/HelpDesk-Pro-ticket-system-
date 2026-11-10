
package com.erdal.helpdeskpro.controller;

import java.util.List;

import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.enums.AssetStatus;
import com.erdal.helpdeskpro.service.AssetService;

public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    public void createAsset(Asset asset) {
        assetService.save(asset);
    }

    public Asset getAssetById(Long id) {
        return assetService.findById(id);
    }

    public Asset getAssetByTag(String assetTag) {
        return assetService.findByAssetTag(assetTag);
    }

    public List<Asset> getAllAssets() {
        return assetService.findAll();
    }

    public List<Asset> getAssetsByStatus(AssetStatus status) {
        return assetService.findAllByStatus(status);
    }

    public void updateAsset(Asset asset) {
        assetService.save(asset);
    }

    public void retireAsset(Long id) {
        assetService.deleteById(id);
    }
}
