package com.example.chitassist.feature.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chitassist.domain.model.DividendMode
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.viewState.collectAsState()
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "IN")) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chit Calculator") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.chitAmount,
                onValueChange = { viewModel.handleIntent(CalculatorContract.Intent.ChitAmountChanged(it)) },
                label = { Text("Total Chit Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.installmentNumber,
                    onValueChange = { viewModel.handleIntent(CalculatorContract.Intent.InstallmentNumberChanged(it)) },
                    label = { Text("Installment") },
                    placeholder = { Text("11") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "/",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                OutlinedTextField(
                    value = state.months,
                    onValueChange = { viewModel.handleIntent(CalculatorContract.Intent.MonthsChanged(it)) },
                    label = { Text("Total Installments") },
                    placeholder = { Text("40") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = state.commissionAmount,
                onValueChange = { viewModel.handleIntent(CalculatorContract.Intent.CommissionAmountChanged(it)) },
                label = { Text("Organizer Commission (Amount)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text("Bid Type:", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.bidType == CalculatorContract.BidType.TOTAL_BID,
                    onClick = { viewModel.handleIntent(CalculatorContract.Intent.BidTypeChanged(CalculatorContract.BidType.TOTAL_BID)) },
                    label = { Text("Total Bid Amount") }
                )
                FilterChip(
                    selected = state.bidType == CalculatorContract.BidType.BID_DISCOUNT,
                    onClick = { viewModel.handleIntent(CalculatorContract.Intent.BidTypeChanged(CalculatorContract.BidType.BID_DISCOUNT)) },
                    label = { Text("Bid Discount") }
                )
            }

            OutlinedTextField(
                value = state.bidAmount,
                onValueChange = { viewModel.handleIntent(CalculatorContract.Intent.BidAmountChanged(it)) },
                label = { 
                    Text(
                        if (state.bidType == CalculatorContract.BidType.TOTAL_BID) 
                            "Winning Bid (Amount Winner Receives)" 
                        else 
                            "Bid Discount (Amount Reduced from Total)"
                    ) 
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { 
                    Text(
                        if (state.bidType == CalculatorContract.BidType.TOTAL_BID) 
                            "e.g. 80,000" 
                        else 
                            "e.g. 20,000"
                    ) 
                }
            )

            Text("Dividend Distribution Mode:", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.dividendMode == DividendMode.EXCLUDING_WINNER,
                    onClick = { viewModel.handleIntent(CalculatorContract.Intent.DividendModeChanged(DividendMode.EXCLUDING_WINNER)) },
                    label = { Text("Excluding Winner") }
                )
                FilterChip(
                    selected = state.dividendMode == DividendMode.INCLUDING_WINNER,
                    onClick = { viewModel.handleIntent(CalculatorContract.Intent.DividendModeChanged(DividendMode.INCLUDING_WINNER)) },
                    label = { Text("Including Winner") }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResultRow("Monthly Installment (Base):", currencyFormat.format(state.monthlyInstallment))
                    ResultRow("Total Commission:", currencyFormat.format(state.totalCommission))
                    
                    if (state.dividend > 0) {
                        ResultRow("Dividend (Per Member):", currencyFormat.format(state.dividend), color = MaterialTheme.colorScheme.primary)
                        ResultRow("Actual Payment:", currencyFormat.format(state.actualPayment), style = MaterialTheme.typography.titleMedium)
                    }
                    
                    HorizontalDivider()
                    ResultRow(
                        "Expected Total Monthly:", 
                        currencyFormat.format(state.effectiveMonthlyInstallment),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            
            Text(
                text = "* Note: This is a simplified calculation. Actual payments may vary based on dividends and auction details.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ResultRow(
    label: String,
    value: String,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = style, color = color)
        Text(text = value, style = style, color = color)
    }
}
