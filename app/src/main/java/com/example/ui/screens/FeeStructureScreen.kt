package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SchoolViewModel
import com.example.ui.components.*

@Composable
fun FeeStructureScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val feeStructures by viewModel.feeStructures.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = "Fee & Billing Settings",
            subtitle = "Define your school's recurring charges, activities, and transport fee schedules.",
            actions = {
                CustomButton(
                    text = "Add Fee Scheme",
                    icon = Icons.Default.Add,
                    onClick = { showAddDialog = true },
                    testTag = "add_fee_scheme_btn"
                )
            }
        )

        InfoBanner(
            text = "Fee structures serve as automated billing blueprints. When you run an automated billing cycles, students are invoiced according to these specified templates.",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "ACTIVE FEES PLANS (${feeStructures.size})",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftGray,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (feeStructures.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandColors.CardSlate),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No fee plans defined yet. Click 'Add Fee Scheme' to start.",
                    color = BrandColors.SoftGray,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                feeStructures.forEach { fee ->
                    FeePlanCard(fee = fee, onDelete = { viewModel.deleteFeeStructure(fee.id) })
                }
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
    }

    if (showAddDialog) {
        AddFeePlanDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, amount, category, frequency, description ->
                viewModel.createFeeStructure(title, amount, category, frequency, description)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun FeePlanCard(
    fee: FeeStructure,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandColors.RoyalIndigo.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = fee.category.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandColors.RoyalIndigo,
                                    fontSize = 10.sp
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandColors.GoldAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = fee.frequency.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandColors.GoldAccent,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = fee.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.SoftWhite
                        )
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_fee_${fee.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Plan",
                        tint = BrandColors.CrimsonRed.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = fee.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = BrandColors.SoftGray,
                    lineHeight = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = BrandColors.BorderSlate, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Standard Amount Due",
                    style = MaterialTheme.typography.labelMedium.copy(color = BrandColors.SoftGray)
                )
                Text(
                    text = "₹${String.format("%,.2f", fee.amount)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = BrandColors.SoftWhite
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeePlanDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, FeeCategory, FeeFrequency, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FeeCategory.TUITION) }
    var selectedFrequency by remember { mutableStateOf(FeeFrequency.MONTHLY) }
    var description by remember { mutableStateOf("") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var frequencyDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Fee Scheme",
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftWhite
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title (e.g. Science Lab Fee)") },
                    modifier = Modifier.fillMaxWidth().testTag("fee_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrandColors.SoftWhite,
                        unfocusedTextColor = BrandColors.SoftWhite,
                        focusedBorderColor = BrandColors.RoyalIndigo,
                        unfocusedBorderColor = BrandColors.BorderSlate
                    )
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (INR)") },
                    modifier = Modifier.fillMaxWidth().testTag("fee_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrandColors.SoftWhite,
                        unfocusedTextColor = BrandColors.SoftWhite,
                        focusedBorderColor = BrandColors.RoyalIndigo,
                        unfocusedBorderColor = BrandColors.BorderSlate
                    )
                )

                // Category Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fee Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BrandColors.SoftWhite,
                            unfocusedTextColor = BrandColors.SoftWhite,
                            focusedBorderColor = BrandColors.RoyalIndigo,
                            unfocusedBorderColor = BrandColors.BorderSlate
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        FeeCategory.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    selectedCategory = category
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Frequency Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = frequencyDropdownExpanded,
                    onExpandedChange = { frequencyDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedFrequency.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Frequency") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = frequencyDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BrandColors.SoftWhite,
                            unfocusedTextColor = BrandColors.SoftWhite,
                            focusedBorderColor = BrandColors.RoyalIndigo,
                            unfocusedBorderColor = BrandColors.BorderSlate
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = frequencyDropdownExpanded,
                        onDismissRequest = { frequencyDropdownExpanded = false }
                    ) {
                        FeeFrequency.values().forEach { freq ->
                            DropdownMenuItem(
                                text = { Text(freq.displayName) },
                                onClick = {
                                    selectedFrequency = freq
                                    frequencyDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("fee_desc_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrandColors.SoftWhite,
                        unfocusedTextColor = BrandColors.SoftWhite,
                        focusedBorderColor = BrandColors.RoyalIndigo,
                        unfocusedBorderColor = BrandColors.BorderSlate
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (title.isNotEmpty() && amount > 0) {
                        onSave(title, amount, selectedCategory, selectedFrequency, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                modifier = Modifier.testTag("save_fee_plan_btn")
            ) {
                Text("Create Scheme", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = BrandColors.SoftGray)
            ) {
                Text("Cancel")
            }
        },
        containerColor = BrandColors.CardSlate,
        titleContentColor = BrandColors.SoftWhite
    )
}
