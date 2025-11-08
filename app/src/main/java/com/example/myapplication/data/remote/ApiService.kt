package com.example.myapplication.data.remote

import retrofit2.http.GET

data class UserDto(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String? = null,
    val website: String? = null,
    val address: Address? = null,
    val company: Company? = null
) {
    data class Address(val city: String? = null)
    data class Company(val name: String? = null)
}

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<UserDto>
}
