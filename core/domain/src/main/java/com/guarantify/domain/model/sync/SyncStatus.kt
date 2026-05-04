package com.guarantify.domain.model.sync

enum class SyncStatus {
    PENDING,
    READY_TO_SYNC,
    SYNCED,
    UPLOAD_IMAGE_FAILED,
    SYNC_FAILED
}