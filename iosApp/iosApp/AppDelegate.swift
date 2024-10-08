//
//  AppDelegate.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import GoogleMaps
import shared

class AppDelegate: NSObject, UIApplicationDelegate {

    private(set) var appSet: AppObserve! = nil
    
    var app: AppObserve {
        guard let appSet else {
            let ap = AppObserve()
            self.appSet = ap
            return ap
        }
        return appSet
    }
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        GMSServices.provideAPIKey(ConstSecureKt.GOOGLE_MAP_KEY)
        return true
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        appSet = nil
    }
}
