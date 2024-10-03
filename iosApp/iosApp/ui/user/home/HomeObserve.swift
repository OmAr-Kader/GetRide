//
//  HomeObserve.swift
//  iosApp
//
//  Created by OmAr on 02/10/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class HomeObserve : ObservableObject {
    
    @Inject
    private var project: Project
    
    private var scope = Scope()
    
    @MainActor
    @Published var state = State()
    
    
    private func setIsProcess(_ isProcess: Bool) {
        scope.launchMain {
            self.state = self.state.copy(isProcess: isProcess)
        }
    }
    
    @MainActor func setMainProcess(_ isProcess: Bool) {
        self.state = self.state.copy(isProcess: isProcess)
    }
    
    struct State {
        
        private(set) var isProcess: Bool = false
        
        @MainActor
        mutating func copy(
            isProcess: Bool? = nil
        ) -> Self {
            self.isProcess = isProcess ?? self.isProcess
            return self
        }
    }
}

/*import GoogleMaps
func asa() {
    
    if let path = GMSPath(fromEncodedPath: "") {
        let polyline = GMSPolyline(path: path)
        polyline.strokeColor = .blue
        polyline.strokeWidth = 5.0
        polyline.map = mapView // Add polyline to map
    }
}*/
