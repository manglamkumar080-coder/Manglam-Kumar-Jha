package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

object SchoolDatabase {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    // --- State Flows for Jetpack Compose ---
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _teachers = MutableStateFlow<List<Teacher>>(emptyList())
    val teachers: StateFlow<List<Teacher>> = _teachers.asStateFlow()

    private val _feeStructures = MutableStateFlow<List<FeeStructure>>(emptyList())
    val feeStructures: StateFlow<List<FeeStructure>> = _feeStructures.asStateFlow()

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    private val _bankAccounts = MutableStateFlow<Map<String, BankAccount>>(emptyMap())
    val bankAccounts: StateFlow<Map<String, BankAccount>> = _bankAccounts.asStateFlow()

    private val _transactions = MutableStateFlow<List<BankTransaction>>(emptyList())
    val transactions: StateFlow<List<BankTransaction>> = _transactions.asStateFlow()

    private val _loans = MutableStateFlow<List<LoanApplication>>(emptyList())
    val loans: StateFlow<List<LoanApplication>> = _loans.asStateFlow()

    private val _fixedDeposits = MutableStateFlow<List<FixedDeposit>>(emptyList())
    val fixedDeposits: StateFlow<List<FixedDeposit>> = _fixedDeposits.asStateFlow()

    private val _homework = MutableStateFlow<List<HomeworkItem>>(emptyList())
    val homework: StateFlow<List<HomeworkItem>> = _homework.asStateFlow()

    private val _attendance = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendance: StateFlow<List<AttendanceRecord>> = _attendance.asStateFlow()

    private val _examPapers = MutableStateFlow<List<ExamQuestionPaper>>(emptyList())
    val examPapers: StateFlow<List<ExamQuestionPaper>> = _examPapers.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        // 1. Initial Students
        val mockStudents = listOf(
            Student("s1", "Aarav Sharma", "101", "Grade 10-A", "+91 98765 43210", "aarav.sharma@school.edu", "SCHB-AARAV-101"),
            Student("s2", "Ishita Patel", "102", "Grade 10-A", "+91 98765 43211", "ishita.patel@school.edu", "SCHB-ISHITA-102"),
            Student("s3", "Rohan Das", "103", "Grade 10-B", "+91 98765 43212", "rohan.das@school.edu", null),
            Student("s4", "Ananya Iyer", "104", "Grade 11-Science", "+91 98765 43213", "ananya.iyer@school.edu", "SCHB-ANANYA-104"),
            Student("s5", "Kabir Mehta", "105", "Grade 11-Commerce", "+91 98765 43214", "kabir.mehta@school.edu", null),
            Student("s6", "Siddharth Sen", "106", "Grade 9-A", "+91 98765 43215", "sid.sen@school.edu", "SCHB-SID-106")
        )
        _students.value = mockStudents

        // 2. Initial Teachers
        val mockTeachers = listOf(
            Teacher("t1", "Mrs. Shalini Roy", "EMP-201", "Mathematics", "+91 91111 22222", "shalini.roy@school.edu", "SCHB-SHALINI-201"),
            Teacher("t2", "Mr. Amit Verma", "EMP-202", "Physics", "+91 91111 22223", "amit.verma@school.edu", null),
            Teacher("t3", "Dr. Rajesh Gupta", "EMP-203", "Chemistry", "+91 91111 22224", "rajesh.gupta@school.edu", "SCHB-RAJESH-203")
        )
        _teachers.value = mockTeachers

        // 3. Fee Structures
        val mockFeeStructures = listOf(
            FeeStructure("f1", "Senior Secondary Tuition Fee", 15000.0, FeeCategory.TUITION, FeeFrequency.TERM, "Standard tuition fee for Grades 10-12 covering academic instruction."),
            FeeStructure("f2", "School Transport Route A", 2500.0, FeeCategory.TRANSPORT, FeeFrequency.MONTHLY, "Air-conditioned school bus service covering Zone A areas."),
            FeeStructure("f3", "Computer Lab & Robotics", 3500.0, FeeCategory.ACTIVITY, FeeFrequency.QUARTERLY, "Access to high-speed IT labs, microcontrollers, and 3D printers."),
            FeeStructure("f4", "Annual Sports Club & Gym", 4000.0, FeeCategory.SPORTS, FeeFrequency.ANNUALLY, "Access to school playground, swimming pool, and equipment rental."),
            FeeStructure("f5", "Term Examination Charges", 1200.0, FeeCategory.EXAM, FeeFrequency.TERM, "Covers printing of answer booklets, exam sheets, and evaluation software.")
        )
        _feeStructures.value = mockFeeStructures

        // 4. Invoices
        val today = Calendar.getInstance()
        val dateToday = dateFormat.format(today.time)

        val calendarDue = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 15) }
        val dateDue = dateFormat.format(calendarDue.time)

        val calendarOverdue = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -10) }
        val dateOverdue = dateFormat.format(calendarOverdue.time)

        val mockInvoices = listOf(
            Invoice("i1", "s1", "Aarav Sharma", "Grade 10-A", "f1", "Senior Secondary Tuition Fee", FeeCategory.TUITION, 15000.0, "2026-06-01", "2026-06-15", PaymentStatus.PAID, "2026-06-10", PaymentMethod.ONLINE_BANK, "TXN-9021-392"),
            Invoice("i2", "s1", "Aarav Sharma", "Grade 10-A", "f2", "School Transport Route A", FeeCategory.TRANSPORT, 2500.0, dateToday, dateDue, PaymentStatus.PENDING),
            Invoice("i3", "s2", "Ishita Patel", "Grade 10-A", "f1", "Senior Secondary Tuition Fee", FeeCategory.TUITION, 15000.0, dateToday, dateDue, PaymentStatus.PENDING),
            Invoice("i4", "s4", "Ananya Iyer", "Grade 11-Science", "f3", "Computer Lab & Robotics", FeeCategory.ACTIVITY, 3500.0, "2026-05-15", dateOverdue, PaymentStatus.OVERDUE),
            Invoice("i5", "s6", "Siddharth Sen", "Grade 9-A", "f5", "Term Examination Charges", FeeCategory.EXAM, 1200.0, dateToday, dateDue, PaymentStatus.PENDING)
        )
        _invoices.value = mockInvoices

        // 5. Initial Bank Accounts
        val mockBankAccounts = mutableMapOf<String, BankAccount>()

        // Aarav Sharma's Bank Account (Approved)
        mockBankAccounts["SCHB-AARAV-101"] = BankAccount(
            accountNumber = "SCHB-AARAV-101",
            ownerId = "s1",
            ownerName = "Aarav Sharma",
            ownerRole = "STUDENT",
            balance = 24500.0,
            status = AccountStatus.APPROVED,
            createdDate = "2026-01-15",
            cardNumber = "4532  8901  3324  5091",
            cardExpiry = "12/30",
            cardCvv = "341",
            kycAadhar = "5544 3322 1100",
            kycPan = "ABCPS1029K",
            kycVideoStatus = KycVideoStatus.COMPLETED,
            dynamicVideoQuestionsAnswered = 3
        )

        // Ishita Patel's Bank Account (Approved)
        mockBankAccounts["SCHB-ISHITA-102"] = BankAccount(
            accountNumber = "SCHB-ISHITA-102",
            ownerId = "s2",
            ownerName = "Ishita Patel",
            ownerRole = "STUDENT",
            balance = 8900.0,
            status = AccountStatus.APPROVED,
            createdDate = "2026-02-20",
            cardNumber = "4532  8901  4432  9912",
            cardExpiry = "05/31",
            cardCvv = "102",
            kycAadhar = "1122 3344 5566",
            kycPan = "XYZPD9987A",
            kycVideoStatus = KycVideoStatus.COMPLETED,
            dynamicVideoQuestionsAnswered = 3
        )

        // Ananya Iyer's Bank Account (Pending KYC verification)
        mockBankAccounts["SCHB-ANANYA-104"] = BankAccount(
            accountNumber = "SCHB-ANANYA-104",
            ownerId = "s4",
            ownerName = "Ananya Iyer",
            ownerRole = "STUDENT",
            balance = 0.0,
            status = AccountStatus.PENDING_KYC,
            createdDate = "2026-06-25",
            cardNumber = "4532  8901  7751  3321",
            cardExpiry = "10/31",
            cardCvv = "890",
            kycAadhar = "9988 7766 5544",
            kycPan = "POIUY7761L",
            kycVideoStatus = KycVideoStatus.NOT_STARTED,
            dynamicVideoQuestionsAnswered = 0
        )

        // Mrs. Shalini Roy's Bank Account (Approved)
        mockBankAccounts["SCHB-SHALINI-201"] = BankAccount(
            accountNumber = "SCHB-SHALINI-201",
            ownerId = "t1",
            ownerName = "Mrs. Shalini Roy",
            ownerRole = "TEACHER",
            balance = 62000.0,
            status = AccountStatus.APPROVED,
            createdDate = "2025-08-10",
            cardNumber = "4532  8901  1122  4433",
            cardExpiry = "09/29",
            cardCvv = "523",
            kycAadhar = "4433 2211 9988",
            kycPan = "MNBVC9012O",
            kycVideoStatus = KycVideoStatus.COMPLETED,
            dynamicVideoQuestionsAnswered = 3
        )

        // Dr. Rajesh Gupta's Bank Account (Approved)
        mockBankAccounts["SCHB-RAJESH-203"] = BankAccount(
            accountNumber = "SCHB-RAJESH-203",
            ownerId = "t3",
            ownerName = "Dr. Rajesh Gupta",
            ownerRole = "TEACHER",
            balance = 45000.0,
            status = AccountStatus.APPROVED,
            createdDate = "2025-11-12",
            cardNumber = "4532  8901  9900  8811",
            cardExpiry = "02/30",
            cardCvv = "114",
            kycAadhar = "8899 0011 2233",
            kycPan = "PLMKO0987Z",
            kycVideoStatus = KycVideoStatus.COMPLETED,
            dynamicVideoQuestionsAnswered = 3
        )

        // Siddharth Sen's Bank Account (Approved)
        mockBankAccounts["SCHB-SID-106"] = BankAccount(
            accountNumber = "SCHB-SID-106",
            ownerId = "s6",
            ownerName = "Siddharth Sen",
            ownerRole = "STUDENT",
            balance = 3000.0,
            status = AccountStatus.APPROVED,
            createdDate = "2026-06-10",
            cardNumber = "4532  8901  6672  4419",
            cardExpiry = "07/31",
            cardCvv = "911",
            kycAadhar = "6677 8899 0011",
            kycPan = "HGFDS7762Y",
            kycVideoStatus = KycVideoStatus.COMPLETED,
            dynamicVideoQuestionsAnswered = 3
        )

        _bankAccounts.value = mockBankAccounts

        // 6. Bank Transactions
        val mockTransactions = listOf(
            BankTransaction("tx1", "SCHB-SHALINI-201", TransactionType.DEPOSIT, 50000.0, "Monthly Salary Credit", "2026-06-01 09:00"),
            BankTransaction("tx2", "SCHB-AARAV-101", TransactionType.DEPOSIT, 30000.0, "Pocket money / Parent transfer", "2026-06-09 14:22"),
            BankTransaction("tx3", "SCHB-AARAV-101", TransactionType.PAYMENT, 15000.0, "Payment for Invoice #i1: Senior Secondary Tuition Fee", "2026-06-10 11:30"),
            BankTransaction("tx4", "SCHB-RAJESH-203", TransactionType.DEPOSIT, 45000.0, "Monthly Salary Credit", "2026-06-01 09:00"),
            BankTransaction("tx5", "SCHB-ISHITA-102", TransactionType.DEPOSIT, 8900.0, "Initial Deposit", "2026-02-20 10:15")
        )
        _transactions.value = mockTransactions

        // 7. Initial Loans
        val mockLoans = listOf(
            LoanApplication(applicantId = "s1", applicantName = "Aarav Sharma", loanType = "Education", amount = 150000.0, termMonths = 36, status = "APPROVED", appliedDate = "2026-04-12"),
            LoanApplication(applicantId = "s4", applicantName = "Ananya Iyer", loanType = "Education", amount = 200000.0, termMonths = 48, status = "PENDING", appliedDate = "2026-06-20")
        )
        _loans.value = mockLoans

        // 8. Initial Fixed Deposits
        val mockFDs = listOf(
            FixedDeposit(accountNumber = "SCHB-AARAV-101", ownerName = "Aarav Sharma", amount = 50000.0, rate = 7.25, tenureMonths = 12, createdDate = "2026-03-01")
        )
        _fixedDeposits.value = mockFDs

        // 9. Initial Homework Items
        val mockHomework = listOf(
            HomeworkItem(className = "Grade 10-A", subject = "Mathematics", task = "Complete exercises on Quadratic Equations (Ex 4.1 - 4.4)", dueDate = "2026-07-02"),
            HomeworkItem(className = "Grade 10-A", subject = "Physics", task = "Draw light refraction diagrams and write short notes on Fermat's Principle", dueDate = "2026-07-03"),
            HomeworkItem(className = "Grade 11-Science", subject = "Chemistry", task = "Balance organic synthesis reaction worksheets", dueDate = "2026-07-01")
        )
        _homework.value = mockHomework

        // 10. Initial Attendance Records
        val mockAttendance = mutableListOf<AttendanceRecord>()
        _students.value.forEach { s ->
            mockAttendance.add(AttendanceRecord(studentId = s.id, date = "2026-06-26", isPresent = s.id != "s3"))
            mockAttendance.add(AttendanceRecord(studentId = s.id, date = "2026-06-29", isPresent = true))
        }
        _attendance.value = mockAttendance

        // 11. Initial Exam Question Papers
        _examPapers.value = listOf(
            ExamQuestionPaper(
                id = "p1",
                title = "Grade 10 Mid-Term Mathematics Exam",
                subject = "Mathematics",
                className = "Grade 10-A",
                totalMarks = 80,
                durationMinutes = 120,
                instructions = "Attempt all questions. Calculators are strictly prohibited. Show all working steps.",
                createdByTeacherName = "Mrs. Shalini Roy",
                createdDate = "2026-06-28",
                questions = listOf(
                    ExamQuestion(
                        id = "q1_1",
                        questionText = "Solve the quadratic equation: x^2 - 5x + 6 = 0.",
                        marks = 5,
                        type = QuestionType.SHORT,
                        correctAnswer = "x = 2 or x = 3"
                    ),
                    ExamQuestion(
                        id = "q1_2",
                        questionText = "Which of the following is a prime number?",
                        marks = 2,
                        type = QuestionType.MCQ,
                        mcqOptions = listOf("4", "9", "15", "17"),
                        correctAnswer = "17"
                    ),
                    ExamQuestion(
                        id = "q1_3",
                        questionText = "State and prove Pythagoras' Theorem.",
                        marks = 10,
                        type = QuestionType.LONG,
                        correctAnswer = "Detailed step-by-step geometric proof."
                    )
                )
            ),
            ExamQuestionPaper(
                id = "p2",
                title = "Grade 11 Physics Unit Test - Mechanics",
                subject = "Physics",
                className = "Grade 11-Science",
                totalMarks = 40,
                durationMinutes = 60,
                instructions = "Attempt all questions. Use g = 9.8 m/s^2 where necessary. Non-programmable calculators are allowed.",
                createdByTeacherName = "Mr. Amit Verma",
                createdDate = "2026-06-29",
                questions = listOf(
                    ExamQuestion(
                        id = "q2_1",
                        questionText = "A ball is thrown vertically upwards with a velocity of 20 m/s. Find the maximum height reached.",
                        marks = 6,
                        type = QuestionType.SHORT,
                        correctAnswer = "20.4 meters"
                    ),
                    ExamQuestion(
                        id = "q2_2",
                        questionText = "What is the SI unit of force?",
                        marks = 2,
                        type = QuestionType.MCQ,
                        mcqOptions = listOf("Joule", "Watt", "Newton", "Pascal"),
                        correctAnswer = "Newton"
                    )
                )
            )
        )
    }

    // --- Student & Teacher Operations ---
    fun addStudent(name: String, rollNo: String, className: String, contact: String, email: String) {
        val student = Student(name = name, rollNo = rollNo, className = className, parentContact = contact, email = email)
        _students.value = _students.value + student
    }

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
    ): Student {
        val studentId = "s" + (Random.nextInt(10, 999).toString())
        val student = Student(
            id = studentId,
            name = name,
            rollNo = rollNo,
            className = className,
            parentContact = contact,
            email = email
        )
        _students.value = _students.value + student

        if (kycAadhar.isNotEmpty() && kycPan.isNotEmpty()) {
            val account = requestBankAccount(studentId, "STUDENT", kycAadhar, kycPan)
            if (autoApproveKyc) {
                val updatedMap = _bankAccounts.value.toMutableMap()
                val existingAcc = updatedMap[account.accountNumber]
                if (existingAcc != null) {
                    val approvedAcc = existingAcc.copy(status = AccountStatus.APPROVED)
                    updatedMap[account.accountNumber] = approvedAcc
                    _bankAccounts.value = updatedMap
                }
                if (initialDeposit > 0.0) {
                    depositMoney(account.accountNumber, initialDeposit, "Initial Student Wallet Pocket Deposit")
                }
            }
        }

        selectedFeeStructureId?.let { feeId ->
            if (feeId.isNotEmpty()) {
                generateInvoiceForStudent(studentId, feeId)
            }
        }

        return student
    }

    fun addTeacher(name: String, employeeId: String, department: String, contact: String, email: String) {
        val teacher = Teacher(name = name, employeeId = employeeId, department = department, contact = contact, email = email)
        _teachers.value = _teachers.value + teacher
    }

    // --- Exam Question Paper Operations ---
    fun addExamQuestionPaper(paper: ExamQuestionPaper) {
        _examPapers.value = _examPapers.value + paper
    }

    fun deleteExamQuestionPaper(paperId: String) {
        _examPapers.value = _examPapers.value.filterNot { it.id == paperId }
    }

    // --- Fee Structures Operations ---
    fun createFeeStructure(title: String, amount: Double, category: FeeCategory, frequency: FeeFrequency, description: String) {
        val fee = FeeStructure(title = title, amount = amount, category = category, frequency = frequency, description = description)
        _feeStructures.value = _feeStructures.value + fee
    }

    fun deleteFeeStructure(id: String) {
        _feeStructures.value = _feeStructures.value.filterNot { it.id == id }
    }

    // --- Banking and Video KYC Operations ---
    fun requestBankAccount(
        ownerId: String,
        ownerRole: String,
        kycAadhar: String,
        kycPan: String
    ): BankAccount {
        val ownerName = if (ownerRole == "STUDENT") {
            _students.value.find { it.id == ownerId }?.name ?: "Unknown Student"
        } else {
            _teachers.value.find { it.id == ownerId }?.name ?: "Unknown Teacher"
        }

        // Generate dynamic account details
        val randomNum = Random.nextInt(100000, 999999).toString()
        val formattedName = ownerName.take(5).uppercase().replace(" ", "X")
        val accountNum = "SCHB-$formattedName-$randomNum"

        val cardNumber = "4532  8901  ${Random.nextInt(1000, 9999)}  ${Random.nextInt(1000, 9999)}"
        val cardExpiry = "12/31"
        val cardCvv = Random.nextInt(100, 999).toString()

        val newAccount = BankAccount(
            accountNumber = accountNum,
            ownerId = ownerId,
            ownerName = ownerName,
            ownerRole = ownerRole,
            balance = 0.0,
            status = AccountStatus.PENDING_KYC,
            createdDate = dateFormat.format(Date()),
            cardNumber = cardNumber,
            cardExpiry = cardExpiry,
            cardCvv = cardCvv,
            kycAadhar = kycAadhar,
            kycPan = kycPan,
            kycVideoStatus = KycVideoStatus.NOT_STARTED,
            dynamicVideoQuestionsAnswered = 0
        )

        val updatedMap = _bankAccounts.value.toMutableMap()
        updatedMap[accountNum] = newAccount
        _bankAccounts.value = updatedMap

        // Update corresponding Student/Teacher with their bank account number
        if (ownerRole == "STUDENT") {
            _students.value = _students.value.map {
                if (it.id == ownerId) it.copy(bankAccountNumber = accountNum) else it
            }
        } else {
            _teachers.value = _teachers.value.map {
                if (it.id == ownerId) it.copy(bankAccountNumber = accountNum) else it
            }
        }

        return newAccount
    }

    fun updateKycVideoStatus(accountNumber: String, status: KycVideoStatus, questionsAnswered: Int = 0) {
        val updatedMap = _bankAccounts.value.toMutableMap()
        val account = updatedMap[accountNumber]
        if (account != null) {
            val updatedAccount = account.copy(
                kycVideoStatus = status,
                dynamicVideoQuestionsAnswered = questionsAnswered,
                status = if (status == KycVideoStatus.COMPLETED) AccountStatus.APPROVED else account.status
            )
            updatedMap[accountNumber] = updatedAccount
            _bankAccounts.value = updatedMap

            // If approved, create initial mock deposit of $500 for demonstration!
            if (status == KycVideoStatus.COMPLETED) {
                depositMoney(accountNumber, 10000.0, "Welcome Bonus / Initial School Deposit")
            }
        }
    }

    fun depositMoney(accountNumber: String, amount: Double, description: String) {
        val updatedMap = _bankAccounts.value.toMutableMap()
        val account = updatedMap[accountNumber]
        if (account != null) {
            val updatedAccount = account.copy(balance = account.balance + amount)
            updatedMap[accountNumber] = updatedAccount
            _bankAccounts.value = updatedMap

            val txn = BankTransaction(
                accountNumber = accountNumber,
                type = TransactionType.DEPOSIT,
                amount = amount,
                description = description,
                date = timeFormat.format(Date())
            )
            _transactions.value = listOf(txn) + _transactions.value
        }
    }

    fun withdrawMoney(accountNumber: String, amount: Double, description: String): Boolean {
        val updatedMap = _bankAccounts.value.toMutableMap()
        val account = updatedMap[accountNumber]
        if (account != null && account.balance >= amount) {
            val updatedAccount = account.copy(balance = account.balance - amount)
            updatedMap[accountNumber] = updatedAccount
            _bankAccounts.value = updatedMap

            val txn = BankTransaction(
                accountNumber = accountNumber,
                type = TransactionType.WITHDRAWAL,
                amount = amount,
                description = description,
                date = timeFormat.format(Date())
            )
            _transactions.value = listOf(txn) + _transactions.value
            return true
        }
        return false
    }

    // --- Automated Billing Engine ---
    fun generateInvoiceForStudent(studentId: String, feeStructureId: String) {
        val student = _students.value.find { it.id == studentId } ?: return
        val fee = _feeStructures.value.find { it.id == feeStructureId } ?: return

        val today = Calendar.getInstance()
        val dateToday = dateFormat.format(today.time)

        val calendarDue = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 14) }
        val dateDue = dateFormat.format(calendarDue.time)

        val invoice = Invoice(
            studentId = studentId,
            studentName = student.name,
            className = student.className,
            feeStructureId = feeStructureId,
            feeTitle = fee.title,
            feeCategory = fee.category,
            amount = fee.amount,
            issueDate = dateToday,
            dueDate = dateDue,
            status = PaymentStatus.PENDING
        )

        _invoices.value = _invoices.value + invoice
    }

    fun runAutomatedInvoicingForClass(className: String, feeStructureId: String): Int {
        val targetStudents = _students.value.filter { it.className == className }
        var generatedCount = 0
        targetStudents.forEach { student ->
            // Check if student already has a pending invoice for this structure in the current calendar month
            val alreadyBilled = _invoices.value.any {
                it.studentId == student.id &&
                        it.feeStructureId == feeStructureId &&
                        it.issueDate.startsWith(dateFormat.format(Date()).take(7))
            }
            if (!alreadyBilled) {
                generateInvoiceForStudent(student.id, feeStructureId)
                generatedCount++
            }
        }
        return generatedCount
    }

    // --- Payment Processing ---
    fun payInvoice(invoiceId: String, method: PaymentMethod): Pair<Boolean, String> {
        val invoice = _invoices.value.find { it.id == invoiceId } ?: return Pair(false, "Invoice not found")
        if (invoice.status == PaymentStatus.PAID) return Pair(false, "Invoice is already paid")

        when (method) {
            PaymentMethod.ONLINE_BANK -> {
                val student = _students.value.find { it.id == invoice.studentId }
                    ?: return Pair(false, "Student record not found")
                val acctNum = student.bankAccountNumber
                    ?: return Pair(false, "Student does not have an EduTrust Bank Account. Please open one first.")

                val account = _bankAccounts.value[acctNum]
                    ?: return Pair(false, "Bank account $acctNum not found")

                if (account.status != AccountStatus.APPROVED) {
                    return Pair(false, "Bank account is not active. KYC is ${account.status.displayName}.")
                }

                if (account.balance < invoice.amount) {
                    return Pair(false, "Insufficient balance! Available: ₹${String.format("%,.2f", account.balance)}. Required: ₹${String.format("%,.2f", invoice.amount)}.")
                }

                // Deduct from bank account
                val success = withdrawMoney(
                    accountNumber = acctNum,
                    amount = invoice.amount,
                    description = "Payment for Invoice #${invoice.id}: ${invoice.feeTitle}"
                )

                if (success) {
                    // Update invoice to paid
                    _invoices.value = _invoices.value.map {
                        if (it.id == invoiceId) {
                            it.copy(
                                status = PaymentStatus.PAID,
                                paidDate = dateFormat.format(Date()),
                                paymentMethod = PaymentMethod.ONLINE_BANK,
                                paymentRef = "ONLINE-REF-${Random.nextInt(100000, 999999)}"
                            )
                        } else it
                    }
                    return Pair(true, "Successfully paid ₹${String.format("%,.2f", invoice.amount)} using EduTrust Bank Account!")
                } else {
                    return Pair(false, "Transaction failed unexpectedly")
                }
            }
            PaymentMethod.OFFLINE_CASH, PaymentMethod.OFFLINE_CHEQUE -> {
                val refNum = "OFFLINE-REF-${Random.nextInt(100000, 999999)}"
                _invoices.value = _invoices.value.map {
                    if (it.id == invoiceId) {
                        it.copy(
                            status = PaymentStatus.PAID,
                            paidDate = dateFormat.format(Date()),
                            paymentMethod = method,
                            paymentRef = refNum
                        )
                    } else it
                }
                return Pair(true, "Successfully recorded offline payment of ₹${String.format("%,.2f", invoice.amount)}.")
            }
        }
    }

    // --- Financial Reports Aggregation ---
    fun getFinancialStats(): FinancialStats {
        val invoicesList = _invoices.value
        val totalBilled = invoicesList.sumOf { it.amount }
        val totalCollected = invoicesList.filter { it.status == PaymentStatus.PAID }.sumOf { it.amount }
        val totalPending = invoicesList.filter { it.status == PaymentStatus.PENDING }.sumOf { it.amount }
        val totalOverdue = invoicesList.filter { it.status == PaymentStatus.OVERDUE }.sumOf { it.amount }

        val accountsList = _bankAccounts.value.values
        val totalDeposits = accountsList.sumOf { it.balance }
        val activeAccounts = accountsList.count { it.status == AccountStatus.APPROVED }
        val pendingKycAccounts = accountsList.count { it.status == AccountStatus.PENDING_KYC }

        // Group billing by category
        val categoryCollectionMap = invoicesList
            .filter { it.status == PaymentStatus.PAID }
            .groupBy { it.feeCategory }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        return FinancialStats(
            totalBilled = totalBilled,
            totalCollected = totalCollected,
            totalPending = totalPending,
            totalOverdue = totalOverdue,
            totalDeposits = totalDeposits,
            activeAccounts = activeAccounts,
            pendingKycAccounts = pendingKycAccounts,
            collectionByCategory = categoryCollectionMap
        )
    }

    // Dynamic checks for due dates vs today's date to set OVERDUE status automatically
    fun autoCheckOverdueInvoices() {
        val todayStr = dateFormat.format(Date())
        val updated = _invoices.value.map { invoice ->
            if (invoice.status == PaymentStatus.PENDING && invoice.dueDate < todayStr) {
                invoice.copy(status = PaymentStatus.OVERDUE)
            } else {
                invoice
            }
        }
        _invoices.value = updated
    }

    // --- Loan Operations ---
    fun applyForLoan(applicantId: String, applicantName: String, loanType: String, amount: Double, termMonths: Int) {
        val newLoan = LoanApplication(
            applicantId = applicantId,
            applicantName = applicantName,
            loanType = loanType,
            amount = amount,
            termMonths = termMonths,
            appliedDate = dateFormat.format(Date())
        )
        _loans.value = _loans.value + newLoan
    }

    fun setLoanStatus(loanId: String, status: String) {
        _loans.value = _loans.value.map {
            if (it.id == loanId) {
                val updatedLoan = it.copy(status = status)
                // If loan is approved, credit the amount directly to their bank account!
                if (status == "APPROVED") {
                    val bankAccount = _bankAccounts.value.values.find { acc -> acc.ownerId == it.applicantId }
                    if (bankAccount != null) {
                        depositMoney(bankAccount.accountNumber, it.amount, "${it.loanType} Loan Disbursal (#${it.id.take(8).uppercase()})")
                    }
                }
                updatedLoan
            } else {
                it
            }
        }
    }

    // --- Fixed Deposit Operations ---
    fun createFixedDeposit(accountNumber: String, ownerName: String, amount: Double, rate: Double, tenureMonths: Int): Boolean {
        val account = _bankAccounts.value[accountNumber]
        if (account != null && account.balance >= amount) {
            val success = withdrawMoney(accountNumber, amount, "Fixed Deposit Creation ($tenureMonths Months @ $rate%)")
            if (success) {
                val fd = FixedDeposit(
                    accountNumber = accountNumber,
                    ownerName = ownerName,
                    amount = amount,
                    rate = rate,
                    tenureMonths = tenureMonths,
                    createdDate = dateFormat.format(Date())
                )
                _fixedDeposits.value = _fixedDeposits.value + fd
                return true
            }
        }
        return false
    }

    // --- Homework Operations ---
    fun addHomework(className: String, subject: String, task: String, dueDate: String) {
        val item = HomeworkItem(
            className = className,
            subject = subject,
            task = task,
            dueDate = dueDate
        )
        _homework.value = _homework.value + item
    }

    // --- Attendance Operations ---
    fun setAttendance(studentId: String, date: String, isPresent: Boolean) {
        val existing = _attendance.value.filterNot { it.studentId == studentId && it.date == date }
        _attendance.value = existing + AttendanceRecord(studentId, date, isPresent)
    }
}

data class FinancialStats(
    val totalBilled: Double,
    val totalCollected: Double,
    val totalPending: Double,
    val totalOverdue: Double,
    val totalDeposits: Double,
    val activeAccounts: Int,
    val pendingKycAccounts: Int,
    val collectionByCategory: Map<FeeCategory, Double>
)
