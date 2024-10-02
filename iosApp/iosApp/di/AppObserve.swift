//
//  AppObserve.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import Combine
import shared

class AppObserve : ObservableObject {

    @Inject
    private var project: Project

    private var scope = Scope()

    @MainActor
    @Published var navigationPath = NavigationPath()
    
    @MainActor
    @Published var state = State()

    private var preferences: [PreferenceData] = []
    private var prefsTask: Task<Void, Error>? = nil
    private var job: Task<Void, Error>? = nil
    private var userJob: Task<Void, Error>? = nil
    
    @MainActor
    var navigateHome: (Screen) -> Unit {
        return { screen in
            self.state = self.state.copy(homeScreen: screen)
            return ()
        }
    }
    
    @MainActor
    func navigateHomeNoAnimation(_ screen: Screen) -> Unit {
        self.state = self.state.copy(homeScreen: screen)
    }
    
    @MainActor
    func navigateTo(_ screen: Screen) {
        self.navigationPath.append(screen)
    }
    
    @MainActor
    func backPress() {
        if !self.navigationPath.isEmpty {
            self.navigationPath.removeLast()
        }
    }
    
    func fetchUser(invoke: @MainActor @escaping (UserPref?) -> Unit) {
        scope.launchBack {
            self.findPrefString(ConstKt.PREF_ID) { id in
                self.findPrefString(ConstKt.PREF_NAME) { name in
                    self.findPrefString(ConstKt.PREF_PROFILE_IMAGE) { profileImage in
                        self.scope.launchBack {
                            guard let user = try? await AuthKt.userInfo()?.copy(id: Int64(id ?? "") ?? 0, name: name ?? "", profilePicture: profileImage ?? "") else {
                                self.scope.launchMain {
                                    invoke(nil)
                                }
                                return
                            }
                            self.scope.launchMain {
                                self.state = self.state.copy(userPref: user)
                                invoke(user)
                            }
                        }
                    }
                }
            }
        }
    }
    
    func findUser(invoke: @MainActor @escaping (UserPref?) -> Unit) {
        scope.launchBack {
            self.findPrefString(ConstKt.PREF_ID) { id in
                self.findPrefString(ConstKt.PREF_NAME) { name in
                    self.findPrefString(ConstKt.PREF_PROFILE_IMAGE) { profileImage in
                        self.scope.launchBack {
                            guard let user = try? await AuthKt.userInfo()?.copy(id: Int64(id ?? "") ?? 0, name: name ?? "", profilePicture: profileImage ?? "") else {
                                self.scope.launchMain {
                                    invoke(nil)
                                }
                                return
                            }
                            self.scope.launchMain {
                                self.state = self.state.copy(userPref: user)
                                invoke(user)
                            }
                        }
                    }
                }
            }
        }
    }
    
    @MainActor
    func updateUserPref(userPref: UserPref) {
        state = state.copy(userPref: userPref)
    }
    
    func findUserLive(invoke: @MainActor @escaping (UserPref?) -> Unit) {
        userJob = scope.launchBack {
            do {
                try await AuthKt.fetchSupaBaseUser { userPref, status in
                    guard let userPref = userPref else {
                        if (status as? SessionStatusDataNotAuthenticated) != nil {
                            self.scope.launchMain {
                                self.userJob?.cancel()
                                invoke(nil)
                            }
                        }
                        self.scope.launchMain {
                            self.state = self.state.copy(sessionStatus: status)
                        }
                        return
                    }
                    self.findPrefString(ConstKt.PREF_ID) { id in
                        self.findPrefString(ConstKt.PREF_NAME) { name in
                            self.findPrefString(ConstKt.PREF_PROFILE_IMAGE) { profileImage in
                                self.scope.launchBack {
                                    let user = userPref.copy(id: Int64(id ?? "") ?? 0, name: name ?? "", profilePicture: profileImage ?? "")
                                    self.scope.launchMain {
                                        self.state = self.state.copy(userPref: user)
                                        self.userJob?.cancel()
                                        invoke(user)
                                    }
                                }
                            }
                        }
                    }
                }
            } catch {
                logger("findUserLive", error.localizedDescription)
                self.scope.launchMain {
                    self.userJob?.cancel()
                    invoke(nil)
                }
            }
        }
    }
    
    @MainActor
    func findPrefStringMainMain(
        _ key: String,
        value: @MainActor @escaping (String?) -> Unit
    ) {
        scope.launchBack {
            self.findPrefString(key) { it in
                self.scope.launchMain {
                    value(it)
                }
            }
        }
    }
    
    @MainActor
    func findPrefStringMainBack(
        _ key: String,
        value: @BackgroundActor @escaping (String?) -> Unit
    ) {
        scope.launchBack {
            self.findPrefString(key, value: value)
        }
    }
    
    @BackgroundActor
    func findPrefString(
        _ key: String,
        value: @BackgroundActor @escaping (String?) -> Unit
    ) {
        if (preferences.isEmpty) {
            self.inti { pref in
                value(pref.first { it1 in it1.keyString == key }?.value)
            }
        } else {
            value(preferences.first { it in it.keyString == key }?.value)
        }
    }
    
    
    private func inti(invoke: @BackgroundActor @escaping ([PreferenceData]) -> Unit) {
        scope.launchBack {
            do {
                try await self.project.pref.prefs { list in
                    invoke(list)
                }
            } catch {
                invoke([])
            }
        }
    }
    
    @MainActor
    func findArg(screen: Screen) -> (any ScreenConfig)? {
        return state.argOf(screen)
    }
    
    @MainActor
    func writeArguments(_ route: Screen,_ screenConfig: ScreenConfig) {
        state = state.copy(route, screenConfig)
    }
    
    @MainActor
    func signOut(_ invoke: @escaping @MainActor () -> Unit,_ failed: @escaping @MainActor () -> Unit) {
        scope.launchBack {
            guard let result = try? await self.project.pref.deletePrefAll() else {
                self.scope.launchMain {
                    failed()
                }
                return
            }
            if result.int32Value == RealmKt.REALM_SUCCESS {
                self.scope.launchMain {
                    invoke()
                }
            } else {
                self.scope.launchMain {
                    failed()
                }
            }
        }
    }

    
    private func cancelSession() {
        prefsTask?.cancel()
        prefsTask = nil
    }

    struct State {
        
        private(set) var homeScreen: Screen = .AUTH_SCREEN_ROUTE
        private(set) var sessionStatus: SessionStatusData = SessionStatusDataLoadingFromStorage()
        private(set) var userPref: UserPref? = nil
        private(set) var args = [Screen : any ScreenConfig]()
        
        @MainActor
        mutating func copy(
            homeScreen: Screen? = nil,
            sessionStatus: SessionStatusData? = nil,
            userPref: UserPref? = nil,
            args: [Screen : any ScreenConfig]? = nil
        ) -> Self {
            self.homeScreen = homeScreen ?? self.homeScreen
            self.sessionStatus = sessionStatus ?? self.sessionStatus
            self.userPref = userPref ?? self.userPref
            self.args = args ?? self.args
            return self
        }
        
        mutating func argOf(_ screen: Screen) -> (any ScreenConfig)? {
            return args.first { (key: Screen, value: any ScreenConfig) in
                key == screen
            }?.value
        }
        
        mutating func copy<T : ScreenConfig>(_ screen: Screen, _ screenConfig: T) -> Self {
            args[screen] = screenConfig
            return self
        }
    }

    deinit {
        prefsTask?.cancel()
        prefsTask = nil
        userJob?.cancel()
        userJob = nil
        scope.deInit()
    }
    
}
