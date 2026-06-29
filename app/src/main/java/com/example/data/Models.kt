package com.example.data

import java.util.UUID

enum class FeeCategory(val displayName: String) {
    TUITION("Tuition Fee"),
    TRANSPORT("Transportation"),
    ACTIVITY("Activity & Labs"),
    SPORTS("Sports & Gym"),
    EXAM("Examination"),
    LIBRARY("Library Services"),
    OTHER("Other Miscellaneous")
}

enum class FeeFrequency(val displayName: String) {
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    TERM("Per Term"),
    ANNUALLY("Annually"),
    ONE_TIME("One-Time")
}

enum class PaymentStatus(val displayName: String) {
    PAID("Paid"),
    PENDING("Pending"),
    OVERDUE("Overdue")
}

enum class PaymentMethod(val displayName: String) {
    ONLINE_BANK("EduTrust Bank Account"),
    OFFLINE_CASH("Cash / Direct Pay"),
    OFFLINE_CHEQUE("Bank Cheque / DD")
}

enum class AccountStatus(val displayName: String) {
    PENDING_KYC("KYC In Progress"),
    APPROVED("Approved & Active"),
    REJECTED("KYC Rejected")
}

enum class KycVideoStatus(val displayName: String) {
    NOT_STARTED("Not Started"),
    CALL_IN_PROGRESS("In Call"),
    COMPLETED("Completed"),
    FAILED("Verification Failed")
}

data class Student(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val rollNo: String,
    val className: String, // e.g. "Grade 10-A"
    val parentContact: String,
    val email: String,
    val bankAccountNumber: String? = null
)

data class Teacher(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val employeeId: String,
    val department: String, // e.g. "Mathematics"
    val contact: String,
    val email: String,
    val bankAccountNumber: String? = null
)

data class FeeStructure(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: FeeCategory,
    val frequency: FeeFrequency,
    val description: String
)

data class Invoice(
    val id: String = UUID.randomUUID().toString(),
    val studentId: String,
    val studentName: String,
    val className: String,
    val feeStructureId: String,
    val feeTitle: String,
    val feeCategory: FeeCategory,
    val amount: Double,
    val issueDate: String, // YYYY-MM-DD
    val dueDate: String,   // YYYY-MM-DD
    val status: PaymentStatus = PaymentStatus.PENDING,
    val paidDate: String? = null,
    val paymentMethod: PaymentMethod? = null,
    val paymentRef: String? = null
)

data class BankAccount(
    val accountNumber: String, // e.g. "SCHB-100293"
    val ownerId: String,       // StudentId or TeacherId
    val ownerName: String,
    val ownerRole: String,     // "STUDENT" or "TEACHER"
    val balance: Double = 0.0,
    val status: AccountStatus = AccountStatus.PENDING_KYC,
    val createdDate: String,
    val cardNumber: String,    // Masked or virtual debit card number
    val cardExpiry: String,
    val cardCvv: String,
    val kycAadhar: String,
    val kycPan: String,
    val kycVideoStatus: KycVideoStatus = KycVideoStatus.NOT_STARTED,
    val dynamicVideoQuestionsAnswered: Int = 0
)

data class BankTransaction(
    val id: String = UUID.randomUUID().toString(),
    val accountNumber: String,
    val type: TransactionType,
    val amount: Double,
    val description: String,
    val date: String // YYYY-MM-DD HH:MM
)

enum class TransactionType(val displayName: String) {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    PAYMENT("Fee Payment"),
    TRANSFER("Fund Transfer")
}

enum class UserRole(val displayName: String, val category: String) {
    SUPER_ADMIN("Super Admin", "Administration"),
    SCHOOL_ADMIN("School Admin", "Administration"),
    PRINCIPAL("Principal", "Administration"),
    TEACHER("Teacher", "School Staff"),
    STUDENT("Student", "Academic"),
    PARENT("Parent", "Family"),
    BANK_ADMIN("Bank Admin", "Finance Admin"),
    BANK_EMPLOYEE("Bank Employee", "Finance Staff"),
    CUSTOMER("Customer / Parent", "Finance Client")
}

data class LoanApplication(
    val id: String = java.util.UUID.randomUUID().toString(),
    val applicantId: String,
    val applicantName: String,
    val loanType: String, // e.g. "Personal", "Education", "Home", "Vehicle"
    val amount: Double,
    val termMonths: Int,
    val status: String = "PENDING", // "PENDING", "APPROVED", "REJECTED"
    val appliedDate: String
)

data class FixedDeposit(
    val id: String = java.util.UUID.randomUUID().toString(),
    val accountNumber: String,
    val ownerName: String,
    val amount: Double,
    val rate: Double,
    val tenureMonths: Int,
    val createdDate: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: String
)

data class HomeworkItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val className: String,
    val subject: String,
    val task: String,
    val dueDate: String
)

data class AttendanceRecord(
    val studentId: String,
    val date: String,
    val isPresent: Boolean
)

enum class QuestionType {
    MCQ,
    SHORT,
    LONG
}

data class ExamQuestion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val questionText: String,
    val marks: Int,
    val type: QuestionType,
    val mcqOptions: List<String> = emptyList(),
    val correctAnswer: String = ""
)

data class ExamQuestionPaper(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val subject: String,
    val className: String,
    val totalMarks: Int,
    val durationMinutes: Int,
    val instructions: String,
    val questions: List<ExamQuestion> = emptyList(),
    val createdByTeacherName: String,
    val createdDate: String
)


