
package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.Asset;
import com.erdal.helpdeskpro.enums.AssetStatus;

public interface AssetRepository {

    void save(Asset asset);

    Asset findById(Long id);

    Asset findByAssetTag(String assetTag);

    List<Asset> findAll();

    List<Asset> findAllByStatus(AssetStatus status);

    void deleteById(Long id);
}
