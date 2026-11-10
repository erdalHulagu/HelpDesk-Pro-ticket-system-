package com.erdal.helpdeskpro.repository;

import java.util.List;

import com.erdal.helpdeskpro.domain.response.MyAssetResponse;

public interface MyAssetsRepository {

    List<MyAssetResponse> findMyAssets(Long userId);
}