package com.tts.transitapp.Model;

import java.util.Comparator;

public class BusComparator implements Comparator<Bus>
{
    // Compare bus o1 and bus o2; creates custom way to sort
    @Override
    public int compare(Bus o1, Bus o2)
    {
        if(o1.distance < o2.distance)
            return -1;
        if(o1.distance > o2.distance)
            return 1;
        return 0;
    }
}