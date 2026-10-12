package com.example.chitassist.feature.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.chitassist.core.ui.theme.*
import com.example.chitassist.domain.model.DividendMode
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsState()
    CalculatorContent(
        state = state,
        onIntent = viewModel::handleIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorContent(
    state: CalculatorContract.State,
    onIntent: (CalculatorContract.Intent) -> Unit = {}
) {
    val currencyFormat = remember { 
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "CHIT FUND",
                            style = MaterialTheme.typography.labelSmall,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Chit Calculator",
                            style = MaterialTheme.typography.titleLarge,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Help */ }) {
                        Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = "Help", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceContainerLowest
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = state.chitAmount,
                            onValueChange = { 
                                val digits = it.filter { char -> char.isDigit() }
                                onIntent(CalculatorContract.Intent.ChitAmountChanged(digits)) 
                            },
                            label = { Text("Chit Value (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        OutlinedTextField(
                            value = state.months,
                            onValueChange = { 
                                val digits = it.filter { char -> char.isDigit() }
                                onIntent(CalculatorContract.Intent.MonthsChanged(digits)) 
                            },
                            label = { Text("Installments") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = state.installmentNumber,
                            onValueChange = { 
                                val digits = it.filter { char -> char.isDigit() }
                                onIntent(CalculatorContract.Intent.InstallmentNumberChanged(digits)) 
                            },
                            label = { Text("Current Inst.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        OutlinedTextField(
                            value = state.commissionAmount,
                            onValueChange = { 
                                val digits = it.filter { char -> char.isDigit() }
                                onIntent(CalculatorContract.Intent.CommissionAmountChanged(digits)) 
                            },
                            label = { Text("Comm. (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        SegmentedButton(
                            selected = state.bidType == CalculatorContract.BidType.TOTAL_BID,
                            onClick = { onIntent(CalculatorContract.Intent.BidTypeChanged(CalculatorContract.BidType.TOTAL_BID)) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            label = { Text("Bid Amount", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold) }
                        )
                        SegmentedButton(
                            selected = state.bidType == CalculatorContract.BidType.BID_DISCOUNT,
                            onClick = { onIntent(CalculatorContract.Intent.BidTypeChanged(CalculatorContract.BidType.BID_DISCOUNT)) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            label = { Text("Discount", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold) }
                        )
                    }

                    OutlinedTextField(
                        value = state.bidAmount,
                        onValueChange = { 
                            val digits = it.filter { char -> char.isDigit() }
                            onIntent(CalculatorContract.Intent.BidAmountChanged(digits)) 
                        },
                        label = { 
                            Text(
                                if (state.bidType == CalculatorContract.BidType.TOTAL_BID) 
                                    "Enter Bid Amount (₹)" 
                                else 
                                    "Enter Discount Amount (₹)"
                            ) 
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Text(
                        "DIVIDEND DISTRIBUTION",
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.ExtraBold
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        SegmentedButton(
                            selected = state.dividendMode == DividendMode.EXCLUDING_WINNER,
                            onClick = { onIntent(CalculatorContract.Intent.DividendModeChanged(DividendMode.EXCLUDING_WINNER)) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            label = { Text("Excl. Winner", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold) }
                        )
                        SegmentedButton(
                            selected = state.dividendMode == DividendMode.INCLUDING_WINNER,
                            onClick = { onIntent(CalculatorContract.Intent.DividendModeChanged(DividendMode.INCLUDING_WINNER)) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            label = { Text("Incl. Winner", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            // Breakdown Card
            CalculationBreakdownCard(state, currencyFormat)
        }
    }
}


@Composable
fun CalculationBreakdownCard(state: CalculatorContract.State, currencyFormat: NumberFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                "CALCULATION BREAKDOWN",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
                fontWeight = FontWeight.ExtraBold,
                color = Primary.copy(alpha = 0.7f)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BreakdownRow("Monthly Installment", currencyFormat.format(state.monthlyInstallment))
                val monthlyCommission = state.totalCommission / (state.months.replace(",", "").toIntOrNull() ?: 1)
                BreakdownRow("Foreman Commission", "- " + currencyFormat.format(monthlyCommission), isNegative = true)
                BreakdownRow("Auction Dividend", "+ " + currencyFormat.format(state.dividend), isPositive = true)
            }
            
            HorizontalDivider(thickness = 1.dp, color = BorderColor)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Monthly Payable",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Primary
                    )
                    Text(
                        "Final installment amount",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    currencyFormat.format(state.actualPayment),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = Secondary
                )
            }
        }
    }
}

@Composable
fun BreakdownRow(label: String, value: String, isPositive: Boolean = false, isNegative: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = when {
                isPositive -> Success
                isNegative -> Error
                else -> TextPrimary
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    ChitAssistTheme {
        CalculatorContent(
            state = CalculatorContract.State(
                chitAmount = "100000",
                months = "20",
                installmentNumber = "5",
                commissionAmount = "5000",
                bidAmount = "20000",
                bidType = CalculatorContract.BidType.BID_DISCOUNT,
                dividendMode = DividendMode.EXCLUDING_WINNER,
                monthlyInstallment = 5000.0,
                totalCommission = 5000.0,
                dividend = 750.0,
                actualPayment = 4250.0
            )
        )
    }
}
