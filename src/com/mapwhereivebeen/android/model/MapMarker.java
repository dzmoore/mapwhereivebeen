package com.mapwhereivebeen.android.model;

import java.io.Serializable;

public class MapMarker implements Serializable {
	private static final long serialVersionUID = 2238756755756270368L;
	
	private Long id;
	private Integer version;
	private Double longitude;
	private Double latitude;
	private UserMap userMap;
	private String title;
	private String description;
	private boolean active;
	
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
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
		builder.append(", title=");
		builder.append(title);
		builder.append(", description=");
		builder.append(description);
		builder.append(", active=");
		builder.append(active);
		builder.append("]");
		return builder.toString();
	}
	
}
