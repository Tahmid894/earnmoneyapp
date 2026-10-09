package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ScratchCardDialog
import com.example.ui.components.SpinWheelDialog
import com.example.ui.components.TopPointsBar
import com.example.ui.components.VideoAdDialog
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.VideoTaskScreen
import com.example.ui.screens.WithdrawScreen
import com.example.ui.theme.DeepPurpleDark
import com.example.ui.theme.DeepPurplePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.WatchEarnViewModel

@Composable
fun MainScreen(
    viewModel: WatchEarnViewModel = viewModel()
) {
    val userPoints by viewModel.userPoints.collectAsStateWithLifecycle()
    val streakDay by viewModel.streakDay.collectAsStateWithLifecycle()
    val canDailyCheckIn by viewModel.canDailyCheckIn.collectAsStateWithLifecycle()
    val withdrawHistory by viewModel.withdrawHistory.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activeAd by viewModel.activeAd.collectAsStateWithLifecycle()
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()
    val showSpinWheel by viewModel.showSpinWheel.collectAsStateWithLifecycle()
    val showScratchCard by viewModel.showScratchCard.collectAsStateWithLifecycle()

    var currentTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Show Bengali snackbar notifications
    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg.message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearUiMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopPointsBar(
                points = userPoints,
                onPointsBadgeClick = {
                    currentTab = 1 // Navigate to Withdraw / Balance screen
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Tab 0: 'ভিডিও ও টাস্ক' matching Flutter
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {
                        androidx.compose.material3.Icon(
                            imageVector = if (currentTab == 0) Icons.Filled.VideoLibrary else Icons.Outlined.VideoLibrary,
                            contentDescription = "ভিডিও ও টাস্ক"
                        )
                    },
                    label = {
                        Text(
                            text = "ভিডিও ও টাস্ক",
                            fontSize = 12.sp,
                            fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepPurpleDark,
                        selectedTextColor = DeepPurpleDark,
                        indicatorColor = DeepPurplePrimary.copy(alpha = 0.15f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_tab_tasks")
                )

                // Tab 1: 'উইথড্র (টাকা তুলুন)' matching Flutter
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = {
                        androidx.compose.material3.Icon(
                            imageVector = if (currentTab == 1) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                            contentDescription = "উইথড্র"
                        )
                    },
                    label = {
                        Text(
                            text = "উইথড্র (টাকা তুলুন)",
                            fontSize = 12.sp,
                            fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepPurpleDark,
                        selectedTextColor = DeepPurpleDark,
                        indicatorColor = DeepPurplePrimary.copy(alpha = 0.15f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_tab_withdraw")
                )

                // Tab 2: 'প্রোফাইল' matching Flutter
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = {
                        androidx.compose.material3.Icon(
                            imageVector = if (currentTab == 2) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "প্রোফাইল"
                        )
                    },
                    label = {
                        Text(
                            text = "প্রোফাইল",
                            fontSize = 12.sp,
                            fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepPurpleDark,
                        selectedTextColor = DeepPurpleDark,
                        indicatorColor = DeepPurplePrimary.copy(alpha = 0.15f),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = SuccessGreen,
                    contentColor = Color.White,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenSwitch"
            ) { targetScreen ->
                when (targetScreen) {
                    0 -> VideoTaskScreen(
                        currentPoints = userPoints,
                        streakDay = streakDay,
                        canCheckIn = canDailyCheckIn,
                        tasks = viewModel.baseTasks,
                        onTaskClick = { task -> viewModel.handleTaskClick(task) },
                        onDailyCheckInClick = { viewModel.performDailyCheckIn() },
                        onOpenSpinWheel = { viewModel.openSpinWheel() },
                        onOpenScratchCard = { viewModel.openScratchCard() }
                    )
                    1 -> WithdrawScreen(
                        currentPoints = userPoints,
                        withdrawHistory = withdrawHistory,
                        onSubmitWithdrawal = { method, phone, amount ->
                            viewModel.submitWithdrawal(method, phone, amount)
                        }
                    )
                    2 -> ProfileScreen(
                        currentPoints = userPoints,
                        streakDay = streakDay,
                        withdrawHistory = withdrawHistory,
                        userProfile = userProfile,
                        onUpdateProfile = { name, email -> viewModel.updateUserProfile(name, email) },
                        onToggleSound = { viewModel.toggleSound(it) },
                        onToggleVibration = { viewModel.toggleVibration(it) },
                        onResetData = { viewModel.resetDemoData() }
                    )
                }
            }
        }
    }

    // Full-screen simulated video ad player dialog
    activeAd?.let { adState ->
        VideoAdDialog(
            adState = adState,
            onClaimReward = { viewModel.claimVideoAdReward() },
            onDismiss = { viewModel.closeVideoAd() }
        )
    }

    // Lucky Spin Wheel Dialog
    if (showSpinWheel) {
        SpinWheelDialog(
            onDismiss = { viewModel.closeSpinWheel() },
            onRewardClaimed = { pts -> viewModel.claimSpinReward(pts) }
        )
    }

    // Lucky Scratch Card Dialog
    if (showScratchCard) {
        ScratchCardDialog(
            onDismiss = { viewModel.closeScratchCard() },
            onRewardClaimed = { pts -> viewModel.claimScratchReward(pts) }
        )
    }
}
