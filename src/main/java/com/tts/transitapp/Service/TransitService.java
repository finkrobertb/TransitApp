package com.tts.transitapp.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tts.transitapp.Model.Bus;
import com.tts.transitapp.Model.BusComparator;
import com.tts.transitapp.Model.BusRequest;
import com.tts.transitapp.Model.DistanceResponse;
import com.tts.transitapp.Model.GeocodingResponse;
import com.tts.transitapp.Model.Location;

@Service
public class TransitService
{
    // Pulls variable from configuration file (application.properties)
    @Value("${transit_url}")
    public String transitUrl;

    @Value("${geocoding_url}")
    public String geocodingUrl;

    @Value("${distance_url}")
    public String distanceUrl;

    // Api key is stored in Run variable
    @Value("${google_api_key}")
    public String googleApiKey;

    // Queries MARTA to get all buses
    private List<Bus> getBuses()
    {
        // Rest Templates - Imports from springframework.web.client.RestTemplate
        // Rest Templates make web requests (particularly for APIs)
        RestTemplate restTemplate = new RestTemplate();
        // Visit transit URL and return results as object specified Bus
        // Pulls from JSON file
        Bus[] buses = restTemplate.getForObject(transitUrl, Bus[].class);
        return Arrays.asList(buses);
    }

    // Queries Google Geocoding API to get the lat and long of a place in GA
    private Location getCoordinates(String description)
    {
        // If description has spaces, it is changed to + for the url
        description = description.replace(" ", "+");
        // Add the url key information
        String url = geocodingUrl + description + "+GA&key=" + googleApiKey;
        // New resttemplate
        RestTemplate restTemplate = new RestTemplate();
        // visit site with url and geocoding response class
        GeocodingResponse response = restTemplate.getForObject(url, GeocodingResponse.class);
        // Return location from results array inside response
        return response.results.get(0).geometry.location;
    }

    // Queries Google Distance Matrix API to get the distance between two places
    private double getDistance(Location origin, Location destination)
    {
        // Changes URL to include distance, lat/lng, destinations, API key
        String url = distanceUrl + "origins=" + origin.lat + "," + origin.lng + "&destinations=" + destination.lat + "," + destination.lng + "&key=" + googleApiKey;
        // New rest temp
        RestTemplate restTemplate = new RestTemplate();
        // visit site with url and geocoding response class
        DistanceResponse response = restTemplate.getForObject(url, DistanceResponse.class);
        // go into distance response and get the first row's first element, distance
        // value (in meters) and multiply to convert distance into miles
        return response.rows.get(0).elements.get(0).distance.value * 0.000621371;
    }

    // Get all the nearby buses, given the location in request
    public List<Bus> getNearbyBuses(BusRequest request, Location outputLocation)
    {
        // Step 1. get all the buses
        List<Bus> allBuses = this.getBuses();

        // Step 2. Use the geocoding API to lookup the location (lat, lng) of teh
        // request
        Location personLocation = this.getCoordinates(request.address + " " + request.city);
        outputLocation.lat = personLocation.lat;
        outputLocation.lng = personLocation.lng;

        // Initialize nearbyBuses to empty ArrayList
        List<Bus> nearbyBuses = new ArrayList<>();

        // Step 3. Loop through all the buses to find nearby buses only and add them to
        // nearbybuses
        for(Bus bus : allBuses)
        {
            Location busLocation = new Location();
            busLocation.lat = bus.LATITUDE;
            busLocation.lng = bus.LONGITUDE;

            // we are going to perform a fuzzy distance comparison between each bus and user
            // to prefilter out buses that are clearly too far away
            double latDistance = Double.parseDouble(busLocation.lat) - Double.parseDouble(personLocation.lat);
            double lngDistance = Double.parseDouble(busLocation.lng) - Double.parseDouble(personLocation.lng);
            if(Math.abs(latDistance) <= 0.02 && Math.abs(lngDistance) <= 0.02)
            {
                double distance = getDistance(busLocation, personLocation);
                if(distance <= 1)
                {
                    bus.distance = (double) Math.round(distance * 100) / 100;
                    nearbyBuses.add(bus);
                }
            }
        }
        
        // Step 4. Sort collections
        Collections.sort(nearbyBuses, new BusComparator());
        return nearbyBuses;
        
    }
}
