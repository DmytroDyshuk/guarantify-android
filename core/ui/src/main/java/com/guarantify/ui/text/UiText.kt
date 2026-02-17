package com.guarantify.ui.text

import androidx.annotation.StringRes

sealed interface UiText {
    data class Plain(val value: String) : UiText
    data class Res(@param:StringRes val id: Int, val args: List<Any> = emptyList()) : UiText
}