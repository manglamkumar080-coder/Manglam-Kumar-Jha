package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserRole
import com.example.ui.Screen
import com.example.ui.SchoolViewModel
import com.example.ui.components.BrandColors
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppLayout(viewModel)
            }
        }
    }
}

@Composable
fun MainAppLayout(viewModel: SchoolViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val activeRole by viewModel.activeRole.collectAsState()
    val context = LocalContext.current

    // Trigger standard native alerts when state messages change
    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandColors.SpaceDark),
        bottomBar = {
            NavigationBar(
                containerColor = BrandColors.CardSlate,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("app_bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentScreen is Screen.Dashboard,
                    onClick = { viewModel.navigateTo(Screen.Dashboard) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Console", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandColors.RoyalIndigo,
                        selectedTextColor = BrandColors.SoftWhite,
                        unselectedIconColor = BrandColors.SoftGray,
                        unselectedTextColor = BrandColors.SoftGray,
                        indicatorColor = BrandColors.BorderSlate
                    ),
                    modifier = Modifier.testTag("nav_item_dashboard")
                )

                NavigationBarItem(
                    selected = currentScreen is Screen.Admission,
                    onClick = { viewModel.navigateTo(Screen.Admission) },
                    icon = { Icon(Icons.Default.School, contentDescription = "Admissions") },
                    label = { Text("Admissions", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandColors.RoyalIndigo,
                        selectedTextColor = BrandColors.SoftWhite,
                        unselectedIconColor = BrandColors.SoftGray,
                        unselectedTextColor = BrandColors.SoftGray,
                        indicatorColor = BrandColors.BorderSlate
                    ),
                    modifier = Modifier.testTag("nav_item_admission")
                )

                NavigationBarItem(
                    selected = currentScreen is Screen.FeeStructures,
                    onClick = { viewModel.navigateTo(Screen.FeeStructures) },
                    icon = { Icon(Icons.Default.SettingsInputComponent, contentDescription = "Fee Settings") },
                    label = { Text("Fee Rules", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandColors.RoyalIndigo,
                        selectedTextColor = BrandColors.SoftWhite,
                        unselectedIconColor = BrandColors.SoftGray,
                        unselectedTextColor = BrandColors.SoftGray,
                        indicatorColor = BrandColors.BorderSlate
                    ),
                    modifier = Modifier.testTag("nav_item_fee_rules")
                )

                NavigationBarItem(
                    selected = currentScreen is Screen.Invoices,
                    onClick = { viewModel.navigateTo(Screen.Invoices) },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Invoices") },
                    label = { Text("Invoices", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandColors.RoyalIndigo,
                        selectedTextColor = BrandColors.SoftWhite,
                        unselectedIconColor = BrandColors.SoftGray,
                        unselectedTextColor = BrandColors.SoftGray,
                        indicatorColor = BrandColors.BorderSlate
                    ),
                    modifier = Modifier.testTag("nav_item_invoices")
                )

                NavigationBarItem(
                    selected = currentScreen is Screen.SchoolBank,
                    onClick = { viewModel.navigateTo(Screen.SchoolBank) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "School Bank") },
                    label = { Text("Bank", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandColors.RoyalIndigo,
                        selectedTextColor = BrandColors.SoftWhite,
                        unselectedIconColor = BrandColors.SoftGray,
                        unselectedTextColor = BrandColors.SoftGray,
                        indicatorColor = BrandColors.BorderSlate
                    ),
                    modifier = Modifier.testTag("nav_item_bank")
                )

                NavigationBarItem(
                    selected = currentScreen is Screen.Reports,
                    onClick = { viewModel.navigateTo(Screen.Reports) },
                    icon = { Icon(Icons.Default.QueryStats, contentDescription = "Reports") },
                    label = { Text("Reports", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandColors.RoyalIndigo,
                        selectedTextColor = BrandColors.SoftWhite,
                        unselectedIconColor = BrandColors.SoftGray,
                        unselectedTextColor = BrandColors.SoftGray,
                        indicatorColor = BrandColors.BorderSlate
                    ),
                    modifier = Modifier.testTag("nav_item_reports")
                )

                if (activeRole == UserRole.TEACHER) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.ExamPapers,
                        onClick = { viewModel.navigateTo(Screen.ExamPapers) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Exam Maker") },
                        label = { Text("Exam Maker", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandColors.RoyalIndigo,
                            selectedTextColor = BrandColors.SoftWhite,
                            unselectedIconColor = BrandColors.SoftGray,
                            unselectedTextColor = BrandColors.SoftGray,
                            indicatorColor = BrandColors.BorderSlate
                        ),
                        modifier = Modifier.testTag("nav_item_exam_papers")
                    )
                }
            }
        },
        containerColor = BrandColors.SpaceDark
    ) { innerPadding ->
        // Constrain widescreen canvas to 720dp maximum width to keep layouts pristine on tablets/Web stream
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BrandColors.SpaceDark),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        (fadeIn() + slideInVertically { it / 2 }).togetherWith(fadeOut())
                    },
                    label = "screen_routing_transition"
                ) { screen ->
                    when (screen) {
                        is Screen.Dashboard -> DashboardScreen(viewModel = viewModel)
                        is Screen.Admission -> AdmissionScreen(viewModel = viewModel)
                        is Screen.FeeStructures -> FeeStructureScreen(viewModel = viewModel)
                        is Screen.Invoices -> InvoicesScreen(viewModel = viewModel)
                        is Screen.SchoolBank -> SchoolBankScreen(viewModel = viewModel)
                        is Screen.Reports -> ReportsScreen(viewModel = viewModel)
                        is Screen.ExamPapers -> ExamPapersScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
