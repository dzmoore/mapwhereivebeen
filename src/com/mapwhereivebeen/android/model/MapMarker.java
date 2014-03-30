package com.mapwhereivebeen.android.model;

public class MapMarker {
	private Long id;
	private Integer version;
	private Double longitude;
	private Double latitude;
	private UserMap userMap;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public UserMap getUserMap() {
		return userMap;
	}
	public void setUserMap(UserMap userMap) {
		this.userMap = userMap;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MapMarker [id=");
		builder.append(id);
		builder.append(", version=");
		builder.append(version);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", latitude=");
		builder.append(latitude);
		builder.append(", userMap=");
		builder.append(userMap);
		builder.append("]");
		return builder.toString();
	}
	
	
}
