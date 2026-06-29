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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SchoolViewModel
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamPapersScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val examPapers by viewModel.examPapers.collectAsState()
    val draftTitle by viewModel.draftExamTitle.collectAsState()
    val draftSubject by viewModel.draftExamSubject.collectAsState()
    val draftClass by viewModel.draftExamClass.collectAsState()
    val draftMarks by viewModel.draftExamTotalMarks.collectAsState()
    val draftDuration by viewModel.draftExamDuration.collectAsState()
    val draftInstructions by viewModel.draftExamInstructions.collectAsState()
    val draftQuestions by viewModel.draftQuestions.collectAsState()
    val aiDraftText by viewModel.aiDraftedText.collectAsState()
    val aiLoading by viewModel.examAiLoading.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: Vault, 1: AI Creator & Builder
    var selectedPaperForPreview by remember { mutableStateOf<ExamQuestionPaper?>(null) }

    // Manual Question Input Fields
    var manualQuestionText by remember { mutableStateOf("") }
    var manualType by remember { mutableStateOf(QuestionType.SHORT) }
    var manualMarks by remember { mutableStateOf("5") }
    var manualOptionA by remember { mutableStateOf("") }
    var manualOptionB by remember { mutableStateOf("") }
    var manualOptionC by remember { mutableStateOf("") }
    var manualOptionD by remember { mutableStateOf("") }
    var manualCorrectAnswer by remember { mutableStateOf("") }

    var aiTopicPrompt by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandColors.SpaceDark)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                title = "Exam Vault & AI Builder",
                subtitle = "Authorized Academic Staff Portal Only"
            )

            // Role Banner Warning
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandColors.RoyalIndigo.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, BrandColors.RoyalIndigo.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = BrandColors.RoyalIndigo,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            "Verified Teacher Terminal Access",
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.SoftWhite,
                            fontSize = 12.sp
                        )
                        Text(
                            "Questions generated or finalized here are encrypted and locked within the school's ERP exam storage vaults.",
                            color = BrandColors.SoftGray,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Tab Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(BrandColors.CardSlate, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Button(
                    onClick = { activeTab = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == 0) BrandColors.SpaceDark else Color.Transparent,
                        contentColor = if (activeTab == 0) BrandColors.SoftWhite else BrandColors.SoftGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_exam_vault")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Exam Vault (${examPapers.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { activeTab = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == 1) BrandColors.SpaceDark else Color.Transparent,
                        contentColor = if (activeTab == 1) BrandColors.SoftWhite else BrandColors.SoftGray
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tab_exam_builder")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("AI Paper Maker", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            AnimatedContent(
                targetState = activeTab,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut())
                },
                label = "exam_tab_transition",
                modifier = Modifier.weight(1f)
            ) { tabIndex ->
                if (tabIndex == 0) {
                    // Vault Tab
                    if (examPapers.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Inbox,
                                    contentDescription = null,
                                    tint = BrandColors.SoftGray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text("No exam papers found in secure vault.", color = BrandColors.SoftGray, fontSize = 14.sp)
                                Button(
                                    onClick = { activeTab = 1 },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo)
                                ) {
                                    Text("Build First Paper", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(examPapers) { paper ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
                                    border = BorderStroke(1.dp, BrandColors.BorderSlate),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedPaperForPreview = paper }
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .background(BrandColors.RoyalIndigo.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(paper.subject, fontSize = 10.sp, color = BrandColors.RoyalIndigo, fontWeight = FontWeight.Bold)
                                            }
                                            Text(
                                                "Class: ${paper.className}",
                                                color = BrandColors.SoftGray,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            paper.title,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = BrandColors.SoftWhite,
                                            fontSize = 16.sp
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Default.Grade, contentDescription = null, modifier = Modifier.size(12.dp), tint = BrandColors.GoldAccent)
                                                Text("${paper.totalMarks} Marks", fontSize = 11.sp, color = BrandColors.SoftGray)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(12.dp), tint = BrandColors.SoftGray)
                                                Text("${paper.durationMinutes} Mins", fontSize = 11.sp, color = BrandColors.SoftGray)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(12.dp), tint = BrandColors.SoftGray)
                                                Text("${paper.questions.size} Questions", fontSize = 11.sp, color = BrandColors.SoftGray)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        HorizontalDivider(color = BrandColors.BorderSlate)
                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "By: ${paper.createdByTeacherName} (${paper.createdDate})",
                                                fontSize = 10.sp,
                                                color = BrandColors.SoftGray
                                            )

                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                IconButton(
                                                    onClick = { selectedPaperForPreview = paper },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Visibility, contentDescription = "Preview Exam", tint = BrandColors.SoftWhite, modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(
                                                    onClick = { viewModel.deleteExamPaper(paper.id) },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandColors.CrimsonRed, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Builder Tab
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Section 1: Basic Paper Meta Setup
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
                                border = BorderStroke(1.dp, BrandColors.BorderSlate),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("1. Configure Examination Paper", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = draftTitle,
                                        onValueChange = { viewModel.updateDraftInfo(it, draftSubject, draftClass, draftMarks, draftDuration, draftInstructions) },
                                        label = { Text("Exam Paper Title (e.g., Mathematics Finals)", color = BrandColors.SoftGray) },
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

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = draftSubject,
                                            onValueChange = { viewModel.updateDraftInfo(draftTitle, it, draftClass, draftMarks, draftDuration, draftInstructions) },
                                            label = { Text("Subject", color = BrandColors.SoftGray) },
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
                                            value = draftClass,
                                            onValueChange = { viewModel.updateDraftInfo(draftTitle, draftSubject, it, draftMarks, draftDuration, draftInstructions) },
                                            label = { Text("Class / Grade", color = BrandColors.SoftGray) },
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

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = draftMarks.toString(),
                                            onValueChange = { viewModel.updateDraftInfo(draftTitle, draftSubject, draftClass, it.toIntOrNull() ?: 50, draftDuration, draftInstructions) },
                                            label = { Text("Total Marks", color = BrandColors.SoftGray) },
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
                                            value = draftDuration.toString(),
                                            onValueChange = { viewModel.updateDraftInfo(draftTitle, draftSubject, draftClass, draftMarks, it.toIntOrNull() ?: 120, draftInstructions) },
                                            label = { Text("Duration (Mins)", color = BrandColors.SoftGray) },
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

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = draftInstructions,
                                        onValueChange = { viewModel.updateDraftInfo(draftTitle, draftSubject, draftClass, draftMarks, draftDuration, it) },
                                        label = { Text("Instructions for Candidates", color = BrandColors.SoftGray) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = BrandColors.RoyalIndigo,
                                            unfocusedBorderColor = BrandColors.BorderSlate
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 2
                                    )
                                }
                            }
                        }

                        // Section 2: AI Question Generator
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
                                border = BorderStroke(1.dp, BrandColors.BorderSlate),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandColors.GoldAccent, modifier = Modifier.size(18.dp))
                                        Text("2. Generate Draft with Gemini AI", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Enter topics or syllabus to draft structured questions instantly.",
                                        fontSize = 11.sp,
                                        color = BrandColors.SoftGray
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = aiTopicPrompt,
                                        onValueChange = { aiTopicPrompt = it },
                                        label = { Text("Topic: e.g. Trigonometry, Periodic Table, Photosynthesis", color = BrandColors.SoftGray) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = BrandColors.RoyalIndigo,
                                            unfocusedBorderColor = BrandColors.BorderSlate
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Button(
                                        onClick = {
                                            if (aiTopicPrompt.isEmpty()) {
                                                viewModel.showMessage("Please specify topic or syllabus chapter.")
                                            } else {
                                                viewModel.generateExamQuestionsWithAi(aiTopicPrompt)
                                            }
                                        },
                                        enabled = !aiLoading,
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("ai_generate_exam_button")
                                    ) {
                                        if (aiLoading) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Syllabus Architect Drafting...", fontWeight = FontWeight.Bold)
                                        } else {
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Text("Generate and Auto-populate ➔", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    if (aiDraftText.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 200.dp)
                                                .background(BrandColors.SpaceDark, RoundedCornerShape(8.dp))
                                                .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(8.dp))
                                                .verticalScroll(rememberScrollState())
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = aiDraftText,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Section 3: Add Question Manually
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
                                border = BorderStroke(1.dp, BrandColors.BorderSlate),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("3. Add Manual Question to Builder", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = manualQuestionText,
                                        onValueChange = { manualQuestionText = it },
                                        label = { Text("Question Statement", color = BrandColors.SoftGray) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = BrandColors.RoyalIndigo,
                                            unfocusedBorderColor = BrandColors.BorderSlate
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Type Selector Row
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("Type", color = BrandColors.SoftGray, fontSize = 11.sp)
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                QuestionType.values().forEach { type ->
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .border(
                                                                1.dp,
                                                                if (manualType == type) BrandColors.RoyalIndigo else BrandColors.BorderSlate,
                                                                RoundedCornerShape(8.dp)
                                                            )
                                                            .background(
                                                                if (manualType == type) BrandColors.RoyalIndigo.copy(alpha = 0.15f) else Color.Transparent,
                                                                RoundedCornerShape(8.dp)
                                                            )
                                                            .clickable { manualType = type }
                                                            .padding(vertical = 8.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(type.name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                    }
                                                }
                                            }
                                        }

                                        OutlinedTextField(
                                            value = manualMarks,
                                            onValueChange = { manualMarks = it },
                                            label = { Text("Marks", color = BrandColors.SoftGray) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = BrandColors.RoyalIndigo,
                                                unfocusedBorderColor = BrandColors.BorderSlate
                                            ),
                                            modifier = Modifier.width(80.dp),
                                            singleLine = true
                                        )
                                    }

                                    if (manualType == QuestionType.MCQ) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("MCQ Options", color = BrandColors.SoftGray, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = manualOptionA,
                                            onValueChange = { manualOptionA = it },
                                            label = { Text("Option A", color = BrandColors.SoftGray) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = BrandColors.RoyalIndigo,
                                                unfocusedBorderColor = BrandColors.BorderSlate
                                            ),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = manualOptionB,
                                            onValueChange = { manualOptionB = it },
                                            label = { Text("Option B", color = BrandColors.SoftGray) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = BrandColors.RoyalIndigo,
                                                unfocusedBorderColor = BrandColors.BorderSlate
                                            ),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = manualOptionC,
                                            onValueChange = { manualOptionC = it },
                                            label = { Text("Option C (Optional)", color = BrandColors.SoftGray) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = BrandColors.RoyalIndigo,
                                                unfocusedBorderColor = BrandColors.BorderSlate
                                            ),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        OutlinedTextField(
                                            value = manualOptionD,
                                            onValueChange = { manualOptionD = it },
                                            label = { Text("Option D (Optional)", color = BrandColors.SoftGray) },
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White,
                                                focusedBorderColor = BrandColors.RoyalIndigo,
                                                unfocusedBorderColor = BrandColors.BorderSlate
                                            ),
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = manualCorrectAnswer,
                                        onValueChange = { manualCorrectAnswer = it },
                                        label = { Text("Answer Key / Marking Rubric (Optional)", color = BrandColors.SoftGray) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = BrandColors.RoyalIndigo,
                                            unfocusedBorderColor = BrandColors.BorderSlate
                                        ),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            if (manualQuestionText.isEmpty()) {
                                                viewModel.showMessage("Please fill in question statement.")
                                            } else {
                                                val opts = if (manualType == QuestionType.MCQ) {
                                                    listOfNotNull(
                                                        manualOptionA.ifEmpty { null },
                                                        manualOptionB.ifEmpty { null },
                                                        manualOptionC.ifEmpty { null },
                                                        manualOptionD.ifEmpty { null }
                                                    )
                                                } else emptyList()

                                                viewModel.addDraftQuestion(
                                                    questionText = manualQuestionText,
                                                    marks = manualMarks.toIntOrNull() ?: 5,
                                                    type = manualType,
                                                    mcqOptions = opts,
                                                    correctAnswer = manualCorrectAnswer
                                                )

                                                // Reset question fields
                                                manualQuestionText = ""
                                                manualOptionA = ""
                                                manualOptionB = ""
                                                manualOptionC = ""
                                                manualOptionD = ""
                                                manualCorrectAnswer = ""
                                                viewModel.showMessage("Added question to build list!")
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandColors.EmeraldGreen),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Text("Insert Question into Build Draft", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Section 4: Current Build Draft list
                        item {
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
                                            "4. Draft Question List (${draftQuestions.size})",
                                            fontWeight = FontWeight.Bold,
                                            color = BrandColors.SoftWhite,
                                            fontSize = 14.sp
                                        )
                                        if (draftQuestions.isNotEmpty()) {
                                            TextButton(onClick = { viewModel.clearDraft() }) {
                                                Text("Reset Draft", color = BrandColors.CrimsonRed, fontSize = 12.sp)
                                            }
                                        }
                                    }

                                    if (draftQuestions.isEmpty()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("No questions in active draft. Generate with Gemini above or type manually.", color = BrandColors.SoftGray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                                    } else {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        draftQuestions.forEachIndexed { index, question ->
                                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            "Q${index + 1}. ${question.questionText}",
                                                            color = Color.White,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                        if (question.type == QuestionType.MCQ && question.mcqOptions.isNotEmpty()) {
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                            question.mcqOptions.forEach { opt ->
                                                                Text("• $opt", color = BrandColors.SoftGray, fontSize = 11.sp, modifier = Modifier.padding(start = 12.dp))
                                                            }
                                                        }
                                                        if (question.correctAnswer.isNotEmpty()) {
                                                            Text("Ans: ${question.correctAnswer}", color = BrandColors.GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                                                        }
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        Box(
                                                            modifier = Modifier
                                                                .background(BrandColors.BorderSlate, RoundedCornerShape(6.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text("${question.marks} M", fontSize = 10.sp, color = BrandColors.SoftWhite, fontWeight = FontWeight.Bold)
                                                        }
                                                        IconButton(
                                                            onClick = { viewModel.removeDraftQuestion(question.id) },
                                                            modifier = Modifier.size(24.dp)
                                                        ) {
                                                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Remove", tint = BrandColors.CrimsonRed, modifier = Modifier.size(16.dp))
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                HorizontalDivider(color = BrandColors.BorderSlate.copy(alpha = 0.5f))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = { viewModel.saveDraftAsExamPaper() },
                                            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("save_exam_paper_button")
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Text("Encrypt & Save Paper to Secure Vault", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Preview Overlay Dialog (Print Layout Preview)
        selectedPaperForPreview?.let { paper ->
            AlertDialog(
                onDismissRequest = { selectedPaperForPreview = null },
                confirmButton = {
                    Button(
                        onClick = { selectedPaperForPreview = null },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo)
                    ) {
                        Text("Close Portal Terminal", fontWeight = FontWeight.Bold)
                    }
                },
                properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White, // Traditional white question paper texture
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(8.dp)
                    ) {
                        // Header Box
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "EDUTRUST INTERNATIONAL SECURE EXAM SYSTEM",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "OFFICIAL QUESTION PAPER STORAGE ENGINE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Metadata Details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subject: ${paper.subject}", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                            Text("Class: ${paper.className}", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Duration: ${paper.durationMinutes} Minutes", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                            Text("Max Marks: ${paper.totalMarks}", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Exam Title: ${paper.title}", fontWeight = FontWeight.ExtraBold, color = Color.Black, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color.Black, thickness = 2.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Instructions
                        Text("GENERAL INSTRUCTIONS:", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 11.sp)
                        Text(paper.instructions, color = Color.DarkGray, fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.Black, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Question rendering
                        if (paper.questions.isEmpty()) {
                            Text("No questions found in this paper.", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            paper.questions.forEachIndexed { qIndex, question ->
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            "Q${qIndex + 1}. ${question.questionText}",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            fontSize = 12.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            "[${question.marks} Marks]",
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(start = 12.dp)
                                        )
                                    }

                                    if (question.type == QuestionType.MCQ && question.mcqOptions.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        question.mcqOptions.forEach { opt ->
                                            Text(
                                                "   • $opt",
                                                color = Color.DarkGray,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(start = 12.dp)
                                            )
                                        }
                                    }

                                    if (question.correctAnswer.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "   * Marking Rubric Answer Key: ${question.correctAnswer}",
                                            color = Color(0xFF1E3A8A), // Deep blue indicator
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(start = 12.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(color = Color.LightGray)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            "End of Exam Paper. Prepared by: ${paper.createdByTeacherName} on ${paper.createdDate}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }
    }
}
