package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.Screen
import com.example.ui.SchoolViewModel
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeRole by viewModel.activeRole.collectAsState()
    val stats by viewModel.financialStats.collectAsState()
    val bankAccounts by viewModel.filteredBankAccounts.collectAsState()
    val invoices by viewModel.filteredInvoices.collectAsState()
    val recentTransactions by viewModel.transactions.collectAsState()
    
    // Additional domain flows
    val loans by viewModel.loans.collectAsState()
    val fixedDeposits by viewModel.fixedDeposits.collectAsState()
    val homework by viewModel.homework.collectAsState()
    val attendance by viewModel.attendance.collectAsState()
    val students by viewModel.students.collectAsState()

    // UI state managers
    var showRoleMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- Custom Enterprise Banner with Active Role Switcher ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(BrandColors.RoyalIndigo.copy(alpha = 0.4f), Color.Transparent),
                        radius = 400f
                    )
                )
                .background(BrandColors.CardSlate)
                .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "EduTrust ERP Hub",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = BrandColors.SoftWhite,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = "Enterprise Management & Digital Banking",
                            style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.SoftGray)
                        )
                    }

                    // Role switcher badge
                    Box {
                        Button(
                            onClick = { showRoleMenu = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("role_switcher_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text(
                                    text = activeRole.displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false },
                            modifier = Modifier.background(BrandColors.CardSlate)
                        ) {
                            UserRole.values().forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(role.displayName, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(role.category, fontSize = 10.sp, color = BrandColors.SoftGray)
                                        }
                                    },
                                    onClick = {
                                        viewModel.setActiveRole(role)
                                        showRoleMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Core Metrics Section (Dynamic representation) ---
        Text(
            text = "SYSTEM METRICS & FINANCIALS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftGray,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "ERP Collected Fees",
                value = "₹${String.format("%,.0f", stats.totalCollected)}",
                icon = Icons.Default.Payments,
                color = BrandColors.EmeraldGreen,
                modifier = Modifier.weight(1f),
                subtitle = "Total bills: ₹${String.format("%,.0f", stats.totalBilled)}"
            )
            MetricCard(
                title = "Total Bank Deposits",
                value = "₹${String.format("%,.0f", stats.totalDeposits)}",
                icon = Icons.Default.AccountBalance,
                color = BrandColors.ElectricBlue,
                modifier = Modifier.weight(1f),
                subtitle = "Active A/C: ${stats.activeAccounts}"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- Dynamic Content Panel depending on ACTIVE ROLE ---
        Text(
            text = "ROLE-BASED WORKSPACE: ${activeRole.displayName.uppercase()}",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                color = BrandColors.GoldAccent,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        AnimatedContent(
            targetState = activeRole,
            transitionSpec = {
                (fadeIn() + slideInVertically()).togetherWith(fadeOut())
            },
            label = "role_workspace_transition"
        ) { role ->
            when (role) {
                UserRole.SUPER_ADMIN, UserRole.SCHOOL_ADMIN, UserRole.PRINCIPAL -> {
                    AdminWorkspaceView(viewModel = viewModel, stats = stats)
                }
                UserRole.TEACHER -> {
                    TeacherWorkspaceView(viewModel = viewModel, homeworkList = homework, students = students)
                }
                UserRole.STUDENT -> {
                    StudentWorkspaceView(viewModel = viewModel, homeworkList = homework, bankAccounts = bankAccounts)
                }
                UserRole.PARENT -> {
                    ParentWorkspaceView(viewModel = viewModel, invoices = invoices, bankAccounts = bankAccounts)
                }
                UserRole.BANK_ADMIN, UserRole.BANK_EMPLOYEE -> {
                    BankEmployeeWorkspaceView(viewModel = viewModel, bankAccounts = bankAccounts, loans = loans)
                }
                UserRole.CUSTOMER -> {
                    CustomerWorkspaceView(viewModel = viewModel, bankAccounts = bankAccounts, loans = loans)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Standard Universal Section: School Bank Directory & Transactions ---
        Text(
            text = "SCHOOL BANK DIRECTORY",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftGray,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (bankAccounts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandColors.CardSlate)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No bank accounts registered yet.", color = BrandColors.SoftGray)
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                items(bankAccounts) { acct ->
                    RowAccountCard(account = acct, onClick = { viewModel.navigateTo(Screen.SchoolBank) })
                }
            }
        }

        // --- Real-time Ledger Transactions ---
        Text(
            text = "CENTRAL TRANSACTIONS LEDGER",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftGray,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (recentTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandColors.CardSlate)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No transactions logged yet.", color = BrandColors.SoftGray)
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                recentTransactions.take(4).forEach { txn ->
                    TransactionRow(txn = txn)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- GLOBAL AI ERP INTERACTIVE ASSISTANT DRAWER (Chatbot) ---
        InteractiveAiChatAssistantView(viewModel = viewModel)

        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ==================== ROLE WORKSPACE VIEWPORTS ====================

@Composable
fun AdminWorkspaceView(viewModel: SchoolViewModel, stats: FinancialStats) {
    val aiOutput by viewModel.aiOutput.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()
    
    Card(
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
        border = BorderStroke(1.dp, BrandColors.BorderSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "AI-Powered ERP Insights",
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftWhite,
                fontSize = 16.sp
            )
            Text(
                "Run instant deep neural predictions on academic performance & fee collection risks.",
                fontSize = 12.sp,
                color = BrandColors.SoftGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.runPredictiveModel("Attendance & Graduation Forecast", "Analyze cohort attendance profiles across s1 to s6 for drop-out metrics.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Predict Attendance", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.runPredictiveModel("Academic Performance & Grade Forecast", "Synthesize mock scores to predict secondary board results and rank curves.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.ElectricBlue),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Predict Grades", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    viewModel.runPredictiveModel("AI Report Card Remarks", "Aarav Sharma: Math score 95, physics score 88, english literature score 78. Draft final report remarks.")
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.GoldAccent),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Text("AI-Generate Student Assessment", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.navigateTo(Screen.Admission)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.EmeraldGreen),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Text("Open Admissions Portal ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            if (aiLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BrandColors.ElectricBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Consulting Gemini AI API...", color = BrandColors.SoftGray, fontSize = 12.sp)
                }
            }

            if (aiOutput.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandColors.SpaceDark)
                        .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = aiOutput,
                        color = Color.White,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TeacherWorkspaceView(viewModel: SchoolViewModel, homeworkList: List<HomeworkItem>, students: List<Student>) {
    var subject by remember { mutableStateOf("") }
    var taskText by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("2026-07-05") }
    var selectedClass by remember { mutableStateOf("Grade 10-A") }

    Card(
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
        border = BorderStroke(1.dp, BrandColors.BorderSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Upload Academic Assignment / Homework",
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftWhite,
                fontSize = 16.sp
            )
            Text(
                "Distribute assignments instantly across ERP student portals.",
                fontSize = 11.sp,
                color = BrandColors.SoftGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Inputs
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject", color = BrandColors.SoftGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = BrandColors.RoyalIndigo,
                    unfocusedBorderColor = BrandColors.BorderSlate
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = taskText,
                onValueChange = { taskText = it },
                label = { Text("Task Instructions", color = BrandColors.SoftGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = BrandColors.RoyalIndigo,
                    unfocusedBorderColor = BrandColors.BorderSlate
                ),
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (subject.isEmpty() || taskText.isEmpty()) {
                        viewModel.showMessage("Please fill in assignment details")
                    } else {
                        viewModel.addHomework(selectedClass, subject, taskText, dueDate)
                        subject = ""
                        taskText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.EmeraldGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publish Assignment", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BrandColors.BorderSlate)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Attendance Log Grid (Today)", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            students.forEach { student ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(student.name, color = Color.White, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.setAttendance(student.id, "2026-06-29", true) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.EmeraldGreen.copy(alpha = 0.2f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text("Present", color = BrandColors.EmeraldGreen, fontSize = 10.sp)
                        }
                        Button(
                            onClick = { viewModel.setAttendance(student.id, "2026-06-29", false) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.CrimsonRed.copy(alpha = 0.2f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                        ) {
                            Text("Absent", color = BrandColors.CrimsonRed, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentWorkspaceView(viewModel: SchoolViewModel, homeworkList: List<HomeworkItem>, bankAccounts: List<BankAccount>) {
    val myAccount = bankAccounts.find { it.ownerId == "s1" } // Demo s1 Aarav Sharma

    Card(
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
        border = BorderStroke(1.dp, BrandColors.BorderSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Digital ID Card & Wallet QR",
                    fontWeight = FontWeight.Bold,
                    color = BrandColors.SoftWhite,
                    fontSize = 16.sp
                )
                Box(
                    modifier = Modifier
                        .background(BrandColors.RoyalIndigo, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("RFID Validated", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual ID Card Layout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(BrandColors.SpaceDark, BrandColors.BorderSlate)))
                    .border(1.dp, BrandColors.RoyalIndigo, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Aarav Sharma", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                        Text("Grade 10-A • Roll 101", color = BrandColors.SoftGray, fontSize = 12.sp)
                        Text("EduTrust Student Wallet: ACTIVE", color = BrandColors.ElectricBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Balance: ₹${myAccount?.let { String.format("%,.2f", it.balance) } ?: "0.00"}",
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandColors.GoldAccent,
                            fontSize = 16.sp
                        )
                    }

                    // High tech visual QR simulation
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing grid lines for QR look
                        Canvas(modifier = Modifier.size(62.dp)) {
                            drawRect(color = Color.Black, size = this.size)
                            // Draw simulated central white block
                            drawRect(
                                color = Color.White,
                                topLeft = androidx.compose.ui.geometry.Offset(15f, 15f),
                                size = androidx.compose.ui.geometry.Size(this.size.width - 30f, this.size.height - 30f)
                            )
                            drawRect(
                                color = Color.Black,
                                topLeft = androidx.compose.ui.geometry.Offset(25f, 25f),
                                size = androidx.compose.ui.geometry.Size(this.size.width - 50f, this.size.height - 50f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Pending Assignments", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            homeworkList.forEach { hw ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandColors.SpaceDark)
                        .padding(10.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(hw.subject, color = BrandColors.ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Due: ${hw.dueDate}", color = BrandColors.CrimsonRed, fontSize = 10.sp)
                        }
                        Text(hw.task, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ParentWorkspaceView(viewModel: SchoolViewModel, invoices: List<Invoice>, bankAccounts: List<BankAccount>) {
    val studentInvoices = invoices.filter { it.studentId == "s1" } // Aarav Sharma child
    val parentAccount = bankAccounts.find { it.ownerId == "s1" } // Linked pocket wallet

    Card(
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
        border = BorderStroke(1.dp, BrandColors.BorderSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Financial Control for Child: Aarav Sharma",
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftWhite,
                fontSize = 16.sp
            )
            Text(
                "Directly authorize pending school dues using linked accounts.",
                fontSize = 11.sp,
                color = BrandColors.SoftGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text("Pending Child Fee Invoices", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            studentInvoices.filter { it.status != PaymentStatus.PAID }.forEach { inv ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandColors.SpaceDark)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(inv.feeTitle, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("₹${String.format("%,.0f", inv.amount)} • Due ${inv.dueDate}", color = BrandColors.SoftGray, fontSize = 10.sp)
                        }
                        Button(
                            onClick = {
                                viewModel.payInvoiceDirect(inv.id, PaymentMethod.ONLINE_BANK)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                        ) {
                            Text("Pay via Bank", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BrandColors.BorderSlate)
            Spacer(modifier = Modifier.height(12.dp))

            // Student Pocket Limit
            Text("Allowance Cap Manager", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 13.sp)
            Text("Set maximum child QR/RFID spending allowance per month.", fontSize = 11.sp, color = BrandColors.SoftGray)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pocket Limit: ₹3,000 / month", color = Color.White, fontSize = 12.sp)
                Button(
                    onClick = { viewModel.showMessage("Child spending limit updated successfully!") },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.GoldAccent),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text("Modify Limit", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BankEmployeeWorkspaceView(viewModel: SchoolViewModel, bankAccounts: List<BankAccount>, loans: List<LoanApplication>) {
    val pendingKyc = bankAccounts.filter { it.status == AccountStatus.PENDING_KYC }
    val pendingLoans = loans.filter { it.status == "PENDING" }

    Card(
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
        border = BorderStroke(1.dp, BrandColors.BorderSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Digital Bank ERP Operations Panel",
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftWhite,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text("KYC Verification Queue", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 13.sp)
            Text("Review and clear digital photo/Aadhar KYC applications.", fontSize = 11.sp, color = BrandColors.SoftGray)
            Spacer(modifier = Modifier.height(8.dp))

            if (pendingKyc.isEmpty()) {
                Text("No pending KYC applications.", color = BrandColors.EmeraldGreen, fontSize = 12.sp)
            } else {
                pendingKyc.forEach { acc ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandColors.SpaceDark)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(acc.ownerName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Aadhar: ${acc.kycAadhar} • PAN: ${acc.kycPan}", color = BrandColors.SoftGray, fontSize = 10.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { SchoolDatabase.updateKycVideoStatus(acc.accountNumber, KycVideoStatus.COMPLETED, 3) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.EmeraldGreen),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text("Approve", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BrandColors.BorderSlate)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Pending Loan Approval Pipeline", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (pendingLoans.isEmpty()) {
                Text("No pending loan approvals.", color = BrandColors.EmeraldGreen, fontSize = 12.sp)
            } else {
                pendingLoans.forEach { loan ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BrandColors.SpaceDark)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${loan.applicantName} (${loan.loanType})", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Amount: ₹${String.format("%,.0f", loan.amount)} • ${loan.termMonths} Mo", color = BrandColors.SoftGray, fontSize = 10.sp)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { viewModel.approveLoan(loan.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.EmeraldGreen),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text("Disburse", fontSize = 10.sp)
                                }
                                Button(
                                    onClick = { viewModel.rejectLoan(loan.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.CrimsonRed),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text("Reject", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerWorkspaceView(viewModel: SchoolViewModel, bankAccounts: List<BankAccount>, loans: List<LoanApplication>) {
    val myAccount = bankAccounts.find { it.ownerId == "s1" } // s1 mock customer
    var sendAmount by remember { mutableStateOf("") }
    var sendTarget by remember { mutableStateOf("") }

    var loanAmount by remember { mutableStateOf("") }
    var loanType by remember { mutableStateOf("Education") }

    Card(
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
        border = BorderStroke(1.dp, BrandColors.BorderSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Digital Banking & KYC Services",
                fontWeight = FontWeight.Bold,
                color = BrandColors.SoftWhite,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Send Money Form
            Text("Send Money (NEFT / RTGS / UPI)", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = sendTarget,
                onValueChange = { sendTarget = it },
                label = { Text("Beneficiary Account Number / UPI ID", color = BrandColors.SoftGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = BrandColors.RoyalIndigo,
                    unfocusedBorderColor = BrandColors.BorderSlate
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = sendAmount,
                onValueChange = { sendAmount = it },
                label = { Text("Amount (₹)", color = BrandColors.SoftGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = BrandColors.RoyalIndigo,
                    unfocusedBorderColor = BrandColors.BorderSlate
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val amountVal = sendAmount.toDoubleOrNull()
                    if (myAccount == null) {
                        viewModel.showMessage("No active bank account found.")
                    } else if (amountVal == null || amountVal <= 0) {
                        viewModel.showMessage("Enter a valid transfer amount.")
                    } else if (sendTarget.isEmpty()) {
                        viewModel.showMessage("Enter target beneficiary.")
                    } else {
                        val success = SchoolDatabase.withdrawMoney(myAccount.accountNumber, amountVal, "Fund Transfer to $sendTarget")
                        if (success) {
                            viewModel.showMessage("Transfer of ₹${String.format("%,.0f", amountVal)} successful!")
                            sendAmount = ""
                            sendTarget = ""
                        } else {
                            viewModel.showMessage("Insufficient balance!")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Instant Transfer", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BrandColors.BorderSlate)
            Spacer(modifier = Modifier.height(12.dp))

            // 2. Loan application
            Text("Request ERP Education / Personal Loan", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = loanAmount,
                onValueChange = { loanAmount = it },
                label = { Text("Requested Amount (₹)", color = BrandColors.SoftGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = BrandColors.RoyalIndigo,
                    unfocusedBorderColor = BrandColors.BorderSlate
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val amt = loanAmount.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        viewModel.showMessage("Enter a valid loan amount")
                    } else {
                        viewModel.applyForLoan("s1", "Aarav Sharma", loanType, amt, 24)
                        loanAmount = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.GoldAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply for Loan", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==================== GLOBAL INTERACTIVE AI CHATBOT ====================

@Composable
fun InteractiveAiChatAssistantView(viewModel: SchoolViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val chatInput by viewModel.chatInput.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
        border = BorderStroke(1.dp, BrandColors.BorderSlate),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(BrandColors.EmeraldGreen, CircleShape)
                )
                Text(
                    "EduTrust ERP AI Assistant",
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandColors.SoftWhite,
                    fontSize = 16.sp
                )
            }
            Text(
                "Ask for attendance predictions, homework help, report card drafting, or spend recommendations.",
                fontSize = 11.sp,
                color = BrandColors.SoftGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Scrollable Chat log
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandColors.SpaceDark)
                    .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    chatMessages.forEach { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 12.dp,
                                            topEnd = 12.dp,
                                            bottomStart = if (msg.isUser) 12.dp else 0.dp,
                                            bottomEnd = if (msg.isUser) 0.dp else 12.dp
                                        )
                                    )
                                    .background(
                                        if (msg.isUser) BrandColors.RoyalIndigo else BrandColors.CardSlate
                                    )
                                    .padding(10.dp)
                                    .widthIn(max = 240.dp)
                            ) {
                                Column {
                                    Text(
                                        text = msg.content,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                    Text(
                                        text = msg.timestamp,
                                        color = BrandColors.SoftGray,
                                        fontSize = 9.sp,
                                        modifier = Modifier
                                            .align(Alignment.End)
                                            .padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (aiLoading) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = BrandColors.ElectricBlue, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gemini is typing...", color = BrandColors.SoftGray, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chat input row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { viewModel.setChatInput(it) },
                    placeholder = { Text("Ask Gemini AI...", color = BrandColors.SoftGray, fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BrandColors.RoyalIndigo,
                        unfocusedBorderColor = BrandColors.BorderSlate
                    ),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = { viewModel.sendChatMessage() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(50.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}
