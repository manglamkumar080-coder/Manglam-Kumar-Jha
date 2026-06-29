package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SchoolViewModel
import com.example.ui.components.*

@Composable
fun ReportsScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.financialStats.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = "Financial Analytics & Audits",
            subtitle = "Verify general ledger journals, track categorical school collections, and review cash flows."
        )

        // Category breakdown progress bars
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(16.dp))
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Collections by Fee Category",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = BrandColors.SoftWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                val maxCategoryVal = stats.collectionByCategory.values.maxOrNull() ?: 1.0

                FeeCategory.values().forEach { category ->
                    val collectedAmount = stats.collectionByCategory[category] ?: 0.0
                    val fraction = (collectedAmount / maxCategoryVal).toFloat()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = BrandColors.SoftWhite,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "₹${String.format("%,.0f", collectedAmount)}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = BrandColors.GoldAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { if (maxCategoryVal > 0) fraction else 0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = BrandColors.RoyalIndigo,
                                trackColor = BrandColors.BorderSlate
                            )
                        }
                    }
                }
            }
        }

        // --- Custom Canvas Visual Cashflow Chart ---
        Text(
            text = "DAILY COLLECTIONS CHART (JUNE 2026)",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftGray,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(16.dp))
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Weekly Realized Revenue Trend",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = BrandColors.SoftWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Custom bar graph with Canvas
                val values = listOf(15000f, 25000f, 10000f, 35000f, 18000f, 45000f)
                val labels = listOf("Wk 1", "Wk 2", "Wk 3", "Wk 4", "Wk 5", "Wk 6")
                val maxValue = 50000f

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val axisOffsetY = canvasHeight - 30f
                    val axisStartX = 40f
                    val chartWidth = canvasWidth - axisStartX - 20f
                    val spacing = chartWidth / values.size
                    val barWidth = spacing * 0.45f

                    // Draw baseline
                    drawLine(
                        color = BrandColors.BorderSlate,
                        start = Offset(axisStartX, axisOffsetY),
                        end = Offset(canvasWidth, axisOffsetY),
                        strokeWidth = 2f
                    )

                    // Draw vertical guide lines and bars
                    values.forEachIndexed { idx, value ->
                        val barHeight = (value / maxValue) * (canvasHeight - 60f)
                        val leftX = axisStartX + (idx * spacing) + (spacing - barWidth) / 2
                        val topY = axisOffsetY - barHeight

                        // Draw background track for bar
                        drawRoundRect(
                            color = BrandColors.BorderSlate.copy(alpha = 0.2f),
                            topLeft = Offset(leftX, 10f),
                            size = Size(barWidth, axisOffsetY - 10f),
                            cornerRadius = CornerRadius(6f, 6f)
                        )

                        // Draw realized value bar (with gradient)
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(BrandColors.RoyalIndigo, BrandColors.ElectricBlue)
                            ),
                            topLeft = Offset(leftX, topY),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                    }
                }

                // Draw X-axis Labels beneath the Canvas using Row spacing to align
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    labels.forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BrandColors.SoftGray,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // --- FULL TRANSACTION AUDIT TRAIL ---
        Text(
            text = "CORE BANKING LEDGER JOURNAL",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftGray,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandColors.CardSlate),
                contentAlignment = Alignment.Center
            ) {
                Text("No audited ledger journals found.", color = BrandColors.SoftGray)
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                transactions.forEach { txn ->
                    TransactionRow(txn = txn)
                }
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
    }
}
