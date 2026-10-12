package com.example.chitassist.feature.chits.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chitassist.core.ui.theme.*
import com.example.chitassist.domain.model.Chit
import com.example.chitassist.domain.model.ChitStatus
import com.example.chitassist.feature.dashboard.ChitItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChitListScreen(
    onChitClick: (Long) -> Unit,
    onEditChit: (Long) -> Unit,
    onAddChit: () -> Unit,
    viewModel: ChitListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ChitListContract.Effect.NavigationToEditChit -> onEditChit(effect.chitId)
                is ChitListContract.Effect.ShowError -> { }
            }
        }
    }

    ChitListContent(
        state = state,
        onChitClick = onChitClick,
        onAddChit = onAddChit,
        onEditChit = { viewModel.handleIntent(ChitListContract.Intent.EditChit(it)) },
        onDeleteChit = { viewModel.handleIntent(ChitListContract.Intent.DeleteChit(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChitListContent(
    state: ChitListContract.State,
    onChitClick: (Long) -> Unit,
    onAddChit: () -> Unit,
    onEditChit: (Chit) -> Unit,
    onDeleteChit: (Chit) -> Unit
) {
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
                            "My Chits",
                            style = MaterialTheme.typography.titleLarge,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceContainerLowest
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddChit,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Chit")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Total Active",
                    value = state.chits.count { it.status.name == "ACTIVE" }.toString(),
                    modifier = Modifier.weight(1f),
                    color = Secondary
                )
                StatCard(
                    label = "Completed",
                    value = state.chits.count { it.status.name == "COMPLETED" }.toString(),
                    modifier = Modifier.weight(1f),
                    color = Primary
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.chits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No chits joined yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = onAddChit) {
                            Text("Join your first chit")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.chits) { chit ->
                        ChitItem(
                            chit = chit,
                            onClick = { onChitClick(chit.id) },
                            onEdit = { onEditChit(chit) },
                            onDelete = { onDeleteChit(chit) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChitListScreenPreview() {
    val sampleChits = listOf(
        Chit(
            id = 1L,
            userId = 1L,
            name = "Kalyan Jewelers Chit",
            totalValue = 100000.0,
            monthlyContribution = 5000.0,
            numberOfInstallments = 20,
            startDate = System.currentTimeMillis(),
            paymentDueDay = 10,
            completedInstallments = 5,
            status = ChitStatus.ACTIVE
        ),
        Chit(
            id = 2L,
            userId = 1L,
            name = "Home Savings",
            totalValue = 500000.0,
            monthlyContribution = 10000.0,
            numberOfInstallments = 50,
            startDate = System.currentTimeMillis(),
            paymentDueDay = 5,
            completedInstallments = 12,
            status = ChitStatus.ACTIVE
        )
    )

    ChitAssistTheme {
        ChitListContent(
            state = ChitListContract.State(
                chits = sampleChits,
                isLoading = false
            ),
            onChitClick = {},
            onAddChit = {},
            onEditChit = {},
            onDeleteChit = {}
        )
    }
}

@Preview(showBackground = true, name = "Chit List - Empty")
@Composable
fun ChitListScreenEmptyPreview() {
    ChitAssistTheme {
        ChitListContent(
            state = ChitListContract.State(
                chits = emptyList(),
                isLoading = false
            ),
            onChitClick = {},
            onAddChit = {},
            onEditChit = {},
            onDeleteChit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StatCardPreview() {
    ChitAssistTheme {
        StatCard(
            label = "Total Active",
            value = "2",
            color = Secondary,
            modifier = Modifier.padding(16.dp)
        )
    }
}
