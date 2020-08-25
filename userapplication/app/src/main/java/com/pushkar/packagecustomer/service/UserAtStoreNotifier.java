package com.pushkar.packagecustomer.service;

import com.estimote.coresdk.recognition.packets.Beacon;
import com.pushkar.packagecustomer.model.service.UserAtStoreRequest;
import com.pushkar.packagecustomer.repository.HomeRepository;
import com.pushkar.packagecustomer.utils.Validate;

import java.util.List;

public class UserAtStoreNotifier {
    private HomeRepository homeRepository = HomeRepository.getHomeRepository();

    void notifyUserAtStore(List<Beacon> beacons, String email, String uuid){
        com.pushkar.packagecustomer.model.Beacon beacon = getFoundBeacon(beacons,uuid);
        UserAtStoreRequest request = new UserAtStoreRequest(email,beacon);
        homeRepository.notifyAdminUserAtStore(request);
    }
    private com.pushkar.packagecustomer.model.Beacon getFoundBeacon(List<Beacon> beacons, String uuid){
        Beacon closestBeaconDetected = getClosestBeacon(beacons);
        String major = String.valueOf(closestBeaconDetected.getMajor());
        String minor = String.valueOf(closestBeaconDetected.getMinor());
        return new com.pushkar.packagecustomer.model.Beacon(uuid,major,minor);
    }

    private Beacon getClosestBeacon(List<Beacon> beacons){
        Beacon closestBeacon = beacons.get(0);
        for (Beacon beacon: beacons) {
            if(Validate.getDistanceToBeacon(beacon)<Validate.getDistanceToBeacon(closestBeacon)){
                closestBeacon = beacon;
            }
        }
        return closestBeacon;
    }
}
