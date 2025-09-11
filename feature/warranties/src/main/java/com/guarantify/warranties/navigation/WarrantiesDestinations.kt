package com.guarantify.warranties.navigation

import kotlinx.serialization.Serializable

sealed class WarrantiesDestinations {

    @Serializable
    data object WarrantiesScreen : WarrantiesDestinations()

//    @Serializable
//    data object WarrantyDetails : WarrantiesDestinations()

}