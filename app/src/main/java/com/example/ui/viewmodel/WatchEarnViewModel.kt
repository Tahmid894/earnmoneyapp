package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RewardRepository
import com.example.model.PaymentMethod
import com.example.model.TaskItem
import com.example.model.TaskType
import com.example.model.UserProfile
import com.example.model.WithdrawRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ActiveAdState(
    val task: TaskItem,
    val remainingSeconds: Int,
    val totalSeconds: Int,
    val isCompleted: Boolean = false,
    val isPlaying: Boolean = true
)

data class UiSnackbarMessage(
    val message: String,
    val isSuccess: Boolean = true
)

class WatchEarnViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RewardRepository(application.applicationContext)

    private val _userPoints = MutableStateFlow(repository.getPoints())
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    private val _streakDay = MutableStateFlow(repository.getStreakDay())
    val streakDay: StateFlow<Int> = _streakDay.asStateFlow()

    private val _canDailyCheckIn = MutableStateFlow(checkCanDailyCheckIn())
    val canDailyCheckIn: StateFlow<Boolean> = _canDailyCheckIn.asStateFlow()

    private val _withdrawHistory = MutableStateFlow(repository.getWithdrawRequests())
    val withdrawHistory: StateFlow<List<WithdrawRequest>> = _withdrawHistory.asStateFlow()

    private val _userProfile = MutableStateFlow(repository.getUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _activeAd = MutableStateFlow<ActiveAdState?>(null)
    val activeAd: StateFlow<ActiveAdState?> = _activeAd.asStateFlow()

    private val _uiMessage = MutableStateFlow<UiSnackbarMessage?>(null)
    val uiMessage: StateFlow<UiSnackbarMessage?> = _uiMessage.asStateFlow()

    private val _showSpinWheel = MutableStateFlow(false)
    val showSpinWheel: StateFlow<Boolean> = _showSpinWheel.asStateFlow()

    private val _showScratchCard = MutableStateFlow(false)
    val showScratchCard: StateFlow<Boolean> = _showScratchCard.asStateFlow()

    private var adCountdownJob: Job? = null

    val baseTasks: List<TaskItem> = listOf(
        TaskItem(
            id = "ad_1",
            title = "Watch Ad #1 (Reward: 50 Pts)",
            subtitle = "একটি ছোট বিজ্ঞাপন দেখে ৫০ পয়েন্ট নিন।",
            rewardPoints = 50,
            type = TaskType.VIDEO_AD,
            sponsorName = "Daraz Mega Discount",
            durationSeconds = 5
        ),
        TaskItem(
            id = "ad_2",
            title = "Watch Ad #2 (Reward: 50 Pts)",
            subtitle = "প্রমোশনাল ভিডিও দেখে ৫০ পয়েন্ট নিন।",
            rewardPoints = 50,
            type = TaskType.VIDEO_AD,
            sponsorName = "Foodpanda Express 50% Off",
            durationSeconds = 5
        ),
        TaskItem(
            id = "daily_checkin",
            title = "Daily Check-in (Reward: 100 Pts)",
            subtitle = "প্রতিদিন একবার ক্লাইম করুন।",
            rewardPoints = 100,
            type = TaskType.DAILY_CHECKIN
        ),
        TaskItem(
            id = "ad_3",
            title = "Tech Ad #3 (Reward: 60 Pts)",
            subtitle = "নতুন স্মার্টফোন ও টেক গ্যাজেটের ভিডিও দেখুন।",
            rewardPoints = 60,
            type = TaskType.VIDEO_AD,
            sponsorName = "Samsung Galaxy Review",
            durationSeconds = 6
        ),
        TaskItem(
            id = "ad_4",
            title = "Gaming Ad #4 (Reward: 75 Pts)",
            subtitle = "নতুন গেম ট্রেইলার দেখে ৭৫ পয়েন্ট নিন।",
            rewardPoints = 75,
            type = TaskType.VIDEO_AD,
            sponsorName = "Free Fire Pro Championship",
            durationSeconds = 6
        ),
        TaskItem(
            id = "ad_5",
            title = "Shopping Ad #5 (Reward: 50 Pts)",
            subtitle = "পাঠাও রাইডস স্পেশাল ডিসকাউন্ট ভিডিও।",
            rewardPoints = 50,
            type = TaskType.VIDEO_AD,
            sponsorName = "Pathao Top Deals",
            durationSeconds = 5
        )
    )

    private fun checkCanDailyCheckIn(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        return repository.getLastCheckInDate() != today
    }

    fun addPoints(points: Int, customMessage: String? = null) {
        val updated = repository.addPoints(points)
        _userPoints.value = updated
        val msg = customMessage ?: "অভিনন্দন! আপনি $points পয়েন্ট অর্জন করেছেন।"
        _uiMessage.value = UiSnackbarMessage(msg, isSuccess = true)
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun handleTaskClick(task: TaskItem) {
        when (task.type) {
            TaskType.DAILY_CHECKIN -> {
                performDailyCheckIn()
            }
            TaskType.VIDEO_AD -> {
                startVideoAd(task)
            }
            TaskType.LUCKY_SPIN -> {
                _showSpinWheel.value = true
            }
            TaskType.SCRATCH_CARD -> {
                _showScratchCard.value = true
            }
        }
    }

    fun performDailyCheckIn() {
        if (!checkCanDailyCheckIn()) {
            _uiMessage.value = UiSnackbarMessage("আপনি আজকের ডেইলি চেক-ইন বোনাস ইতিমধ্যে নিয়ে নিয়েছেন!", isSuccess = false)
            return
        }
        val (success, reward) = repository.recordDailyCheckIn()
        if (success) {
            _userPoints.value = repository.getPoints()
            _streakDay.value = repository.getStreakDay()
            _canDailyCheckIn.value = false
            _uiMessage.value = UiSnackbarMessage("অভিনন্দন! ডেইলি চেক-ইন সম্পন্ন করে $reward পয়েন্ট পেয়েছেন!", isSuccess = true)
        }
    }

    fun startVideoAd(task: TaskItem) {
        adCountdownJob?.cancel()
        val duration = task.durationSeconds
        _activeAd.value = ActiveAdState(
            task = task,
            remainingSeconds = duration,
            totalSeconds = duration,
            isCompleted = false,
            isPlaying = true
        )

        adCountdownJob = viewModelScope.launch {
            for (sec in (duration - 1) downTo 0) {
                delay(1000)
                val current = _activeAd.value
                if (current != null && current.isPlaying) {
                    if (sec == 0) {
                        _activeAd.value = current.copy(remainingSeconds = 0, isCompleted = true)
                    } else {
                        _activeAd.value = current.copy(remainingSeconds = sec)
                    }
                }
            }
        }
    }

    fun claimVideoAdReward() {
        val adState = _activeAd.value ?: return
        if (adState.isCompleted) {
            val pts = adState.task.rewardPoints
            addPoints(pts, "অভিনন্দন! আপনি $pts পয়েন্ট অর্জন করেছেন।")
            repository.markTaskCompleted(adState.task.id)
            _activeAd.value = null
        }
    }

    fun closeVideoAd() {
        adCountdownJob?.cancel()
        _activeAd.value = null
    }

    fun openSpinWheel() {
        _showSpinWheel.value = true
    }

    fun closeSpinWheel() {
        _showSpinWheel.value = false
    }

    fun claimSpinReward(points: Int) {
        addPoints(points, "ভাগ্যবান চাকা ঘুরিয়ে $points পয়েন্ট জিতেছেন!")
        _showSpinWheel.value = false
    }

    fun openScratchCard() {
        _showScratchCard.value = true
    }

    fun closeScratchCard() {
        _showScratchCard.value = false
    }

    fun claimScratchReward(points: Int) {
        addPoints(points, "স্ক্র্যাচ কার্ড ঘষে $points পয়েন্ট পেয়েছেন!")
        _showScratchCard.value = false
    }

    fun submitWithdrawal(
        method: PaymentMethod,
        phone: String,
        amountBdtStr: String
    ): Boolean {
        val trimmedPhone = phone.trim()
        val trimmedAmount = amountBdtStr.trim()

        if (trimmedPhone.isEmpty() || trimmedAmount.isEmpty()) {
            _uiMessage.value = UiSnackbarMessage("সবগুলো ঘর সঠিকভাবে পূরণ করুন!", isSuccess = false)
            return false
        }

        // Validate Bangladeshi phone number: 11 digits, starts with 01
        if (!trimmedPhone.matches(Regex("^01[3-9][0-9]{8}$"))) {
            _uiMessage.value = UiSnackbarMessage("সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন (যেমন: 017XXXXXXXX)", isSuccess = false)
            return false
        }

        val amountBdt = trimmedAmount.toDoubleOrNull()
        if (amountBdt == null || amountBdt <= 0) {
            _uiMessage.value = UiSnackbarMessage("সঠিক টাকার পরিমাণ লিখুন!", isSuccess = false)
            return false
        }

        val requiredPoints = (amountBdt * 10).toInt()
        val currentPoints = _userPoints.value

        if (currentPoints < requiredPoints) {
            _uiMessage.value = UiSnackbarMessage(
                "পর্যাপ্ত পয়েন্ট নেই! ৳${String.format(Locale.US, "%.2f", amountBdt)} তোলার জন্য $requiredPoints পয়েন্ট প্রয়োজন। (বর্তমান: $currentPoints)",
                isSuccess = false
            )
            return false
        }

        val deducted = repository.deductPoints(requiredPoints)
        if (!deducted) {
            _uiMessage.value = UiSnackbarMessage("উইথড্র করতে সমস্যা হয়েছে, আবার চেষ্টা করুন!", isSuccess = false)
            return false
        }

        _userPoints.value = repository.getPoints()

        val request = WithdrawRequest(
            id = "TXN-" + UUID.randomUUID().toString().substring(0, 8).uppercase(Locale.US),
            method = method,
            accountNumber = trimmedPhone,
            amountBdt = amountBdt,
            pointsDeducted = requiredPoints,
            timestamp = System.currentTimeMillis(),
            status = "পেন্ডিং (Pending)"
        )

        repository.saveWithdrawRequest(request)
        _withdrawHistory.value = repository.getWithdrawRequests()

        val methodName = if (method == PaymentMethod.BKASH) "বিকাশ" else "নগদ"
        _uiMessage.value = UiSnackbarMessage(
            "$methodName নম্বরে ৳${String.format(Locale.US, "%.2f", amountBdt)} টাকার উইথড্র রিকোয়েস্ট সফল হয়েছে!",
            isSuccess = true
        )
        return true
    }

    fun updateUserProfile(name: String, email: String) {
        repository.updateUserProfile(name, email)
        _userProfile.value = repository.getUserProfile()
        _uiMessage.value = UiSnackbarMessage("প্রোফাইল তথ্য সফলভাবে আপডেট হয়েছে!", isSuccess = true)
    }

    fun toggleVibration(enabled: Boolean) {
        repository.toggleSettings(vibration = enabled)
        _userProfile.value = repository.getUserProfile()
    }

    fun toggleSound(enabled: Boolean) {
        repository.toggleSettings(sound = enabled)
        _userProfile.value = repository.getUserProfile()
    }

    fun resetDemoData() {
        repository.resetDataForTesting()
        _userPoints.value = repository.getPoints()
        _streakDay.value = repository.getStreakDay()
        _canDailyCheckIn.value = true
        _withdrawHistory.value = emptyList()
        _uiMessage.value = UiSnackbarMessage("সব ডাটা রিসেট করা হয়েছে (শুরুর ব্যালেন্স: ২৫০ পয়েন্ট)", isSuccess = true)
    }
}
