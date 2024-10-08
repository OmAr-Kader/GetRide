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
    
    var  s: Task<Void, Error>? = nil
    private var jobDriverRideInserts: Task<Void, Error>? = nil
    private var jobDriverRideRequests: Task<Void, Error>? = nil

    private var jobRideRequest: Task<Void, Error>? = nil
    private var jobRideInitial: Task<Void, Error>? = nil
    private var jobRide: Task<Void, Error>? = nil
    
    @MainActor
    func loadRequests(driverId: Long, currentLocation: Location, popUpSheet: @escaping @MainActor () -> Unit) {
        if let it = state.mapData.currentLocation {
            if (it.latitude == currentLocation.latitude && it.longitude == currentLocation.longitude && jobDriverRideInserts != nil) {
                loggerError("request", "return")
                return
            }
        }
        
        jobDriverRideInserts?.cancel()
        jobDriverRideInserts = scope.launchBack {
            try? await self.project.ride.getNearRideInserts(currentLocation: currentLocation) { newRequest in
                self.scope.launchMain {
                    self.state = self.state.copy(requests: self.state.requests + [newRequest])
                }
            }
        }
        jobDriverRideInserts?.cancel()
        jobDriverRideInserts = scope.launchBack {
            try? await self.project.ride.getNearRideRequestsForDriver(currentLocation: currentLocation) { requests in
                self.scope.launchMain {
                    if (self.state.ride == nil) {
                        if let it = requests.first(where: { it in
                            it.isDriverChosen(driverId: driverId) && it.chosenRide != 0
                        }) {
                            self.fetchRide(rideId: it.chosenRide, popUpSheet: popUpSheet)
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
    
    private func fetchRide(rideId: Long, popUpSheet: @escaping @MainActor () -> Unit) {
        jobRide?.cancel()
        jobRide = scope.launchBack {
            do {
                try await self.project.ride.getRideById(rideId: rideId) { ride in
                    self.scope.launchMain {
                        if (self.state.ride == nil) {
                            popUpSheet()
                        }
                        self.state = self.state.copy(ride: ride, isProcess: false)
                    }
                }
            } catch {
                self.setIsProcess(false)
            }
        }
    }
    
    @MainActor
    func checkForActiveRide(driverId: Long, invoke: @escaping @MainActor () -> Unit) {
        jobRideInitial?.cancel()
        let state = self.state
        jobRideInitial = scope.launchBack {
            do {
                try await self.project.ride.getActiveRideForDriver(driverId: driverId) { ride in
                    self.scope.launchMain {
                        if (state.ride == nil && ride != nil) {
                            invoke()
                        }
                        self.state = self.state.copy(ride: ride, isProcess: false)
                    }
                }
            } catch {
                self.setIsProcess(false)
            }
        }
    }
    
    @MainActor
    func setLastLocation(lat: Double, lng: Double) {
        state = state.copy(mapData: state.mapData.copy(currentLocation: CLLocationCoordinate2D(latitude: lat, longitude: lng)))
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
    func updateCurrentLocation(currentLocation: CLLocationCoordinate2D) {
        state = state.copy(mapData: state.mapData.copy(currentLocation: currentLocation))
        scope.launchBack {
            let _ = try? await self.project.pref.updatePref(
                pref: [
                    PreferenceData(keyString: ConstKt.PREF_LAST_LATITUDE, value: String(currentLocation.latitude)),
                    PreferenceData(keyString: ConstKt.PREF_LAST_LONGITUDE, value: String(currentLocation.longitude))
                ]
            )
        }
    }
    
    func updateRide(ride: Ride, newStatus: Int32) {
        scope.launchBack {
            let _ = try? await self.project.ride.editRide(item: ride.copy(status: newStatus))
        }
    }

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
        jobDriverRideInserts?.cancel()
        jobDriverRideRequests?.cancel()
        jobRideRequest?.cancel()
        jobRideInitial?.cancel()
        jobRide?.cancel()
        jobDriverRideInserts = nil
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
