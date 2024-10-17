//
//  HomeObserve.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared
import GoogleMaps

class HomeObserve : ObservableObject {
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    private var jobRideRequest: Task<Void, Error>? = nil
    private var jobRideInitial: Task<Void, Error>? = nil
    private var jobRide: Task<Void, Error>? = nil
    
    @MainActor
    func checkForActiveRide(userId: Long, invoke: @escaping @MainActor () -> Unit, refreshScope: @escaping @MainActor (MapData) -> Unit) {
        jobRideInitial?.cancel()
        jobRideInitial = scope.launchBack {
            do {
                try await self.project.ride.getActiveRideForUser(userId: userId) { ride in
                    if self.jobRideInitial == nil {
                        return
                    }
                    self.scope.launchMain {
                        if self.state.ride == nil && ride != nil {
                            invoke()
                        }
                        if (ride?.status == -2) {
                            if self.jobRideInitial != nil {
                                self.scope.launchBack {
                                    try? await self.project.ride.cancelRideRealTime()
                                }
                                self.jobRideInitial?.cancel()
                                self.jobRideInitial = nil
                            }
                            var mapData = self.state.mapData
                            self.state = self.state.copy { currentState in
                                currentState.mapData = mapData.copy { it in
                                    it.driverPoint = nil
                                    return it
                                }
                                currentState.ride = nil
                                currentState.isProcess = false
                                return currentState
                            }
                        } else {
                            if let ride, let routePoints = self.state.mapData.routePoints, routePoints.isEmpty {
                                self.fetchRoute(start: ride.from.toGoogleLatLng(), end: ride.to.toGoogleLatLng(), invoke: refreshScope)
                            }
                            var mapData = self.state.mapData
                            self.state = self.state.copy { currentState in
                                currentState.mapData = mapData.copy { it in
                                    it.driverPoint = ride?.currentDriver.toGoogleLatLng()
                                    it.startPoint = ride?.from.toGoogleLatLng()
                                    it.fromText = ride?.from.title ?? ""
                                    it.endPoint = ride?.to.toGoogleLatLng()
                                    it.toText = ride?.to.title ?? ""
                                    return it
                                }
                                currentState.ride = ride
                                currentState.isProcess = false
                                return currentState
                            }
                        }
                    }
                }
            } catch {
                self.setIsProcess(false)
            }
        }
    }
    
    @MainActor
    func cancelRideFromUser(ride: Ride) {
        scope.launchBack {
            let _ = try? await self.project.ride.editRide(item: ride.copy(status: -2))
        }
    }
    
    private func fetchRoute(start: CLLocationCoordinate2D, end: CLLocationCoordinate2D, invoke: @escaping @MainActor (MapData) -> Unit) {
        setIsProcess(true)
        scope.launchBack {
            do {
                try await RideRouteKt.fetchAndDecodeRoute(
                    start: GoogleLocation(lat: start.latitude, lng: start.longitude),
                    end: GoogleLocation(lat: end.latitude, lng: end.longitude)
                ) { routePoints, duration, distance in
                    if let routes = routePoints {
                        let durationText = duration != nil ? ConverterKt.convertSecondsToHoursMinutes(duration!.int64Value) : ""
                        let distanceText = duration != nil ? ConverterKt.convertMetersToKmAndMeters(distance!.int64Value) : ""
                        let durationDistance = (distanceText.isEmpty ? "" : durationText + " (\(distanceText))")
                        self.scope.launchMain {
                            let newMapData = self.state.mapData.copy(
                                durationDistance: durationDistance,
                                routePoints: routes
                            )
                            invoke(newMapData)
                            self.state = self.state.copy { currentState in
                                currentState.mapData = newMapData
                                currentState.duration = duration?.int64Value
                                currentState.durationText = durationText
                                currentState.distance = distance?.int64Value
                                currentState.distanceText = distanceText
                                currentState.fare = ConverterKt.calculateFare(duration: duration, distance: distance)
                                currentState.isProcess = false
                                return currentState
                            }
                        }
                    } else {
                        self.setIsProcess(false)
                    }
                }
            } catch {
                self.setIsProcess(false)
            }
        }
    }
    
    @MainActor
    func searchForLocationOfPlaceFrom(fromText: String) {
        scope.launchBack {
            if let it = try? await RideRouteKt.searchForPlaceLocation(placeName: fromText) {
                self.scope.launchMain {
                    self.state = self.state.copy(locationsFrom: it, locationsTo: [])
                }
            }
        }
    }
    
    @MainActor
    func searchForLocationOfPlaceTo(toText: String) {
        scope.launchBack {
            if let it = try? await RideRouteKt.searchForPlaceLocation(placeName: toText) {
                self.scope.launchMain {
                    self.state = self.state.copy(locationsFrom: [], locationsTo: it)
                }
            }
        }
    }
    
    @MainActor
    func setMapMarks(latLng: CLLocationCoordinate2D, invoke: @escaping @MainActor (MapData) -> Unit) {
        let startPoint: CLLocationCoordinate2D?
        let endPoint: CLLocationCoordinate2D?
        if state.mapData.startPoint == nil {
            startPoint = latLng
            endPoint = nil
        } else {
            startPoint = nil
            endPoint = latLng
        }
        self.checkForPlacesNames(startPoint: startPoint, endPoint: endPoint)
        self.checkForRoutes(startPoint: startPoint, endPoint: endPoint, invoke: invoke)
        var newMapData = self.state.mapData
        self.state = self.state.copy(
            mapData: newMapData.copy { it in
                it.startPoint = startPoint ?? state.mapData.startPoint
                it.endPoint = endPoint ?? state.mapData.endPoint
                return it
            }
        )
    }
    
    @MainActor
    private func checkForRoutes(startPoint: CLLocationCoordinate2D?, endPoint: CLLocationCoordinate2D?, invoke: @escaping @MainActor (MapData) -> Unit) {
        guard let startPoint = (startPoint ?? state.mapData.startPoint) else { return }
        guard let endPoint = (endPoint ?? state.mapData.endPoint) else { return }
        fetchRoute(start: startPoint, end: endPoint, invoke: invoke)
    }
    
    
    @MainActor
    private func checkForPlacesNames(startPoint: CLLocationCoordinate2D?, endPoint: CLLocationCoordinate2D?) {
        if let startPoint {
            scope.launchBack {
                if let name = try? await RideRouteKt.fetchPlaceName(location: GoogleLocation(lat: startPoint.latitude, lng: startPoint.longitude)) {
                    self.scope.launchMain {
                        self.state = self.state.copy(mapData: self.state.mapData.copy(fromText: name), fromText: name)
                    }
                }
            }
        }
        if let endPoint {
            scope.launchBack {
                if let name = try? await RideRouteKt.fetchPlaceName(location: GoogleLocation(lat: endPoint.latitude, lng: endPoint.longitude)) {
                    self.scope.launchMain {
                        self.state = self.state.copy(mapData: self.state.mapData.copy(toText: name), toText: name)
                    }
                }
            }
        }
    }
    
    @MainActor
    func setFromText(fromText: String) {
        self.state = self.state.copy(fromText: fromText)
    }
    
    @MainActor
    func setFrom(from: Location, invoke: @escaping @MainActor (MapData) -> Unit) {
        let startPoint = CLLocationCoordinate2D(latitude: from.latitude, longitude: from.longitude)
        self.checkForRoutes(startPoint: startPoint, endPoint: nil, invoke: invoke)
        self.state = self.state.copy(
            mapData: state.mapData.copy(
                startPoint: startPoint,
                fromText: from.title
            ),
            fromText: from.title,
            locationsFrom: []
        )
    }
    
    @MainActor
    func clearFromList() {
        self.state = self.state.copy(locationsFrom: [])
    }
    
    @MainActor
    func clearFromText() {
        var mapData = self.state.mapData
        self.state = self.state.copy { currentState in
            currentState.mapData = mapData.copy { it in
                it.startPoint = nil
                it.fromText = ""
                it.durationDistance = ""
                it.routePoints = nil
                return it
            }
            currentState.duration = nil
            currentState.durationText = ""
            currentState.distance = nil
            currentState.distanceText = ""
            currentState.fare = 0.0
            currentState.fromText = ""
            return currentState
        }
    }
    
    @MainActor
    func setToText(toText: String) {
        self.state = self.state.copy(toText: toText)
    }
    
    @MainActor
    func setTo(to: Location, invoke: @escaping @MainActor (MapData) -> Unit) {
        let endPoint = CLLocationCoordinate2D(latitude: to.latitude, longitude: to.longitude)
        checkForRoutes(startPoint: nil, endPoint: endPoint, invoke: invoke)
        self.state = self.state.copy(
            mapData: state.mapData.copy(
                endPoint: endPoint,
                toText: to.title
            ),
            toText: to.title,
            locationsTo: []
        )
    }
    
    @MainActor
    func clearToList() {
        self.state = self.state.copy(locationsTo: [])
    }
    
    @MainActor
    func clearToText() {
        var mapData = self.state.mapData
        self.state = self.state.copy { currentState in
            currentState.mapData = mapData.copy { it in
                it.endPoint = nil
                it.toText = ""
                it.durationDistance = ""
                it.routePoints = nil
                return it
            }
            currentState.duration = nil
            currentState.durationText = ""
            currentState.distance = nil
            currentState.distanceText = ""
            currentState.fare = 0.0
            currentState.toText = ""
            return currentState
        }
    }
    
    @MainActor
    func updateCurrentLocation(currentLocation: CLLocationCoordinate2D, update: @MainActor () -> Unit) {
        let it = self.state.mapData
        if !it.isCurrentAlmostSameArea(newCurrent: currentLocation) {
            update()
            updateCurrentLocationPref(currentLocation: currentLocation)
            self.state = self.state.copy(mapData: state.mapData.copy(currentLocation: currentLocation))
        }
    }
    
    @MainActor
    private func updateCurrentLocationPref(currentLocation: CLLocationCoordinate2D) {
        self.scope.launchBack {
            let _ = try? await self.project.pref.updatePref(
                pref: [
                    PreferenceData(keyString: ConstKt.PREF_LAST_LATITUDE, value: String(currentLocation.latitude)),
                    PreferenceData(keyString: ConstKt.PREF_LAST_LONGITUDE, value: String(currentLocation.longitude))
                ]
            )
        }
    }
    
    @MainActor
    func submitFeedback(driverId: Long, rate: Float) {
        self.clearRide()
        self.scope.launchBack {
            let _ = try? await self.project.driver.addEditDriverRate(driverId: driverId, rate: rate)
        }
    }
    
    @MainActor
    func clearRide() {
        if jobRide != nil || jobRideInitial != nil {
            self.scope.launchBack {
                try? await self.project.ride.cancelRideRealTime()
            }
            jobRide?.cancel()
            jobRideInitial?.cancel()
            jobRide = nil
            jobRideInitial = nil
        }
        var mapData = self.state.mapData
        self.state = self.state.copy { currentState in
            currentState.mapData = mapData.copy { it in
                it.driverPoint = nil
                it.startPoint = nil
                it.fromText = ""
                it.endPoint = nil
                it.toText = ""
                it.durationDistance = ""
                it.routePoints = nil
                return it
            }
            currentState.duration = nil
            currentState.durationText = ""
            currentState.distance = nil
            currentState.distanceText = ""
            currentState.fare = 0.0
            currentState.toText = ""
            currentState.ride = nil
            currentState.isProcess = false
            return currentState
        }
    }
    
    @MainActor
    func pushRideRequest(userId: Long) {
        setIsProcess(true)
        let state = state
        let mapData = state.mapData
        scope.launchBack {
            guard let from = mapData.startPoint else { return }
            guard let to = mapData.endPoint else { return }
            let it = RideRequest().copy(
                id: 0,
                userId: userId,
                from: Location(latitude: from.latitude, longitude: from.longitude, title: ""),
                to: Location(latitude: to.latitude, longitude: to.longitude, title: ""),
                durationDistance: mapData.durationDistance,
                fare: state.fare,
                date: DateKt.dateNow
            )
            if let rideRequest = try? await self.project.ride.addNewRideRequest(item: it) {
                self.fetchRequestLive(rideRequestId: rideRequest.id)
            } else {
                self.setIsProcess(false)
            }
        }
    }
    
    @MainActor
    func cancelRideRequest(rideRequest: RideRequest) {
        if jobRideRequest != nil {
            self.scope.launchBack {
                try? await self.project.ride.cancelRideRequestRealTime()
            }
            jobRideRequest?.cancel()
            jobRideRequest = nil
        }
        self.state = self.state.copy { currentState in
            currentState.rideRequest = nil
            currentState.isProcess = false
            return currentState
        }
        scope.launchBack {
            let _ = try? await self.project.ride.deleteRideRequest(id: rideRequest.id)
        }
    }
    
    func acceptProposal(userId: Long, rideRequest: RideRequest, proposal: RideProposal, invoke: @escaping @MainActor () -> Unit, failed: @escaping @MainActor () -> Unit) {
        setIsProcess(true)
        scope.launchBack {
            if let ride = try? await self.project.ride.addNewRide(
                item: Ride().copy(
                    userId: userId,
                    driverId: proposal.driverId,
                    from: rideRequest.from,
                    to: rideRequest.to,
                    currentDriver: proposal.currentDriver,
                    fare: proposal.fare,
                    status: 0,
                    date: DateKt.dateNow,
                    durationDistance: rideRequest.durationDistance,
                    driverName: proposal.driverName
                )
            ) {
                if let _ = try? await self.project.ride.editRideRequest(item: rideRequest.copy(chosenDriver: proposal.driverId, chosenRide: ride.id)) {
                    self.fetchRide(rideId: ride.id, invoke: invoke)
                }
            } else {
                self.scope.launchMain {
                    self.setMainProcess(false)
                    failed()
                }
            }
        }
    }
    
    private func fetchRequestLive(rideRequestId: Long) {
        jobRideRequest?.cancel()
        jobRideRequest = scope.launchBack {
            do {
                try await self.project.ride.getRideRequestById(rideRequestId: rideRequestId) { rideRequest in
                    if self.jobRideRequest == nil {
                        return
                    }
                    self.scope.launchMain {
                        self.state = self.state.copy { currentState in
                            currentState.rideRequest = rideRequest
                            currentState.isProcess = false
                            return currentState
                        }
                    }
                }
            } catch {
                self.setIsProcess(false)
            }
        }
    }
    
    private func fetchRide(rideId: Long, invoke: @escaping @MainActor () -> Unit) {
        jobRide?.cancel()
        jobRide = scope.launchBack {
            do {
                try await self.project.ride.getRideById(rideId: rideId) { ride in
                    if self.jobRide == nil {
                        return
                    }
                    self.scope.launchMain {
                        if self.state.ride == nil && ride != nil {
                            invoke()
                        }
                        if self.jobRideRequest != nil {
                            self.scope.launchBack {
                                try? await self.project.ride.cancelRideRequestRealTime()
                            }
                            self.jobRideRequest?.cancel()
                            self.jobRideRequest = nil
                        }
                        if (ride?.status == -2) {
                            if self.jobRide != nil {
                                self.scope.launchBack {
                                    try? await self.project.ride.cancelRideRealTime()
                                }
                                self.jobRide?.cancel()
                                self.jobRide = nil
                            }
                            var mapData = self.state.mapData
                            self.state = self.state.copy { currentState in
                                currentState.mapData = mapData.copy { it in
                                    it.driverPoint = nil
                                    return it
                                }
                                currentState.ride = nil
                                currentState.isProcess = false
                                return currentState
                            }
                        } else {
                            self.state = self.state.copy { currentState in
                                currentState.mapData = self.state.mapData.copy { it in
                                    it.driverPoint = ride?.currentDriver.toGoogleLatLng()
                                    return it
                                }
                                currentState.rideRequest = nil
                                currentState.ride = ride
                                currentState.isProcess = false
                                return currentState
                            }
                        }
                    }
                    
                }
            } catch {
                self.setIsProcess(false)
            }
        }
    }
    
    @MainActor
    func getPrefLastLocation(
        findPreference: @escaping @BackgroundActor (String, @BackgroundActor @escaping (String?) -> Unit) -> Unit,
        invoke: @escaping @MainActor (Double, Double) -> Unit,
        failed:  @escaping @MainActor () -> Unit
    ) {
        scope.launchBack {
            findPreference(ConstKt.PREF_LAST_LATITUDE) { latitude in
                findPreference(ConstKt.PREF_LAST_LONGITUDE) { longitude in
                    if let lat = (latitude != nil ? Double(latitude!) : nil), let lng = (longitude != nil ? Double(longitude!) : nil) {
                        self.scope.launchMain {
                            invoke(lat, lng)
                        }
                    } else {
                        self.scope.launchMain {
                            failed()
                        }
                    }
                }
            }
        }
    }
    
    @MainActor
    func signOut(invoke: @escaping @MainActor () -> Unit, failed: @escaping @MainActor () -> Unit) {
        setIsProcess(true)
        scope.launchBack {
            let it = try? await self.project.pref.deletePrefAll()
            if let it = it, it.int32Value == RealmKt.REALM_SUCCESS {
                do {
                    try await AuthKt.signOutAuth(invoke: {
                        self.scope.launchMain {
                            self.setMainProcess(false)
                            invoke()
                        }
                    }, failed: {
                        self.scope.launchMain {
                            self.setMainProcess(false)
                            failed()
                        }
                    })
                } catch {
                    self.scope.launchMain {
                        self.setMainProcess(false)
                        failed()
                    }
                }
            } else {
                self.scope.launchMain {
                    self.setMainProcess(false)
                    failed()
                }
            }
        }
    }
    
    private func setIsProcess(_ isProcess: Bool) {
        scope.launchMain {
            self.state = self.state.copy(isProcess: isProcess)
        }
    }
    
    @MainActor func setMainProcess(_ isProcess: Bool) {
        self.state = self.state.copy(isProcess: isProcess)
    }
    
    struct State {
        
        var mapData: MapData = MapData()
        var duration: Long? = nil
        var durationText: String? = nil
        var distance: Long? = nil
        var distanceText: String = ""
        var fare: Double = 0.0
        var fromText: String = ""
        var toText: String = ""
        var locationsFrom: [Location] = []
        var locationsTo: [Location] = []
        var rideRequest: RideRequest? = nil
        var ride: Ride? = nil
        var isProcess: Bool = true
        
        @MainActor
        mutating func copy(updates: (inout Self) -> Self) -> Self { // Only helpful For struct or class with nil values
            self = updates(&self)
            return self
        }
        
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
