function Main(mapContainerId, mapName, latLngCenter, zoom, enableAndroid) {
	this.mapContainerId = mapContainerId;
	this.mapName = mapName;
	this.latLngCenter = latLngCenter;
	this.zoom = zoom;
	this.enableAndroid = enableAndroid;

    this.currentCenter = this.latLngCenter;

}

Main.prototype.init = function() {
    this.map = L.mapbox.map(this.mapContainerId, this.mapName);
    this.map.setView(this.latLngCenter, this.zoom);
    
    MainActivityJavascriptInterface.loadMapMarkers();
} 

Main.prototype.addCenterCoordinatesToDatabase = function() {
	if (this.enableAndroid) {
        MainActivityJavascriptInterface.addCoordinatesToDatabase(this.currentCenter.lat, this.currentCenter.lng);
        
	} 
}

Main.prototype.addMapMarker = function(markerJson) {
	L.mapbox.featureLayer(markerJson).addTo(this.map);
}

Main.prototype.addMarkerCenter = function() {
	this.currentCenter = this.map.getCenter();
	
	thar = this;
	L.mapbox.featureLayer({
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
	}).addTo(this.map);
	
	this.addCenterCoordinatesToDatabase();
}
