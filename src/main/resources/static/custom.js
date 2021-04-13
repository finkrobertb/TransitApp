let map;
function initMap() {
	map = new google.maps.Map(document.getElementById('map'), {
		//center: { lat: parseFloat(busLocations[0].LATITUDE), lng: parseFloat(busLocations[0].LONGITUDE) },
		center: { lat: parseFloat(personLocation.lat), lng: parseFloat(personLocation.lng) },
		zoom: 15,
		scrollwheel: false
	});

	// Bus image
	let image = {
		url: '/bus.png',
		scaledSize: new google.maps.Size(50, 50)
	};

	// Person image
	let image2 = {
		url: '/person.png',
		scaledSize: new google.maps.Size(50, 50)
	};

	// Show bus marker
	for (i = 0; i < busLocations.length; i++) {
		let marker = new google.maps.Marker({
			position: { lat: parseFloat(busLocations[i].LATITUDE), lng: parseFloat(busLocations[i].LONGITUDE) },
			map: map,
			icon: image,
			//animation: google.maps.Animation.BOUNCE
		});
	}

	// Person marker
	let personMarker = new google.maps.Marker({
		//position: { lat: parseFloat(busLocations[0].LATITUDE), lng: parseFloat(busLocations[0].LONGITUDE) },
		position: { lat: parseFloat(personLocation.lat), lng: parseFloat(personLocation.lng) },
		map: map,
		icon: image2,
		animation: google.maps.Animation.BOUNCE
	});

	// bus info
	var contentString = '<h2>' + bus.VEHICLE + '</h2>';

	var infowindow = new google.maps.InfoWindow({
		content: contentString
	});

	google.maps.event.addListener(marker, 'click', function() {
		infowindow.open(map, marker);
	});
}