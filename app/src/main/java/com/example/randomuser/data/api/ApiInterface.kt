package com.example.randomuser.data.api

import com.example.randomuser.data.models.User
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("/api")
    suspend fun getUser(): Response<User>
}