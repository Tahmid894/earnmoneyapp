package com.example.model

enum class TaskType {
    VIDEO_AD,
    DAILY_CHECKIN,
    LUCKY_SPIN,
    SCRATCH_CARD
}

data class TaskItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val rewardPoints: Int,
    val type: TaskType,
    val isCompletedToday: Boolean = false,
    val sponsorName: String = "Sponsor Ad",
    val durationSeconds: Int = 6
)
