//
//  AuthObserve.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class AuthObserve : ObservableObject {
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    @MainActor func setName(_ name: String) {
        self.state = self.state.copy(name: name, isErrorPressed: false)
    }
    
    @MainActor func setPhone(_ phone: String) {
        self.state = self.state.copy(phone: phone, isErrorPressed: false)
    }

    @MainActor func setEmail(_ email: String) {
        self.state = self.state.copy(email: email, isErrorPressed: false)
    }

    @MainActor func setPassword(_ password: String) {
        self.state = self.state.copy(password: password, isErrorPressed: false)
    }

    @MainActor func toggleScreen() {
        self.state = self.state.copy(isLoginScreen: !state.isLoginScreen, isErrorPressed: false)
    }
    
    @MainActor
    func createNewUser(invoke: @escaping @MainActor () -> Unit, failed: @escaping @MainActor () -> Unit) {
        let state = state
        if (state.email.isEmpty || state.password.isEmpty || state.name.isEmpty || state.email.isEmpty || state.phone.isEmpty) {
            setIsError(true)
            return
        }
        setMainProcess(true)
        scope.launchBack {
            do {
                try await AuthKt.registerAuth(
                    user: UserPref().copy(
                        email: state.email,
                        name: state.name
                    ), passwordUser: state.password, invoke: { _ in
                        self.scope.launchBack {
                            await self.doSignUp(state, invoke, failed)
                        }
                    }, failed: {
                        self.failedAuth(failed)
                    })
            } catch {
                self.failedAuth(failed)
            }
        }
    }
    
    @BackgroundActor
    private func doSignUp(_ state: State,_ invoke: @escaping @MainActor () -> Unit,_ failed: @escaping @MainActor () -> Unit) async {
        if let userBase = try? await AuthKt.userInfo() {
            let user = User().copy(
                authId: userBase.authId,
                email: userBase.email,
                phone: state.phone,
                name: state.name
            )
            if let newUser = try? await project.user.addNewUser(item: user) {
                do {
                    try await self.project.pref.updatePref(pref: [
                        PreferenceData(keyString: ConstKt.PREF_ID, value: String(newUser.id)),
                        PreferenceData(keyString: ConstKt.PREF_NAME, value: newUser.name),
                        PreferenceData(keyString: ConstKt.PREF_PROFILE_IMAGE, value: newUser.profilePicture)
                    ])
                    self.scope.launchMain {
                        invoke()
                    }
                } catch let e {
                    loggerError("updatePref", e.localizedDescription)
                    self.scope.launchMain {
                        invoke()
                    }
                }
            } else {
                self.failedAuth(failed)
            }
        } else {
            self.failedAuth(failed)
        }
    }

    
    @MainActor
    func loginUser(invoke: @escaping @MainActor () -> Unit, failed: @escaping @MainActor () -> Unit) {
        let state = state
        if (state.email.isEmpty || state.password.isEmpty) {
            setIsError(true)
            return
        }
        setMainProcess(true)
        scope.launchBack {
            await self.doLogin(state, invoke, failed)
        }
    }
    
    
    @BackgroundActor
    private func doLogin(_ state: State,_ invoke: @escaping @MainActor () -> Unit,_ failed: @escaping @MainActor () -> Unit)  async {
        do {
            try await AuthKt.signInAuth(emailUser: state.email, passwordUser: state.password, invoke: {
                self.scope.launchBack {
                    if let userPref = try? await AuthKt.userInfo() {
                        if let user = try? await self.project.user.getUserOnAuthId(authId: userPref.authId) {
                            do {
                                try await self.project.pref.updatePref(pref: [
                                    PreferenceData(keyString: ConstKt.PREF_ID, value: String(user.id)),
                                    PreferenceData(keyString: ConstKt.PREF_NAME, value: user.name),
                                    PreferenceData(keyString: ConstKt.PREF_PROFILE_IMAGE, value: user.profilePicture)
                                ])
                                self.scope.launchMain {
                                    invoke()
                                }
                            } catch let e {
                                loggerError("updatePref", e.localizedDescription)
                                self.scope.launchMain {
                                    invoke()
                                }
                            }
                        } else {
                            self.failedAuth(failed)
                        }
                    } else {
                        self.failedAuth(failed)
                    }
                }
            }, failed: { _ in
                self.failedAuth(failed)
            })
        } catch {
            self.failedAuth(failed)
        }
    }
    
    @BackgroundActor
    private func failedAuth(_ failed: @escaping @MainActor () -> Unit) {
        self.scope.launchMain {
            self.setMainProcess(false)
            failed()
        }
    }
    
    @MainActor
    private func setIsError(_ isError: Bool) {
        self.state = self.state.copy(isErrorPressed: isError)
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
        
        private(set) var name: String = ""
        private(set) var email: String = ""
        private(set) var phone: String = ""
        private(set) var password: String = ""
        private(set) var isLoginScreen: Bool = false
        private(set) var isProcess: Bool = false
        private(set) var isErrorPressed: Bool = false
        
        @MainActor
        mutating func copy(updates: (inout Self) -> Void) -> Self { // Only helpful For struct or class with nil values
            updates(&self)
            return self
        }
        
        @MainActor
        mutating func copy(
            name: String? = nil,
            phone: String? = nil,
            email: String? = nil,
            password: String? = nil,
            isLoginScreen: Bool? = nil,
            isProcess: Bool? = nil,
            isErrorPressed: Bool? = nil
        ) -> Self {
            self.name = name ?? self.name
            self.phone = phone ?? self.phone
            self.email = email ?? self.email
            self.password = password ?? self.password
            self.isLoginScreen = isLoginScreen ?? self.isLoginScreen
            self.isProcess = isProcess ?? self.isProcess
            self.isErrorPressed =  isErrorPressed ?? self.isErrorPressed
            return self
        }
    }
    
}
