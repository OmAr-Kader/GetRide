//
//  LocationManager.swift
//  iosApp
//
//  Created by OmAr on 14/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import CoreLocation

class LocationManager: NSObject, CLLocationManagerDelegate {
    
    private var locationCallback: ((CLLocationCoordinate2D) -> Unit)? = nil
    private var invoke: (() -> Unit)? = nil
    private var failed: (() -> Unit)? = nil

    private let locationManager = CLLocationManager()
    

    
    override init() {
        super.init()
        self.locationManager.delegate = self
        
        // Request user authorization
        self.locationManager.requestWhenInUseAuthorization()
        
        // Set desired accuracy (e.g., best accuracy)
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        
        // Set distance filter (e.g., 50 meters)
        self.locationManager.distanceFilter = 50 // in meters
        
    }


    private func stopLocationTracking() {
        locationManager.stopUpdatingLocation()
    }
    
    func startLocationTracking(locationCallback: @escaping (CLLocationCoordinate2D) -> Unit) {
        self.locationCallback = locationCallback
        locationManager.startUpdatingLocation()
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let location = locations.last {
            self.locationCallback?(location.coordinate)
        }
    }
    
    // Handle any location error
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Failed to get location: \(error.localizedDescription)")
    }
    
    // Check if location services are enabled on the device
    @BackgroundActor
    func checkLocationStates(invoke: () -> Unit, failed: () -> Unit) {
        if CLLocationManager.locationServicesEnabled() {
            invoke()
        } else {
            failed()
            print("Location services are off. Please enable them in settings.")
        }
    }


    // Check the app's location authorization status
    func checkLocationPermission(invoke: @escaping () -> Unit, failed: @escaping () -> Unit) {
        let status = locationManager.authorizationStatus
        switch status {
        case .notDetermined:
            self.invoke = invoke
            self.failed = failed
            locationManager.requestWhenInUseAuthorization()
        case .denied:
            self.invoke = invoke
            self.failed = failed
            openAppSettings()
        case .authorizedWhenInUse, .authorizedAlways:
            invoke()
        default:
            break
        }
    }


    // CLLocationManagerDelegate method
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        switch status {
        case .authorizedWhenInUse, .authorizedAlways:
            invoke?()
        default:
            failed?()
        }
    }
    
    func openAppSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            if UIApplication.shared.canOpenURL(url) {
                UIApplication.shared.open(url)
            }
        }
    }
    
    deinit {
        self.locationCallback = nil
        stopLocationTracking()
        invoke = nil
        failed = nil
    }
}
