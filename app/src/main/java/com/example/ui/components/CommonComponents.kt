package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PaymentStatus

// Modern Dark Neo-Banking Colors
object BrandColors {
    val SpaceDark = Color(0xFF0F172A)     // Deep slate navy background
    val CardSlate = Color(0xFF1E293B)     // Dark slate card fill
    val BorderSlate = Color(0xFF334155)   // Slate border
    val ElectricBlue = Color(0xFF3B82F6)  // Radiant secondary
    val RoyalIndigo = Color(0xFF6366F1)   // Banking purple-blue accent
    val GoldAccent = Color(0xFFEAB308)    // Golden VIP accent
    val EmeraldGreen = Color(0xFF10B981)  // Success paid green
    val CrimsonRed = Color(0xFFEF4444)    // Overdue alert red
    val SoftWhite = Color(0xFFF8FAFC)      // High contrast text
    val SoftGray = Color(0xFF94A3B8)      // Muted label text
}

@Composable
fun ScreenHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandColors.SoftWhite,
                    letterSpacing = (-0.5).sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BrandColors.SoftGray
                )
            )
        }
        if (actions != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    testTag: String = ""
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = BrandColors.CardSlate
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BrandColors.SoftGray,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = BrandColors.SoftWhite,
                    letterSpacing = (-1).sp
                )
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = BrandColors.SoftGray,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: PaymentStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        PaymentStatus.PAID -> Pair(BrandColors.EmeraldGreen.copy(alpha = 0.12f), BrandColors.EmeraldGreen)
        PaymentStatus.PENDING -> Pair(BrandColors.GoldAccent.copy(alpha = 0.12f), BrandColors.GoldAccent)
        PaymentStatus.OVERDUE -> Pair(BrandColors.CrimsonRed.copy(alpha = 0.12f), BrandColors.CrimsonRed)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.displayName.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun InfoBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BrandColors.RoyalIndigo.copy(alpha = 0.1f))
            .border(1.dp, BrandColors.RoyalIndigo.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Info",
            tint = BrandColors.RoyalIndigo,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = BrandColors.SoftWhite.copy(alpha = 0.9f),
                lineHeight = 16.sp
            ),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isSecondary: Boolean = false,
    enabled: Boolean = true,
    testTag: String = ""
) {
    val brush = if (isSecondary) {
        Brush.horizontalGradient(listOf(BrandColors.CardSlate, BrandColors.CardSlate))
    } else {
        Brush.horizontalGradient(listOf(BrandColors.RoyalIndigo, BrandColors.ElectricBlue))
    }

    val borderStroke = if (isSecondary) {
        Modifier.border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(12.dp))
    } else Modifier

    val opacity = if (enabled) 1f else 0.5f

    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .background(brush)
            .then(borderStroke)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSecondary) BrandColors.SoftWhite else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = if (isSecondary) BrandColors.SoftWhite else Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.25.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TransactionRow(txn: com.example.data.BankTransaction, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            when (txn.type) {
                                com.example.data.TransactionType.DEPOSIT -> BrandColors.EmeraldGreen.copy(alpha = 0.15f)
                                com.example.data.TransactionType.WITHDRAWAL -> BrandColors.CrimsonRed.copy(alpha = 0.15f)
                                com.example.data.TransactionType.PAYMENT -> BrandColors.RoyalIndigo.copy(alpha = 0.15f)
                                com.example.data.TransactionType.TRANSFER -> BrandColors.ElectricBlue.copy(alpha = 0.15f)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (txn.type) {
                            com.example.data.TransactionType.DEPOSIT -> Icons.Default.ArrowDownward
                            com.example.data.TransactionType.WITHDRAWAL -> Icons.Default.ArrowUpward
                            com.example.data.TransactionType.PAYMENT -> Icons.Default.ReceiptLong
                            com.example.data.TransactionType.TRANSFER -> Icons.Default.SwapHoriz
                        },
                        contentDescription = null,
                        tint = when (txn.type) {
                            com.example.data.TransactionType.DEPOSIT -> BrandColors.EmeraldGreen
                            com.example.data.TransactionType.WITHDRAWAL -> BrandColors.CrimsonRed
                            com.example.data.TransactionType.PAYMENT -> BrandColors.RoyalIndigo
                            com.example.data.TransactionType.TRANSFER -> BrandColors.ElectricBlue
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = txn.description,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 200.dp)
                    )
                    Text(
                        text = "${txn.accountNumber} • ${txn.date}",
                        fontSize = 10.sp,
                        color = BrandColors.SoftGray
                    )
                }
            }
            Text(
                text = "${if (txn.type == com.example.data.TransactionType.DEPOSIT) "+" else "-"}₹${String.format("%,.0f", txn.amount)}",
                fontWeight = FontWeight.ExtraBold,
                color = if (txn.type == com.example.data.TransactionType.DEPOSIT) BrandColors.EmeraldGreen else Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun RowAccountCard(account: com.example.data.BankAccount, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(180.dp)
            .clickable(onClick = onClick)
            .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (account.ownerRole == "STUDENT") BrandColors.ElectricBlue.copy(alpha = 0.15f)
                            else BrandColors.GoldAccent.copy(alpha = 0.15f),
                            CircleShape
                        )
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (account.ownerRole == "STUDENT") Icons.Default.School else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (account.ownerRole == "STUDENT") BrandColors.ElectricBlue else BrandColors.GoldAccent,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = account.status.displayName,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (account.status == com.example.data.AccountStatus.APPROVED) BrandColors.EmeraldGreen else BrandColors.GoldAccent
                )
            }
            Text(
                text = account.ownerName,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = account.accountNumber,
                fontSize = 10.sp,
                color = BrandColors.SoftGray,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "₹${String.format("%,.0f", account.balance)}",
                fontWeight = FontWeight.ExtraBold,
                color = BrandColors.SoftWhite,
                fontSize = 14.sp
            )
        }
    }
}

