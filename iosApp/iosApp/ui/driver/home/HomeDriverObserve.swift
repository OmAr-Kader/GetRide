//
//  HomeDriverObserve.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared
import GoogleMaps

class HomeDriverObserve : ObservableObject {
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    private var jobDriverRideInsertsDeletes: Task<Void, Error>? = nil
    private var jobDriverRideRequests: Task<Void, Error>? = nil
    
    private var jobRideRequest: Task<Void, Error>? = nil
    private var jobRideInitial: Task<Void, Error>? = nil
    private var jobRide: Task<Void, Error>? = nil
    
    @MainActor
    func loadRequests(driverId: Long, currentLocation: Location, popUpSheet: @escaping @MainActor () -> Unit, refreshScope: @escaping @MainActor (MapData) -> Unit) {
        if let it = state.mapData.currentLocation {
            if (it.latitude == currentLocation.latitude && it.longitude == currentLocation.longitude && jobDriverRideInsertsDeletes != nil) {
                loggerError("request", "return")
                return
            }
        }
        
        jobDriverRideInsertsDeletes?.cancel()
        jobDriverRideInsertsDeletes = scope.launchBack {
            try? await self.project.ride.getNearRideInsertsDeletes(currentLocation: currentLocation, onInsert: { newRequest in
                self.scope.launchMain {
                    self.state = self.state.copy(requests: self.state.requests + [newRequest])
                }
            }, onDelete: { id in
                self.scope.launchMain(block: {
                    if let index = self.state.requests.firstIndex(where: { it in it.id == id.int64Value }) {
                        var requests = self.state.requests
                        requests.remove(at: index)
                        self.state = self.state.copy(requests: requests)
                    }
                })
            })
        }
        jobDriverRideRequests?.cancel()
        jobDriverRideRequests = scope.launchBack {
            try? await self.project.ride.getNearRideRequestsForDriver(currentLocation: currentLocation) { requests in
                self.scope.launchMain {
                    if (self.state.ride == nil) {
                        if let it = requests.first(where: { it in
                            it.isDriverChosen(driverId: driverId) && it.chosenRide != 0
                        }) {
                            self.fetchRide(rideId: it.chosenRide, popUpSheet: popUpSheet, refreshScope: refreshScope)
                        }
                    }
                }
                let proposalHadSubmit: RideRequest? = requests.first(where: { request in
                    request.driverProposals.first { it in it.driverId == driverId } != nil
                })
                let requestsForDriver = (proposalHadSubmit != nil) ? ConverterKt.toDriverCannotSubmit(requests, idHadSubmit: proposalHadSubmit!.id) : requests
                self.scope.launchMain {
                    self.state = self.state.copy(requests: requestsForDriver)
                }
            }
        }
        
    }
    
    private func fetchRide(rideId: Long, popUpSheet: @escaping @MainActor () -> Unit, refreshScope: @escaping @MainActor (MapData) -> Unit) {
        jobRide?.cancel()
        jobRide = scope.launchBack {
            do {
                try await self.project.ride.getRideById(rideId: rideId) { ride in
                    self.scope.launchMain {
                        if self.state.ride == nil && ride != nil {
                            popUpSheet()
                        }
                        if (ride?.status == -1) {
                            self.jobRide?.cancel()
                            self.jobRide = nil
                            self.self.state = self.state.copy(mapData: self.state.mapData.copy(driverPoint: nil), ride: nil, isProcess: false)
                        } else {
                            if let ride, let routePoints = self.state.mapData.routePoints, routePoints.isEmpty {
                                self.showOnMap(start: ride.from.toGoogleLatLng(), end: ride.to.toGoogleLatLng(), refreshScope: refreshScope)
                            }
                            self.state = self.state.copy(ride: ride, isProcess: false)
                        }
                    }
                }
            } catch {
                self.setIsProcess(false)
            }
        }
    }
    
    @MainActor
    func checkForActiveRide(driverId: Long, invoke: @escaping @MainActor () -> Unit, refreshScope: @escaping @MainActor (MapData) -> Unit) {
        jobRideInitial?.cancel()
        let state = self.state
        jobRideInitial = scope.launchBack {
            do {
                try await self.project.ride.getActiveRideForDriver(driverId: driverId) { ride in
                    self.scope.launchMain {
                        if (state.ride == nil && ride != nil) {
                            invoke()
                        }
                        if (ride?.status == -1) {
                            self.jobRide?.cancel()
                            self.jobRide = nil
                            self.self.state = self.state.copy(mapData: self.state.mapData.copy(driverPoint: nil), ride: nil, isProcess: false)
                        } else {
                            if let ride, let routePoints = self.state.mapData.routePoints, routePoints.isEmpty {
                                self.showOnMap(start: ride.from.toGoogleLatLng(), end: ride.to.toGoogleLatLng(), refreshScope: refreshScope)
                            }
                            self.state = self.state.copy(ride: ride, isProcess: false)
                        }
                    }
                }
            } catch {
                self.setIsProcess(false)
            }
        }
    }
    
    @MainActor
    func showOnMap(start: CLLocationCoordinate2D, end: CLLocationCoordinate2D, refreshScope: @escaping @MainActor (MapData) -> Unit) {
        setIsProcess(true)
        let state = state
        scope.launchBack {
            do {
                try await RideRouteKt.fetchAndDecodeRoute(
                    start: GoogleLocation(lat: start.latitude, lng: start.longitude),
                    end: GoogleLocation(lat: end.latitude, lng: end.longitude)
                ) { routePoints, _, _ in
                    
                    if let routes = routePoints {
                        let newMapData = state.mapData.copy(
                            startPoint: start,
                            endPoint: end,
                            routePoints: routes
                        )
                        self.scope.launchMain {
                            refreshScope(newMapData)
                            self.state = self.state.copy(mapData: newMapData, isProcess: false)
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
    func submitProposal(rideRequestId: Long, driverId: Long, driverName: String, fare: Double, location: Location, invoke: @escaping @MainActor () -> Unit) {
        setIsProcess(true)
        let state = self.state
        scope.launchBack {
            if let _ = try? await self.project.ride.editAddDriverProposal(
                rideRequestId: rideRequestId,
                rideProposal: RideProposal(driverId: driverId, driverName: driverName, rate: state.rate?.rate ?? 5.0, fare: fare, currentDriver: location, date: DateKt.dateNow)
            ) {
                self.scope.launchMain {
                    invoke()
                }
            }
        }
    }
    
    @MainActor
    func cancelProposal(request: RideRequest, driverId: Long) {
        setIsProcess(true)
        scope.launchBack {
            if let proposalToRemove = request.driverProposals.first(where: { it in it.driverId == driverId }) {
                if let _ = try? await self.project.ride.editRemoveDriverProposal(rideRequestId: request.id, proposalToRemove: proposalToRemove) {
                    self.setIsProcess(false)
                }
            } else {
                self.setIsProcess(false)
            }
        }
    }
    
    @MainActor
    func updateCurrentLocation(currentLocation: CLLocationCoordinate2D, update: @MainActor () -> Unit) {
        let it = self.state.mapData
        if !it.isCurrentAlmostSameArea(newCurrent: currentLocation) {
            update()
            self.updateCurrentLocationPref(currentLocation: currentLocation)
            self.updateRideLocation(currentLocation: currentLocation)
            self.state = self.state.copy(mapData: self.state.mapData.copy(currentLocation: currentLocation))
        }
    }
    
    @MainActor
    private func updateRideLocation(currentLocation: CLLocationCoordinate2D) {
        if let ride = self.state.ride {
            self.scope.launchBack {
                let _ = try? await self.project.ride.editDriverLocation(rideId: ride.id, driverLocation: currentLocation.toLocation())
            }
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
    func updateRide(ride: Ride, newStatus: Int32) {
        scope.launchBack {
            let _ = try? await self.project.ride.editRide(item: ride.copy(status: newStatus))
        }
    }
    
    @MainActor
    func submitFeedback(driverId: Long, rate: Float) {
        clearRide()
        scope.launchBack {
            let _ = try? await self.project.driver.addEditDriverRate(driverId: driverId, rate: rate)
        }
    }
    
    @MainActor
    func clearRide() {
        jobRide?.cancel()
        jobRideInitial?.cancel()
        jobRide = nil
        jobRideInitial = nil
        state = state.copy(
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
    
    deinit {
        jobDriverRideInsertsDeletes?.cancel()
        jobDriverRideRequests?.cancel()
        jobRideRequest?.cancel()
        jobRideInitial?.cancel()
        jobRide?.cancel()
        jobDriverRideInsertsDeletes = nil
        jobDriverRideRequests = nil
        jobRideRequest = nil
        jobRideInitial = nil
        jobRide = nil
    }
    
    struct State {
        
        private(set) var requests: [RideRequest] = []
        private(set) var mapData: MapData = MapData()
        private(set) var routes: String? = nil
        private(set) var ride: Ride? = nil
        private(set) var rate: DriverRate? = nil
        private(set) var isProcess: Bool = true
        
        @MainActor
        mutating func copy(
            requests: [RideRequest]? = nil,
            mapData: MapData? = nil,
            routes: String? = nil,
            ride: Ride? = nil,
            rate: DriverRate? = nil,
            isProcess: Bool? = nil
        ) -> Self {
            self.requests = requests ?? self.requests
            self.mapData = mapData ?? self.mapData
            self.routes = routes ?? self.routes
            self.ride = ride ?? self.ride
            self.rate = rate ?? self.rate
            self.isProcess = isProcess ?? self.isProcess
            return self
        }
    }
}
