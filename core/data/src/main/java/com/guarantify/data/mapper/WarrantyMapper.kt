package com.guarantify.data.mapper

import com.guarantify.data.database.entity.WarrantyEntity
import com.guarantify.data.network.dto.WarrantyDto
import com.guarantify.domain.model.Warranty
import java.time.LocalDate

fun Warranty.toDto(): WarrantyDto {
    return WarrantyDto(
        localId = this.localId,
        remoteId = this.remoteId,
        userId = this.userId,
        title = this.title,
        purchaseDate = this.purchaseDate.toString(),
        warrantyPeriod = this.warrantyPeriod,
        expirationDate = this.expirationDate.toString(),
        shopName = this.shopName,
        photoUrl = this.photoUrl,
        notes = this.notes
    )
}

fun Warranty.toEntity(): WarrantyEntity {
    return WarrantyEntity(
        remoteId = this.remoteId,
        userId = this.userId,
        title = this.title,
        purchaseDate = this.purchaseDate.toString(),
        warrantyPeriod = this.warrantyPeriod,
        expirationDate = this.expirationDate.toString(),
        shopName = this.shopName,
        photoUrl = this.photoUrl,
        notes = this.notes
    )
}

@Suppress("NewApi")
fun WarrantyDto.toDomain(): Warranty {
    return Warranty(
        localId = localId,
        remoteId = remoteId,
        userId = userId,
        title = title,
        purchaseDate = LocalDate.parse(purchaseDate),
        warrantyPeriod = warrantyPeriod,
        expirationDate = LocalDate.parse(expirationDate),
        shopName = shopName,
        photoUrl = photoUrl,
        notes = notes
    )
}

fun WarrantyDto.toEntity(): WarrantyEntity {
    return WarrantyEntity(
        localId = this.localId,
        remoteId = this.remoteId,
        userId = this.userId,
        title = this.title,
        purchaseDate = this.purchaseDate,
        warrantyPeriod = this.warrantyPeriod,
        expirationDate = this.expirationDate,
        shopName = this.shopName,
        photoUrl = this.photoUrl,
        notes = this.notes
    )
}

fun WarrantyEntity.toDto(): WarrantyDto {
    return WarrantyDto(
        localId = this.localId,
        remoteId = this.remoteId,
        userId = this.userId,
        title = this.title,
        purchaseDate = this.purchaseDate,
        warrantyPeriod = this.warrantyPeriod,
        expirationDate = this.expirationDate,
        shopName = this.shopName,
        photoUrl = this.photoUrl,
        notes = this.notes
    )
}

@Suppress("NewApi")
fun WarrantyEntity.toDomain(): Warranty {
    return Warranty(
        localId = this.localId,
        remoteId = this.remoteId,
        userId = this.userId,
        title = this.title,
        purchaseDate = LocalDate.parse(this.purchaseDate),
        warrantyPeriod = this.warrantyPeriod,
        expirationDate = LocalDate.parse(this.expirationDate),
        shopName = this.shopName,
        photoUrl = this.photoUrl,
        notes = this.notes
    )
}