package com.kucingselfie.jetpackdipractice.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kucingselfie.jetpackdipractice.api.ApiResponse
import retrofit2.Response

object ApiUtil {
    fun <T : Any> successCall(data: T) = createCall(Response.success(data))

    fun <T : Any> createCall(response: Response<T>) = MutableLiveData<ApiResponse<T>>().apply {
        value = ApiResponse.create(response)
    } as LiveData<ApiResponse<T>>
}