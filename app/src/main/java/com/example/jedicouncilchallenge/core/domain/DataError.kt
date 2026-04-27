package com.example.jedicouncilchallenge.core.domain

sealed interface DataError : Error {
    enum class Network : DataError {
        NO_INTERNET, REQUEST_TIMEOUT, SERVER_ERROR, NOT_FOUND, UNKNOWN
    }
    enum class Parsing : DataError { MALFORMED_RESPONSE }
    enum class Local : DataError { DISK_FULL, UNKNOWN }
}
