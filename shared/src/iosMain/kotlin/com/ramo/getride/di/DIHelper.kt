package com.ramo.getride.di

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Needed only for iOS to be able to use Koin-injected classes
 */
class DIHelper : KoinComponent {
    val project: Project by inject()
}