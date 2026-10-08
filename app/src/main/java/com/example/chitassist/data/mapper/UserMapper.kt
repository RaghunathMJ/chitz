package com.example.chitassist.data.mapper

import com.example.chitassist.core.database.entity.UserEntity
import com.example.chitassist.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        mobileNumber = mobileNumber,
        email = email,
        city = city,
        dateOfBirth = dateOfBirth
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        mobileNumber = mobileNumber,
        email = email,
        city = city,
        dateOfBirth = dateOfBirth
    )
}
