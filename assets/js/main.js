var map = L.mapbox.map('map', 'dzmoore.hh7ibp3a');

map.setView([40, -74.50], 9)
MainActivityJavascriptInterface.updateCenter(40, -74.50);

map.on('click', function(e) {
    var centerLatLong = map.getCenter();
    MainActivityJavascriptInterface.updateCenter(centerLatLong.lat, centerLatLong.lng);
    //alert('lat: ' + centerLatLong.lat + "; lng: " + centerLatLong.lng);
});
