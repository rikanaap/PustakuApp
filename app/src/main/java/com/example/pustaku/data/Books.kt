package com.example.pustaku.data

data class Books(
    val bookId : String? = null,
    val imageid : String? = null,
    val title : String? = null,
    val shortdesc : String? = null,
    val chapter : String? = null,
    val chaptertitle : String? =  null,
    val booktext : String? = null,
    val recommend : Boolean? = null,
    val publisher : String? = null,
    val releaseDate : String? = null
)
