package com.mapwhereivebeen.android.model;

import java.io.Serializable;

public class UserMap implements Serializable {
	private static final long serialVersionUID = 5635344741374986857L;
	
	private Long id;
	private String mapIdentifier;
	private Integer version;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMapIdentifier() {
		return mapIdentifier;
	}
	public void setMapIdentifier(String mapIdentifier) {
		this.mapIdentifier = mapIdentifier;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserMap [id=");
		builder.append(id);
		builder.append(", mapIdentifier=");
		builder.append(mapIdentifier);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}
	
	
}