package com.pp.session01_f23apidemok.models

data class Address(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String,
    val geo: Geo,
) {
}