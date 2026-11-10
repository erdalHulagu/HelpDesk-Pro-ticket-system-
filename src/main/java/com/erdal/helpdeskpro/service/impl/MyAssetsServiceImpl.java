package com.erdal.helpdeskpro.service.impl;

import java.util.List;

import com.erdal.helpdeskpro.domain.response.MyAssetResponse;
import com.erdal.helpdeskpro.repository.MyAssetsRepository;
import com.erdal.helpdeskpro.service.MyAssetsService;

public class MyAssetsServiceImpl implements MyAssetsService {

    private final MyAssetsRepository myAssetsRepository;

    public MyAssetsServiceImpl(
            MyAssetsRepository myAssetsRepository) {

        this.myAssetsRepository = myAssetsRepository;
    }

    @Override
    public List<MyAssetResponse> getMyAssets(Long userId) {

        return myAssetsRepository.findMyAssets(userId);
    }
}