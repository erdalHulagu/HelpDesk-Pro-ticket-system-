package com.erdal.helpdeskpro.controller;

import java.util.List;

import com.erdal.helpdeskpro.domain.response.MyAssetResponse;
import com.erdal.helpdeskpro.service.MyAssetsService;

public class MyAssetsController {

    private final MyAssetsService myAssetsService;

    public MyAssetsController(MyAssetsService myAssetsService) {
        this.myAssetsService = myAssetsService;
    }

    public List<MyAssetResponse> getMyAssets(Long userId) {
        return myAssetsService.getMyAssets(userId);
    }
}