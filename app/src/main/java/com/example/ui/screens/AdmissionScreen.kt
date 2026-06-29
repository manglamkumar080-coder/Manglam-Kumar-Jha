package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SchoolViewModel
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdmissionScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.students.collectAsState()
    val feeStructures by viewModel.feeStructures.collectAsState()
    val bankAccounts by viewModel.bankAccounts.collectAsState()

    // Form inputs
    var name by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("Grade 10-A") }
    var parentContact by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Integrated banking details
    var linkBankAccount by remember { mutableStateOf(true) }
    var aadharNumber by remember { mutableStateOf("") }
    var panNumber by remember { mutableStateOf("") }
    var initialDepositStr by remember { mutableStateOf("10000") }
    var autoApproveKyc by remember { mutableStateOf(true) }

    // Integrated fee structures
    var allocateInitialFee by remember { mutableStateOf(false) }
    var selectedFeeStructureId by remember { mutableStateOf("") }

    // AI Profiler Inputs
    var aiPriorGpa by remember { mutableStateOf("") }
    var aiPrevSchool by remember { mutableStateOf("") }
    var aiRemarks by remember { mutableStateOf("") }

    val aiAdmissionOutput by viewModel.admissionAiOutput.collectAsState()
    val aiAdmissionLoading by viewModel.admissionAiLoading.collectAsState()

    val classOptions = listOf("Grade 9-A", "Grade 10-A", "Grade 11-Science", "Grade 11-Arts", "Grade 12-Science", "Grade 12-Commerce")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Section ---
        ScreenHeader(
            title = "Digital Admission Portal",
            subtitle = "Admit new students, auto-provision secure wallets & configure instant billing."
        )

        // --- SECTION 1: AI Admissions Profiler (GEMINI) ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
            border = BorderStroke(1.dp, BrandColors.BorderSlate),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BrandColors.GoldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        "AI Applicant Evaluation Engine",
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.SoftWhite,
                        fontSize = 16.sp
                    )
                }

                Text(
                    "Evaluate academic eligibility, recommend optimal subject streams, and generate welcome letters powered by Gemini 3.5.",
                    fontSize = 11.sp,
                    color = BrandColors.SoftGray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = aiPriorGpa,
                        onValueChange = { aiPriorGpa = it },
                        label = { Text("Prior GPA / % Score", color = BrandColors.SoftGray, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BrandColors.RoyalIndigo,
                            unfocusedBorderColor = BrandColors.BorderSlate
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = aiPrevSchool,
                        onValueChange = { aiPrevSchool = it },
                        label = { Text("Previous Institution", color = BrandColors.SoftGray, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BrandColors.RoyalIndigo,
                            unfocusedBorderColor = BrandColors.BorderSlate
                        ),
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = aiRemarks,
                    onValueChange = { aiRemarks = it },
                    label = { Text("Extracurricular Achievements / Principal Remarks", color = BrandColors.SoftGray, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BrandColors.RoyalIndigo,
                        unfocusedBorderColor = BrandColors.BorderSlate
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (name.isEmpty()) {
                                viewModel.showMessage("Please fill the applicant's name in the form first!")
                            } else if (aiPriorGpa.isEmpty()) {
                                viewModel.showMessage("Please specify the prior GPA score for evaluation.")
                            } else {
                                viewModel.runAdmissionAiAssessment(name, aiPriorGpa, aiRemarks, aiPrevSchool)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admission_ai_evaluate_button")
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (aiAdmissionLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Evaluate Applicant Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (aiAdmissionOutput.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearAdmissionAiOutput() },
                            colors = ButtonDefaults.textButtonColors(contentColor = BrandColors.SoftGray)
                        ) {
                            Text("Clear", fontSize = 11.sp)
                        }
                    }
                }

                AnimatedVisibility(visible = aiAdmissionOutput.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandColors.SpaceDark)
                            .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = BrandColors.EmeraldGreen, modifier = Modifier.size(14.dp))
                                Text("Gemini AI Admissions Report", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 12.sp)
                            }
                            Text(
                                text = aiAdmissionOutput,
                                color = BrandColors.SoftWhite,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // --- SECTION 2: ADMISSION REGISTRATION FORM ---
        Card(
            colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
            border = BorderStroke(1.dp, BrandColors.BorderSlate),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.AppRegistration,
                        contentDescription = null,
                        tint = BrandColors.ElectricBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        "Student Registration Profile",
                        fontWeight = FontWeight.Bold,
                        color = BrandColors.SoftWhite,
                        fontSize = 16.sp
                    )
                }

                // Student name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Full Name", color = BrandColors.SoftGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BrandColors.RoyalIndigo,
                        unfocusedBorderColor = BrandColors.BorderSlate
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Roll number
                    OutlinedTextField(
                        value = rollNo,
                        onValueChange = { rollNo = it },
                        label = { Text("Roll Number", color = BrandColors.SoftGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BrandColors.RoyalIndigo,
                            unfocusedBorderColor = BrandColors.BorderSlate
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    // Class selection dropdown
                    var classMenuExpanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1.5f)) {
                        OutlinedTextField(
                            value = className,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Assigned Grade", color = BrandColors.SoftGray) },
                            trailingIcon = {
                                IconButton(onClick = { classMenuExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrandColors.RoyalIndigo,
                                unfocusedBorderColor = BrandColors.BorderSlate
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = classMenuExpanded,
                            onDismissRequest = { classMenuExpanded = false },
                            modifier = Modifier.background(BrandColors.CardSlate)
                        ) {
                            classOptions.forEach { cls ->
                                DropdownMenuItem(
                                    text = { Text(cls, color = Color.White) },
                                    onClick = {
                                        className = cls
                                        classMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Contact
                    OutlinedTextField(
                        value = parentContact,
                        onValueChange = { parentContact = it },
                        label = { Text("Guardian Contact", color = BrandColors.SoftGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BrandColors.RoyalIndigo,
                            unfocusedBorderColor = BrandColors.BorderSlate
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = BrandColors.SoftGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = BrandColors.RoyalIndigo,
                            unfocusedBorderColor = BrandColors.BorderSlate
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                HorizontalDivider(color = BrandColors.BorderSlate, modifier = Modifier.padding(vertical = 4.dp))

                // --- INTEGRATED DIGITAL WALLET BANKING ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { linkBankAccount = !linkBankAccount },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = BrandColors.EmeraldGreen, modifier = Modifier.size(18.dp))
                        Column {
                            Text("Auto-Provision EduTrust Savings Wallet", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Text("Assign secure RFID card & bank account with admission.", fontSize = 10.sp, color = BrandColors.SoftGray)
                        }
                    }
                    Switch(
                        checked = linkBankAccount,
                        onCheckedChange = { linkBankAccount = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandColors.EmeraldGreen,
                            checkedTrackColor = BrandColors.EmeraldGreen.copy(alpha = 0.3f)
                        )
                    )
                }

                AnimatedVisibility(visible = linkBankAccount) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = aadharNumber,
                                onValueChange = { aadharNumber = it },
                                label = { Text("Student Aadhar UIDAI", color = BrandColors.SoftGray, fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = BrandColors.RoyalIndigo,
                                    unfocusedBorderColor = BrandColors.BorderSlate
                                ),
                                modifier = Modifier.weight(1.2f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = panNumber,
                                onValueChange = { panNumber = it },
                                label = { Text("Parent PAN Card", color = BrandColors.SoftGray, fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = BrandColors.RoyalIndigo,
                                    unfocusedBorderColor = BrandColors.BorderSlate
                                ),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = initialDepositStr,
                                onValueChange = { initialDepositStr = it },
                                label = { Text("Initial Allowance Deposit (₹)", color = BrandColors.SoftGray, fontSize = 11.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = BrandColors.RoyalIndigo,
                                    unfocusedBorderColor = BrandColors.BorderSlate
                                ),
                                modifier = Modifier.weight(1.2f),
                                singleLine = true
                            )

                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { autoApproveKyc = !autoApproveKyc }
                                    .padding(start = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = autoApproveKyc,
                                    onCheckedChange = { autoApproveKyc = it },
                                    colors = CheckboxDefaults.colors(checkedColor = BrandColors.EmeraldGreen)
                                )
                                Text("Bypass Video KYC", color = Color.White, fontSize = 11.sp)
                            }
                        }
                    }
                }

                HorizontalDivider(color = BrandColors.BorderSlate, modifier = Modifier.padding(vertical = 4.dp))

                // --- INTEGRATED FEE STRUCTURE AUTO-BILLING ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { allocateInitialFee = !allocateInitialFee },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = BrandColors.GoldAccent, modifier = Modifier.size(18.dp))
                        Column {
                            Text("Automatic Fee Structure Allocation", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Text("Auto-generate a pending fee invoice on successful admission.", fontSize = 10.sp, color = BrandColors.SoftGray)
                        }
                    }
                    Switch(
                        checked = allocateInitialFee,
                        onCheckedChange = { allocateInitialFee = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandColors.GoldAccent,
                            checkedTrackColor = BrandColors.GoldAccent.copy(alpha = 0.3f)
                        )
                    )
                }

                AnimatedVisibility(visible = allocateInitialFee) {
                    if (feeStructures.isEmpty()) {
                        Text(
                            "No active fee rules created. Go to Fee Rules tab to add rules.",
                            color = BrandColors.SoftGray,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    } else {
                        var feeMenuExpanded by remember { mutableStateOf(false) }
                        val activeStructure = feeStructures.find { it.id == selectedFeeStructureId }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = activeStructure?.let { "${it.title} (₹${String.format("%,.0f", it.amount)})" } ?: "Select Fee Structure Rule...",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Fee Structure", color = BrandColors.SoftGray) },
                                trailingIcon = {
                                    IconButton(onClick = { feeMenuExpanded = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = BrandColors.RoyalIndigo,
                                    unfocusedBorderColor = BrandColors.BorderSlate
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = feeMenuExpanded,
                                onDismissRequest = { feeMenuExpanded = false },
                                modifier = Modifier.background(BrandColors.CardSlate)
                            ) {
                                feeStructures.forEach { fs ->
                                    DropdownMenuItem(
                                        text = { Text("${fs.title} (₹${String.format("%,.0f", fs.amount)})", color = Color.White) },
                                        onClick = {
                                            selectedFeeStructureId = fs.id
                                            feeMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Submit Admission Button
                Button(
                    onClick = {
                        val depositValue = initialDepositStr.toDoubleOrNull() ?: 0.0
                        if (name.trim().isEmpty()) {
                            viewModel.showMessage("Please specify student full name!")
                        } else if (rollNo.trim().isEmpty()) {
                            viewModel.showMessage("Please specify roll number!")
                        } else if (parentContact.trim().isEmpty()) {
                            viewModel.showMessage("Please provide parent/guardian contact!")
                        } else if (linkBankAccount && (aadharNumber.isEmpty() || panNumber.isEmpty())) {
                            viewModel.showMessage("Please enter valid Aadhar & PAN for Wallet opening!")
                        } else {
                            viewModel.admitStudent(
                                name = name.trim(),
                                rollNo = rollNo.trim(),
                                className = className,
                                contact = parentContact.trim(),
                                email = email.trim(),
                                kycAadhar = if (linkBankAccount) aadharNumber else "",
                                kycPan = if (linkBankAccount) panNumber else "",
                                initialDeposit = if (linkBankAccount) depositValue else 0.0,
                                autoApproveKyc = autoApproveKyc,
                                selectedFeeStructureId = if (allocateInitialFee) selectedFeeStructureId else null
                            )
                            // Clear form
                            name = ""
                            rollNo = ""
                            parentContact = ""
                            email = ""
                            aadharNumber = ""
                            panNumber = ""
                            initialDepositStr = "10000"
                            selectedFeeStructureId = ""
                            allocateInitialFee = false
                            viewModel.clearAdmissionAiOutput()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.EmeraldGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admission_submit_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admit & Enrol Student", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
                }
            }
        }

        // --- SECTION 3: RECENTLY ADMITTED ROSTER ---
        Text(
            text = "RECENT ADMISSIONS ROSTER",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftGray,
                letterSpacing = 1.sp
            )
        )

        if (students.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandColors.CardSlate),
                contentAlignment = Alignment.Center
            ) {
                Text("No admissions recorded in database.", color = BrandColors.SoftGray)
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                students.reversed().forEach { student ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(16.dp)),
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
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(BrandColors.RoyalIndigo.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.PersonOutline,
                                        contentDescription = null,
                                        tint = BrandColors.ElectricBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${student.className} • Roll ${student.rollNo}",
                                        fontSize = 10.sp,
                                        color = BrandColors.SoftGray
                                    )
                                    Text(
                                        text = "Email: ${student.email.ifEmpty { "N/A" }} • Tel: ${student.parentContact}",
                                        fontSize = 10.sp,
                                        color = BrandColors.SoftGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Dynamic Status of student wallet linked
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (student.bankAccountNumber != null) {
                                    val acc = bankAccounts[student.bankAccountNumber]
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (acc?.status == AccountStatus.APPROVED) BrandColors.EmeraldGreen.copy(alpha = 0.15f)
                                                else BrandColors.GoldAccent.copy(alpha = 0.15f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (acc?.status == AccountStatus.APPROVED) "Wallet Approved" else "Wallet Pending KYC",
                                            color = if (acc?.status == AccountStatus.APPROVED) BrandColors.EmeraldGreen else BrandColors.GoldAccent,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (acc != null) {
                                        Text(
                                            text = "Bal: ₹${String.format("%,.0f", acc.balance)}",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .background(BrandColors.BorderSlate, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Wallet Excluded",
                                            color = BrandColors.SoftGray,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}
