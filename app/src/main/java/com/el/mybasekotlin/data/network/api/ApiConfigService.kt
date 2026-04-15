package com.el.mybasekotlin.data.network.api

import com.el.mybasekotlin.data.model.ConfigResponse
import retrofit2.http.GET

interface ApiConfigService {
    @GET("api_v2/menu/134.html")
    suspend fun configApp(): ConfigResponse
}
