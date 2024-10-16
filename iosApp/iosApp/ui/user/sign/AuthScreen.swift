//
//  AuthScreen.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI

struct AuthScreen : View {
    
    @StateObject var app: AppObserve
    
    @Inject
    private var theme: Theme
    
    @StateObject private var obs: AuthObserve = AuthObserve()
    @State private var toast: Toast? = nil
    
    var body: some View {
        let state = obs.state
        ZStack(alignment: .center) {
            VStack(alignment: .center) {
                Spacer()
                Text(state.isLoginScreen ? "Login" : "Sign Up")
                    .font(.headline)
                    .foregroundColor(theme.textColor)
                Spacer().frame(height: 16)
                if !state.isLoginScreen {
                    OutlinedTextField(text: state.name, onChange: obs.setName, hint: "Name", isError: false, errorMsg: "Name Is Empty", theme: theme, cornerRadius: 12, lineLimit: 1, keyboardType: UIKeyboardType.alphabet).transition(.opacity)
                    Spacer().frame(height: 16)
                    OutlinedTextField(text: state.phone, onChange: obs.setPhone, hint: "Phone", isError: false, errorMsg: "Phone Is Empty", theme: theme, cornerRadius: 12, lineLimit: 1, keyboardType: UIKeyboardType.phonePad).transition(.opacity)
                    Spacer().frame(height: 16)
                }
                OutlinedTextField(text: state.email, onChange: obs.setEmail, hint: "Email", isError: false, errorMsg: "Email Is Empty", theme: theme, cornerRadius: 12, lineLimit: 1, keyboardType: UIKeyboardType.emailAddress)
                Spacer().frame(height: 16)
                OutlinedSecureField(text: state.password, onChange: obs.setPassword, hint: "Password", isError: false, errorMsg: "Password", theme: theme, cornerRadius: 12)
                Button(action: {
                    if state.isLoginScreen {
                        obs.loginUser {
                            app.findUser { user in
                                obs.setMainProcess(false)
                                if user != nil {
                                    app.navigateHome(.HOME_SCREEN_ROUTE)
                                } else {
                                    toast = Toast(style: .error, message: "Failed")
                                }
                            }
                        } failed: {
                            toast = Toast(style: .error, message: "Failed")
                        }
                    } else {
                        obs.createNewUser {
                            app.findUser { user in
                                obs.setMainProcess(false)
                                if user != nil {
                                    app.navigateHome(.HOME_SCREEN_ROUTE)
                                } else {
                                    toast = Toast(style: .error, message: "Failed")
                                }
                            }
                        } failed: {
                            toast = Toast(style: .error, message: "Failed")
                        }
                    }
                }, label: {
                    Text(state.isLoginScreen ? "Login" : "Sign Up")
                        .font(.headline)
                        .foregroundColor(Color.black)
                }).padding()
                    .background(RoundedRectangle(cornerRadius: 15).fill(theme.primary))
                Spacer().frame(height: 16)
                Button(action: {
                    withAnimation {
                        obs.toggleScreen()
                    }
                }, label: {
                    Text(state.isLoginScreen ? "Don't have an account? Sign Up" : "Already have an account? Login")
                        .foregroundColor(theme.textColor)
                })
                Spacer()
            }.padding()
            LoadingScreen(isLoading: state.isProcess)
        }.toolbar(.hidden).background(theme.background).toastView(toast: $toast, backColor: theme.background)
    }
}
