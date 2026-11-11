package com.erdal.helpdeskpro.domain.response;

import java.time.LocalDateTime;

import com.erdal.helpdeskpro.domain.request.AssetRequest;

public class AssetRequestResponse {

    private Long id;
    private UserInfo user;
    private String category;
    private String description;
    private String status;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AssetInfo asset;

    public static AssetRequestResponse fromEntity(AssetRequest request) {
        AssetRequestResponse response = new AssetRequestResponse();

        response.id = request.getId();
        response.category = request.getCategory().name();
        response.description = request.getDescription();
        response.status = request.getStatus().name();
        response.reviewedAt = request.getReviewedAt();
        response.createdAt = request.getCreatedAt();
        response.updatedAt = request.getUpdatedAt();

        if (request.getUser() != null) {
            UserInfo userInfo = new UserInfo();
            userInfo.id = request.getUser().getId();
            userInfo.fullName = request.getUser().getUsername();
            userInfo.email = request.getUser().getEmail();
            response.user = userInfo;
        }

        if (request.getAsset() != null) {
            AssetInfo assetInfo = new AssetInfo();
            assetInfo.id = request.getAsset().getId();
            assetInfo.assetTag = request.getAsset().getAssetTag();
            assetInfo.name = request.getAsset().getName();
            response.asset = assetInfo;
        }

        return response;
    }

    public static class UserInfo {
        private Long id;
        private String fullName;
        private String email;

        public Long getId() { return id; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
    }

    public static class AssetInfo {
        private Long id;
        private String assetTag;
        private String name;

        public Long getId() { return id; }
        public String getAssetTag() { return assetTag; }
        public String getName() { return name; }
    }

    public Long getId() { return id; }
    public UserInfo getUser() { return user; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public AssetInfo getAsset() { return asset; }
}
