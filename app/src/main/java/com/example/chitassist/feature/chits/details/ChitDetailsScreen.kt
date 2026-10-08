package com.example.chitassist.feature.chits.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chitassist.domain.model.Payment
import com.example.chitassist.domain.model.PaymentStatus
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChitDetailsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChitDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ChitDetailsContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(state.chit?.name ?: "Chit Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.chit?.let { chit ->
                    item {
                        val statusColor = when (chit.status.name) {
                            "ACTIVE" -> MaterialTheme.colorScheme.primary
                            "COMPLETED" -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                            else -> MaterialTheme.colorScheme.error
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Organizer: ${chit.organizer ?: "N/A"}",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = "Dividend: ${chit.dividendMode.name.replace("_", " ")}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    val statusSuffix = if (chit.isWon) " (Won in #${chit.winningInstallmentNumber})" else " (Not Won)"
                                    val statusText = if (chit.status.name == "ACTIVE") {
                                        "ACTIVE$statusSuffix"
                                    } else {
                                        chit.status.name
                                    }

                                    Surface(
                                        color = statusColor.copy(alpha = 0.1f),
                                        shape = MaterialTheme.shapes.extraSmall
                                    ) {
                                        Text(
                                            text = statusText,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = statusColor,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Total Value: ₹${chit.totalValue}")
                                Text(text = "Monthly Contribution: ₹${chit.monthlyContribution}")
                                Text(text = "Installments: ${chit.numberOfInstallments}")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Payments", style = MaterialTheme.typography.titleMedium)
                    }
                }

                val sortedPayments = state.payments.sortedBy { it.installmentNumber }
                val hasAnyWinner = sortedPayments.any { it.isWinner }
                
                itemsIndexed(
                    items = sortedPayments,
                    key = { _, payment -> payment.id }
                ) { index, payment ->
                    val isPreviousPaid = if (index == 0) true else sortedPayments[index - 1].status == PaymentStatus.PAID
                    val isNextPending = if (index == sortedPayments.lastIndex) true else sortedPayments[index + 1].status == PaymentStatus.PENDING
                    
                    val canChangeStatus = if (payment.status == PaymentStatus.PENDING) isPreviousPaid else isNextPending

                    PaymentItem(
                        payment = payment,
                        onStatusChange = { isPaid, bidAmount ->
                            viewModel.onIntent(ChitDetailsContract.Intent.OnPaymentStatusChanged(payment, isPaid, bidAmount))
                        },
                        onWinnerChange = { isWinner ->
                            viewModel.onIntent(ChitDetailsContract.Intent.OnWinnerStatusChanged(payment, isWinner))
                        },
                        isWinnerEnabled = payment.isWinner || !hasAnyWinner,
                        canChangeStatus = canChangeStatus
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentItem(
    payment: Payment,
    onStatusChange: (Boolean, String) -> Unit,
    onWinnerChange: (Boolean) -> Unit,
    isWinnerEnabled: Boolean,
    canChangeStatus: Boolean
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    var bidAmountText by remember(payment.id) { mutableStateOf(if (payment.bidAmount > 0) payment.bidAmount.toString() else "") }
    val currencyFormat = remember { java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "IN")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (payment.status == PaymentStatus.PAID) 
                MaterialTheme.colorScheme.surfaceVariant 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (payment.chitName.isNotBlank()) {
                        Text(
                            text = payment.chitName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Installment #${payment.installmentNumber}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Due: ${dateFormat.format(Date(payment.dueDate))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (payment.status == PaymentStatus.PAID) "Paid" else "Pending",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (payment.status == PaymentStatus.PAID) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.outline
                    )
                    Checkbox(
                        checked = payment.status == PaymentStatus.PAID,
                        onCheckedChange = { onStatusChange(it, bidAmountText) },
                        enabled = canChangeStatus
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = payment.isWinner,
                    onCheckedChange = onWinnerChange,
                    enabled = isWinnerEnabled
                )
                Text(
                    text = "I won this bid",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isWinnerEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = bidAmountText,
                    onValueChange = {
                        bidAmountText = it
                    },
                    label = { Text("Winning Bid Amount") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = payment.status != PaymentStatus.PAID && canChangeStatus
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    if (payment.dividend > 0) {
                        Text(
                            text = "Dividend: ${currencyFormat.format(payment.dividend)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Net: ${currencyFormat.format(payment.netAmount)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
