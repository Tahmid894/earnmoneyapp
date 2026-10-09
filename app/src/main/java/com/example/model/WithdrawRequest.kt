package com.example.model

enum class PaymentMethod(val displayName: String, val bengaliName: String) {
    BKASH("bKash", "বিকাশ (Bkash)"),
    NAGAD("Nagad", "নগদ (Nagad)")
}

data class WithdrawRequest(
    val id: String,
    val method: PaymentMethod,
    val accountNumber: String,
    val amountBdt: Double,
    val pointsDeducted: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "পেন্ডিং (Pending)"
)
