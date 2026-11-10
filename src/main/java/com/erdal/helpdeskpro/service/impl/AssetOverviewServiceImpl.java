package com.erdal.helpdeskpro.service.impl;

import com.erdal.helpdeskpro.domain.dtos.AssetOverviewDTO;
import com.erdal.helpdeskpro.repository.AssetOverviewRepository;
import com.erdal.helpdeskpro.service.AssetOverviewService;

public class AssetOverviewServiceImpl implements AssetOverviewService {

    private final AssetOverviewRepository assetOverviewRepository;

    public AssetOverviewServiceImpl(
            AssetOverviewRepository assetOverviewRepository) {
        this.assetOverviewRepository = assetOverviewRepository;
    }

    @Override
    public AssetOverviewDTO getOverview() {
        return assetOverviewRepository.getOverview();
    }
}