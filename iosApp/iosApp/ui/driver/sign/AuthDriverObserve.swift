//
//  AuthDriverObserve.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class AuthDriverObserve : ObservableObject {
    
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
    
    @MainActor func setCarModule(_ carModule: String) {
        self.state = self.state.copy(carModule: carModule, isErrorPressed: false)
    }
    
    @MainActor func setCarNumber(_ carNumber: String) {
        self.state = self.state.copy(carNumber: carNumber, isErrorPressed: false)
    }

    @MainActor func setCarColor(_ carColor: String) {
        self.state = self.state.copy(carColor: carColor, isErrorPressed: false)
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
        if (state.email.isEmpty || state.password.isEmpty || state.name.isEmpty || state.email.isEmpty || state.phone.isEmpty || state.carModule.isEmpty || state.carColor.isEmpty || state.carNumber.isEmpty) {
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
            let driver = Driver().copy(
                authId: userBase.authId,
                email: userBase.email,
                phone: state.phone,
                driverName: state.name
            )
            if let newDriver = try? await project.driver.addNewDriver(item: driver) {
                do {
                    try await self.project.pref.updatePref(pref: [
                        PreferenceData(keyString: ConstKt.PREF_ID, value: String(newDriver.id)),
                        PreferenceData(keyString: ConstKt.PREF_NAME, value: newDriver.driverName),
                        PreferenceData(keyString: ConstKt.PREF_PROFILE_IMAGE, value: newDriver.profilePicture)
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
                        if let driver = try? await self.project.driver.getDriverOnAuthId(authId: userPref.authId) {
                            do {
                                try await self.project.pref.updatePref(pref: [
                                    PreferenceData(keyString: ConstKt.PREF_ID, value: String(driver.id)),
                                    PreferenceData(keyString: ConstKt.PREF_NAME, value: driver.driverName),
                                    PreferenceData(keyString: ConstKt.PREF_PROFILE_IMAGE, value: driver.profilePicture)
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
        private(set) var carModule: String = ""
        private(set) var carNumber: String = ""
        private(set) var carColor: String = ""
        private(set) var password: String = ""
        private(set) var isLoginScreen: Bool = false
        private(set) var isProcess: Bool = false
        private(set) var isErrorPressed: Bool = false
        
        @MainActor
        mutating func copy(
            name: String? = nil,
            email: String? = nil,
            phone: String? = nil,
            carModule: String? = nil,
            carNumber: String? = nil,
            carColor: String? = nil,
            password: String? = nil,
            isLoginScreen: Bool? = nil,
            isProcess: Bool? = nil,
            isErrorPressed: Bool? = nil
        ) -> Self {
            self.name = name ?? self.name
            self.phone = phone ?? self.phone
            self.email = email ?? self.email
            self.carModule = carModule ?? self.carModule
            self.carNumber = carNumber ?? self.carNumber
            self.carColor = carColor ?? self.carColor
            self.password = password ?? self.password
            self.isLoginScreen = isLoginScreen ?? self.isLoginScreen
            self.isProcess = isProcess ?? self.isProcess
            self.isErrorPressed =  isErrorPressed ?? self.isErrorPressed
            return self
        }
    }
}
