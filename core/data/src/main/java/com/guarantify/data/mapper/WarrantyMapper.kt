package com.guarantify.data.mapper

import com.guarantify.data.network.dto.WarrantyDto
import com.guarantify.domain.model.Warranty
import java.time.LocalDate

fun Warranty.toDto(): WarrantyDto {
    return WarrantyDto(
        id = this.id,
        userId = this.userId,
        title = this.title,
        purchaseDate = this.purchaseDate.toString(),
        warrantyPeriod = this.warrantyPeriod,
        expirationDate = this.expirationDate.toString(),
        shopName = this.shopName,
        photoUrl = this.photoUrl,
        notes = this.notes,
        isSynced = this.isSynced
    )
}

@Suppress("NewApi")
fun WarrantyDto.toDomain(): Warranty {
    return Warranty(
        id = id,
        userId = userId,
        title = title,
        purchaseDate = LocalDate.parse(purchaseDate),
        warrantyPeriod = warrantyPeriod,
        expirationDate = LocalDate.parse(expirationDate),
        shopName = shopName,
        photoUrl = photoUrl,
        notes = notes,
        isSynced = isSynced
    )
}