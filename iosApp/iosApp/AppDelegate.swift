//
//  AppDelegate.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

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
    
    
    func applicationWillTerminate(_ application: UIApplication) {
        appSet = nil
    }
}
