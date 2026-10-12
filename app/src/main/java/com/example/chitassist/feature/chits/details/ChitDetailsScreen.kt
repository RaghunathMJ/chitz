package com.example.chitassist.feature.chits.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                title = {
                    Column {
                        Text(
                            "CHIT FUND",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            state.chit?.name ?: "Chit Details",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.chit?.let { chit ->
                    item {
                        Spacer(modifier = Modifier.height(4.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "TOTAL VALUE",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "₹${chit.totalValue}",
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Surface(
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = chit.status.name,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    DetailItem("Installments", chit.numberOfInstallments.toString(), MaterialTheme.colorScheme.onPrimary)
                                    DetailItem("Monthly Amt", "₹${chit.monthlyContribution}", MaterialTheme.colorScheme.onPrimary)
                                    DetailItem("Dividend", chit.dividendMode.name.take(4), MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        }
                        
                        if (chit.organizer != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Organizer: ${chit.organizer}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Payment Schedule",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (payment.status == PaymentStatus.PAID) 
                MaterialTheme.colorScheme.surfaceContainerHigh
            else 
                MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "INSTALLMENT #${payment.installmentNumber}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Due: ${dateFormat.format(Date(payment.dueDate))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (payment.status == PaymentStatus.PAID) "PAID" else "PENDING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (payment.status == PaymentStatus.PAID) 
                            androidx.compose.ui.graphics.Color(0xFF4CAF50)
                        else 
                            MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Checkbox(
                        checked = payment.status == PaymentStatus.PAID,
                        onCheckedChange = { onStatusChange(it, bidAmountText) },
                        enabled = canChangeStatus,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (payment.isWinner) MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f) else androidx.compose.ui.graphics.Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    onClick = { if (isWinnerEnabled) onWinnerChange(!payment.isWinner) },
                    enabled = isWinnerEnabled
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = payment.isWinner,
                            onClick = null,
                            enabled = isWinnerEnabled,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Won this auction",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (payment.isWinner) FontWeight.Bold else FontWeight.Normal,
                            color = if (isWinnerEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = bidAmountText,
                    onValueChange = { bidAmountText = it },
                    label = { Text("Bid Amt (₹)", fontSize = 10.sp) },
                    modifier = Modifier.weight(1.2f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = payment.status != PaymentStatus.PAID && canChangeStatus,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodySmall
                )
                
                Column(
                    modifier = Modifier.weight(1.5f),
                    horizontalAlignment = Alignment.End
                ) {
                    if (payment.dividend > 0) {
                        Text(
                            text = "Div: + ₹${payment.dividend.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = currencyFormat.format(payment.netAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "NET PAYABLE",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 7.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}
