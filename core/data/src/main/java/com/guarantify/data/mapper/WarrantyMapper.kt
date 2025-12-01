package com.guarantify.data.mapper

import com.guarantify.data.database.entity.WarrantyEntity
import com.guarantify.data.network.dto.WarrantyDto
import com.guarantify.domain.model.Warranty
import java.time.LocalDate
import java.util.UUID

fun Warranty.toDto(): WarrantyDto {
    return WarrantyDto(
        id = this.id,
        userId = this.userId,
        title = this.title,
        purchaseDate = this.purchaseDate.toString(),
        expirationDate = this.expirationDate.toString(),
        storeName = this.storeName,
        brand = this.brand,
        amount = this.amount,
        photoUrl = this.photoUrl,
        notes = this.notes,
        updatedAt = this.updatedAt
    )
}

fun Warranty.toEntityWithGeneratedIdIfNeeded(): WarrantyEntity {
    val localId = this.id.ifBlank { UUID.randomUUID().toString() }

    return WarrantyEntity(
        id = localId,
        userId = this.userId,
        title = this.title,
        purchaseDate = this.purchaseDate,
        expirationDate = this.expirationDate,
        storeName = this.storeName,
        brand = this.brand,
        amount = this.amount,
        photoUrl = this.photoUrl,
        notes = this.notes,
        updatedAt = System.currentTimeMillis(),
        isSynced = false
    )
}

fun WarrantyDto.toEntity(): WarrantyEntity {
    return WarrantyEntity(
        id = this.id,
        userId = this.userId,
        title = this.title,
        purchaseDate = LocalDate.parse(this.purchaseDate),
        expirationDate = LocalDate.parse(this.expirationDate),
        storeName = this.storeName,
        brand = this.brand,
        amount = this.amount,
        photoUrl = this.photoUrl,
        notes = this.notes,
        updatedAt = this.updatedAt
    )
}

fun WarrantyEntity.toDto(): WarrantyDto {
    return WarrantyDto(
        id = this.id,
        userId = this.userId,
        title = this.title,
        purchaseDate = this.purchaseDate.toString(),
        expirationDate = this.expirationDate.toString(),
        storeName = this.storeName,
        brand = this.brand,
        amount = this.amount,
        photoUrl = this.photoUrl,
        notes = this.notes,
        updatedAt = this.updatedAt
    )
}

fun WarrantyEntity.toDomain(): Warranty {
    return Warranty(
        id = this.id,
        userId = this.userId,
        title = this.title,
        purchaseDate = this.purchaseDate,
        expirationDate = this.expirationDate,
        storeName = this.storeName,
        brand = this.brand,
        amount = this.amount,
        photoUrl = this.photoUrl,
        notes = this.notes,
        updatedAt = this.updatedAt
    )
}