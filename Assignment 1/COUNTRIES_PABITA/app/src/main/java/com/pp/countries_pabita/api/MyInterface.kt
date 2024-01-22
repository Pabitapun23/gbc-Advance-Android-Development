package com.pp.countries_pabita.api

import com.pp.countries_pabita.models.Country
import retrofit2.http.GET

interface MyInterface {

    @GET("/v3.1/independent")
    suspend fun getAllCountries():List<Country>
}