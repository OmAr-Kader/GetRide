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
        findPreferenceMainBack: @escaping @MainActor (String, @BackgroundActor @escaping (String?) -> Unit) -> Unit,
        findPreferenceMainMain: @escaping @MainActor (String, @MainActor @escaping (String?) -> Unit) -> Unit,
        findPreference: @escaping @BackgroundActor (String, @BackgroundActor @escaping (String?) -> Unit) -> Unit
    ) -> some View {
        switch target {
        case .AUTH_SCREEN_ROUTE:
            AuthScreen(app: app)
        case .HOME_SCREEN_ROUTE:
            HomeScreen(userPref: app.state.userPref ?? UserPref(), findPreferenceMainBack: findPreferenceMainBack, navigateToScreen: navigateToScreen, navigateHome: navigateHome)
        case .AUTH_SCREEN_DRIVER_ROUTE:
            AuthDriverScreen(app: app)
        case .HOME_SCREEN_DRIVER_ROUTE:
            HomeDriverScreen(userPref: app.state.userPref ?? UserPref(), findPreference: findPreference, navigateToScreen: navigateToScreen, navigateHome: navigateHome)
        }
    }
}

let TEMP_IS_DRIVER = true

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
