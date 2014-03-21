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
    
    thar = this;
    theMap = this.map;
    this.map.on('mousedown', function(e) {
    	thar.currentCenter = theMap.getCenter();
    	
    	thar.updateCenterAndroid();
    });
} 

Main.prototype.updateCenterAndroid = function() {
	if (this.enableAndroid) {
        MainActivityJavascriptInterface.updateCenter(this.currentCenter.lat, this.currentCenter.lng);
	} else {
		print_out('current loc: ' + this.currentCenter);
	}
}

Main.prototype.setMarkerCenter = function() {
	L.marker(this.currentCenter).addTo(this.map);
}

function print_out(txt) {
    $('#p-output').text(txt);
} 