package com.rizkyizh.panopticon.data.remote.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(

    @field:SerializedName("msg")
    val msg: String? = null
)