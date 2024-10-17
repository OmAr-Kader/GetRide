//
//  HomeScreen.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import GoogleMaps
import shared

struct HomeScreen : View {
    
    
    let userPref: UserPref
    let findPreference: @BackgroundActor (String, @BackgroundActor @escaping (String?) -> Unit) -> Unit
    let navigateToScreen: @MainActor (ScreenConfig, Screen) -> Unit
    let navigateHome: @MainActor (Screen) -> Unit
    
    @Inject
    private var theme: Theme
    
    @MainActor
    @State private var cameraPositionState: GMSCameraUpdate = GMSCameraUpdate.setCamera(GMSCameraPosition.camera(withTarget: defaultLocation, zoom: 15))
    
    @MainActor
    @StateObject private var obs: HomeObserve = HomeObserve()
    
    @MainActor
    @State private var toast: Toast? = nil
    
    @MainActor
    @State private var isOpen = false
    
    @State private var expandedHeight: CGFloat = 420 // RideSheetDragHandler + RideSheet + Padding
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
    
    private var isPresented: Binding<Bool> {
        Binding(get: { // @OmAr-Kader
            obs.state.rideRequest != nil
        }, set: { _ in

        })
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
                                    obs.checkForActiveRide(userId: userPref.id, invoke: popUpSheet, refreshScope: refreshScope)
                                }
                            } setMapMarks: { latLng in
                                obs.setMapMarks(latLng: latLng, invoke: refreshScope)

                            }
                            Spacer().frame(height: 50)
                        }
                        Spacer()
                    }
                }.sheet(isPresented: isPresented) {
                    if let rideRequest = state.rideRequest {
                        if #available(iOS 16.4, *) {
                            RideRequestSheet(rideRequest: rideRequest) {
                                if let it = state.rideRequest {
                                    obs.cancelRideRequest(rideRequest: it)
                                }
                            } acceptProposal: { proposal in
                                obs.acceptProposal(userId: userPref.id, rideRequest: rideRequest, proposal: proposal, invoke: popUpSheet) {
                                    toast = Toast(style: .error, message: "Failed")
                                }
                            }.background(theme.backDark)
                                .edgesIgnoringSafeArea(.all)
                                .presentationBackground(theme.backDark)
                                .presentationDetents([.large, .custom(CommentSheetDetent.self)])
                                .presentationContentInteraction(.scrolls)
                                .interactiveDismissDisabled()
                        } else {
                            RideRequestSheet(rideRequest: rideRequest) {
                                if let it = state.rideRequest {
                                    obs.cancelRideRequest(rideRequest: it)
                                }
                            } acceptProposal: { proposal in
                                obs.acceptProposal(userId: userPref.id, rideRequest: rideRequest, proposal: proposal, invoke: popUpSheet) {
                                    toast = Toast(style: .error, message: "Failed")
                                }
                            }.background(theme.backDark)
                                .edgesIgnoringSafeArea(.all).presentationDetents([.large, .custom(CommentSheetDetent.self)])
                                .interactiveDismissDisabled()
                        }
                    }
                }
                BottomSheetView(expandedHeight: $expandedHeight, collapsedHeight: $collapsedHeight, currentOffset: $currentOffset) {
                    RideSheetDragHandler(isUserHaveRide: state.ride != nil, theme: theme)
                } content: {
                    if let ride = state.ride {
                        RideSheet(ride: ride, theme: theme) {
                            obs.cancelRideFromUser(ride: ride)
                        } submitFeedback: { it in
                            obs.submitFeedback(driverId: ride.driverId, rate: it)
                        } clearRide: {
                            obs.clearRide()
                        }
                    } else {
                        MapSheetUser(obs: obs, userId: userPref.id, refreshScope: refreshScope, theme: theme) { txt in
                            toast = Toast(style: .error, message: txt)
                        }
                    }
                    Spacer()
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
                            navigateHome(.AUTH_SCREEN_ROUTE)
                        } failed: {
                            toast = Toast(style: .error, message: "Failed")
                        }
                    }
                }
            }
        }.background(theme.background).toastView(toast: $toast, backColor: theme.backDark).task {
            obs.getPrefLastLocation(findPreference: findPreference) { lat, lng in
                cameraPositionState = GMSCameraUpdate.setCamera(GMSCameraPosition.camera(withLatitude: lat, longitude: lng, zoom: 15))
            } failed: {}
        }//.ignoresSafeArea(.keyboard, edges: .bottom)
    }
}

struct MapSheetUser : View {
    
    @StateObject var obs: HomeObserve
    let userId: Long
    let refreshScope: (MapData) -> Unit
    let theme: Theme
    let snakeBar: (String) -> Unit
    
    @FocusState private var fromFocus: Bool
    @FocusState private var toFocus: Bool

    var body: some View {
        let state = obs.state
        VStack {
            Spacer().frame(height: 5)
            VStack {
                HStack {
                    Spacer()
                    Text(
                        state.mapData.durationDistance
                    ).foregroundStyle(theme.textColor).font(.system(size: 20))
                    Spacer()
                    if (state.fare != 0.0) {
                        Text(
                            "\(state.fare.toPriceFormat())"
                        ).foregroundStyle(theme.textColor).font(.system(size: 20))
                        Spacer()
                    }
                }.padding()
                Spacer().frame(height: 5)
                OutlinedTextFieldTrailingIcon(text: state.fromText, onChange: obs.setFromText, hint: "Ride From", isError: false, errorMsg: "Ride From Is Empty", theme: theme, cornerRadius: 12, lineLimit: 1, keyboardType: UIKeyboardType.default, isFocused: $fromFocus
                ) {
                    if (state.fromText == state.mapData.fromText) {
                        Button {
                            obs.clearFromText()
                        } label: {
                            Image(systemName: "xmark").foregroundColor(theme.textHintColor)
                        }
                    } else {
                        Button {
                            Task { @MainActor in await MainActor.run { fromFocus = false } }
                            obs.searchForLocationOfPlaceFrom(fromText: state.fromText)
                        } label: {
                            Image(systemName: "magnifyingglass").foregroundColor(theme.textHintColor)
                        }
                    }
                }.onSubmit {
                    Task { @MainActor in await MainActor.run { fromFocus = false } }
                    obs.searchForLocationOfPlaceFrom(fromText: state.fromText)
                }.submitLabel(.search)
                if !state.locationsFrom.isEmpty {
                    VStack {
                        Spacer().frame(height: 3)
                        Text("Locations: From")
                        VStack {
                            ForEach(state.locationsFrom, id: \.self) { locationFrom in
                                Button {
                                    obs.setFrom(from: locationFrom) { mapData in
                                        refreshScope(mapData)
                                        Task { @MainActor in await MainActor.run { fromFocus = false } }
                                    }
                                } label: {
                                    ScrollView(.horizontal, showsIndicators: false) {
                                        Text(locationFrom.title).foregroundStyle(theme.textHintColor)
                                    }.padding(all: 3)
                                }
                                .buttonStyle(PlainButtonStyle()) // Remove default button styling
                            }
                        }
                    }.shadow(color: theme.background.opacity(0.2), radius: 10, x: 0, y: 2)
                        .background(theme.background)
                        .cornerRadius(8)
                }
                Spacer().frame(height: 10)
                OutlinedTextFieldTrailingIcon(text: state.toText, onChange: obs.setToText, hint: "To", isError: false, errorMsg: "Ride To Is Empty", theme: theme, cornerRadius: 12, lineLimit: 1, keyboardType: UIKeyboardType.default, isFocused: $toFocus
                ) {
                    if (state.toText == state.mapData.toText) {
                        Button {
                            obs.clearToText()
                        } label: {
                            Image(systemName: "xmark").foregroundColor(theme.textHintColor)
                        }
                    } else {
                        Button {
                            Task { @MainActor in await MainActor.run { toFocus = false } }
                            obs.searchForLocationOfPlaceTo(toText: state.fromText)
                        } label: {
                            Image(systemName: "magnifyingglass").foregroundColor(theme.textHintColor)
                        }
                    }
                }.onSubmit {
                    Task { @MainActor in await MainActor.run { toFocus = false } }
                    obs.searchForLocationOfPlaceTo(toText: state.toText)
                }.submitLabel(.search)
                if !state.locationsTo.isEmpty {
                    VStack {
                        Spacer().frame(height: 3)
                        Text("Locations: To")
                        VStack {
                            ForEach(state.locationsTo, id: \.self) { locationTo in
                                Button {
                                    obs.setTo(to: locationTo) { mapData in
                                        refreshScope(mapData)
                                        Task { @MainActor in await MainActor.run { toFocus = false } }
                                    }
                                } label: {
                                    ScrollView(.horizontal, showsIndicators: false) {
                                        Text(locationTo.title).foregroundStyle(theme.textHintColor)
                                    }.padding(all: 3)
                                }.buttonStyle(PlainButtonStyle()).padding() // Remove default button styling
                            }
                        }
                    }.shadow(color: theme.background.opacity(0.2), radius: 10, x: 0, y: 2)
                        .background(theme.background)
                        .cornerRadius(8)
                }
            }
            Spacer()
            HStack {
                Spacer()
                Button {
                    if state.mapData.startPoint != nil && state.mapData.endPoint != nil {
                        if (userId != 0) {
                            obs.pushRideRequest(userId: userId)
                        } else {
                            snakeBar("Something Wrong")
                        }
                    } else {
                        snakeBar("Pick destination, Please")
                    }
                } label: {
                    HStack {
                        ImageAsset(icon: "taxi", tint: theme.textForPrimaryColor).frame(width: 30, height: 30)
                        if state.mapData.startPoint != nil && state.mapData.endPoint != nil {
                            Spacer().frame(width: 3)
                            Text("Get Ride")
                                .foregroundColor(theme.textForPrimaryColor)
                                .font(.system(size: 18))
                        }
                    }.padding(top: 7, leading: 15, bottom: 7, trailing: 15)
                        .background(RoundedRectangle(cornerRadius: 14, style: .continuous).fill(theme.primary))
                }
            }
            Spacer().frame(height: 10)
        }.padding(leading: 20, trailing: 20).frame(height: 350).toolbar {
            ToolbarItem(placement: .keyboard) {
               Button("Close") {
                   if fromFocus {
                       Task { @MainActor in await MainActor.run { fromFocus = false } }
                   } else if toFocus {
                       Task { @MainActor in await MainActor.run { toFocus = false } }
                   }
               }
            }
        }
    }
}

struct RideSheet : View {
    
    let ride: Ride
    let theme: Theme
    let cancelRide: () -> Unit
    let submitFeedback: (Float) -> Unit
    let clearRide: () -> Unit
    
    var body: some View {
        let statusTitle: String = switch ride.status {
        case 0: "\(ride.driverName) is getting ready to go." // To Update Status To 1
        case 1: "\(ride.driverName) on way" // To Update Status To 2
        case 2: "\(ride.driverName) waiting for you" // To Update Status To 3
        case 3: "Ride Started" // To Update Status To 4
        case 4: "Give \(ride.driverName) feedback"
        default: "Canceled"
        }
        VStack {
            Spacer().frame(height: 5)
            Text(statusTitle).padding().foregroundStyle(theme.textColor).font(.system(size: 18)).onStart()
            Spacer().frame(height: 10)
            HStack {
                Text(
                    ride.durationDistance
                ).foregroundStyle(theme.textColor).font(.system(size: 15))
                Spacer().frame(minWidth: 10)
                Text(
                    "Fare: \(ride.fare.toPriceFormat())"
                ).foregroundStyle(theme.textColor).font(.system(size: 18))
                Spacer()
            }.padding()
            Spacer().frame(height: 10)
            if ride.status == 4 {
                RatingBar(rating: 0, onRate: submitFeedback)
            }
            HStack {
                Spacer()
                if (ride.status != -2 && ride.status != -1 && ride.status != 3 && ride.status != 4) {
                    Button {
                        cancelRide()
                    } label: {
                        Text("Cancel Ride")
                            .padding(top: 7, leading: 10, bottom: 7, trailing: 10)
                            .foregroundColor(.black)
                            .background(RoundedRectangle(cornerRadius: 7, style: .continuous).fill(.red))
                    }
                }
                if (ride.status == 4) {
                    Spacer()
                    Button {
                        clearRide()
                    } label:{
                        Text("Close")
                            .padding(top: 7, leading: 30, bottom: 7, trailing: 30)
                            .foregroundColor(.black)
                            .background(RoundedRectangle(cornerRadius: 7, style: .continuous).fill(.green))
                    }
                }
                Spacer()
            }.padding()
            Spacer().frame(height: 5)
        }.padding(leading: 20, trailing: 20).frame(minHeight: 100)
    }
}

struct RideRequestSheet : View {
    
    let rideRequest: RideRequest
    
    let cancelRideRequest: () -> Unit
    let acceptProposal: (RideProposal) -> Unit
    
    @Inject
    private var theme: Theme
    
    var body: some View {
        NavigationStack {
            VStack(alignment: .center) {
                Text(
                    "Waiting for Drivers"
                ).padding().foregroundStyle(theme.textColor).font(.system(size: 20)).onStart()
                //LinearProgressIndicator
                Spacer().frame(height: 5)
                ScrollView {
                    Spacer().frame(height: 5)
                    HStack {
                        Spacer().frame(width: 5)
                        Text(
                            rideRequest.durationDistance
                        ).foregroundStyle(theme.textColor).font(.system(size: 20))
                        Spacer().frame(width: 5)
                        if (rideRequest.fare != 0.0) {
                            Text(
                                rideRequest.fare.toPriceFormat()
                            ).foregroundStyle(theme.textColor).font(.system(size: 20))
                            Spacer().frame(width: 5)
                        }
                    }
                    LazyVStack {
                        ForEach(rideRequest.driverProposals, id: \.driverId) { proposal in
                            VStack {
                                HStack {
                                    Text(
                                        proposal.driverName
                                    ).foregroundStyle(theme.textColor).font(.system(size: 18))
                                    Spacer()
                                    Text(
                                        "Fare: \(proposal.fare.toPriceFormat())"
                                    ).foregroundStyle(theme.textColor).font(.system(size: 18))
                                    Spacer()
                                }
                                Spacer().frame(height: 5)
                                if proposal.rate != 0 {
                                    RatingBar(rating: 4, starSize: 20)
                                }
                                Spacer().frame(height: 5)
                                HStack {
                                    Spacer()
                                    /*Button {
                                     
                                     } label: {
                                     Text("Reject")
                                     .padding(top: 7, leading: 10, bottom: 7, trailing: 10)
                                     .foregroundColor(.black)
                                     .background(RoundedRectangle(cornerRadius: 7, style: .continuous).fill(.red))
                                     }
                                     Spacer()*/
                                    Button {
                                        acceptProposal(proposal)
                                    } label: {
                                        Text("Accept")
                                            .padding(top: 7, leading: 10, bottom: 7, trailing: 10)
                                            .foregroundColor(.black)
                                            .background(RoundedRectangle(cornerRadius: 7, style: .continuous).fill(.green))
                                    }
                                    //Spacer()
                                }.padding()
                            }
                            Spacer().frame(height: 10)
                        }.padding(leading: 20, trailing: 20)
                    }.frame(height: 350, alignment: .top)
                }.frame(alignment: .top)
                Spacer().frame(height: 5)
                HStack {
                    Spacer()
                    Button {
                        cancelRideRequest()
                    } label: {
                        Text("Cancel Ride")
                            .padding(top: 10, leading: 10, bottom: 10, trailing: 10)
                            .foregroundColor(.black)
                            .background(RoundedRectangle(cornerRadius: 7, style: .continuous).fill(.green))
                    }
                    //Spacer()
                }.padding()
                Spacer().frame(height: 5)
            }.background(theme.backDark)
        }.navigationTitle("Waiting for Drivers")
    }
}

struct RideSheetDragHandler : View {

    let isUserHaveRide: Bool
    let theme: Theme

    var body: some View {
        VStack {
            Capsule()
                .fill(theme.textGrayColor)
                .frame(width: 40, height: 6)
                .padding(.top, 10)
                .padding(.bottom, 6)
            if isUserHaveRide {
                VStack(alignment: .center) {
                    Text(
                        "Ride Status"
                    ).foregroundStyle(theme.textColor).font(.system(size: 16))
                    //Spacer().frame(height: 10)
                }
            }
        }
    }
}

