    package com.example.fitness.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val dob: String? = null,
    val nickname: String? = null,
    val avatarUriString: String? = null,
    val role: String = "user"
)
