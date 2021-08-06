package com.sayuriai.duka.utils;

import com.sayuriai.duka.models.Service;

import java.util.ArrayList;
import java.util.List;

public class ServicesList {

    List<Service> servicesList = new ArrayList<>();

    public List<Service> getServicesList() {
        servicesList.add(new Service("Photography", 1));
        servicesList.add(new Service("Videography", 2));
        servicesList.add(new Service("Tutoring", 3));
        servicesList.add(new Service("Proofreading essays", 4));
        servicesList.add(new Service("Shoe Laundry", 5));
        servicesList.add(new Service("Design", 6));
        servicesList.add(new Service("Visual artist", 7));
        servicesList.add(new Service("Makeup", 8));
        servicesList.add(new Service("Braiding", 9));
        servicesList.add(new Service("Manicure", 10));
        servicesList.add(new Service("Massage", 11));
        servicesList.add(new Service("Fitness instructor", 12));
        servicesList.add(new Service("DJ", 13));
        servicesList.add(new Service("Professional MC", 14));
        return servicesList;
    }
}
