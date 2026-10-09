package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PaymentMethod
import com.example.model.WithdrawRequest
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DeepPurpleDark
import com.example.ui.theme.DeepPurplePrimary
import com.example.ui.theme.GoldReward
import com.example.ui.theme.NagadOrange
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WithdrawScreen(
    currentPoints: Int,
    withdrawHistory: List<WithdrawRequest>,
    onSubmitWithdrawal: (PaymentMethod, String, String) -> Boolean
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.BKASH) }
    var phoneInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }

    // 10 points = 1 BDT (matching Flutter formula: currentPoints * 0.10)
    val takaValue = currentPoints * 0.10

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("withdraw_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Balance Card matching Flutter:
        // Card(color: Colors.deepPurple[550], elevation: 4, shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)))
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("balance_card"),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    DeepPurpleDark,
                                    Color(0xFF4A148C),
                                    Color(0xFF311B92)
                                )
                            )
                        )
                        .padding(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "আপনার মোট ব্যালেন্স",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 15.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "$currentPoints Points",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.Black.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "প্রাক্কলিত মূল্য: ৳${String.format(Locale.US, "%.2f", takaValue)} BDT",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = GoldReward,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "কনভার্শন রেট: ১০ পয়েন্ট = ১ টাকা",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // 2. Payment Method Selector matching Flutter:
        // Text('পেমেন্ট মাধ্যম সিলেক্ট করুন:', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold))
        item {
            Column {
                Text(
                    text = "পেমেন্ট মাধ্যম সিলেক্ট করুন:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Bkash Option
                    MethodSelectionCard(
                        title = "বিকাশ (Bkash)",
                        brandColor = BkashPink,
                        isSelected = selectedMethod == PaymentMethod.BKASH,
                        onClick = { selectedMethod = PaymentMethod.BKASH },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("method_bkash")
                    )

                    // Nagad Option
                    MethodSelectionCard(
                        title = "নগদ (Nagad)",
                        brandColor = NagadOrange,
                        isSelected = selectedMethod == PaymentMethod.NAGAD,
                        onClick = { selectedMethod = PaymentMethod.NAGAD },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("method_nagad")
                    )
                }
            }
        }

        // 3. Quick Amount Selector Chips
        item {
            Column {
                Text(
                    text = "দ্রুত টাকার পরিমাণ বাছুন:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10, 25, 50, 100).forEach { amount ->
                        val isEnough = (amount * 10) <= currentPoints
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (amountInput == amount.toString()) DeepPurplePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    amountInput = amount.toString()
                                }
                        ) {
                            Text(
                                text = "৳$amount",
                                color = if (amountInput == amount.toString()) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    // Max button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                val maxBdt = (currentPoints / 10).coerceAtLeast(0)
                                amountInput = maxBdt.toString()
                            }
                    ) {
                        Text(
                            text = "সব তুলুন",
                            color = DeepPurpleDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // 4. TextFields matching Flutter:
        // TextField(controller: _phoneController, decoration: InputDecoration(labelText: '$_selectedMethod অ্যাকাউন্ট নম্বর'))
        // TextField(controller: _amountController, decoration: InputDecoration(labelText: 'টাকার পরিমাণ (BDT)'))
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = {
                        Text("${selectedMethod.displayName} অ্যাকাউন্ট নম্বর (যেমন: 017XXXXXXXX)")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "Phone",
                            tint = if (selectedMethod == PaymentMethod.BKASH) BkashPink else NagadOrange
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("phone_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (selectedMethod == PaymentMethod.BKASH) BkashPink else NagadOrange
                    )
                )

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("টাকার পরিমাণ (BDT)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Money,
                            contentDescription = "Money",
                            tint = DeepPurpleDark
                        )
                    },
                    trailingIcon = {
                        val amount = amountInput.toDoubleOrNull() ?: 0.0
                        val ptsNeeded = (amount * 10).toInt()
                        if (amount > 0) {
                            Text(
                                text = "= $ptsNeeded Pts",
                                color = DeepPurplePrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepPurpleDark
                    )
                )

                // Info banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = DeepPurplePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "সর্বনিম্ন উইথড্র ৳১০ (১০০ পয়েন্ট)। পেমেন্ট ২৪ ঘণ্টার মধ্যে পাঠানো হবে।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 5. Submit Button matching Flutter:
                // ElevatedButton(child: Text('উইথড্র রিকোয়েস্ট পাঠান'))
                Button(
                    onClick = {
                        val success = onSubmitWithdrawal(selectedMethod, phoneInput, amountInput)
                        if (success) {
                            phoneInput = ""
                            amountInput = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_withdraw_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepPurpleDark,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = GoldReward
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "উইথড্র রিকোয়েস্ট পাঠান",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 6. Withdrawal History Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = DeepPurpleDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "উইথড্রয়াল হিস্ট্রি (${withdrawHistory.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (withdrawHistory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "এখনও কোনো উইথড্র রিকোয়েস্ট করা হয়নি।",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(withdrawHistory) { request ->
                WithdrawHistoryItem(request = request)
            }
        }
    }
}

@Composable
fun MethodSelectionCard(
    title: String,
    brandColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) brandColor else Color.Gray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) brandColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = brandColor
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) brandColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun WithdrawHistoryItem(request: WithdrawRequest) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(request.timestamp) { dateFormat.format(Date(request.timestamp)) }
    val brandColor = if (request.method == PaymentMethod.BKASH) BkashPink else NagadOrange

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${request.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(brandColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (request.method == PaymentMethod.BKASH) "bK" else "Ng",
                        color = brandColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "${request.method.displayName} • ${request.accountNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "$formattedDate • ${request.pointsDeducted} Pts",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "৳${String.format(Locale.US, "%.2f", request.amountBdt)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepPurpleDark
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SuccessGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = request.status,
                        color = SuccessGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
