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
    let findPreferenceMainBack: @MainActor (String, @BackgroundActor @escaping (String?) -> Unit) -> Unit
    let navigateToScreen: @MainActor (ScreenConfig, Screen) -> Unit
    let navigateHome: @MainActor (Screen) -> Unit
    
    @Inject
    private var theme: Theme
    
    @State private var cameraPositionState: GMSCameraPosition = GMSCameraPosition.camera(withTarget: defaultLocation, zoom: 15)
    @StateObject private var obs: HomeDriverObserve = HomeDriverObserve()
    @State private var toast: Toast? = nil
    let popUpSheet: () -> Unit = {
        //scope.launch { sheetState.bottomSheetState.show() }
    }
    var refreshScope: (MapData) -> Unit {
        { mapData in
            let centerCoordinate = calculateCenterLocationCoordinate2D(from: [mapData.startPoint, mapData.endPoint, mapData.currentLocation])
            self.cameraPositionState = GMSCameraPosition.camera(withTarget: centerCoordinate,zoom: 15)
        }
    }
    var body: some View {
        let state = obs.state
        ZStack(alignment: .topLeading) {
            MapScreen(
                mapData: state.mapData,
                cameraPositionState: $cameraPositionState,
                updateCurrentLocation: { currentLocation, update in
                    obs.updateCurrentLocation(currentLocation: currentLocation) {
                        update()
                        obs.loadRequests(driverId: userPref.id, currentLocation: currentLocation.toLocation(), popUpSheet: popUpSheet, refreshScope: refreshScope)
                    }
                },
                theme: theme
            ) { _, _ in

            }.onAppear {
                self.cameraPositionState = GMSCameraPosition.camera(withTarget: state.mapData.currentLocation ?? defaultLocation, zoom: 15)
            }
            LoadingScreen(isLoading: state.isProcess)
        }.toolbar(.hidden).background(theme.background).toastView(toast: $toast, backColor: theme.background)
    }
}
