package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class Screen(val title: String) {
    object Dashboard : Screen("Dashboard")
    object FeeStructures : Screen("Fee Settings")
    object Invoices : Screen("Invoices & Billing")
    object SchoolBank : Screen("EduTrust School Bank")
    object Reports : Screen("Financial Reports")
    object Admission : Screen("Admissions")
    object ExamPapers : Screen("Exam Paper Maker")
}

class SchoolViewModel : ViewModel() {

    // --- Navigation State ---
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // --- Linked Flows from SchoolDatabase ---
    val students = SchoolDatabase.students
    val teachers = SchoolDatabase.teachers
    val feeStructures = SchoolDatabase.feeStructures
    val invoices = SchoolDatabase.invoices
    val bankAccounts = SchoolDatabase.bankAccounts
    val transactions = SchoolDatabase.transactions
    val loans = SchoolDatabase.loans
    val fixedDeposits = SchoolDatabase.fixedDeposits
    val homework = SchoolDatabase.homework
    val attendance = SchoolDatabase.attendance
    val examPapers = SchoolDatabase.examPapers

    // --- Role State ---
    private val _activeRole = MutableStateFlow(UserRole.SUPER_ADMIN)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    fun setActiveRole(role: UserRole) {
        _activeRole.value = role
        showMessage("Switched to role: ${role.displayName}")
    }

    // --- Filters & UI States ---
    private val _invoiceSearchQuery = MutableStateFlow("")
    val invoiceSearchQuery: StateFlow<String> = _invoiceSearchQuery.asStateFlow()

    private val _invoiceStatusFilter = MutableStateFlow<PaymentStatus?>(null)
    val invoiceStatusFilter: StateFlow<PaymentStatus?> = _invoiceStatusFilter.asStateFlow()

    private val _bankSearchQuery = MutableStateFlow("")
    val bankSearchQuery: StateFlow<String> = _bankSearchQuery.asStateFlow()

    private val _selectedClassForAutoBilling = MutableStateFlow("Grade 10-A")
    val selectedClassForAutoBilling: StateFlow<String> = _selectedClassForAutoBilling.asStateFlow()

    private val _selectedFeeForAutoBilling = MutableStateFlow<String>("")
    val selectedFeeForAutoBilling: StateFlow<String> = _selectedFeeForAutoBilling.asStateFlow()

    // --- Account Opening Wizard Flow States ---
    private val _isOpeningAccount = MutableStateFlow(false)
    val isOpeningAccount: StateFlow<Boolean> = _isOpeningAccount.asStateFlow()

    private val _wizardStep = MutableStateFlow(1) // 1: Info, 2: Document Upload, 3: Video KYC, 4: Approved/Result
    val wizardStep: StateFlow<Int> = _wizardStep.asStateFlow()

    // Info Step
    private val _kycRole = MutableStateFlow("STUDENT") // STUDENT or TEACHER
    val kycRole: StateFlow<String> = _kycRole.asStateFlow()

    private val _selectedOwnerId = MutableStateFlow("")
    val selectedOwnerId: StateFlow<String> = _selectedOwnerId.asStateFlow()

    private val _aadharNumber = MutableStateFlow("")
    val aadharNumber: StateFlow<String> = _aadharNumber.asStateFlow()

    private val _panNumber = MutableStateFlow("")
    val panNumber: StateFlow<String> = _panNumber.asStateFlow()

    // Documents Step (Live Simulation)
    private val _aadharFrontScanned = MutableStateFlow(false)
    val aadharFrontScanned: StateFlow<Boolean> = _aadharFrontScanned.asStateFlow()

    private val _panScanned = MutableStateFlow(false)
    val panScanned: StateFlow<Boolean> = _panScanned.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // Video KYC Step
    private val _kycVideoStatus = MutableStateFlow(KycVideoStatus.NOT_STARTED)
    val kycVideoStatus: StateFlow<KycVideoStatus> = _kycVideoStatus.asStateFlow()

    private val _kycQuestionIndex = MutableStateFlow(0)
    val kycQuestionIndex: StateFlow<Int> = _kycQuestionIndex.asStateFlow()

    private val _kycTimerSeconds = MutableStateFlow(0)
    val kycTimerSeconds: StateFlow<Int> = _kycTimerSeconds.asStateFlow()

    private val _faceInFrame = MutableStateFlow(false)
    val faceInFrame: StateFlow<Boolean> = _faceInFrame.asStateFlow()

    private val _isProcessingApproval = MutableStateFlow(false)
    val isProcessingApproval: StateFlow<Boolean> = _isProcessingApproval.asStateFlow()

    // Completed Account details
    private val _createdAccount = MutableStateFlow<BankAccount?>(null)
    val createdAccount: StateFlow<BankAccount?> = _createdAccount.asStateFlow()

    // --- Message Alerts ---
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    init {
        // Run auto check for past overdue invoices
        SchoolDatabase.autoCheckOverdueInvoices()

        // Set default fee for auto billing if available
        viewModelScope.launch {
            feeStructures.collect { list ->
                if (list.isNotEmpty() && _selectedFeeForAutoBilling.value.isEmpty()) {
                    _selectedFeeForAutoBilling.value = list.first().id
                }
            }
        }
    }

    // --- Setter Helpers ---
    fun setInvoiceSearch(query: String) { _invoiceSearchQuery.value = query }
    fun setInvoiceStatusFilter(status: PaymentStatus?) { _invoiceStatusFilter.value = status }
    fun setBankSearch(query: String) { _bankSearchQuery.value = query }
    fun setSelectedClass(cls: String) { _selectedClassForAutoBilling.value = cls }
    fun setSelectedFee(feeId: String) { _selectedFeeForAutoBilling.value = feeId }
    fun showMessage(msg: String) { _uiMessage.value = msg }
    fun clearMessage() { _uiMessage.value = null }

    // --- Billing Engine Triggers ---
    fun createFeeStructure(title: String, amount: Double, category: FeeCategory, frequency: FeeFrequency, description: String) {
        SchoolDatabase.createFeeStructure(title, amount, category, frequency, description)
        showMessage("Fee structure '$title' created successfully!")
    }

    fun deleteFeeStructure(id: String) {
        SchoolDatabase.deleteFeeStructure(id)
        showMessage("Fee structure removed")
    }

    fun triggerAutomatedBilling() {
        val count = SchoolDatabase.runAutomatedInvoicingForClass(
            _selectedClassForAutoBilling.value,
            _selectedFeeForAutoBilling.value
        )
        if (count > 0) {
            showMessage("Automated Billing Success: Generated $count invoices for ${_selectedClassForAutoBilling.value}!")
        } else {
            showMessage("No new invoices generated. Students might already be billed for this structure this month.")
        }
    }

    fun payInvoiceDirect(invoiceId: String, method: PaymentMethod) {
        val result = SchoolDatabase.payInvoice(invoiceId, method)
        showMessage(result.second)
    }

    // --- Banking Flow Logic ---
    fun startAccountOpening() {
        _isOpeningAccount.value = true
        _wizardStep.value = 1
        _selectedOwnerId.value = ""
        _aadharNumber.value = ""
        _panNumber.value = ""
        _aadharFrontScanned.value = false
        _panScanned.value = false
        _kycVideoStatus.value = KycVideoStatus.NOT_STARTED
        _kycQuestionIndex.value = 0
        _faceInFrame.value = false
        _createdAccount.value = null
    }

    fun setKycRole(role: String) {
        _kycRole.value = role
        _selectedOwnerId.value = ""
    }

    fun setOwnerId(id: String) {
        _selectedOwnerId.value = id
    }

    fun setAadhar(num: String) { _aadharNumber.value = num }
    fun setPan(num: String) { _panNumber.value = num }

    fun submitInfoStep(): Boolean {
        if (_selectedOwnerId.value.isEmpty()) {
            showMessage("Please select a student or teacher")
            return false
        }
        if (_aadharNumber.value.length < 12) {
            showMessage("Aadhar number must be a 12-digit number")
            return false
        }
        if (_panNumber.value.length < 10) {
            showMessage("PAN number must be a 10-character alphanumeric string")
            return false
        }
        _wizardStep.value = 2
        return true
    }

    fun simulateDocScan(docType: String) {
        viewModelScope.launch {
            _isScanning.value = true
            delay(2000) // 2s simulated optical scanning
            _isScanning.value = false
            if (docType == "AADHAR") {
                _aadharFrontScanned.value = true
                showMessage("Aadhar Card scanned and OCR validated successfully!")
            } else {
                _panScanned.value = true
                showMessage("PAN Card scanned and tax compliance validated successfully!")
            }
        }
    }

    fun submitDocumentsStep(): Boolean {
        if (!_aadharFrontScanned.value || !_panScanned.value) {
            showMessage("Please scan both required documents before proceeding")
            return false
        }
        _wizardStep.value = 3
        return true
    }

    // Video KYC interactive simulation
    val videoKycQuestions = listOf(
        "Please look directly into the camera. Confirm your full name and role in the school.",
        "Hold up your PAN Card clearly next to your face for a snapshot match.",
        "What is the last digit of your Aadhar card? (Say/Type to verify security credentials)."
    )

    fun startVideoKyc() {
        _kycVideoStatus.value = KycVideoStatus.CALL_IN_PROGRESS
        _kycQuestionIndex.value = 0
        _kycTimerSeconds.value = 0
        _faceInFrame.value = false

        viewModelScope.launch {
            // Simulate agent picking up the call
            delay(1500)
            _faceInFrame.value = true
            // Start local seconds counter
            launch {
                while (_kycVideoStatus.value == KycVideoStatus.CALL_IN_PROGRESS) {
                    delay(1000)
                    _kycTimerSeconds.value += 1
                }
            }
        }
    }

    fun answerKycQuestion() {
        val currentIndex = _kycQuestionIndex.value
        if (currentIndex < videoKycQuestions.size - 1) {
            _kycQuestionIndex.value = currentIndex + 1
        } else {
            // End video KYC & generate account!
            viewModelScope.launch {
                _kycVideoStatus.value = KycVideoStatus.COMPLETED
                _isProcessingApproval.value = true
                delay(2500) // Simulate backend creation and security checks

                val account = SchoolDatabase.requestBankAccount(
                    ownerId = _selectedOwnerId.value,
                    ownerRole = _kycRole.value,
                    kycAadhar = _aadharNumber.value,
                    kycPan = _panNumber.value
                )

                SchoolDatabase.updateKycVideoStatus(account.accountNumber, KycVideoStatus.COMPLETED, 3)
                _createdAccount.value = SchoolDatabase.bankAccounts.value[account.accountNumber]

                _isProcessingApproval.value = false
                _wizardStep.value = 4
                showMessage("School Bank Account opened successfully! ₹10,000 Welcome Deposit Credited.")
            }
        }
    }

    fun cancelVideoKyc() {
        _kycVideoStatus.value = KycVideoStatus.NOT_STARTED
        _kycQuestionIndex.value = 0
        _faceInFrame.value = false
    }

    fun finishAccountOpening() {
        _isOpeningAccount.value = false
        _wizardStep.value = 1
        navigateTo(Screen.SchoolBank)
    }

    fun depositToAccount(acctNo: String, amount: Double, desc: String) {
        SchoolDatabase.depositMoney(acctNo, amount, desc)
        showMessage("Credited ₹${String.format("%,.2f", amount)} successfully!")
    }

    fun withdrawFromAccount(acctNo: String, amount: Double, desc: String) {
        val success = SchoolDatabase.withdrawMoney(acctNo, amount, desc)
        if (success) {
            showMessage("Withdrew ₹${String.format("%,.2f", amount)} successfully!")
        } else {
            showMessage("Withdrawal failed: Insufficient funds!")
        }
    }

    // Filters logic
    val filteredInvoices = combine(invoices, invoiceSearchQuery, invoiceStatusFilter) { list, search, status ->
        list.filter { invoice ->
            val matchesSearch = invoice.studentName.contains(search, ignoreCase = true) ||
                    invoice.id.contains(search, ignoreCase = true) ||
                    invoice.className.contains(search, ignoreCase = true) ||
                    invoice.feeTitle.contains(search, ignoreCase = true)

            val matchesStatus = status == null || invoice.status == status

            matchesSearch && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredBankAccounts = combine(bankAccounts, bankSearchQuery) { map, search ->
        map.values.filter { account ->
            account.ownerName.contains(search, ignoreCase = true) ||
                    account.accountNumber.contains(search, ignoreCase = true) ||
                    account.ownerRole.contains(search, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialStats = invoices.map {
        SchoolDatabase.getFinancialStats()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SchoolDatabase.getFinancialStats())

    // --- Loan ViewModel Actions ---
    fun applyForLoan(applicantId: String, applicantName: String, loanType: String, amount: Double, termMonths: Int) {
        SchoolDatabase.applyForLoan(applicantId, applicantName, loanType, amount, termMonths)
        showMessage("Applied for ₹${String.format("%,.0f", amount)} $loanType Loan successfully!")
    }

    fun approveLoan(loanId: String) {
        SchoolDatabase.setLoanStatus(loanId, "APPROVED")
        showMessage("Loan application approved and funds disbursed!")
    }

    fun rejectLoan(loanId: String) {
        SchoolDatabase.setLoanStatus(loanId, "REJECTED")
        showMessage("Loan application rejected.")
    }

    // --- Fixed Deposit ViewModel Actions ---
    fun createFixedDeposit(acctNo: String, ownerName: String, amount: Double, rate: Double, termMonths: Int) {
        val success = SchoolDatabase.createFixedDeposit(acctNo, ownerName, amount, rate, termMonths)
        if (success) {
            showMessage("Created ₹${String.format("%,.0f", amount)} FD at $rate% APY!")
        } else {
            showMessage("Insufficient balance in your account to create Fixed Deposit!")
        }
    }

    // --- Homework & Attendance ViewModel Actions ---
    fun addHomework(className: String, subject: String, task: String, dueDate: String) {
        SchoolDatabase.addHomework(className, subject, task, dueDate)
        showMessage("Homework uploaded successfully for $className!")
    }

    fun setAttendance(studentId: String, date: String, isPresent: Boolean) {
        SchoolDatabase.setAttendance(studentId, date, isPresent)
    }

    // --- AI & Chatbot States & Actions ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(content = "Welcome to EduTrust ERP AI Assistant! I can help you with homework coaching, grade prediction, transaction fraud scans, or financial recommendations. Ask me anything!", isUser = false, timestamp = "12:00")
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    fun setChatInput(text: String) {
        _chatInput.value = text
    }

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiOutput = MutableStateFlow("")
    val aiOutput: StateFlow<String> = _aiOutput.asStateFlow()

    fun sendChatMessage() {
        val prompt = _chatInput.value.trim()
        if (prompt.isEmpty()) return

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val userMsg = ChatMessage(content = prompt, isUser = true, timestamp = sdf.format(Date()))
        _chatMessages.value = _chatMessages.value + userMsg
        _chatInput.value = ""

        viewModelScope.launch {
            _aiLoading.value = true
            val systemIns = "You are a secure, modern AI assistant integrated into a combined School Management and Banking ERP. Answer helpful questions based on student records and banking dashboards."
            val reply = GeminiHelper.generateContent(prompt, systemIns)
            val botMsg = ChatMessage(content = reply, isUser = false, timestamp = sdf.format(Date()))
            _chatMessages.value = _chatMessages.value + botMsg
            _aiLoading.value = false
        }
    }

    fun runPredictiveModel(type: String, promptInput: String) {
        viewModelScope.launch {
            _aiLoading.value = true
            _aiOutput.value = ""
            val systemIns = "You are an AI ERP Predictive Analytics Engine. Generate precise predictive forecasts in neat markdown format."
            val reply = GeminiHelper.generateContent("Analyze and predict for $type: $promptInput", systemIns)
            _aiOutput.value = reply
            _aiLoading.value = false
        }
    }

    // --- Student Admission State & Actions ---
    private val _admissionAiOutput = MutableStateFlow("")
    val admissionAiOutput: StateFlow<String> = _admissionAiOutput.asStateFlow()

    private val _admissionAiLoading = MutableStateFlow(false)
    val admissionAiLoading: StateFlow<Boolean> = _admissionAiLoading.asStateFlow()

    fun admitStudent(
        name: String,
        rollNo: String,
        className: String,
        contact: String,
        email: String,
        kycAadhar: String,
        kycPan: String,
        initialDeposit: Double,
        autoApproveKyc: Boolean,
        selectedFeeStructureId: String? = null
    ) {
        val s = SchoolDatabase.admitStudent(
            name, rollNo, className, contact, email, kycAadhar, kycPan, initialDeposit, autoApproveKyc, selectedFeeStructureId
        )
        showMessage("Successfully Admitted student ${s.name} into $className!")
    }

    fun runAdmissionAiAssessment(studentName: String, gpa: String, recommendations: String, previousSchool: String) {
        viewModelScope.launch {
            _admissionAiLoading.value = true
            _admissionAiOutput.value = ""
            val systemIns = "You are an AI Admission Evaluator for EduTrust International School. Analyze the applicant's academic history, GPA, and recommendations, and output: 1. Recommended Class / Stream, 2. A beautiful, friendly personalized Welcome Message from the Principal, 3. Proposed initial wallet allocation recommendation based on economic indicators or academic merit."
            val prompt = "Evaluate applicant $studentName. Prior GPA: $gpa. Previous School: $previousSchool. Remarks / Extracurriculars: $recommendations."
            val reply = GeminiHelper.generateContent(prompt, systemIns)
            _admissionAiOutput.value = reply
            _admissionAiLoading.value = false
        }
    }

    fun clearAdmissionAiOutput() {
        _admissionAiOutput.value = ""
    }

    // --- Exam Question Paper Maker Flow States ---
    private val _draftExamTitle = MutableStateFlow("")
    val draftExamTitle: StateFlow<String> = _draftExamTitle.asStateFlow()

    private val _draftExamSubject = MutableStateFlow("")
    val draftExamSubject: StateFlow<String> = _draftExamSubject.asStateFlow()

    private val _draftExamClass = MutableStateFlow("Grade 10-A")
    val draftExamClass: StateFlow<String> = _draftExamClass.asStateFlow()

    private val _draftExamTotalMarks = MutableStateFlow(50)
    val draftExamTotalMarks: StateFlow<Int> = _draftExamTotalMarks.asStateFlow()

    private val _draftExamDuration = MutableStateFlow(120)
    val draftExamDuration: StateFlow<Int> = _draftExamDuration.asStateFlow()

    private val _draftExamInstructions = MutableStateFlow("Answer all questions. Show working steps clearly.")
    val draftExamInstructions: StateFlow<String> = _draftExamInstructions.asStateFlow()

    private val _draftQuestions = MutableStateFlow<List<ExamQuestion>>(emptyList())
    val draftQuestions: StateFlow<List<ExamQuestion>> = _draftQuestions.asStateFlow()

    private val _examAiLoading = MutableStateFlow(false)
    val examAiLoading: StateFlow<Boolean> = _examAiLoading.asStateFlow()

    private val _aiDraftedText = MutableStateFlow("")
    val aiDraftedText: StateFlow<String> = _aiDraftedText.asStateFlow()

    fun updateDraftInfo(title: String, subject: String, className: String, totalMarks: Int, duration: Int, instructions: String) {
        _draftExamTitle.value = title
        _draftExamSubject.value = subject
        _draftExamClass.value = className
        _draftExamTotalMarks.value = totalMarks
        _draftExamDuration.value = duration
        _draftExamInstructions.value = instructions
    }

    fun addDraftQuestion(questionText: String, marks: Int, type: QuestionType, mcqOptions: List<String> = emptyList(), correctAnswer: String = "") {
        val question = ExamQuestion(
            questionText = questionText,
            marks = marks,
            type = type,
            mcqOptions = mcqOptions,
            correctAnswer = correctAnswer
        )
        _draftQuestions.value = _draftQuestions.value + question
    }

    fun removeDraftQuestion(questionId: String) {
        _draftQuestions.value = _draftQuestions.value.filterNot { it.id == questionId }
    }

    fun clearDraft() {
        _draftExamTitle.value = ""
        _draftExamSubject.value = ""
        _draftExamClass.value = "Grade 10-A"
        _draftExamTotalMarks.value = 50
        _draftExamDuration.value = 120
        _draftExamInstructions.value = "Answer all questions. Show working steps clearly."
        _draftQuestions.value = emptyList()
        _aiDraftedText.value = ""
    }

    fun saveDraftAsExamPaper() {
        val title = _draftExamTitle.value.trim()
        val subject = _draftExamSubject.value.trim()
        if (title.isEmpty() || subject.isEmpty()) {
            showMessage("Please specify Exam Title and Subject")
            return
        }
        val paper = ExamQuestionPaper(
            title = title,
            subject = subject,
            className = _draftExamClass.value,
            totalMarks = _draftExamTotalMarks.value,
            durationMinutes = _draftExamDuration.value,
            instructions = _draftExamInstructions.value,
            questions = _draftQuestions.value,
            createdByTeacherName = "Mrs. Shalini Roy", // Fallback / Demo teacher
            createdDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        SchoolDatabase.addExamQuestionPaper(paper)
        clearDraft()
        showMessage("Exam Paper '$title' saved to Vault!")
    }

    fun deleteExamPaper(paperId: String) {
        SchoolDatabase.deleteExamQuestionPaper(paperId)
        showMessage("Exam Paper deleted successfully.")
    }

    fun generateExamQuestionsWithAi(topicPrompt: String) {
        viewModelScope.launch {
            _examAiLoading.value = true
            _aiDraftedText.value = ""
            val title = _draftExamTitle.value.ifEmpty { "Exam Test" }
            val subject = _draftExamSubject.value.ifEmpty { "Science" }
            val className = _draftExamClass.value
            val systemIns = "You are an expert curriculum designer and educator. Draft a comprehensive exam question paper with exact sections and clear marks allocation. Include multiple choice questions with options and answers, short questions, and long essay questions."
            val prompt = "Draft an exam question paper for class '$className', subject '$subject', focusing on topic or chapter: '$topicPrompt'. The paper should be approximately ${_draftExamTotalMarks.value} marks, with ${_draftExamDuration.value} minutes duration."
            val reply = GeminiHelper.generateContent(prompt, systemIns)
            _aiDraftedText.value = reply
            
            // Auto-populate questions into draft
            val parsedQuestions = parseQuestionsFromMarkdown(reply)
            if (parsedQuestions.isNotEmpty()) {
                _draftQuestions.value = _draftQuestions.value + parsedQuestions
            }
            
            _examAiLoading.value = false
            showMessage("AI Questions generated and auto-populated into draft!")
        }
    }

    private fun parseQuestionsFromMarkdown(text: String): List<ExamQuestion> {
        val list = mutableListOf<ExamQuestion>()
        try {
            val lines = text.split("\n")
            var currentQuestionText = ""
            var currentOptions = mutableListOf<String>()
            var currentType = QuestionType.SHORT
            var currentMarks = 5
            
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("**Q") || trimmed.startsWith("Q") || trimmed.startsWith("Question")) {
                    // Save previous question
                    if (currentQuestionText.isNotEmpty()) {
                        list.add(ExamQuestion(
                            questionText = currentQuestionText,
                            marks = currentMarks,
                            type = currentType,
                            mcqOptions = if (currentType == QuestionType.MCQ) currentOptions.toList() else emptyList()
                        ))
                        currentOptions = mutableListOf()
                    }
                    
                    // Parse new question text
                    currentQuestionText = trimmed.replace(Regex("^\\*\\*Q\\d+\\.\\*\\*|^Q\\d+\\.|^Question\\s+\\d+:"), "").trim()
                    
                    // Extract marks if present
                    if (currentQuestionText.contains("marks", ignoreCase = true)) {
                        val match = Regex("(?i)\\((\\d+)\\s*marks?\\)").find(currentQuestionText)
                        currentMarks = match?.groupValues?.get(1)?.toIntOrNull() ?: 5
                    } else {
                        currentMarks = 5
                    }
                    
                    currentType = when {
                        currentQuestionText.contains("multiple choice", ignoreCase = true) || currentQuestionText.contains("choose", ignoreCase = true) -> QuestionType.MCQ
                        currentMarks >= 10 -> QuestionType.LONG
                        else -> QuestionType.SHORT
                    }
                } else if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                    val opt = trimmed.substring(2).trim()
                    if (opt.startsWith("A)") || opt.startsWith("B)") || opt.startsWith("C)") || opt.startsWith("D)") ||
                        opt.startsWith("a)") || opt.startsWith("b)") || opt.startsWith("c)") || opt.startsWith("d)")) {
                        currentType = QuestionType.MCQ
                        currentOptions.add(opt)
                    }
                }
            }
            
            // Add last question
            if (currentQuestionText.isNotEmpty()) {
                list.add(ExamQuestion(
                    questionText = currentQuestionText,
                    marks = currentMarks,
                    type = currentType,
                    mcqOptions = if (currentType == QuestionType.MCQ) currentOptions.toList() else emptyList()
                ))
            }
        } catch (e: Exception) {
            // Fail silently
        }
        return list
    }
}
