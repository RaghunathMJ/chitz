package com.example.chitassist.feature.profile.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chitassist.core.ui.theme.ChitAssistTheme

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is EditProfileContract.Effect.NavigateBack -> onNavigateBack()
                is EditProfileContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    EditProfileContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = onNavigateBack,
        onNameChanged = { viewModel.handleIntent(EditProfileContract.Intent.OnNameChanged(it)) },
        onEmailChanged = { viewModel.handleIntent(EditProfileContract.Intent.OnEmailChanged(it)) },
        onCityChanged = { viewModel.handleIntent(EditProfileContract.Intent.OnCityChanged(it)) },
        onSaveClick = { viewModel.handleIntent(EditProfileContract.Intent.SaveProfile) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileContent(
    state: EditProfileContract.State,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onCityChanged: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Edit Profile", 
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChanged,
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChanged,
                    label = { Text("Email (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = state.city,
                    onValueChange = onCityChanged,
                    label = { Text("City (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Changes", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    ChitAssistTheme {
        EditProfileContent(
            state = EditProfileContract.State(
                name = "John Doe",
                email = "john.doe@example.com",
                city = "Chennai",
                isLoading = false
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBackClick = {},
            onNameChanged = {},
            onEmailChanged = {},
            onCityChanged = {},
            onSaveClick = {}
        )
    }
}
