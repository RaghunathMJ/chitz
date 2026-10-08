package com.example.chitassist.feature.chits.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chitassist.domain.model.DividendMode
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChitScreen(
    chitId: Long? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddChitViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(chitId) {
        if (chitId != null) {
            viewModel.onIntent(AddChitContract.Intent.LoadChit(chitId))
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.startDate
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                AddChitContract.Effect.NavigationBack -> onNavigateBack()
                is AddChitContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onIntent(AddChitContract.Intent.OnStartDateChanged(it))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (chitId != null) "Edit Chit" else "Add Chit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onIntent(AddChitContract.Intent.OnNameChanged(it)) },
                label = { Text("Chit Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.organizer,
                onValueChange = { viewModel.onIntent(AddChitContract.Intent.OnOrganizerChanged(it)) },
                label = { Text("Organizer (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.totalValue,
                onValueChange = { viewModel.onIntent(AddChitContract.Intent.OnTotalValueChanged(it)) },
                label = { Text("Total Value") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.monthlyContribution,
                onValueChange = { viewModel.onIntent(AddChitContract.Intent.OnMonthlyContributionChanged(it)) },
                label = { Text("Contribution Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.numberOfInstallments,
                onValueChange = { viewModel.onIntent(AddChitContract.Intent.OnInstallmentsChanged(it)) },
                label = { Text("Number of Installments") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.commissionAmount,
                onValueChange = { viewModel.onIntent(AddChitContract.Intent.OnCommissionAmountChanged(it)) },
                label = { Text("Organizer Commission (Amount)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(state.startDate)),
                onValueChange = {},
                label = { Text("Start Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Frequency (Interval in months)", style = MaterialTheme.typography.labelLarge)
            
            var expanded by remember { mutableStateOf(false) }
            val intervals = listOf(
                1 to "Monthly",
                2 to "Every 2 Months",
                3 to "Quarterly (3 Months)",
                4 to "Every 4 Months",
                6 to "Half Yearly (6 Months)"
            )
            val selectedLabel = intervals.find { it.first == state.intervalMonths }?.second ?: "Custom (${state.intervalMonths} months)"

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedLabel,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    intervals.forEach { (interval, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.onIntent(AddChitContract.Intent.OnIntervalChanged(interval))
                                expanded = false
                            }
                        )
                    }
                }
            }

            Text("Dividend Distribution Mode", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.dividendMode == DividendMode.EXCLUDING_WINNER,
                    onClick = { viewModel.onIntent(AddChitContract.Intent.OnDividendModeChanged(DividendMode.EXCLUDING_WINNER)) },
                    label = { Text("Excluding Winner") }
                )
                FilterChip(
                    selected = state.dividendMode == DividendMode.INCLUDING_WINNER,
                    onClick = { viewModel.onIntent(AddChitContract.Intent.OnDividendModeChanged(DividendMode.INCLUDING_WINNER)) },
                    label = { Text("Including Winner") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onIntent(AddChitContract.Intent.OnSaveClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text("Save Chit")
            }
        }
    }
}
