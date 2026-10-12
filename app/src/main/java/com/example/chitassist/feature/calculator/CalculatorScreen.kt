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
import com.example.chitassist.core.ui.theme.ChitAssistTheme
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
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Chit Calculator",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Help */ }) {
                        Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = "Help")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            label = { Text("Chit Value (₹)", fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                        OutlinedTextField(
                            value = state.months,
                            onValueChange = { 
                                val digits = it.filter { char -> char.isDigit() }
                                onIntent(CalculatorContract.Intent.MonthsChanged(digits)) 
                            },
                            label = { Text("Installments", fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge
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
                            label = { Text("Current Inst.", fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                        OutlinedTextField(
                            value = state.commissionAmount,
                            onValueChange = { 
                                val digits = it.filter { char -> char.isDigit() }
                                onIntent(CalculatorContract.Intent.CommissionAmountChanged(digits)) 
                            },
                            label = { Text("Comm. (₹)", fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    }

                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        SegmentedButton(
                            selected = state.bidType == CalculatorContract.BidType.TOTAL_BID,
                            onClick = { onIntent(CalculatorContract.Intent.BidTypeChanged(CalculatorContract.BidType.TOTAL_BID)) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            label = { Text("Bid Amount", fontSize = 13.sp, fontWeight = FontWeight.Medium) }
                        )
                        SegmentedButton(
                            selected = state.bidType == CalculatorContract.BidType.BID_DISCOUNT,
                            onClick = { onIntent(CalculatorContract.Intent.BidTypeChanged(CalculatorContract.BidType.BID_DISCOUNT)) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            label = { Text("Discount", fontSize = 13.sp, fontWeight = FontWeight.Medium) }
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
                                    "Enter Discount Amount (₹)",
                                fontSize = 13.sp
                            ) 
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        "Dividend Distribution",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        SegmentedButton(
                            selected = state.dividendMode == DividendMode.EXCLUDING_WINNER,
                            onClick = { onIntent(CalculatorContract.Intent.DividendModeChanged(DividendMode.EXCLUDING_WINNER)) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            label = { Text("Excl. Winner", fontSize = 13.sp, fontWeight = FontWeight.Medium) }
                        )
                        SegmentedButton(
                            selected = state.dividendMode == DividendMode.INCLUDING_WINNER,
                            onClick = { onIntent(CalculatorContract.Intent.DividendModeChanged(DividendMode.INCLUDING_WINNER)) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            label = { Text("Incl. Winner", fontSize = 13.sp, fontWeight = FontWeight.Medium) }
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Breakdown",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            BreakdownRow("Chit Value", currencyFormat.format(state.chitAmount.replace(",", "").toDoubleOrNull() ?: 0.0))
            BreakdownRow("Base Installment", currencyFormat.format(state.monthlyInstallment))
            BreakdownRow("Commission", "- " + currencyFormat.format(state.totalCommission / (state.months.replace(",", "").toIntOrNull() ?: 1)))
            BreakdownRow("Auction Dividend", "+ " + currencyFormat.format(state.dividend), isPositive = true)
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surfaceContainer)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Installment",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    currencyFormat.format(state.actualPayment),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun BreakdownRow(label: String, value: String, isPositive: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = if (isPositive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
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
