package com.example.jedicouncilchallenge.core.presentation

import com.example.jedicouncilchallenge.R
import com.example.jedicouncilchallenge.core.domain.DataError

fun DataError.toUiText(): UiText = when (this) {
    DataError.Network.NO_INTERNET -> UiText.StringResource(R.string.error_no_internet)
    DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(R.string.error_timeout)
    DataError.Network.SERVER_ERROR -> UiText.StringResource(R.string.error_server)
    DataError.Network.NOT_FOUND -> UiText.StringResource(R.string.error_not_found)
    DataError.Network.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
    DataError.Parsing.MALFORMED_RESPONSE -> UiText.StringResource(R.string.error_unknown)
    DataError.Local.DISK_FULL -> UiText.StringResource(R.string.error_unknown)
    DataError.Local.UNKNOWN -> UiText.StringResource(R.string.error_unknown)
}
