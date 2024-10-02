import SwiftUI
import shared

extension View {
    
    @MainActor @ViewBuilder func targetScreen(
        _ target: Screen,
        _ app: AppObserve,
        navigateTo: @MainActor @escaping (Screen) -> Unit,
        navigateToScreen: @MainActor @escaping (ScreenConfig, Screen) -> Unit,
        navigateHome: @MainActor @escaping (Screen) -> Unit,
        backPress: @MainActor @escaping () -> Unit,
        screenConfig: @MainActor @escaping (Screen) -> (any ScreenConfig)?,
        findPreferenceMainBack: @MainActor (String, @BackgroundActor @escaping (String?) -> Unit) -> Unit,
        findPreferenceMainMain: @MainActor (String, @MainActor @escaping (String?) -> Unit) -> Unit,
        findPreference: @BackgroundActor (String, @BackgroundActor @escaping (String?) -> Unit) async -> Unit
    ) -> some View {
        switch target {
        case .AUTH_SCREEN_ROUTE:
            SplashScreen()
        case .HOME_SCREEN_ROUTE:
            SplashScreen()
        case .AUTH_SCREEN_DRIVER_ROUTE:
            SplashScreen()
        case .HOME_SCREEN_DRIVER_ROUTE:
            SplashScreen()
        }
    }
}

enum Screen : Hashable {
    case AUTH_SCREEN_ROUTE
    case AUTH_SCREEN_DRIVER_ROUTE
    case HOME_SCREEN_ROUTE
    case HOME_SCREEN_DRIVER_ROUTE

}

protocol ScreenConfig {}

struct HomeRoute: ScreenConfig {
    let userPref: UserPref
}
