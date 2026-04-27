package com.example.jedicouncilchallenge.core.data

import com.example.jedicouncilchallenge.core.domain.DataError
import com.example.jedicouncilchallenge.core.domain.Result
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend inline fun <T> safeCall(execute: () -> T): Result<T, DataError.Network> {
    return try {
        Result.Success(execute())
    } catch (e: UnknownHostException) {
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: SocketTimeoutException) {
        Result.Error(DataError.Network.REQUEST_TIMEOUT)
    } catch (e: HttpException) {
        Result.Error(
            when (e.code()) {
                404 -> DataError.Network.NOT_FOUND
                in 500..599 -> DataError.Network.SERVER_ERROR
                else -> DataError.Network.UNKNOWN
            }
        )
    } catch (e: Exception) {
        Result.Error(DataError.Network.UNKNOWN)
    }
}
