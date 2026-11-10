
package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.enums.AssetStatus;
import com.erdal.helpdeskpro.repository.AssetRepository;
import com.erdal.helpdeskpro.service.AssetService;

public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    public AssetServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public void save(Asset asset) {
        assetRepository.save(asset);
    }

    @Override
    public Asset findById(Long id) {
        return assetRepository.findById(id);
    }

    @Override
    public Asset findByAssetTag(String assetTag) {
        return assetRepository.findByAssetTag(assetTag);
    }

    @Override
    public List<Asset> findAll() {
        return assetRepository.findAll();
    }

    @Override
    public List<Asset> findAllByStatus(AssetStatus status) {
        return assetRepository.findAllByStatus(status);
    }

    @Override
    public void deleteById(Long id) {
        assetRepository.deleteById(id);
    }
}
