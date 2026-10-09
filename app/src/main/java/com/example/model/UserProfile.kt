package com.example.model

data class UserProfile(
    val name: String = "Tahmid Sheikh",
    val email: String = "user@gmail.com",
    val referralCode: String = "TAHMID77",
    val invitedFriends: Int = 3,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)
