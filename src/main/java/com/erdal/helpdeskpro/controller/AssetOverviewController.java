package com.erdal.helpdeskpro.controller;

import com.erdal.helpdeskpro.domain.dtos.AssetOverviewDTO;
import com.erdal.helpdeskpro.service.AssetOverviewService;

public class AssetOverviewController {

    private final AssetOverviewService assetOverviewService;

    public AssetOverviewController(AssetOverviewService assetOverviewService) {
        this.assetOverviewService = assetOverviewService;
    }

    public AssetOverviewDTO getOverview() {
        return assetOverviewService.getOverview();
    }
}