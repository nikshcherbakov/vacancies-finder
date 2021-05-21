let googleMapsLat = $('#googleMapsLat');
let googleMapsLon = $('#googleMapsLong');

$('#googleMaps').locationpicker({
    location: {
        latitude: googleMapsLat.val(),
        longitude: googleMapsLon.val()
    },
    radius: 0,
    inputBinding: {
        latitudeInput: googleMapsLat,
        longitudeInput: googleMapsLon,
    },
    enableAutocomplete: true,
    onchanged: function (currentLocation, radius, isMarkerDropped) {
        // Uncomment line below to show alert on each Location Changed event
        //alert("Location changed. New location (" + currentLocation.latitude + ", " + currentLocation.longitude + ")");
    }
});
