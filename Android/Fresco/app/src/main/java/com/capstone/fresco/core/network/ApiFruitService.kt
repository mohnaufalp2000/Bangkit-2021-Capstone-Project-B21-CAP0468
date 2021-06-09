package com.capstone.fresco.core.network

import com.capstone.fresco.core.model.FruitResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiFruitService {
    @GET("api/fruit/{name}")
    fun getAll(@Path("name") name: String): Call<FruitResponse>
}