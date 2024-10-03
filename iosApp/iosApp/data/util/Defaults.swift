//
//  Defaults.swift
//  iosApp
//
//  Created by OmAr on 14/08/2024.
//  Copyright © 2024 orgName. All rights reserved.
//

import shared

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

