package com.example.pustaku.data

data class Users(
    val userId : String? = null,
    val userUsername : String? = null,
    val userEmail : String? = null,
    val userPassword : String? = null,
    val userLevel : Int? = null,
    val userProfilePhoto : String? = null
):java.io.Serializable
