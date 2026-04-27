package com.example.jedicouncilchallenge.core.presentation

import android.content.Context
import androidx.annotation.StringRes

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = emptyArray()
    ) : UiText

    fun asString(context: Context): String = when (this) {
        is DynamicString -> value
        is StringResource -> context.getString(id, *args)
    }
}
