package com.erdal.helpdeskpro.service;

import java.util.List;

import com.erdal.helpdeskpro.domain.response.MyAssetResponse;

public interface MyAssetsService {

    List<MyAssetResponse> getMyAssets(Long userId);
}