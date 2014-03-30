package com.mapwhereivebeen.android.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class FeatureLayerMarker {
/*
 * 
	    // this feature is in the GeoJSON format: see geojson.org
	    // for the full specification
	    type: 'Feature',
	    geometry: {
	        type: 'Point',
	        // coordinates here are in longitude, latitude order because
	        // x, y is the standard for GeoJSON and many formats
	        coordinates: [thar.currentCenter.lng, thar.currentCenter.lat]
	    },
	    properties: {
	        title: 'marker',
	        description: 'description',
	        // one can customize markers by adding simplestyle properties
	        // http://mapbox.com/developers/simplestyle/
	        'marker-size': 'large',
	        'marker-color': '#f0a'
	    }
	}
 */
	public static class Geometry {
		private String type;
		private double[] coordinates;
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public double[] getCoordinates() {
			return coordinates;
		}

		public void setCoordinates(double[] coordinates) {
			this.coordinates = coordinates;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Geometry [type=");
			builder.append(type);
			builder.append(", coordinates=");
			builder.append(coordinates);
			builder.append("]");
			return builder.toString();
		}
	}
	
	private String type;
	private Geometry geometry;
	private Map<String, String> properties;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	@Override
	public String toString() {
		final int maxLen = 10;
		StringBuilder builder = new StringBuilder();
		builder.append("FeatureLayerMarker [type=");
		builder.append(type);
		builder.append(", geometry=");
		builder.append(geometry);
		builder.append(", properties=");
		builder.append(properties != null ? toString(properties.entrySet(),
				maxLen) : null);
		builder.append("]");
		return builder.toString();
	}
	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
	
	
}
