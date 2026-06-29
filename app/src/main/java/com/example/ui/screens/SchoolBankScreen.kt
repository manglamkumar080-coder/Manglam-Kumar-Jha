package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SchoolViewModel
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolBankScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val isOpeningAccount by viewModel.isOpeningAccount.collectAsState()
    val bankAccounts by viewModel.filteredBankAccounts.collectAsState()
    val searchQuery by viewModel.bankSearchQuery.collectAsState()

    var activeAccountForTransactions by remember { mutableStateOf<BankAccount?>(null) }
    var transactionTypeSelected by remember { mutableStateOf<TransactionType>(TransactionType.DEPOSIT) }
    var showTransactionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isOpeningAccount) {
            // --- ACCOUNT OPENING WIZARD ---
            AccountOpeningWizard(viewModel = viewModel)
        } else {
            // --- MAIN BANKING LIST DASHBOARD ---
            ScreenHeader(
                title = "EduTrust School Bank",
                subtitle = "Manage personal savings accounts, deposit allowances, and verify student KYC documents.",
                actions = {
                    CustomButton(
                        text = "Open Bank Account",
                        icon = Icons.Default.Add,
                        onClick = { viewModel.startAccountOpening() },
                        testTag = "open_acct_start_btn"
                    )
                }
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setBankSearch(it) },
                placeholder = { Text("Search account number, owner name, teacher, student...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BrandColors.SoftGray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("bank_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = BrandColors.SoftWhite,
                    unfocusedTextColor = BrandColors.SoftWhite,
                    focusedBorderColor = BrandColors.RoyalIndigo,
                    unfocusedBorderColor = BrandColors.BorderSlate,
                    unfocusedPlaceholderColor = BrandColors.SoftGray
                ),
                shape = RoundedCornerShape(12.dp)
            )

            if (bankAccounts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No bank accounts found matching search.",
                        color = BrandColors.SoftGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(bankAccounts) { acct ->
                        BankDashboardRow(
                            account = acct,
                            onDeposit = {
                                activeAccountForTransactions = acct
                                transactionTypeSelected = TransactionType.DEPOSIT
                                showTransactionDialog = true
                            },
                            onWithdraw = {
                                activeAccountForTransactions = acct
                                transactionTypeSelected = TransactionType.WITHDRAWAL
                                showTransactionDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showTransactionDialog && activeAccountForTransactions != null) {
        BankTransactionDialog(
            account = activeAccountForTransactions!!,
            type = transactionTypeSelected,
            onDismiss = {
                showTransactionDialog = false
                activeAccountForTransactions = null
            },
            onConfirm = { amount, desc ->
                if (transactionTypeSelected == TransactionType.DEPOSIT) {
                    viewModel.depositToAccount(activeAccountForTransactions!!.accountNumber, amount, desc)
                } else {
                    viewModel.withdrawFromAccount(activeAccountForTransactions!!.accountNumber, amount, desc)
                }
                showTransactionDialog = false
                activeAccountForTransactions = null
            }
        )
    }
}

@Composable
fun BankDashboardRow(
    account: BankAccount,
    onDeposit: () -> Unit,
    onWithdraw: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BrandColors.CardSlate)
            .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Debit Card Visual
        VirtualDebitCard(account = account)

        Spacer(modifier = Modifier.height(16.dp))

        // Actions Bar
        if (account.status == AccountStatus.APPROVED) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomButton(
                    text = "Deposit Cash",
                    icon = Icons.Default.ArrowDownward,
                    onClick = onDeposit,
                    modifier = Modifier.weight(1f),
                    isSecondary = true,
                    testTag = "deposit_btn_${account.accountNumber}"
                )
                CustomButton(
                    text = "Withdraw Cash",
                    icon = Icons.Default.ArrowUpward,
                    onClick = onWithdraw,
                    modifier = Modifier.weight(1f),
                    isSecondary = true,
                    testTag = "withdraw_btn_${account.accountNumber}"
                )
            }
        } else {
            InfoBanner(
                text = "This account is currently awaiting documents verification and Video KYC clearance. Please complete verification steps."
            )
        }
    }
}

@Composable
fun VirtualDebitCard(
    account: BankAccount,
    modifier: Modifier = Modifier
) {
    val gradient = if (account.ownerRole == "TEACHER") {
        Brush.linearGradient(
            colors = listOf(Color(0xFF1E1B4B), Color(0xFF311042), Color(0xFF4C0519)),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFF0F172A), Color(0xFF1E3A8A), Color(0xFF1D4ED8)),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        // Card Glow Elements
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(120.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (account.ownerRole == "TEACHER") BrandColors.GoldAccent.copy(alpha = 0.15f)
                            else BrandColors.ElectricBlue.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Bank Logo and Role
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = BrandColors.GoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "EDUTRUST SCHOOL BANK",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = account.ownerRole,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (account.ownerRole == "TEACHER") BrandColors.GoldAccent else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            // Middle: Chip Visual and Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Simulating gold chip
                Box(
                    modifier = Modifier
                        .size(36.dp, 26.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFCD34D), Color(0xFFD97706))
                            )
                        )
                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "AVAILABLE BALANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 8.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "₹${String.format("%,.2f", account.balance)}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            // Footer: Card number and Expiry
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = account.cardNumber,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = account.ownerName.uppercase(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "EXPIRY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 7.sp
                            )
                        )
                        Text(
                            text = account.cardExpiry,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CVV",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 7.sp
                            )
                        )
                        Text(
                            text = account.cardCvv,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountOpeningWizard(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val step by viewModel.wizardStep.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Personal School Bank Account Wizard",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = BrandColors.SoftWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Step $step of 4",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = BrandColors.GoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { step / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = BrandColors.RoyalIndigo,
                trackColor = BrandColors.BorderSlate
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                1 -> Step1CollectInfo(viewModel = viewModel)
                2 -> Step2DocumentUpload(viewModel = viewModel)
                3 -> Step3VideoKyc(viewModel = viewModel)
                4 -> Step4ApprovalSummary(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1CollectInfo(
    viewModel: SchoolViewModel
) {
    val role by viewModel.kycRole.collectAsState()
    val students by viewModel.students.collectAsState()
    val teachers by viewModel.teachers.collectAsState()
    val selectedId by viewModel.selectedOwnerId.collectAsState()
    val aadhar by viewModel.aadharNumber.collectAsState()
    val pan by viewModel.panNumber.collectAsState()

    var selectDropdownExpanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Step 1: Assign Account Owner & KYC Info",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite)
        )

        // Segmented role selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BrandColors.SpaceDark)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (role == "STUDENT") BrandColors.RoyalIndigo else Color.Transparent)
                    .clickable { viewModel.setKycRole("STUDENT") }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("STUDENT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (role == "TEACHER") BrandColors.RoyalIndigo else Color.Transparent)
                    .clickable { viewModel.setKycRole("TEACHER") }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("TEACHER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // Dropdown to pick person who has NO bank account yet
        ExposedDropdownMenuBox(
            expanded = selectDropdownExpanded,
            onExpandedChange = { selectDropdownExpanded = it }
        ) {
            val unassignedPeople = if (role == "STUDENT") {
                students.filter { it.bankAccountNumber == null }
            } else {
                teachers.filter { it.bankAccountNumber == null }
            }

            val currentSelectedName = if (role == "STUDENT") {
                students.find { it.id == selectedId }?.name ?: "Select a Student"
            } else {
                teachers.find { it.id == selectedId }?.name ?: "Select a Teacher"
            }

            OutlinedTextField(
                value = currentSelectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text(if (role == "STUDENT") "Select Student" else "Select Teacher") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectDropdownExpanded) },
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
                expanded = selectDropdownExpanded,
                onDismissRequest = { selectDropdownExpanded = false }
            ) {
                if (unassignedPeople.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No unassigned records found") },
                        onClick = {}
                    )
                } else {
                    unassignedPeople.forEach { person ->
                        val textName = if (person is Student) {
                            "${person.name} (${person.className})"
                        } else if (person is Teacher) {
                            "${person.name} (${person.department})"
                        } else {
                            ""
                        }
                        val personId = if (person is Student) {
                            person.id
                        } else if (person is Teacher) {
                            person.id
                        } else {
                            ""
                        }
                        DropdownMenuItem(
                            text = { Text(textName) },
                            onClick = {
                                viewModel.setOwnerId(personId)
                                selectDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = aadhar,
            onValueChange = { if (it.length <= 12) viewModel.setAadhar(it) },
            label = { Text("12-Digit Aadhar Card Number") },
            placeholder = { Text("e.g. 554433221100") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("aadhar_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = BrandColors.SoftWhite,
                unfocusedTextColor = BrandColors.SoftWhite,
                focusedBorderColor = BrandColors.RoyalIndigo,
                unfocusedBorderColor = BrandColors.BorderSlate
            )
        )

        OutlinedTextField(
            value = pan,
            onValueChange = { if (it.length <= 10) viewModel.setPan(it) },
            label = { Text("10-Character PAN Card Alphanumeric") },
            placeholder = { Text("e.g. ABCDE1234F") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("pan_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = BrandColors.SoftWhite,
                unfocusedTextColor = BrandColors.SoftWhite,
                focusedBorderColor = BrandColors.RoyalIndigo,
                unfocusedBorderColor = BrandColors.BorderSlate
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomButton(
                text = "Cancel",
                isSecondary = true,
                onClick = { viewModel.finishAccountOpening() },
                modifier = Modifier.weight(1f)
            )
            CustomButton(
                text = "Next: Scans Docs",
                onClick = { viewModel.submitInfoStep() },
                modifier = Modifier.weight(1.2f),
                testTag = "submit_info_step_btn"
            )
        }
    }
}

@Composable
fun Step2DocumentUpload(
    viewModel: SchoolViewModel
) {
    val aadharScanned by viewModel.aadharFrontScanned.collectAsState()
    val panScanned by viewModel.panScanned.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val scanningY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanning_line"
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Step 2: Interactive Document OCR Verification",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite)
        )

        InfoBanner(
            text = "Verify authentic copies of user identifications. Trigger the simulator to perform optical character reading and tax compliance scans."
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Aadhar Card Container
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        1.dp,
                        if (aadharScanned) BrandColors.EmeraldGreen else BrandColors.BorderSlate,
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = BrandColors.SpaceDark)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (aadharScanned) Icons.Default.CheckCircle else Icons.Default.ContactMail,
                        contentDescription = null,
                        tint = if (aadharScanned) BrandColors.EmeraldGreen else BrandColors.SoftGray,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        "Aadhar Card Scan",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.SoftWhite
                        )
                    )
                    CustomButton(
                        text = if (aadharScanned) "Re-Scan" else "Scan Aadhar",
                        onClick = { viewModel.simulateDocScan("AADHAR") },
                        isSecondary = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isScanning,
                        testTag = "scan_aadhar_btn"
                    )
                }
            }

            // PAN Card Container
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        1.dp,
                        if (panScanned) BrandColors.EmeraldGreen else BrandColors.BorderSlate,
                        RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = BrandColors.SpaceDark)
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (panScanned) Icons.Default.CheckCircle else Icons.Default.AssignmentInd,
                        contentDescription = null,
                        tint = if (panScanned) BrandColors.EmeraldGreen else BrandColors.SoftGray,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        "PAN Card Scan",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.SoftWhite
                        )
                    )
                    CustomButton(
                        text = if (panScanned) "Re-Scan" else "Scan PAN",
                        onClick = { viewModel.simulateDocScan("PAN") },
                        isSecondary = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isScanning,
                        testTag = "scan_pan_btn"
                    )
                }
            }
        }

        // SCANNING CAMERA VIEWFINDER SIMULATOR
        AnimatedVisibility(
            visible = isScanning,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
                    .border(2.dp, BrandColors.ElectricBlue, RoundedCornerShape(12.dp))
            ) {
                // Viewfinder lines
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .align(Alignment.TopStart)
                        .offset(y = (140 * scanningY).dp)
                        .background(BrandColors.ElectricBlue)
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = BrandColors.ElectricBlue)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "LIVE OCR COMPLIANCE ANALYZING...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BrandColors.ElectricBlue,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomButton(
                text = "Back",
                isSecondary = true,
                onClick = { viewModel.startAccountOpening() },
                modifier = Modifier.weight(1f)
            )
            CustomButton(
                text = "Next: Video KYC",
                onClick = { viewModel.submitDocumentsStep() },
                modifier = Modifier.weight(1.2f),
                enabled = aadharScanned && panScanned && !isScanning,
                testTag = "submit_docs_step_btn"
            )
        }
    }
}

@Composable
fun Step3VideoKyc(
    viewModel: SchoolViewModel
) {
    val kycStatus by viewModel.kycVideoStatus.collectAsState()
    val questionIndex by viewModel.kycQuestionIndex.collectAsState()
    val timerSeconds by viewModel.kycTimerSeconds.collectAsState()
    val faceInFrame by viewModel.faceInFrame.collectAsState()
    val isProcessingApproval by viewModel.isProcessingApproval.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Step 3: Interactive Verification Call (Video KYC)",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite)
        )

        if (kycStatus == KycVideoStatus.NOT_STARTED) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandColors.SpaceDark)
                    .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(BrandColors.RoyalIndigo.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoCall,
                            contentDescription = null,
                            tint = BrandColors.RoyalIndigo,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        "Secure Video KYC Session",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Text(
                        "Initiate a live simulated session with a virtual banking agent. Prepare PAN card and align your device camera.",
                        style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.SoftGray, textAlign = TextAlign.Center)
                    )
                    CustomButton(
                        text = "Connect with Officer",
                        onClick = { viewModel.startVideoKyc() },
                        testTag = "start_video_kyc_btn"
                    )
                }
            }
        } else if (isProcessingApproval) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = BrandColors.GoldAccent, strokeWidth = 5.dp, modifier = Modifier.size(50.dp))
                    Text(
                        "AUDITING COMPLIANCE CERTIFICATE...",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandColors.GoldAccent,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        "Running fraud checks, checking tax declarations, and generating virtual school debit card.",
                        style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.SoftGray, textAlign = TextAlign.Center),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            // CALL IN PROGRESS SCREEN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
                    .border(2.dp, BrandColors.CrimsonRed, RoundedCornerShape(12.dp))
                    .height(240.dp)
            ) {
                // Video Stream Simulacrum (Using a canvas to draw camera frame guide)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = if (faceInFrame) BrandColors.EmeraldGreen.copy(alpha = 0.3f) else Color.Red.copy(alpha = 0.3f),
                        radius = 180f,
                        center = center,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                    )
                }

                // Blinking Blip (Blinking Recording Indicator)
                val infiniteTransition = rememberInfiniteTransition(label = "recording")
                val alphaRecording by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                    label = "blinking"
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(BrandColors.CrimsonRed.copy(alpha = alphaRecording), CircleShape)
                    )
                    Text(
                        text = "REC",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                // Call duration timer
                val min = timerSeconds / 60
                val sec = timerSeconds % 60
                Text(
                    text = String.format("%02d:%02d", min, sec),
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontFamily = FontFamily.Monospace),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                )

                // Virtual Agent Dialogue
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                        .fillMaxWidth()
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = BrandColors.GoldAccent, modifier = Modifier.size(14.dp))
                            Text(
                                "VERIFICATION OFFICER (AI)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BrandColors.GoldAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.videoKycQuestions[questionIndex],
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White, lineHeight = 15.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { viewModel.cancelVideoKyc() },
                    colors = ButtonDefaults.textButtonColors(contentColor = BrandColors.CrimsonRed)
                ) {
                    Text("End Call", fontWeight = FontWeight.Bold)
                }

                CustomButton(
                    text = if (questionIndex == viewModel.videoKycQuestions.size - 1) "Finish Call & Verify" else "Answer & Continue",
                    icon = Icons.Default.KeyboardArrowRight,
                    onClick = { viewModel.answerKycQuestion() },
                    testTag = "answer_kyc_question_btn"
                )
            }
        }
    }
}

@Composable
fun Step4ApprovalSummary(
    viewModel: SchoolViewModel
) {
    val createdAcct by viewModel.createdAccount.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(BrandColors.EmeraldGreen.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = BrandColors.EmeraldGreen,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "KYC Verified Successfully!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = BrandColors.SoftWhite
            )
        )

        Text(
            text = "Welcome to EduTrust School Bank. Your personal checking and deposits account is now fully approved and active.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = BrandColors.SoftGray,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        if (createdAcct != null) {
            VirtualDebitCard(account = createdAcct!!)

            Spacer(modifier = Modifier.height(8.dp))

            // Summary Info box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandColors.SpaceDark)
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ReceiptField(label = "New Account Number", value = createdAcct!!.accountNumber)
                    ReceiptField(label = "Primary Card Number", value = createdAcct!!.cardNumber.take(14) + "XXXX")
                    ReceiptField(label = "Initial Welcome Balance", value = "₹10,000.00")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        CustomButton(
            text = "Activate & Go to Bank Dashboard",
            onClick = { viewModel.finishAccountOpening() },
            modifier = Modifier.fillMaxWidth(),
            testTag = "activate_acct_btn"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankTransactionDialog(
    account: BankAccount,
    type: TransactionType,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf(if (type == TransactionType.DEPOSIT) "Pocket Cash Deposit" else "Direct ATM Cash Withdrawal") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (type == TransactionType.DEPOSIT) "Log Cash Deposit" else "Log Cash Withdrawal",
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftWhite
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "A/C Number: ${account.accountNumber}\nOwner: ${account.ownerName}",
                    style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.SoftGray)
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (INR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("transaction_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BrandColors.SoftWhite,
                        unfocusedTextColor = BrandColors.SoftWhite,
                        focusedBorderColor = BrandColors.RoyalIndigo,
                        unfocusedBorderColor = BrandColors.BorderSlate
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Log Memo / Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("transaction_desc_input"),
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
                    if (amount > 0) {
                        onConfirm(amount, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                modifier = Modifier.testTag("confirm_transaction_btn")
            ) {
                Text("Confirm Log", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = BrandColors.SoftGray)) {
                Text("Cancel")
            }
        },
        containerColor = BrandColors.CardSlate,
        titleContentColor = BrandColors.SoftWhite
    )
}
