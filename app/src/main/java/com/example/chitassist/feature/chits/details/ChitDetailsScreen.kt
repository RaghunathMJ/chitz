package com.example.chitassist.feature.chits.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.chitassist.core.ui.theme.*
import com.example.chitassist.domain.model.Chit
import com.example.chitassist.domain.model.ChitStatus
import com.example.chitassist.domain.model.Payment
import com.example.chitassist.domain.model.PaymentStatus
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.NumberFormat
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

    ChitDetailsContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onPaymentStatusChanged = { payment, isPaid, bidAmount ->
            viewModel.onIntent(ChitDetailsContract.Intent.OnPaymentStatusChanged(payment, isPaid, bidAmount))
        },
        onWinnerStatusChanged = { payment, isWinner ->
            viewModel.onIntent(ChitDetailsContract.Intent.OnWinnerStatusChanged(payment, isWinner))
        },
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChitDetailsContent(
    state: ChitDetailsContract.State,
    onNavigateBack: () -> Unit,
    onPaymentStatusChanged: (Payment, Boolean, String) -> Unit,
    onWinnerStatusChanged: (Payment, Boolean) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "CHIT SCHEME",
                            style = MaterialTheme.typography.labelSmall,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                state.chit?.name ?: "Chit Details",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (state.chit?.status == ChitStatus.ACTIVE) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Success, CircleShape)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Background),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp)
            ) {
                state.chit?.let { chit ->
                    item {
                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(32.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                PrimaryContainer,
                                                Primary
                                            )
                                        )
                                    )
                                    .padding(24.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            text = "TOTAL SCHEME VALUE",
                                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Bold
                                        )
                                        val currencyFormat = remember {
                                            java.text.NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
                                                maximumFractionDigits = 0
                                            }
                                        }
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(SpanStyle(
                                                    fontSize = 22.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    baselineShift = BaselineShift(0.2f)
                                                )) {
                                                    append("₹")
                                                }
                                                append(currencyFormat.format(chit.totalValue).replace("₹", ""))
                                            },
                                            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
                                            color = Color.White,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    
                                    val statusColor = if (chit.status == ChitStatus.COMPLETED) Success else Secondary
                                    Surface(
                                        color = statusColor.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = if (chit.status == ChitStatus.COMPLETED) "COMPLETED" else "ACTIVE",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = statusColor,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(28.dp))
                                
                                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val timeline = remember(chit.startDate, chit.numberOfInstallments, chit.intervalMonths) {
                                        val sdf = SimpleDateFormat("MMM yy", Locale.getDefault())
                                        val calendar = Calendar.getInstance().apply {
                                            timeInMillis = chit.startDate
                                        }
                                        val start = sdf.format(calendar.time)
                                        calendar.add(Calendar.MONTH, (chit.numberOfInstallments - 1) * chit.intervalMonths)
                                        val end = sdf.format(calendar.time)
                                        "$start - $end"
                                    }
                                    DetailItem("TIMELINE", timeline, Color.White, Modifier.weight(1.4f))
                                    DetailItem("TOTAL", "${chit.numberOfInstallments}", Color.White, Modifier.weight(0.7f), alignment = Alignment.CenterHorizontally)
                                    val currencyFormat = remember {
                                        java.text.NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
                                            maximumFractionDigits = 0
                                        }
                                    }
                                    DetailItem("MONTHLY DUE", currencyFormat.format(chit.monthlyContribution), Color.White, Modifier.weight(1.2f), alignment = Alignment.End)
                                }
                            }
                        }
                        
                        if (chit.organizer != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.AccountBalanceWallet,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Outline
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = buildAnnotatedString {
                                        append("Organizer: ")
                                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextSecondary)) {
                                            append(chit.organizer)
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Outline
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Installment Schedule",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                                Text(
                                    text = "Track bids and payable dues per cycle",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Outline
                                )
                            }
                            Surface(
                                color = TrackColor,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "${chit.numberOfInstallments}",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Secondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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
                            onPaymentStatusChanged(payment, isPaid, bidAmount)
                        },
                        onWinnerChange = { isWinner ->
                            onWinnerStatusChanged(payment, isWinner)
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
    val decimalFormat = remember { DecimalFormat("#,##,###") }
    var bidAmountText by remember(payment.id) { 
        val initialText = if (payment.bidAmount > 0) decimalFormat.format(payment.bidAmount) else ""
        mutableStateOf(initialText) 
    }
    val currencyFormat = remember { 
        java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, com.example.chitassist.core.ui.theme.BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(com.example.chitassist.core.ui.theme.Secondary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INSTALLMENT #${payment.installmentNumber}",
                        style = MaterialTheme.typography.titleSmall,
                        color = com.example.chitassist.core.ui.theme.Secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (payment.status == PaymentStatus.PAID) 
                            Color(0xFFE8F5E9)
                        else 
                            Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (payment.status == PaymentStatus.PAID) "PAID" else "PENDING",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (payment.status == PaymentStatus.PAID) 
                                Color(0xFF2E7D32)
                            else 
                                Color(0xFFEF6C00),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Checkbox(
                        checked = payment.status == PaymentStatus.PAID,
                        onCheckedChange = { onStatusChange(it, bidAmountText) },
                        enabled = canChangeStatus,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Due Date: ${dateFormat.format(Date(payment.dueDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = isWinnerEnabled) { onWinnerChange(!payment.isWinner) }
                ) {
                    RadioButton(
                        selected = payment.isWinner,
                        onClick = null,
                        enabled = isWinnerEnabled,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Won this auction",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isWinnerEnabled) Color.Black else Color.Gray,
                        fontWeight = if (payment.isWinner) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1.3f)) {
                    Text(
                        text = "BID AMT (₹)",
                        style = MaterialTheme.typography.labelSmall,
                        color = com.example.chitassist.core.ui.theme.Secondary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bidAmountText,
                        onValueChange = { newValue ->
                            val cleanString = newValue.replace("[^\\d]".toRegex(), "")
                            bidAmountText = if (cleanString.isNotEmpty()) {
                                try {
                                    decimalFormat.format(cleanString.toLong())
                                } catch (e: Exception) {
                                    cleanString
                                }
                            } else {
                                ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        enabled = payment.status != PaymentStatus.PAID && canChangeStatus,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = com.example.chitassist.core.ui.theme.Primary
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = com.example.chitassist.core.ui.theme.Background,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = com.example.chitassist.core.ui.theme.OutlineVariant,
                            focusedBorderColor = com.example.chitassist.core.ui.theme.Secondary,
                            disabledTextColor = Color.Gray,
                            disabledContainerColor = com.example.chitassist.core.ui.theme.SurfaceContainer
                        )
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "NET PAYABLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp
                    )
                    Text(
                        text = currencyFormat.format(payment.netAmount),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = com.example.chitassist.core.ui.theme.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start
) {
    Column(modifier = modifier, horizontalAlignment = alignment) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color.copy(alpha = 0.7f),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = color,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChitDetailsScreenPreview() {
    val sampleChit = Chit(
        id = 1L,
        userId = 1L,
        name = "Kalyan Jewelers Chit",
        totalValue = 100000.0,
        monthlyContribution = 5000.0,
        numberOfInstallments = 20,
        startDate = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 60, // 60 days ago
        paymentDueDay = 10,
        completedInstallments = 2,
        status = ChitStatus.ACTIVE,
        organizer = "Kalyan Jewelers"
    )

    val samplePayments = listOf(
        Payment(
            id = 1L,
            chitId = 1L,
            chitName = "Kalyan Jewelers Chit",
            installmentNumber = 1,
            dueDate = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30,
            amount = 5000.0,
            bidAmount = 2000.0,
            dividend = 500.0,
            status = PaymentStatus.PAID,
            isWinner = false
        ),
        Payment(
            id = 2L,
            chitId = 1L,
            chitName = "Kalyan Jewelers Chit",
            installmentNumber = 2,
            dueDate = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 0,
            amount = 5000.0,
            bidAmount = 3000.0,
            dividend = 800.0,
            status = PaymentStatus.PENDING,
            isWinner = true
        ),
        Payment(
            id = 3L,
            chitId = 1L,
            chitName = "Kalyan Jewelers Chit",
            installmentNumber = 3,
            dueDate = System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30,
            amount = 5000.0,
            bidAmount = 0.0,
            dividend = 0.0,
            status = PaymentStatus.PENDING,
            isWinner = false
        )
    )

    ChitAssistTheme {
        ChitDetailsContent(
            state = ChitDetailsContract.State(
                chit = sampleChit,
                payments = samplePayments,
                isLoading = false
            ),
            onNavigateBack = {},
            onPaymentStatusChanged = { _, _, _ -> },
            onWinnerStatusChanged = { _, _ -> }
        )
    }
}
