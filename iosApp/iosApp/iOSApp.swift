import SwiftUI

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    @State var isInjected: Bool = false
    
    var body: some Scene {
        WindowGroup {
            ZStack {
                if isInjected {
                    Main(app: delegate.app)
                } else {
                    SplashScreen().task {
                        await initModule()
                        let _ = await Task { @MainActor in
                            delegate.app.findUserLive { it in
                                logger(String(isInjected))
                                if !isInjected {
                                    withAnimation {
                                        delegate.app.navigateHomeNoAnimation(
                                            it == nil ? (TEMP_IS_DRIVER ? .AUTH_SCREEN_DRIVER_ROUTE : .AUTH_SCREEN_ROUTE) : (TEMP_IS_DRIVER ? .HOME_SCREEN_DRIVER_ROUTE : .HOME_SCREEN_ROUTE)
                                        )
                                        isInjected.toggle()
                                    }
                                }
                            }
                        }.result
                    }
                }
            }
        }
    }
}
