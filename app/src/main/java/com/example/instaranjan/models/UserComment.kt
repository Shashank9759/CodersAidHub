package com.example.instaranjan.models


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.io.Serializable



data class UserComment(
    val user:  @RawValue User,
    val comment: String
) : Serializable {
    // No-argument constructor for Firebase deserialization
    constructor() : this(User(), "")
}
