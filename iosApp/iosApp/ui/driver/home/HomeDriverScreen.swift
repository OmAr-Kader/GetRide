//
//  HomeDriverScreen.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared
import GoogleMaps

struct HomeDriverScreen : View {
    
    let userPref: UserPref
    let findPreference: @BackgroundActor (String, @BackgroundActor @escaping (String?) -> Unit) -> Unit
    let navigateToScreen: @MainActor (ScreenConfig, Screen) -> Unit
    let navigateHome: @MainActor (Screen) -> Unit
    
    @Inject
    private var theme: Theme
    
    @MainActor
    @State private var cameraPositionState: GMSCameraUpdate = GMSCameraUpdate.setCamera(GMSCameraPosition.camera(withTarget: defaultLocation, zoom: 15))
    
    @MainActor
    @StateObject private var obs: HomeDriverObserve = HomeDriverObserve()
    
    @MainActor
    @State private var toast: Toast? = nil
    
    @MainActor
    @State private var isOpen = false
    
    @State private var expandedHeight: CGFloat = 420 // DriverDragHandler + RideRequestsDriverSheet + Padding
    @State private var collapsedHeight: CGFloat = 60
    @State private var currentOffset: CGFloat = -(420 - 60)

    var popUpSheet: () -> Unit {
        {
            withAnimation {
                currentOffset = -(expandedHeight - collapsedHeight)
            }
        }
    }
    
    var refreshScope: (MapData) -> Unit {
        { mapData in
            self.cameraPositionState = GMSCameraUpdate.fit(calculateCenterLocationCoordinate2D(from: [mapData.startPoint, mapData.endPoint, mapData.currentLocation]), withPadding: 100)
        }
    }
    
    var body: some View {
        let state = obs.state
        ZStack(alignment: .topLeading) {
            DrawerView(isOpen: $isOpen, overlayColor: shadowColor) {
                ZStack {
                    ZStack {
                        BarMainScreen {
                            isOpen.toggle()
                        }.onTop()
                        VStack {
                            Spacer().frame(height: 60)
                            MapScreen(mapData: state.mapData, cameraPositionState: $cameraPositionState, theme: theme) { currentLocation, update in
                                obs.updateCurrentLocation(currentLocation: currentLocation) {
                                    update()
                                    obs.loadRequests(driverId: userPref.id, currentLocation: currentLocation.toLocation(), popUpSheet: popUpSheet, refreshScope: refreshScope)
                                }
                            } setMapMarks: { _, _ in}
                            Spacer().frame(height: 50)
                        }
                        Spacer()
                    }
                    BottomSheetView(expandedHeight: $expandedHeight, collapsedHeight: $collapsedHeight, currentOffset: $currentOffset) {
                        DriverDragHandler(isDriverHaveRide: state.ride != nil, theme: theme)
                    } content: {
                        if let ride = state.ride {
                            SubmittedRideRequestSheet(ride: ride, theme: theme) { newStatus in
                                obs.updateRide(ride: ride, newStatus: newStatus)
                            } submitFeedback: { it in
                                obs.submitFeedback(driverId: ride.driverId, rate: it)
                            } clearRide: {
                                obs.clearRide()
                            }
                        } else {
                            RideRequestsDriverSheet(requests: state.requests, theme: theme) { from, to in
                                obs.showOnMap(start: from.toGoogleLatLng(), end: to.toGoogleLatLng(), refreshScope: refreshScope)
                            } submitProposal: { request in
                                if let it = state.mapData.currentLocation?.toLocation() {
                                    obs.submitProposal(rideRequestId: request.id, driverId: userPref.id, driverName: userPref.name, fare: request.fare, location: it) {
                                        obs.showOnMap(start: request.from.toGoogleLatLng(), end: request.to.toGoogleLatLng(), refreshScope: refreshScope)
                                    }
                                }
                            } cancelSubmitProposal: { request in
                                obs.cancelProposal(request: request, driverId: userPref.id)
                            }
                        }
                    }
                }
                LoadingScreen(isLoading: state.isProcess)
            } drawer: {
                DrawerContainer {
                    DrawerText(
                        itemColor: theme.primary,
                        text: "GetRide",
                        textColor: theme.textForPrimaryColor
                    ) {
                        withAnimation {
                            isOpen.toggle()
                        }
                    }
                    DrawerItem(
                        itemColor: theme.backDark,
                        icon: "exit",
                        text: "Sign out",
                        textColor: theme.textColor
                    ) {
                        obs.signOut {
                            exit(0)
                        } failed: {
                            toast = Toast(style: .error, message: "Failed")
                        }
                    }
                }
            }
        }.background(theme.background).toastView(toast: $toast, backColor: theme.backDark).task {
            obs.getPrefLastLocation(findPreference: findPreference) { lat, lng in
                cameraPositionState = GMSCameraUpdate.setCamera(GMSCameraPosition.camera(withLatitude: lat, longitude: lng, zoom: 15))
                obs.checkForActiveRide(driverId: userPref.id, invoke: popUpSheet, refreshScope: refreshScope)
            } failed: {
                obs.checkForActiveRide(driverId: userPref.id, invoke: popUpSheet, refreshScope: refreshScope)
            }
        }
    }
}

struct SubmittedRideRequestSheet : View {
    
    let ride: Ride
    let theme: Theme
    let updateRideStatus: (Int32) -> Unit
    let submitFeedback: (Float) -> Unit
    let clearRide: () -> Unit
    
    var body: some View {
        let actionTitle: String = switch ride.status {
        case 0: "Going to Your Client" // To Update Status To 1
        case 1: "Reached the destination" // To Update Status To 2
        case 2: "Start Your Ride" // To Update Status To 3
        case 3: "Finish Your Ride" // To Update Status To 4
        case 4: "Close"
        default: "Canceled"
        }
        let targetedAction: () -> Int32? = {
            switch ride.status {
            case 0: 1
            case 1: 2
            case 2: 3
            case 3: 4
            default: nil
            }
        }
        VStack {
            Spacer().frame(height: 10)
            HStack {
                Text(
                    ride.durationDistance
                ).padding().foregroundStyle(theme.textColor).font(.system(size: 15))
                Spacer().frame(minWidth: 10)
                Text(
                    "Fare: $\(String(format: "%.00f", ride.fare))"
                ).padding().foregroundStyle(theme.textColor).font(.system(size: 18))
                Spacer()
            }.padding()
            Spacer().frame(height: 10)
            if ride.status == 4 {
                RatingBar(rating: 0, onRate: submitFeedback)
            }
            HStack {
                Spacer()
                if (ride.status != -1 && ride.status != 3 && ride.status != 4) {
                    Button {
                        updateRideStatus(-1)
                    } label: {
                        Text("Cancel Ride")
                            .padding(top: 7, leading: 10, bottom: 7, trailing: 10)
                            .foregroundColor(.black)
                            .background(RoundedRectangle(cornerRadius: 7, style: .continuous).fill(.red))
                    }
                }
                Spacer()
                Button {
                    if (ride.status == 4) {
                        clearRide()
                    } else {
                        if let action = targetedAction() {
                            updateRideStatus(action)
                        }
                    }
                    
                } label:{
                    /*AnimatedText(
                     actionTitle
                     ) { str ->
                     
                     }*/
                    Text(actionTitle)
                        .padding(top: 7, leading: 10, bottom: 7, trailing: 10)
                        .foregroundColor(.black)
                        .background(RoundedRectangle(cornerRadius: 7, style: .continuous).fill(.red))
                }
                Spacer()
            }.padding()
        }.padding(leading: 20, trailing: 20).frame(minHeight: 100)
    }
}

struct RideRequestsDriverSheet : View {
    
    let requests: [RideRequest]
    let theme: Theme
    let showOnMap: (Location, Location) -> Unit
    let submitProposal: (RideRequest) -> Unit
    let cancelSubmitProposal: (RideRequest) -> Unit
    
    var body: some View {
        ScrollView {
            LazyVStack {
                ForEach(requests, id: \.id) { request in
                    VStack {
                        HStack {
                            Text(
                                request.durationDistance
                            ).foregroundStyle(theme.textColor).font(.system(size: 18))
                            Spacer()
                            Text(
                                "Fare: $\(String(format: "%.00f", request.fare))"
                            ).foregroundStyle(theme.textColor).font(.system(size: 18))
                            Spacer()
                        }
                        //Spacer().frame(height: 5)
                        //RatingBar(rating: request.userRate, starSize: 20)
                        Spacer().frame(height: 5)
                        HStack {
                            Spacer()
                            Button {
                                showOnMap(request.from, request.to)
                            } label: {
                                Text("Show On Map")
                                    .padding(top: 7, leading: 10, bottom: 7, trailing: 10)
                                    .foregroundColor(.black)
                                    .background(RoundedRectangle(cornerRadius: 7, style: .continuous).fill(.red))
                            }
                            Spacer()
                            if request.isDriverCanSubmit {
                                Button {
                                    submitProposal(request)
                                } label:{
                                    Text("Submit Proposal")
                                        .padding(top: 7, leading: 10, bottom: 7, trailing: 10)
                                        .foregroundColor(.black)
                                        .background(RoundedRectangle(cornerRadius: 7, style: .continuous).fill(.green))
                                }
                            } else if request.requestHadSubmit {
                                Button {
                                    cancelSubmitProposal(request)
                                } label:{
                                    Text("Cancel Proposal")
                                        .padding(top: 7, leading: 10, bottom: 7, trailing: 10)
                                        .foregroundColor(.black)
                                        .background(RoundedRectangle(cornerRadius: 7, style: .continuous).fill(.green))
                                }
                            }
                            Spacer()
                        }
                    }
                    Spacer().frame(height: 10)
                }.padding(leading: 20, trailing: 20)
            }.frame(height: 350, alignment: .top)
        }
    }
}

struct DriverDragHandler : View {
   
    let isDriverHaveRide: Bool
    let theme: Theme
    
    var body: some View {
        VStack {
            Capsule()
                .fill(theme.textGrayColor)
                .frame(width: 40, height: 6)
                .padding(.top, 10)
                .padding(.bottom, 6)
            VStack(alignment: .center) {
                Text(
                    isDriverHaveRide ? "Ride Status" : "Searching for Ride Requests"
                ).foregroundStyle(theme.textColor).font(.system(size: 16))
                Spacer().frame(height: 10)
                //LinearProgressIndicator(height: 3)
            }
        }
    }
}

struct LinearProgressIndicator: View {
    
    let height: CGFloat
    
    @State private var animationOffset: CGFloat = -1.0 // Start off-screen
    @State private var isAnimating: Bool = false // Control the animation

    var body: some View {
        GeometryReader { geometry in
            ZStack(alignment: .leading) {
                Capsule()
                    .fill(Color.gray.opacity(0.3))
                    .frame(height: height)
                Capsule()
                    .fill(Color.blue.opacity(0.6))
                    .frame(width: geometry.size.width / 3, height: height)
                    .offset(x: animationOffset * geometry.size.width)
                    .onAppear {
                        if !isAnimating {
                            isAnimating = true
                            startAnimation(in: geometry.size.width)
                        }
                    }
            }
        }
        .frame(height: height)
    }
    
    private func startAnimation(in width: CGFloat) {
        withAnimation(
            Animation.linear(duration: 1.5)
                .repeatForever(autoreverses: true)
        ) {
            animationOffset = 1.0
        }
    }
}






