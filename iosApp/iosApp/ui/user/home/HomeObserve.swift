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
                    self.scope.launchMain {
                        if self.state.ride == nil && ride != nil {
                            invoke()
                        }
                        if (ride?.status == -2) {
                            self.jobRideInitial?.cancel()
                            self.jobRideInitial = nil
                            self.state = self.state.copy(mapData: self.state.mapData.copy(driverPoint: nil), ride: nil, isProcess: false)
                        } else {
                            if let ride, let routePoints = self.state.mapData.routePoints, routePoints.isEmpty {
                                self.fetchRoute(start: ride.from.toGoogleLatLng(), end: ride.to.toGoogleLatLng(), invoke: refreshScope)
                            }
                            self.state = self.state.copy(
                                mapData: self.state.mapData.copy(
                                    driverPoint: ride?.currentDriver.toGoogleLatLng(),
                                    startPoint: ride?.from.toGoogleLatLng(),
                                    fromText: ride?.from.title ?? "",
                                    endPoint: ride?.to.toGoogleLatLng(),
                                    toText: ride?.to.title ?? ""
                                ),
                                ride: ride,
                                isProcess: false
                            )
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
                        let durationDistance = (distanceText.isEmpty ? "" : durationText + " (\(distanceText)})")
                        self.scope.launchMain {
                            let newMapData = self.state.mapData.copy(
                                durationDistance: durationDistance,
                                routePoints: routes
                            )
                            invoke(newMapData)
                            self.state = self.state.copy(
                                mapData: newMapData,
                                duration: duration?.int64Value,
                                durationText: durationText,
                                distance: distance?.int64Value,
                                distanceText: distanceText,
                                fare: ConverterKt.calculateFare(duration: duration, distance: distance),
                                isProcess: false
                            )
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
                    self.state = self.state.copy(locationsFrom: it)
                }
            }
        }
    }
    
    @MainActor
    func searchForLocationOfPlaceTo(toText: String) {
        scope.launchBack {
            if let it = try? await RideRouteKt.searchForPlaceLocation(placeName: toText) {
                self.scope.launchMain {
                    self.state = self.state.copy(locationsTo: it)
                }
            }
        }
    }
    
    @MainActor
    func setMapMarks(startPoint: CLLocationCoordinate2D? = nil, endPoint: CLLocationCoordinate2D? = nil, invoke: @escaping @MainActor (MapData) -> Unit) {
        self.checkForPlacesNames(startPoint: startPoint, endPoint: endPoint)
        self.checkForRoutes(startPoint: startPoint, endPoint: endPoint, invoke: invoke)
        self.state = self.state.copy(
            mapData: self.state.mapData.copy(
                startPoint: startPoint ?? state.mapData.startPoint,
                endPoint: endPoint ?? state.mapData.endPoint
            )
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
        self.state = self.state.copy(
            mapData: state.mapData.copy(
                startPoint: nil,
                fromText: "",
                durationDistance: "",
                routePoints: nil
            ),
            duration: nil,
            durationText: "",
            distance: nil,
            distanceText: "",
            fare: 0.0,
            fromText: ""
        )
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
        self.state = self.state.copy(
            mapData: self.state.mapData.copy(
                endPoint: nil,
                toText: "",
                durationDistance: "",
                routePoints: nil
            ),
            duration: nil,
            durationText: "",
            distance: nil,
            distanceText: "",
            fare: 0.0,
            toText: ""
        )
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
    func submitFeedback(userId: Long, rate: Float) {
        self.clearRide()
        self.scope.launchBack {
            let _ = try? await self.project.user.addEditUserRate(userId: userId, rate: rate)
        }
    }
    
    @MainActor
    func clearRide() {
        jobRide?.cancel()
        jobRideInitial?.cancel()
        jobRide = nil
        jobRideInitial = nil
        self.state = self.state.copy(
            mapData: state.mapData.copy(
                driverPoint: nil,
                startPoint: nil,
                fromText: "",
                endPoint: nil,
                toText: "",
                durationDistance: "",
                routePoints: nil
            ),
            ride: nil,
            isProcess: false
        )
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
        state = state.copy(rideRequest: nil, isProcess: false)
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
                    self.scope.launchMain {
                        self.state = self.state.copy(rideRequest: rideRequest, isProcess: false)
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
                    self.scope.launchMain {
                        if (self.state.ride == nil) {
                            invoke()
                        }
                        self.jobRideRequest?.cancel()
                        self.jobRideRequest = nil
                        if (ride?.status == -2) {
                            self.jobRide?.cancel()
                            self.jobRide = nil
                            self.state = self.state.copy(mapData: self.state.mapData.copy(driverPoint: nil), ride: nil, isProcess: false)
                        } else {
                            self.state = self.state.copy(
                                mapData: self.state.mapData.copy(driverPoint: ride?.currentDriver.toGoogleLatLng()),
                                rideRequest: nil,
                                ride: ride,
                                isProcess: false
                            )
                        }
                    }
                
                }
            } catch {
                self.setIsProcess(false)
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
