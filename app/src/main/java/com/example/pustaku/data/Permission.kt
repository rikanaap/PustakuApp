package com.example.pustaku.data

data class Permission(
    var userId: String? = null,
    var userEmail: String? = null,
    var permissionReason: String? = null,
    var permissionImage: String? = null,
    var alreadyAsking : String? = null
):java.io.Serializable
