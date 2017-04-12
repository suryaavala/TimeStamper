package com.sardox.timestamper.Managers;


import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.PhysicalLocation;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    public DataManager() {
    }

    public List<Timestamp> readTimestamps(){
        return sampleTimestamps();
    }

    public void writeTimestamps(){
    }

    public List<Category> readCategories(){
        return sampleCategories();
    }

    public void writeCategories(){
    }

    public AppSettings readSettings(){
        return null;
    }

    public void writeSettings(AppSettings appSettings){
    }

    public boolean noPreviousData(){
        return  true;
    }

    private List<Timestamp> sampleTimestamps(){
        List<Timestamp> sample = new ArrayList<>();
        JetTimestamp now = JetTimestamp.now();

        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000*60*5),  PhysicalLocation.Default, 0, "Default 1"));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000*60*4),  PhysicalLocation.Default, 0, "Default 2"));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000*60*7),  PhysicalLocation.Default, 1, "Sport 3"));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000*60*8),  PhysicalLocation.Default, 1, "Sport 5"));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000*60*6),  PhysicalLocation.Default, 2, "Baby 5"));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000*60*10), PhysicalLocation.Default, 2, "Baby 6 "));
        return sample;
    }

    private List<Category> sampleCategories(){
        List<Category> sample = new ArrayList<>();
        sample.add(new Category("Default", 0));
        sample.add(new Category("Sport", 1));
        sample.add(new Category("Baby", 2));
        return sample;
    }
}
