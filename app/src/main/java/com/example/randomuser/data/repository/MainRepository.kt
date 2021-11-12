package com.example.randomuser.data.repository

import com.example.randomuser.data.api.ApiInterface
import javax.inject.Inject

class MainRepository @Inject constructor(private val api: ApiInterface) {
    suspend fun getProfile() = api.getUser()

}