# Get Ride Demo

**GetRide** is a multi-platform mobile application developed with Kotlin Multiplatform Mobile (KMM), offering a full-featured ride-sharing service. The app integrates advanced ride management, Stripe payment processing, and Google Maps for navigation, while leveraging Supabase for real-time data handling and secure authentication. With support for both Android (Jetpack Compose) and iOS (SwiftUI), GetRide delivers a seamless user experience across platforms.

## Features

### Core Functionality

- **User Authentication**: GetRide uses **Supabase Authentication** to securely manage user sign-up, sign-in, and session handling across devices.
- **Ride Management**: Users can request and track rides in real-time. Drivers receive filtered ride requests that show only nearby clients, making the process more efficient.
- **Driver Rating System**: Users can rate drivers after completing a trip. The driver's rating is updated instantly using a custom SQL function.
- **Trips and Payment History**: Users can access a detailed history of their past trips and payment transactions within the app.
- **Stripe Payment Integration**: Payments are processed securely using Stripe, enabling users to complete transactions with ease.


### Google Maps API Integration

- **Route Calculation**: Fetches and decodes optimized routes between start and end locations for smooth navigation during rides.
- **Location Search**: Users can search for places using text input, improving the flexibility of destination choices.
- **Reverse Geocoding**: Fetches place names and details from latitude and longitude coordinates, ensuring accurate location services.


### Supabase Integration

- **Authentication**: Secure user authentication using Supabase, allowing for email-based sign-ups, logins, and session persistence.
- **Real-time Data**: Supabase handles real-time synchronization of ride requests, trip updates, and other user interactions.
- **Data Storage**: All user and trip-related data are stored securely, with robust permissions and scalability using Supabase.


### Multi-Platform Support

GetRide is built using **Kotlin Multiplatform Mobile (KMM)** for shared business logic across Android and iOS, while using native UI frameworks for each platform:
- **Android**: The app uses **Jetpack Compose** for a modern and reactive user interface.
- **iOS**: The app is powered by **SwiftUI**, providing a smooth and native experience for iOS users.

### Technologies Used

- **Kotlin Multiplatform Mobile (KMM)**: Shared business logic for iOS and Android platforms.
- **Jetpack Compose**: Modern UI toolkit for Android development.
- **SwiftUI**: Native UI framework for iOS development.
- **Supabase**: Handles authentication, real-time data, and secure backend storage.
- **Stripe API**: Processes payments and manages transactions.
- **Google Maps API**: Provides route calculation, location search, and reverse geocoding.
- **Ktor & kotlinx.serialization**: For API communication and data serialization.


## Installation

To install and run **GetRide** locally, follow these steps:
1. **Clone the repository**:
    ```bash
    git clone https://github.com/OmAr-Kader/GetRide.git
    ```
2. **Open the project** in Android Studio or any IDE that supports Kotlin Multiplatform.
3. **Sync dependencies** to ensure all libraries are installed.
4. **Configure Supabase and Stripe**:
    - Create a Supabase project and retrieve your API URL and API key.
    - Set up a Stripe account and obtain your Stripe API key and account ID.
    - Add these configurations to your `local.properties` file:
    ```properties
    SUPABASE_URL=your_supabase_url
    SUPABASE_API_KEY=your_supabase_api_key
    STRIPE_API_KEY=your_stripe_api_key
    STRIPE_ACCOUNT_ID=your_stripe_account_id
    MAPS_API_KEY=your_google_maps_api_key
    ```
5. **Run the application**:
    - **For Android**: Run the app on an emulator or physical device.
    - **For iOS**: Open the project in Xcode, build, and run it on a simulator or a connected iPhone.


## Contribution

We welcome contributions to improve GetRide! To contribute:
1. Fork the repository.
2. Create a feature branch.
3. Submit a pull request with a detailed description of your changes.

## 🔗 Links & Dependencies

[![Kotlin Multiplatform Mobile](https://img.shields.io/static/v1?style=for-the-badge&message=Mobile&color=7F52FF&logo=Kotlin&logoColor=FFFFFF&label=Kotlin+Multiplatform)](https://kotlinlang.org/docs/multiplatform.html)

[![Google Map SDK IOS](https://img.shields.io/static/v1?style=for-the-badge&message=Google+Maps+IOS+SDK&color=0087f4&logo=googlemaps&logoColor=FFFFFF&label=)](https://developers.google.com/maps/documentation/ios-sdk/overview)

[![Google Map SDK Android](https://img.shields.io/static/v1?style=for-the-badge&message=Google+Maps+Android+SDK&color=0075e8&logo=googlemaps&logoColor=FFFFFF&label=)](https://developers.google.com/maps/documentation/android-sdk/overview)

[![Android Studio](https://img.shields.io/static/v1?style=for-the-badge&message=Android+Studio&color=222222&logo=Android+Studio&logoColor=3DDC84&label=)](https://developer.android.com/studio?gclsrc=aw.ds)

[![Xcode](https://img.shields.io/static/v1?style=for-the-badge&message=Xcode&color=147EFB&logo=Xcode&logoColor=FFFFFF&label=)](https://developer.apple.com/documentation/xcode)

[![IOS](https://img.shields.io/static/v1?style=for-the-badge&message=iOS&color=000000&logo=iOS&logoColor=FFFFFF&label=)](https://developer.apple.com/tutorials/app-dev-training)

[![SwiftUi](https://img.shields.io/static/v1?style=for-the-badge&message=Swift-ui&color=F05138&logo=Swift&logoColor=FFFFFF&label=)](https://developer.apple.com/xcode/swiftui/)

[![Compose](https://img.shields.io/static/v1?style=for-the-badge&message=Jetpack+Compose&color=4285F4&logo=Jetpack+Compose&logoColor=FFFFFF&label=)](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-getting-started.html#join-the-community)

[![koin Dependency Injection](https://img.shields.io/static/v1?style=for-the-badge&message=Dependency%20Injection&color=222222&logo=koin&logoColor=3DDC84&label=koin)](https://github.com/InsertKoinIO/koin)

[![Swinject Dependency Injection](https://img.shields.io/static/v1?style=for-the-badge&message=Dependency+Injection&color=ffa841&logo=koin&logoColor=222222&label=Swinject)](https://github.com/Swinject/Swinject)

[![Supabase](https://img.shields.io/static/v1?style=for-the-badge&message=Supabase&color=47A248&logo=Supabase&logoColor=FFFFFF&label=)](https://supabase.com/docs/reference/kotlin/installing)

<!--suppress CheckImageSize -->
Screenshot
-------------

### Android

#### Client
<table>
    <tr>
      <td> <img src="Screenshots/Android/1.png"  width="300" height="667" alt="1"/> </td>
      <td> <img src="Screenshots/Android/2.png"  width="300" height="667" alt="8"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/Android/3.png"  width="300" height="667" alt="2"/> </td>
      <td> <img src="Screenshots/Android/4.png"  width="300" height="667" alt="3"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/Android/5.png"  width="300" height="667" alt="4"/> </td>
      <td> <img src="Screenshots/Android/6.png"  width="300" height="667" alt="5"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/Android/7.png"  width="300" height="667" alt="6"/> </td>
    </tr>
</table>

#### Driver
<table>
    <tr>
      <td> <img src="Screenshots/Android/8.png"  width="300" height="480" alt="1"/> </td>
      <td> <img src="Screenshots/Android/9.png"  width="300" height="480" alt="8"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/Android/10.png"  width="300" height="480" alt="2"/> </td>
      <td> <img src="Screenshots/Android/11.png"  width="300" height="480" alt="3"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/Android/12.png"  width="300" height="480" alt="4"/> </td>
      <td> <img src="Screenshots/Android/13.png"  width="300" height="480" alt="5"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/Android/14.png"  width="300" height="480" alt="6"/> </td>
    </tr>
</table>


### IOS

#### Client
<table>
    <tr>
      <td> <img src="Screenshots/IOS/1.png"  width="300" height="533" alt="1"/> </td>
      <td> <img src="Screenshots/IOS/2.png"  width="300" height="533" alt="8"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/IOS/3.png"  width="300" height="533" alt="2"/> </td>
      <td> <img src="Screenshots/IOS/4.png"  width="300" height="533" alt="3"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/IOS/5.png"  width="300" height="533" alt="4"/> </td>
      <td> <img src="Screenshots/IOS/6.png"  width="300" height="533" alt="5"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/IOS/7.png"  width="300" height="533" alt="6"/> </td>
    </tr>
</table>

#### Driver
<table>
    <tr>
      <td> <img src="Screenshots/IOS/8.png"  width="300" height="533" alt="1"/> </td>
      <td> <img src="Screenshots/IOS/9.png"  width="300" height="533" alt="8"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/IOS/10.png"  width="300" height="533" alt="2"/> </td>
      <td> <img src="Screenshots/IOS/11.png"  width="300" height="533" alt="3"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/IOS/12.png"  width="300" height="533" alt="4"/> </td>
      <td> <img src="Screenshots/IOS/13.png"  width="300" height="533" alt="5"/> </td>
    </tr>
    <tr>
      <td> <img src="Screenshots/IOS/14.png"  width="300" height="533" alt="6"/> </td>
    </tr>
</table>
