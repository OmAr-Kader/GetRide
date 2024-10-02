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
    
