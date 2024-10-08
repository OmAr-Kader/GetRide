//
//  Defaults.swift
//  iosApp
//
//  Created by OmAr on 14/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import GoogleMaps
import shared

extension MapData {
    
    func copy(currentLocation: CLLocationCoordinate2D? = nil, driverPoint: CLLocationCoordinate2D? = nil, startPoint: CLLocationCoordinate2D? = nil, fromText: String? = nil, endPoint: CLLocationCoordinate2D? = nil, toText: String? = nil, durationDistance: String? = nil, routePoints: String? = nil) -> MapData {
        MapData(currentLocation: currentLocation ?? self.currentLocation, driverPoint: driverPoint ?? self.driverPoint, startPoint: startPoint ?? self.startPoint, fromText: fromText ?? self.fromText, endPoint: endPoint ?? self.endPoint, toText: toText ?? self.toText, durationDistance: durationDistance ?? self.durationDistance, routePoints: routePoints ?? self.routePoints)
    }
}

///

extension UserPref {
    
    func copy(authId: String? = nil, id: Int64? = nil, email: String? = nil, phone: String? = nil, name: String? = nil, profilePicture: String? = nil) -> UserPref {
        return UserPref(authId: authId ?? self.authId, id: id ?? self.id, email: email ?? self.email, phone: phone ?? self.phone, name: name ?? self.name, profilePicture: profilePicture ?? self.profilePicture)
    }
}
    
extension User {
    
    func copy(id: Int64? = nil, authId: String? = nil, email: String? = nil, phone: String? = nil, name: String? = nil, profilePicture: String? = nil) -> User {
        return User(id: id ?? self.id, authId: authId ?? self.authId, email: email ?? self.email, phone: phone ?? self.phone, name: name ?? self.name, profilePicture: profilePicture ?? self.profilePicture)
    }
}

extension Driver {
    
    func copy(id: Int64? = nil, authId: String? = nil, email: String? = nil, phone: String? = nil, driverName: String? = nil, car: DriverCar? = nil, profilePicture: String? = nil) -> Driver {
        return Driver(id: id ?? self.id, authId: authId ?? self.authId, email: email ?? self.email, phone: phone ?? self.phone, driverName: driverName ?? self.driverName, car: car ?? self.car, profilePicture: profilePicture ?? self.profilePicture)
    }
}

extension DriverCar {
    
    func copy(driverCar: String? = nil, driverCarNumber: String? = nil, driverCarColor: String? = nil) -> DriverCar {
        return DriverCar(driverCar: driverCar ?? self.driverCar, driverCarNumber: driverCarNumber ?? self.driverCarNumber, driverCarColor: driverCarColor ?? self.driverCarColor)
    }
}

extension Ride {
    
    func copy(id: Int64? = nil, userId: Int64? = nil, driverId: Int64? = nil, from: Location? = nil, to: Location? = nil, currentDriver: Location? = nil, fare: Double? = nil, status: Int32? = nil, date: String? = nil, durationDistance: String? = nil, driverName: String? = nil) -> Ride {
        Ride(id: id ?? self.id, userId: userId ?? self.userId, driverId: driverId ?? self.driverId, from: from ?? self.from, to: to ?? self.to, currentDriver: currentDriver ?? self.currentDriver, fare: fare ?? self.fare, status: status ?? self.status, date: date ?? self.date, durationDistance: durationDistance ?? self.durationDistance, driverName: driverName ?? self.driverName)
    }
}
