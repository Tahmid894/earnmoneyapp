package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.model.PaymentMethod
import com.example.model.UserProfile
import com.example.model.WithdrawRequest
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RewardRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("watch_and_earn_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_POINTS = "key_user_points"
        private const val KEY_INITIAL_SETUP = "key_initial_setup_done"
        private const val KEY_STREAK_DAY = "key_streak_day"
        private const val KEY_LAST_CHECKIN = "key_last_checkin"
        private const val KEY_WITHDRAWALS = "key_withdrawals_json"
        private const val KEY_COMPLETED_TASKS = "key_completed_tasks_set"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_EMAIL = "key_user_email"
        private const val KEY_SOUND = "key_sound_enabled"
        private const val KEY_VIBRATION = "key_vibration_enabled"
    }

    init {
        if (!prefs.getBoolean(KEY_INITIAL_SETUP, false)) {
            // Initial setup matching the user's Flutter app starting points: 250
            prefs.edit()
                .putInt(KEY_POINTS, 250)
                .putInt(KEY_STREAK_DAY, 1)
                .putString(KEY_USER_NAME, "Tahmid Sheikh")
                .putString(KEY_USER_EMAIL, "user@gmail.com")
                .putBoolean(KEY_SOUND, true)
                .putBoolean(KEY_VIBRATION, true)
                .putBoolean(KEY_INITIAL_SETUP, true)
                .apply()
        }
    }

    fun getPoints(): Int = prefs.getInt(KEY_POINTS, 250)

    fun addPoints(amount: Int): Int {
        val current = getPoints()
        val updated = current + amount
        prefs.edit().putInt(KEY_POINTS, updated).apply()
        triggerHapticSuccess()
        return updated
    }

    fun deductPoints(amount: Int): Boolean {
        val current = getPoints()
        if (current < amount) return false
        val updated = current - amount
        prefs.edit().putInt(KEY_POINTS, updated).apply()
        triggerHapticSuccess()
        return true
    }

    fun getStreakDay(): Int = prefs.getInt(KEY_STREAK_DAY, 1)

    fun getLastCheckInDate(): String = prefs.getString(KEY_LAST_CHECKIN, "") ?: ""

    fun recordDailyCheckIn(): Pair<Boolean, Int> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val lastDate = getLastCheckInDate()
        if (today == lastDate) {
            return Pair(false, 0)
        }
        val streak = getStreakDay()
        val reward = 100 // Flutter code reward: 100 Pts
        val nextStreak = if (streak >= 7) 1 else streak + 1

        prefs.edit()
            .putString(KEY_LAST_CHECKIN, today)
            .putInt(KEY_STREAK_DAY, nextStreak)
            .apply()

        addPoints(reward)
        return Pair(true, reward)
    }

    fun getCompletedTasksToday(): Set<String> {
        return prefs.getStringSet(KEY_COMPLETED_TASKS, emptySet()) ?: emptySet()
    }

    fun markTaskCompleted(taskId: String) {
        val set = getCompletedTasksToday().toMutableSet()
        set.add(taskId)
        prefs.edit().putStringSet(KEY_COMPLETED_TASKS, set).apply()
    }

    fun getWithdrawRequests(): List<WithdrawRequest> {
        val raw = prefs.getString(KEY_WITHDRAWALS, null) ?: return emptyList()
        val list = mutableListOf<WithdrawRequest>()
        try {
            val jsonArray = JSONArray(raw)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val methodStr = obj.optString("method", PaymentMethod.BKASH.name)
                val method = try {
                    PaymentMethod.valueOf(methodStr)
                } catch (e: Exception) {
                    PaymentMethod.BKASH
                }
                list.add(
                    WithdrawRequest(
                        id = obj.getString("id"),
                        method = method,
                        accountNumber = obj.getString("accountNumber"),
                        amountBdt = obj.getDouble("amountBdt"),
                        pointsDeducted = obj.getInt("pointsDeducted"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        status = obj.optString("status", "পেন্ডিং (Pending)")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveWithdrawRequest(request: WithdrawRequest) {
        val current = getWithdrawRequests().toMutableList()
        current.add(0, request)
        val jsonArray = JSONArray()
        for (item in current) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("method", item.method.name)
                put("accountNumber", item.accountNumber)
                put("amountBdt", item.amountBdt)
                put("pointsDeducted", item.pointsDeducted)
                put("timestamp", item.timestamp)
                put("status", item.status)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_WITHDRAWALS, jsonArray.toString()).apply()
    }

    fun getUserProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString(KEY_USER_NAME, "Tahmid Sheikh") ?: "Tahmid Sheikh",
            email = prefs.getString(KEY_USER_EMAIL, "user@gmail.com") ?: "user@gmail.com",
            referralCode = "TAHMID77",
            invitedFriends = 3,
            soundEnabled = prefs.getBoolean(KEY_SOUND, true),
            vibrationEnabled = prefs.getBoolean(KEY_VIBRATION, true)
        )
    }

    fun updateUserProfile(name: String, email: String) {
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_EMAIL, email)
            .apply()
    }

    fun toggleSettings(sound: Boolean? = null, vibration: Boolean? = null) {
        val editor = prefs.edit()
        sound?.let { editor.putBoolean(KEY_SOUND, it) }
        vibration?.let { editor.putBoolean(KEY_VIBRATION, it) }
        editor.apply()
    }

    fun resetDataForTesting() {
        prefs.edit()
            .putInt(KEY_POINTS, 250)
            .putString(KEY_LAST_CHECKIN, "")
            .putInt(KEY_STREAK_DAY, 1)
            .putStringSet(KEY_COMPLETED_TASKS, emptySet())
            .remove(KEY_WITHDRAWALS)
            .apply()
    }

    private fun triggerHapticSuccess() {
        if (!prefs.getBoolean(KEY_VIBRATION, true)) return
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(50)
                }
            }
        } catch (e: Exception) {
            // Ignore if vibrator unavailable
        }
    }
}
