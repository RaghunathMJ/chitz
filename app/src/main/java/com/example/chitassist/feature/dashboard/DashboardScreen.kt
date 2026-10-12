package com.example.chitassist.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chitassist.core.ui.theme.*
import com.example.chitassist.domain.model.Chit
import com.example.chitassist.domain.model.ChitStatus
import com.example.chitassist.domain.model.Payment
import com.example.chitassist.domain.model.User
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddChit: () -> Unit,
    onNavigateToEditChit: (Long) -> Unit,
    onNavigateToChitDetails: (Long) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToChitList: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                DashboardContract.Effect.NavigationToLogin -> onLogout()
                is DashboardContract.Effect.NavigationToEditChit -> onNavigateToEditChit(effect.chitId)
                is DashboardContract.Effect.ShowError -> {
                }
            }
        }
    }

    DashboardContent(
        state = state,
        onNavigateToAddChit = onNavigateToAddChit,
        onNavigateToChitDetails = onNavigateToChitDetails,
        onNavigateToProfile = onNavigateToProfile,
        onViewAllChits = onNavigateToChitList,
        onEditChit = { viewModel.onIntent(DashboardContract.Intent.OnEditChitClicked(it)) },
        onDeleteChit = { viewModel.onIntent(DashboardContract.Intent.OnDeleteChitClicked(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    state: DashboardContract.State,
    onNavigateToAddChit: () -> Unit,
    onNavigateToChitDetails: (Long) -> Unit,
    onNavigateToProfile: () -> Unit,
    onViewAllChits: () -> Unit,
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
                            "Home",
                            style = MaterialTheme.typography.titleLarge,
                            color = Primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Primary)
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Surface(
                            modifier = Modifier.size(36.dp),
                            shape = CircleShape,
                            color = Primary
                        ) {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = "Profile",
                                tint = OnPrimary,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddChit,
                containerColor = TrackColor,
                contentColor = Primary,
                shape = RoundedCornerShape(20.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Join New Chit", fontWeight = FontWeight.Bold) }
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
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    GreetingHeader(userName = state.user?.name ?: "User")
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    PortfolioHeroCard(
                        totalValue = state.chits.sumOf { it.totalValue },
                        chitCount = state.chits.size
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }

                if (state.upcomingPayments.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Installment Schedule",
                            badgeCount = state.upcomingPayments.size,
                            subtitle = "Scheduled cycles"
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    itemsIndexed(state.upcomingPayments) { index, payment ->
                        UpcomingPaymentItem(payment = payment, isFirst = index == 0)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                item {
                    SectionHeader(
                        title = "Your Chits",
                        badgeCount = state.chits.size,
                        onViewAllClick = onViewAllChits
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (state.chits.isEmpty()) {
                    item {
                        EmptyChitsPlaceholder()
                    }
                } else {
                    items(state.chits) { chit ->
                        ChitItem(
                            chit = chit,
                            onClick = { onNavigateToChitDetails(chit.id) },
                            onEdit = { onEditChit(chit) },
                            onDelete = { onDeleteChit(chit) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingHeader(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    append("Welcome, $userName ")
                    append("👋")
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Text(
                text = "Your chit portfolio is active & up to date",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
        }
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = TrackColor,
        ) {
            IconButton(onClick = { /* History */ }) {
                Icon(
                    Icons.Outlined.Description,
                    contentDescription = "History",
                    tint = Primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun PortfolioHeroCard(
    totalValue: Double,
    chitCount: Int
) {
    val currencyFormat = remember { 
        java.text.NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }
    }
    val formattedValue = currencyFormat.format(totalValue)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Primary,
                            Color(0xFF001A5E)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    "TOTAL PORTFOLIO VALUE",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = OnPrimary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        formattedValue,
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
                        fontWeight = FontWeight.Black,
                        color = OnPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = CircleShape,
                        color = Success
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Across $chitCount active chit funds",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnPrimary.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String = "",
    badgeCount: Int? = null,
    onViewAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            if (badgeCount != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = TrackColor,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            badgeCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Secondary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        
        if (onViewAllClick != null) {
            TextButton(onClick = onViewAllClick) {
                Text(
                    "View All",
                    style = MaterialTheme.typography.titleSmall,
                    color = Secondary
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Secondary
                )
            }
        } else {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Outline
            )
        }
    }
}

@Composable
fun EmptyChitsPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Savings,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Primary.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Chits Found",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start by adding your first chit fund to track installments and dividends effortlessly.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChitItem(
    chit: Chit,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val currencyFormat = remember { 
        java.text.NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }
    }
    val statusColor = when (chit.status.name) {
        "ACTIVE" -> Secondary
        "COMPLETED" -> Success
        else -> Error
    }

    val progress = if (chit.numberOfInstallments > 0) {
        chit.completedInstallments.toFloat() / chit.numberOfInstallments
    } else 0f

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F3F4))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top row with Name and Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chit.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            color = Color(0xFF00236F)
                        ),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Installment progress: ${chit.completedInstallments}/${chit.numberOfInstallments}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currencyFormat.format(chit.totalValue),
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                            color = Color(0xFF1D2129),
                            fontWeight = FontWeight.Black
                        )
                        Box(modifier = Modifier.padding(start = 2.dp)) {
                            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color(0xFF74777F))
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    onClick = {
                                        showMenu = false
                                        onEdit()
                                    },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        showMenu = false
                                        onDelete()
                                    },
                                    leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                                )
                            }
                        }
                    }
                    Text(
                        text = "Total Value",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF44474E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Indicator
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = statusColor,
                trackColor = Color(0xFFE8EAF6)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom row with Timeline and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = "Timeline",
                        tint = Color(0xFF74777F),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
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
                    Text(
                        text = buildAnnotatedString {
                            pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1D2129)))
                            append(timeline)
                            pop()
                            pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold, color = Secondary))
                            append(" • ${chit.dividendMode.name.replace("_", " ").uppercase()}")
                            pop()
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF44474E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (chit.status.name == "ACTIVE") "IN PROGRESS" else "COMPLETED",
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun UpcomingPaymentItem(payment: Payment, isFirst: Boolean = false) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val currencyFormat = remember { 
        java.text.NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }
    }
    
    val daysLeft = remember(payment.dueDate) {
        val diff = payment.dueDate - System.currentTimeMillis()
        (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    }

    val statusColor = if (daysLeft <= 3) Error else Primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = payment.chitName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Installment #${payment.installmentNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = currencyFormat.format(payment.amount),
                        style = MaterialTheme.typography.titleLarge,
                        color = OnSurface,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Installment amount",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp, color = BorderColor)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isFirst) Icons.Default.Verified else Icons.Default.CalendarMonth,
                        contentDescription = "Due Date",
                        tint = if (isFirst) Primary else Outline,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Date: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = dateFormat.format(Date(payment.dueDate)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }
                
                Surface(
                    color = if (daysLeft <= 3) WarningContainer else statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (daysLeft == 0) "DUE TODAY" else "$daysLeft DAYS LEFT",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (daysLeft <= 3) Warning else statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    val sampleUser = User(
        id = 1L,
        name = "John Doe",
        mobileNumber = "9876543210",
        email = "john.doe@example.com",
        city = "Chennai",
        dateOfBirth = "01-01-1990"
    )

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

    val samplePayments = listOf(
        Payment(
            id = 1L,
            chitId = 1L,
            chitName = "Kalyan Jewelers Chit",
            installmentNumber = 6,
            dueDate = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 2,
            amount = 5000.0
        )
    )

    ChitAssistTheme {
        DashboardContent(
            state = DashboardContract.State(
                user = sampleUser,
                chits = sampleChits,
                upcomingPayments = samplePayments,
                isLoading = false
            ),
            onNavigateToAddChit = {},
            onNavigateToChitDetails = {},
            onNavigateToProfile = {},
            onViewAllChits = {},
            onEditChit = {},
            onDeleteChit = {}
        )
    }
}

