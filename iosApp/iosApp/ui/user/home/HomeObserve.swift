//
//  HomeObserve.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class HomeObserve : ObservableObject {
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    
    private func setIsProcess(_ isProcess: Bool) {
        scope.launchMain {
            self.state = self.state.copy(isProcess: isProcess)
        }
    }
    
    @MainActor func setMainProcess(_ isProcess: Bool) {
        self.state = self.state.copy(isProcess: isProcess)
    }
    
    struct State {
        
        private(set) var mapData: MapData = MapData()
        private(set) var duration: Long? = nil
        private(set) var durationText: String? = nil
        private(set) var distance: Long? = nil
        private(set) var distanceText: String = ""
        private(set) var fare: Double = 0.0
        private(set) var fromText: String = ""
        private(set) var toText: String = ""
        private(set) var locationsFrom: [Location] = []
        private(set) var locationsTo: [Location] = []
        private(set) var rideRequest: RideRequest? = nil
        private(set) var ride: Ride? = nil
        private(set) var isProcess: Bool = true
        
        @MainActor
        mutating func copy(
            mapData: MapData? = nil,
            duration: Long?? = nil,
            durationText: String? = nil,
            distance: Long? = nil,
            distanceText: String? = nil,
            fare: Double? = nil,
            fromText: String? = nil,
            toText: String? = nil,
            locationsFrom: [Location]? = nil,
            locationsTo: [Location]? = nil,
            rideRequest: RideRequest? = nil,
            ride: Ride? = nil,
            isProcess: Bool? = nil
        ) -> Self {
            self.mapData = mapData ?? self.mapData
            self.duration = duration ?? self.duration
            self.durationText = durationText ?? self.durationText
            self.distance = distance ?? self.distance
            self.distanceText = distanceText ?? self.distanceText
            self.fare = fare ?? self.fare
            self.fromText = fromText ?? self.fromText
            self.toText = toText ?? self.toText
            self.locationsFrom = locationsFrom ?? self.locationsFrom
            self.locationsTo = locationsTo ?? self.locationsTo
            self.rideRequest = rideRequest ?? self.rideRequest
            self.ride = ride ?? self.ride
            self.isProcess = isProcess ?? self.isProcess
            return self
        }
    }

}

/*import GoogleMaps
func asa() {
    
    if let path = GMSPath(fromEncodedPath: "") {
        let polyline = GMSPolyline(path: path)
        polyline.strokeColor = .blue
        polyline.strokeWidth = 5.0
        polyline.map = mapView // Add polyline to map
    }
}*/
