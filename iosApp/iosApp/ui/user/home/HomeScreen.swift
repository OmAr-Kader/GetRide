//
//  HomeScreen.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import SwiftUI
import shared

struct HomeScreen : View {
    
    let userPref: UserPref
    let findPreferenceMainBack: @MainActor (String, @BackgroundActor @escaping (String?) -> Unit) -> Unit
    let navigateToScreen: @MainActor (ScreenConfig, Screen) -> Unit
    let navigateHome: @MainActor (Screen) -> Unit
    
    @Inject
    private var theme: Theme
    
    @StateObject private var obs: HomeObserve = HomeObserve()
    @State private var toast: Toast? = nil
    
    @MainActor
    @State private var isOpen = false
    
    var body: some View {
        let state = obs.state
        ZStack(alignment: .topLeading) {
            DrawerView(isOpen: $isOpen, overlayColor: shadowColor) {
                ZStack(alignment: .topLeading) {
                }/*.sheet(isPresented: Binding(get: {
                    state.isComment
                }, set: { it in
                    obs.hide()
                })) {
                    /*CommentBottomSheet(memeLord: state.commentMeme, commentText: state.commentText, onValueComment: obs.onValueComment, onComment: { it in
                        obs.onComment(userBase: userBase, postId: it.post.id) {
                            
                        }
                    })*/
                }*///.presentationDetents([.medium, .custom(CommentSheetDetent.self)])
                    //.presentationBackground(theme.backDark)
                    //.presentationContentInteraction(.scrolls)
                    //.interactiveDismissDisabled()
                    
            } drawer: {
                DrawerContainer {
                    DrawerText(
                        itemColor: theme.primary,
                        text: "Curso",
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
            LoadingBar(isLoading: state.isProcess)
        }.background(theme.background).toastView(toast: $toast, backColor: theme.backDark)
    }
}
