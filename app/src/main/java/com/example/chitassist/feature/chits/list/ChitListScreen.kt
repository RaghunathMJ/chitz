package com.example.chitassist.feature.chits.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chitassist.feature.dashboard.ChitItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChitListScreen(
    onChitClick: (Long) -> Unit,
    onEditChit: (Long) -> Unit,
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Chits") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.chits.isEmpty()) {
                Text(
                    text = "No chits found.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.chits) { chit ->
                        ChitItem(
                            chit = chit,
                            onClick = { onChitClick(chit.id) },
                            onEdit = { viewModel.handleIntent(ChitListContract.Intent.EditChit(chit)) },
                            onDelete = { viewModel.handleIntent(ChitListContract.Intent.DeleteChit(chit)) }
                        )
                    }
                }
            }
        }
    }
}
