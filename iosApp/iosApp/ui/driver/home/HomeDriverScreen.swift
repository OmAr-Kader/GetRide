//
//  HomeDriverScreen.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//


import SwiftUI
import shared

struct HomeDriverScreen : View {
    
    let userPref: UserPref
    let findPreferenceMainBack: @MainActor (String, @BackgroundActor @escaping (String?) -> Unit) -> Unit
    let navigateToScreen: @MainActor (ScreenConfig, Screen) -> Unit
    let navigateHome: @MainActor (Screen) -> Unit
    
    @Inject
    private var theme: Theme
    
    @StateObject private var obs: HomeDriverObserve = HomeDriverObserve()
    @State private var toast: Toast? = nil
    
    var body: some View {
        let state = obs.state
        ZStack(alignment: .topLeading) {
            
            LoadingScreen(isLoading: state.isProcess)
        }.toolbar(.hidden).background(theme.background).toastView(toast: $toast, backColor: theme.background)
    }
}
