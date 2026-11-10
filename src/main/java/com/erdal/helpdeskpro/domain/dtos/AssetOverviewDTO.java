package com.erdal.helpdeskpro.domain.dtos;

public class AssetOverviewDTO {

	private long totalAssets;
	private long assignedAssets;
	private long availableAssets;
	private long maintenanceAssets;
	private long retiredAssets;
	private long lostAssets;

	public AssetOverviewDTO() {
	}

	public AssetOverviewDTO(long totalAssets, long assignedAssets, long availableAssets, long maintenanceAssets,
			long retiredAssets, long lostAssets) {

		this.totalAssets = totalAssets;
		this.assignedAssets = assignedAssets;
		this.availableAssets = availableAssets;
		this.maintenanceAssets = maintenanceAssets;
		this.retiredAssets = retiredAssets;
		this.lostAssets = lostAssets;
	}

	public long getTotalAssets() {
		return totalAssets;
	}

	public void setTotalAssets(long totalAssets) {
		this.totalAssets = totalAssets;
	}

	public long getAssignedAssets() {
		return assignedAssets;
	}

	public void setAssignedAssets(long assignedAssets) {
		this.assignedAssets = assignedAssets;
	}

	public long getAvailableAssets() {
		return availableAssets;
	}

	public void setAvailableAssets(long availableAssets) {
		this.availableAssets = availableAssets;
	}

	public long getMaintenanceAssets() {
		return maintenanceAssets;
	}

	public void setMaintenanceAssets(long maintenanceAssets) {
		this.maintenanceAssets = maintenanceAssets;
	}

	public long getRetiredAssets() {
		return retiredAssets;
	}

	public void setRetiredAssets(long retiredAssets) {
		this.retiredAssets = retiredAssets;
	}

	public long getLostAssets() {
		return lostAssets;
	}

	public void setLostAssets(long lostAssets) {
		this.lostAssets = lostAssets;
	}
}