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
    enableAutocomplete: true
});
