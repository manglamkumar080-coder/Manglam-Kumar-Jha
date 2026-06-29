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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.SchoolViewModel
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    viewModel: SchoolViewModel,
    modifier: Modifier = Modifier
) {
    val invoices by viewModel.filteredInvoices.collectAsState()
    val searchQuery by viewModel.invoiceSearchQuery.collectAsState()
    val activeFilter by viewModel.invoiceStatusFilter.collectAsState()

    val feeStructures by viewModel.feeStructures.collectAsState()
    val selectedClass by viewModel.selectedClassForAutoBilling.collectAsState()
    val selectedFeeId by viewModel.selectedFeeForAutoBilling.collectAsState()

    var showBillingRunPanel by remember { mutableStateOf(false) }
    var selectedInvoiceForPayment by remember { mutableStateOf<Invoice?>(null) }
    var selectedInvoiceForReceipt by remember { mutableStateOf<Invoice?>(null) }

    var classDropdownExpanded by remember { mutableStateOf(false) }
    var feeDropdownExpanded by remember { mutableStateOf(false) }

    val mockClasses = listOf("Grade 9-A", "Grade 10-A", "Grade 10-B", "Grade 11-Science", "Grade 11-Commerce")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = "Billing & Receipts",
            subtitle = "Generate class-wide fees, track pending student balances, and issue payment receipts.",
            actions = {
                CustomButton(
                    text = if (showBillingRunPanel) "Hide Auto-Billing" else "Automated Billing",
                    icon = if (showBillingRunPanel) Icons.Default.KeyboardArrowUp else Icons.Default.Autorenew,
                    onClick = { showBillingRunPanel = !showBillingRunPanel },
                    isSecondary = true,
                    testTag = "toggle_autobilling_btn"
                )
            }
        )

        // --- Automated Invoicing Panel ---
        AnimatedVisibility(
            visible = showBillingRunPanel,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(16.dp))
                    .padding(bottom = 20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Automated Monthly Billing Cycle",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.SoftWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Instantly audit a whole classroom roster and generate due invoices based on defined fee scheme rules.",
                        style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.SoftGray)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Class Select Dropdown
                        ExposedDropdownMenuBox(
                            expanded = classDropdownExpanded,
                            onExpandedChange = { classDropdownExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedClass,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Target Class") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = BrandColors.SoftWhite,
                                    unfocusedTextColor = BrandColors.SoftWhite,
                                    focusedBorderColor = BrandColors.RoyalIndigo,
                                    unfocusedBorderColor = BrandColors.BorderSlate
                                ),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = classDropdownExpanded,
                                onDismissRequest = { classDropdownExpanded = false }
                            ) {
                                mockClasses.forEach { cls ->
                                    DropdownMenuItem(
                                        text = { Text(cls) },
                                        onClick = {
                                            viewModel.setSelectedClass(cls)
                                            classDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Fee Select Dropdown
                        ExposedDropdownMenuBox(
                            expanded = feeDropdownExpanded,
                            onExpandedChange = { feeDropdownExpanded = it },
                            modifier = Modifier.weight(1.2f)
                        ) {
                            val activeFeeName = feeStructures.find { it.id == selectedFeeId }?.title ?: "Select Plan"
                            OutlinedTextField(
                                value = activeFeeName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Fee Scheme") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = feeDropdownExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = BrandColors.SoftWhite,
                                    unfocusedTextColor = BrandColors.SoftWhite,
                                    focusedBorderColor = BrandColors.RoyalIndigo,
                                    unfocusedBorderColor = BrandColors.BorderSlate
                                ),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = feeDropdownExpanded,
                                onDismissRequest = { feeDropdownExpanded = false }
                            ) {
                                feeStructures.forEach { fee ->
                                    DropdownMenuItem(
                                        text = { Text("${fee.title} (₹${fee.amount})") },
                                        onClick = {
                                            viewModel.setSelectedFee(fee.id)
                                            feeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    CustomButton(
                        text = "Trigger Class Bill Generation Run",
                        icon = Icons.Default.PlayArrow,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.triggerAutomatedBilling()
                        },
                        testTag = "trigger_auto_billing_btn"
                    )
                }
            }
        }

        // --- Filter Tabs and Search Row ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setInvoiceSearch(it) },
                placeholder = { Text("Search by student, class, invoice ID...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BrandColors.SoftGray) },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("invoice_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = BrandColors.SoftWhite,
                    unfocusedTextColor = BrandColors.SoftWhite,
                    focusedBorderColor = BrandColors.RoyalIndigo,
                    unfocusedBorderColor = BrandColors.BorderSlate,
                    unfocusedPlaceholderColor = BrandColors.SoftGray
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // --- Filter chips ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChipItem(
                text = "All Bills",
                isSelected = activeFilter == null,
                onClick = { viewModel.setInvoiceStatusFilter(null) }
            )
            FilterChipItem(
                text = "Paid",
                isSelected = activeFilter == PaymentStatus.PAID,
                onClick = { viewModel.setInvoiceStatusFilter(PaymentStatus.PAID) }
            )
            FilterChipItem(
                text = "Pending",
                isSelected = activeFilter == PaymentStatus.PENDING,
                onClick = { viewModel.setInvoiceStatusFilter(PaymentStatus.PENDING) }
            )
            FilterChipItem(
                text = "Overdue",
                isSelected = activeFilter == PaymentStatus.OVERDUE,
                onClick = { viewModel.setInvoiceStatusFilter(PaymentStatus.OVERDUE) }
            )
        }

        // --- Invoices List ---
        if (invoices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No invoices found matching current search/filter.",
                    color = BrandColors.SoftGray,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(invoices) { invoice ->
                    InvoiceItemCard(
                        invoice = invoice,
                        onCollect = { selectedInvoiceForPayment = invoice },
                        onViewReceipt = { selectedInvoiceForReceipt = invoice }
                    )
                }
            }
        }
    }

    // --- Collect payment Modal ---
    if (selectedInvoiceForPayment != null) {
        PaymentModal(
            invoice = selectedInvoiceForPayment!!,
            viewModel = viewModel,
            onDismiss = { selectedInvoiceForPayment = null },
            onConfirmPayment = { invoiceId, method ->
                viewModel.payInvoiceDirect(invoiceId, method)
                selectedInvoiceForPayment = null
            }
        )
    }

    // --- Digital Receipt Viewer Modal ---
    if (selectedInvoiceForReceipt != null) {
        ReceiptModal(
            invoice = selectedInvoiceForReceipt!!,
            onDismiss = { selectedInvoiceForReceipt = null }
        )
    }
}

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) BrandColors.RoyalIndigo else BrandColors.CardSlate)
            .border(
                1.dp,
                if (isSelected) BrandColors.RoyalIndigo else BrandColors.BorderSlate,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else BrandColors.SoftGray
            )
        )
    }
}

@Composable
fun InvoiceItemCard(
    invoice: Invoice,
    onCollect: () -> Unit,
    onViewReceipt: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandColors.BorderSlate, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = BrandColors.CardSlate),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INV-#${invoice.id.take(8).uppercase()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.SoftGray
                        )
                    )
                    Text(
                        text = invoice.studentName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.SoftWhite
                        )
                    )
                    Text(
                        text = invoice.className,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BrandColors.SoftGray
                        )
                    )
                }
                StatusBadge(status = invoice.status)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = BrandColors.BorderSlate, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = invoice.feeTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BrandColors.SoftWhite.copy(alpha = 0.9f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Due by: ${invoice.dueDate}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (invoice.status == PaymentStatus.OVERDUE) BrandColors.CrimsonRed else BrandColors.SoftGray,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                Text(
                    text = "₹${String.format("%,.2f", invoice.amount)}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = BrandColors.SoftWhite
                    )
                )
            }

            if (invoice.status != PaymentStatus.PAID) {
                Spacer(modifier = Modifier.height(14.dp))
                CustomButton(
                    text = "Collect Bill Payment",
                    icon = Icons.Default.AccountBalanceWallet,
                    onClick = onCollect,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "collect_btn_${invoice.id}"
                )
            } else {
                Spacer(modifier = Modifier.height(14.dp))
                CustomButton(
                    text = "View Digital Receipt",
                    icon = Icons.Default.Receipt,
                    onClick = onViewReceipt,
                    isSecondary = true,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "receipt_btn_${invoice.id}"
                )
            }
        }
    }
}

@Composable
fun PaymentModal(
    invoice: Invoice,
    viewModel: SchoolViewModel,
    onDismiss: () -> Unit,
    onConfirmPayment: (String, PaymentMethod) -> Unit
) {
    val students by viewModel.students.collectAsState()
    val bankAccounts by viewModel.bankAccounts.collectAsState()

    val student = students.find { it.id == invoice.studentId }
    val bankAcctNo = student?.bankAccountNumber
    val bankAccount = bankAcctNo?.let { bankAccounts[it] }

    var selectedMethod by remember { mutableStateOf(PaymentMethod.OFFLINE_CASH) }
    var offlineNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Collect Student Payment",
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
                // Summary of bill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandColors.SpaceDark)
                        .padding(12.dp)
                ) {
                    Column {
                        Text("INV-#${invoice.id.uppercase().take(8)}", style = MaterialTheme.typography.labelSmall.copy(color = BrandColors.SoftGray))
                        Text(invoice.studentName, style = MaterialTheme.typography.titleMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                        Text("${invoice.feeTitle} • ${invoice.className}", style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.SoftGray))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Total Due: ₹${String.format("%,.2f", invoice.amount)}",
                            style = MaterialTheme.typography.titleLarge.copy(color = BrandColors.SoftWhite, fontWeight = FontWeight.Black)
                        )
                    }
                }

                Text(
                    text = "CHOOSE PAYMENT METHOD",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = BrandColors.SoftGray)
                )

                // Option 1: School Bank Account (Online Instant Balance deduction)
                val accountAvailable = bankAccount != null && bankAccount.status == AccountStatus.APPROVED
                val canPayWithBank = accountAvailable && bankAccount!!.balance >= invoice.amount

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (selectedMethod == PaymentMethod.ONLINE_BANK) BrandColors.RoyalIndigo else BrandColors.BorderSlate,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = accountAvailable) {
                            selectedMethod = PaymentMethod.ONLINE_BANK
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMethod == PaymentMethod.ONLINE_BANK) BrandColors.RoyalIndigo.copy(alpha = 0.08f) else Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMethod == PaymentMethod.ONLINE_BANK,
                            onClick = { if (accountAvailable) selectedMethod = PaymentMethod.ONLINE_BANK },
                            enabled = accountAvailable,
                            colors = RadioButtonDefaults.colors(selectedColor = BrandColors.RoyalIndigo)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "EduTrust Bank Debit",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (accountAvailable) BrandColors.SoftWhite else BrandColors.SoftGray
                                )
                            )
                            if (accountAvailable) {
                                Text(
                                    "A/C Balance: ₹${String.format("%,.2f", bankAccount!!.balance)}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (canPayWithBank) BrandColors.EmeraldGreen else BrandColors.CrimsonRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                if (!canPayWithBank) {
                                    Text("Insufficient account funds", style = MaterialTheme.typography.labelSmall.copy(color = BrandColors.CrimsonRed))
                                }
                            } else {
                                Text(
                                    "No approved school bank account registered for student.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.CrimsonRed, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }

                // Option 2: Cash Payment Offline
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (selectedMethod == PaymentMethod.OFFLINE_CASH) BrandColors.RoyalIndigo else BrandColors.BorderSlate,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedMethod = PaymentMethod.OFFLINE_CASH },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMethod == PaymentMethod.OFFLINE_CASH) BrandColors.RoyalIndigo.copy(alpha = 0.08f) else Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMethod == PaymentMethod.OFFLINE_CASH,
                            onClick = { selectedMethod = PaymentMethod.OFFLINE_CASH },
                            colors = RadioButtonDefaults.colors(selectedColor = BrandColors.RoyalIndigo)
                        )
                        Column {
                            Text("Cash Receipt (Offline)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite))
                            Text("Collect and log physical cash manually.", style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.SoftGray))
                        }
                    }
                }

                // Option 3: Cheque / Demand Draft Offline
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (selectedMethod == PaymentMethod.OFFLINE_CHEQUE) BrandColors.RoyalIndigo else BrandColors.BorderSlate,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedMethod = PaymentMethod.OFFLINE_CHEQUE },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMethod == PaymentMethod.OFFLINE_CHEQUE) BrandColors.RoyalIndigo.copy(alpha = 0.08f) else Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMethod == PaymentMethod.OFFLINE_CHEQUE,
                            onClick = { selectedMethod = PaymentMethod.OFFLINE_CHEQUE },
                            colors = RadioButtonDefaults.colors(selectedColor = BrandColors.RoyalIndigo)
                        )
                        Column {
                            Text("Cheque / Demand Draft", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite))
                            Text("Record check number & clear manually.", style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.SoftGray))
                        }
                    }
                }

                if (selectedMethod != PaymentMethod.ONLINE_BANK) {
                    OutlinedTextField(
                        value = offlineNotes,
                        onValueChange = { offlineNotes = it },
                        label = { Text("Receipt Notes / Cheque ID") },
                        placeholder = { Text("Enter payment receipt logs...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("payment_ref_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BrandColors.SoftWhite,
                            unfocusedTextColor = BrandColors.SoftWhite,
                            focusedBorderColor = BrandColors.RoyalIndigo,
                            unfocusedBorderColor = BrandColors.BorderSlate
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmPayment(invoice.id, selectedMethod) },
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo),
                modifier = Modifier.testTag("confirm_payment_btn")
            ) {
                Text("Process Payment", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = BrandColors.SoftGray)) {
                Text("Dismiss")
            }
        },
        containerColor = BrandColors.CardSlate,
        titleContentColor = BrandColors.SoftWhite
    )
}

@Composable
fun ReceiptModal(
    invoice: Invoice,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Payment Receipt", fontWeight = FontWeight.Bold, color = BrandColors.SoftWhite)
                Icon(Icons.Default.CheckCircle, contentDescription = "Paid", tint = BrandColors.EmeraldGreen, modifier = Modifier.size(24.dp))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Divider(color = BrandColors.BorderSlate)

                ReceiptField(label = "Student Name", value = invoice.studentName)
                ReceiptField(label = "Classroom", value = invoice.className)
                ReceiptField(label = "Bill Details", value = invoice.feeTitle)
                ReceiptField(label = "Paid Date", value = invoice.paidDate ?: "N/A")
                ReceiptField(label = "Payment Mode", value = invoice.paymentMethod?.displayName ?: "N/A")
                ReceiptField(label = "Receipt Audit Ref", value = invoice.paymentRef ?: "N/A")

                Divider(color = BrandColors.BorderSlate)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Cleared", style = MaterialTheme.typography.titleMedium.copy(color = BrandColors.SoftGray, fontWeight = FontWeight.Bold))
                    Text(
                        "₹${String.format("%,.2f", invoice.amount)}",
                        style = MaterialTheme.typography.titleLarge.copy(color = BrandColors.EmeraldGreen, fontWeight = FontWeight.Black)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandColors.SpaceDark)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "EduTrust Financial Core Audit Secured",
                        style = MaterialTheme.typography.labelSmall.copy(color = BrandColors.SoftGray, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.RoyalIndigo)
            ) {
                Text("Close Receipt", color = Color.White)
            }
        },
        containerColor = BrandColors.CardSlate,
        titleContentColor = BrandColors.SoftWhite
    )
}

@Composable
fun ReceiptField(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.SoftGray))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(color = BrandColors.SoftWhite, fontWeight = FontWeight.Bold))
    }
}
