package com.jk.share.models

import java.util.UUID

data class User(
    var id: String = UUID.randomUUID().toString(),
    var email : String = "",
    var password : String = "",
    var name : String = "",
    var phoneNumber : String = ""
)