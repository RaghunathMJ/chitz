package com.example.chitassist.domain.model

data class User(
    val id: Long,
    val name: String,
    val mobileNumber: String,
    val email: String?,
    val city: String?,
    val dateOfBirth: String?
)
