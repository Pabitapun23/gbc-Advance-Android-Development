package com.pp.session01_f23apidemok.api

import com.pp.session01_f23apidemok.models.User
import retrofit2.http.GET
import retrofit2.http.Path


interface MyInterface {
    // @GET, @PATH = are called annotation
    // suspend - keyword that means this function should run in the background
    @GET("/users")
    suspend fun getAllUsers(): List<User>

    // ENDPOINT: https://jsonplaceholder.typicode.com/users/4
    @GET("/users/4")
    suspend fun getSingleUser(): User

    // Option 1:
    @GET("/users/3")
    suspend fun getSingleUser3(): User

    @GET("/users/2")
    suspend fun getSingleUser2(): User

    @GET("/users/1")
    suspend fun getSingleUser1(): User

    // option 2:
    // Programmatically specify which user to retrieve:
    @GET("/users/{id}")
    suspend fun getUserById(@Path("id") selectedUserId:Int): User

}
