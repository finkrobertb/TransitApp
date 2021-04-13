package com.tts.transitapp.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.tts.transitapp.Model.Bus;
import com.tts.transitapp.Model.BusRequest;
import com.tts.transitapp.Model.Location;
import com.tts.transitapp.Service.TransitService;

@Controller
public class TransitController
{

    @Autowired
    private TransitService apiService;

    @GetMapping("/")
    public String redirectRoot()
    {
        return "redirect:/buses";
    }

    @GetMapping("/buses")
    public String getBusesPage(Model model)
    {
        model.addAttribute("request", new BusRequest());
        return "index";
    }

    @PostMapping("/buses")
    public String getNearbyBuses(BusRequest request, Model model)
    {
        Location location = new Location();
        List<Bus> buses = apiService.getNearbyBuses(request, location);
        model.addAttribute("buses", buses);
        model.addAttribute("request", request);
        model.addAttribute("personLocation", location);
        return "index";
    }

    // @PostMapping("/buses")
    // public String getNearbyBuses(BusRequest request, Model model)
    // {
    // List<Bus> buses = apiService.getNearbyBuses(request);
    // model.addAttribute("buses", buses);
    // model.addAttribute("request", request);
    // return "index";
    // }

}