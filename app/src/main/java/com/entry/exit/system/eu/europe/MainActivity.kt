package com.entry.exit.system.eu.europe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.entry.exit.system.eu.europe.ui.theme.EntryExitSystemEUTravelToEuropeTheme
import com.entry.exit.system.eu.europe.ui.theme.LightBlueBackground

data class Airport(
    val iataCode: String,
    val city: String,
    val country: String
)

data class TripAdvisory(
    val from: String,
    val to: String,
    val dateRange: String,
    val status: String,
    val statusColor: Color,
    val riskIndex: Int,
    val advisory: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EntryExitSystemEUTravelToEuropeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = LightBlueBackground
                ) { innerPadding ->
                    TravelCompanionFlow(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TravelCompanionFlow(modifier: Modifier = Modifier) {
    val currentScreen = remember { mutableStateOf(0) }
    val vacationPlanned = remember { mutableStateOf<Boolean?>(null) }
    val vacationDate = remember { mutableStateOf<String?>(null) }
    val selectedCountries = remember { mutableStateListOf<String>() }
    val flightsBooked = remember { mutableStateOf<Boolean?>(null) }
    val originAirport = remember { mutableStateOf<Airport?>(null) }
    val destinationAirport = remember { mutableStateOf<Airport?>(null) }

    when (currentScreen.value) {
        0 -> IntroScreen(
            modifier = modifier,
            onNext = { currentScreen.value = 1 }
        )
        1 -> OriginAirportScreen(
            modifier = modifier,
            onAirportSelected = { airport ->
                originAirport.value = airport
                currentScreen.value = 2
            }
        )
        2 -> DestinationAirportScreen(
            modifier = modifier,
            onAirportSelected = { airport ->
                destinationAirport.value = airport
                currentScreen.value = 3
            }
        )
        3 -> VacationPlanningScreen(
            modifier = modifier,
            onYes = {
                vacationPlanned.value = true
                currentScreen.value = 4
            },
            onNo = {
                vacationPlanned.value = false
                currentScreen.value = 4
            }
        )
        4 -> DateSelectionScreen(
            modifier = modifier,
            onDateSelected = { date ->
                vacationDate.value = date
                currentScreen.value = 5
            }
        )
        5 -> CountriesSelectionScreen(
            modifier = modifier,
            onCountriesSelected = { countries ->
                selectedCountries.clear()
                selectedCountries.addAll(countries)
                currentScreen.value = 6
            }
        )
        6 -> FlightsBookingScreen(
            modifier = modifier,
            onYes = {
                flightsBooked.value = true
                currentScreen.value = 7
            },
            onNo = {
                flightsBooked.value = false
                currentScreen.value = 7
            }
        )
        7 -> SummaryScreen(
            modifier = modifier,
            onHome = { currentScreen.value = 0 }
        )
    }
}

@Composable
fun IntroScreen(modifier: Modifier = Modifier, onNext: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Image(
                painter = painterResource(id = R.drawable.select_continent),
                contentDescription = "Cassy Mascot",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 32.dp)
            )
        }

        AnimatedVisibility(
            visible = true,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Text(
                text = "Hi, I am Cassy\nYour travel companion",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                modifier = Modifier.padding(bottom = 80.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        ActionButton(
            text = "Next",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(72.dp)
        )
    }
}

@Composable
fun OriginAirportScreen(
    modifier: Modifier = Modifier,
    onAirportSelected: (Airport) -> Unit
) {
    val airports = getEuropeanAirports()
    val selectedAirport = remember { mutableStateOf<Airport?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val showCustomInput = remember { mutableStateOf(false) }
    val customAirportName = remember { mutableStateOf("") }
    val showConfirmationDialog = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val filteredAirports = remember(searchQuery.value) {
        if (searchQuery.value.isEmpty()) {
            airports
        } else {
            airports.filter {
                it.iataCode.contains(searchQuery.value, ignoreCase = true) ||
                it.city.contains(searchQuery.value, ignoreCase = true) ||
                it.country.contains(searchQuery.value, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.select_continent),
            contentDescription = "Cassy Mascot",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "Select your departure\nairport",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        ActionButton(
            text = selectedAirport.value?.let {
                if (it.iataCode == "OTHER") it.city else "${it.iataCode} - ${it.city}"
            } ?: "Select Airport",
            onClick = { showDialog.value = true },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))
    }

    if (showDialog.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = true) { showDialog.value = false },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(LightBlueBackground, RoundedCornerShape(24.dp))
                    .padding(24.dp)
                    .clickable(enabled = false) { }
            ) {
                Text(
                    text = "Departure Airport",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    placeholder = {
                        Text("Search by code or city...",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 16.sp
                    ),
                    singleLine = true
                )

                Text(
                    text = "${filteredAirports.size} airports found",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(300.dp)
                ) {
                    items(filteredAirports) { airport ->
                        AirportItemCard(
                            airport = airport,
                            isSelected = selectedAirport.value?.iataCode == airport.iataCode,
                            onSelect = { selectedAirport.value = airport }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        OtherAirportItemCard(
                            isSelected = selectedAirport.value?.iataCode == "OTHER",
                            onSelect = {
                                selectedAirport.value = Airport("OTHER", "", "")
                                showCustomInput.value = true
                            }
                        )
                    }
                }

                if (showCustomInput.value && selectedAirport.value?.iataCode == "OTHER") {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = customAirportName.value,
                        onValueChange = { customAirportName.value = it },
                        placeholder = { Text("e.g., Berlin Tegel", color = Color.White.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 16.sp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showDialog.value = false
                            searchQuery.value = ""
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    ActionButton(
                        text = "Confirm",
                        onClick = {
                            selectedAirport.value?.let { airport ->
                                if (airport.iataCode == "OTHER" && customAirportName.value.isNotEmpty()) {
                                    selectedAirport.value = Airport("OTHER", customAirportName.value, "")
                                    showDialog.value = false
                                    searchQuery.value = ""
                                    showConfirmationDialog.value = true
                                } else if (airport.iataCode != "OTHER") {
                                    showDialog.value = false
                                    searchQuery.value = ""
                                    showConfirmationDialog.value = true
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    )
                }
            }
        }
    }

    if (showConfirmationDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog.value = false },
            containerColor = LightBlueBackground,
            title = {
                Text(
                    "Airport Selected",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    "Don't worry! In the app you can always change this later.",
                    color = Color.White,
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog.value = false
                        selectedAirport.value?.let { onAirportSelected(it) }
                    }
                ) {
                    Text("Continue", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun DestinationAirportScreen(
    modifier: Modifier = Modifier,
    onAirportSelected: (Airport) -> Unit
) {
    val airports = getEuropeanAirports()
    val selectedAirport = remember { mutableStateOf<Airport?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val showCustomInput = remember { mutableStateOf(false) }
    val customAirportName = remember { mutableStateOf("") }
    val showConfirmationDialog = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }
    val filteredAirports = remember(searchQuery.value) {
        if (searchQuery.value.isEmpty()) {
            airports
        } else {
            airports.filter {
                it.iataCode.contains(searchQuery.value, ignoreCase = true) ||
                it.city.contains(searchQuery.value, ignoreCase = true) ||
                it.country.contains(searchQuery.value, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.select_continent),
            contentDescription = "Cassy Mascot",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "Select your destination\nairport",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        ActionButton(
            text = selectedAirport.value?.let {
                if (it.iataCode == "OTHER") it.city else "${it.iataCode} - ${it.city}"
            } ?: "Select Airport",
            onClick = { showDialog.value = true },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))
    }

    if (showDialog.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = true) { showDialog.value = false },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(LightBlueBackground, RoundedCornerShape(24.dp))
                    .padding(24.dp)
                    .clickable(enabled = false) { }
            ) {
                Text(
                    text = "Destination Airport",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    placeholder = {
                        Text("Search by code or city...",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 16.sp
                    ),
                    singleLine = true
                )

                Text(
                    text = "${filteredAirports.size} airports found",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(300.dp)
                ) {
                    items(filteredAirports) { airport ->
                        AirportItemCard(
                            airport = airport,
                            isSelected = selectedAirport.value?.iataCode == airport.iataCode,
                            onSelect = { selectedAirport.value = airport }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        OtherAirportItemCard(
                            isSelected = selectedAirport.value?.iataCode == "OTHER",
                            onSelect = {
                                selectedAirport.value = Airport("OTHER", "", "")
                                showCustomInput.value = true
                            }
                        )
                    }
                }

                if (showCustomInput.value && selectedAirport.value?.iataCode == "OTHER") {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = customAirportName.value,
                        onValueChange = { customAirportName.value = it },
                        placeholder = { Text("e.g., Rome Fiumicino", color = Color.White.copy(alpha = 0.6f)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedContainerColor = Color.White.copy(alpha = 0.1f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 16.sp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showDialog.value = false
                            searchQuery.value = ""
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    ActionButton(
                        text = "Confirm",
                        onClick = {
                            selectedAirport.value?.let { airport ->
                                if (airport.iataCode == "OTHER" && customAirportName.value.isNotEmpty()) {
                                    selectedAirport.value = Airport("OTHER", customAirportName.value, "")
                                    showDialog.value = false
                                    searchQuery.value = ""
                                    showConfirmationDialog.value = true
                                } else if (airport.iataCode != "OTHER") {
                                    showDialog.value = false
                                    searchQuery.value = ""
                                    showConfirmationDialog.value = true
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    )
                }
            }
        }
    }

    if (showConfirmationDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog.value = false },
            containerColor = LightBlueBackground,
            title = {
                Text(
                    "Airport Selected",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    "Don't worry! In the app you can always change this later.",
                    color = Color.White,
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmationDialog.value = false
                        selectedAirport.value?.let { onAirportSelected(it) }
                    }
                ) {
                    Text("Continue", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun AirportItemCard(
    airport: Airport,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(12.dp)
            )
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSelect()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelect() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,
                uncheckedColor = Color.White.copy(alpha = 0.6f),
                checkmarkColor = LightBlueBackground
            )
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = airport.iataCode,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = airport.city,
                color = Color.White,
                fontSize = 14.sp
            )
            Text(
                text = airport.country,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun OtherAirportItemCard(
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onSelect() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,
                uncheckedColor = Color.White.copy(alpha = 0.6f),
                checkmarkColor = LightBlueBackground
            )
        )
        Text(
            text = "Other (Not in list)",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun VacationPlanningScreen(
    modifier: Modifier = Modifier,
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.select_continent),
            contentDescription = "Cassy Mascot",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "Are you planning your\nvacation this year?",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActionButton(text = "Yes", onClick = onYes, modifier = Modifier.fillMaxWidth().height(72.dp))
            ActionButton(text = "No", onClick = onNo, modifier = Modifier.fillMaxWidth().height(72.dp))
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun DateSelectionScreen(
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit
) {
    val months = listOf(
        "August", "September", "October", "November", "December"
    )
    val selectedMonth = remember { mutableStateOf(-1) }
    val selectedYear = remember { mutableStateOf(-1) }
    val showDialog = remember { mutableStateOf(false) }

    val availableYears = (2026..2035).toList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.select_continent),
            contentDescription = "Cassy Mascot",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "When are you planning\nyour next vacation?",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        ActionButton(
            text = "Choose dates in calendar",
            onClick = { showDialog.value = true },
            modifier = Modifier.fillMaxWidth().height(72.dp)
        )
    }

    if (showDialog.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = true) { showDialog.value = false },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(LightBlueBackground, RoundedCornerShape(24.dp))
                    .padding(24.dp)
                    .clickable(enabled = false) { }
            ) {
                Text(
                    text = "Select Month and Year",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                Text("Month", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                LazyColumn(modifier = Modifier.height(150.dp)) {
                    items(months) { month ->
                        Text(
                            text = month,
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (selectedMonth.value != -1 && selectedMonth.value == months.indexOf(month))
                                        Color.White.copy(alpha = 0.2f)
                                    else
                                        Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedMonth.value = months.indexOf(month) }
                                .padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Year", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
                LazyColumn(modifier = Modifier.height(150.dp)) {
                    items(availableYears) { year ->
                        Text(
                            text = year.toString(),
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (selectedYear.value != -1 && selectedYear.value == year)
                                        Color.White.copy(alpha = 0.2f)
                                    else
                                        Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedYear.value = year }
                                .padding(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDialog.value = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    ActionButton(
                        text = "Confirm",
                        onClick = {
                            if (selectedMonth.value != -1 && selectedYear.value != -1) {
                                val dateString = "${months[selectedMonth.value]} ${selectedYear.value}"
                                showDialog.value = false
                                onDateSelected(dateString)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CountriesSelectionScreen(
    modifier: Modifier = Modifier,
    onCountriesSelected: (List<String>) -> Unit
) {
    val countries = listOf(
        "Austria", "Belgium", "Bulgaria", "Croatia", "Cyprus", "Czech Republic",
        "Denmark", "Estonia", "Finland", "France", "Germany", "Greece",
        "Hungary", "Ireland", "Italy", "Latvia", "Lithuania", "Luxembourg",
        "Malta", "Netherlands", "Poland", "Portugal", "Romania", "Slovakia",
        "Slovenia", "Spain", "Sweden"
    )
    val selectedCountries = remember { mutableStateListOf<String>() }
    val showDialog = remember { mutableStateOf(true) }
    val searchQuery = remember { mutableStateOf("") }
    val filteredCountries = remember(searchQuery.value) {
        if (searchQuery.value.isEmpty()) {
            countries
        } else {
            countries.filter {
                it.contains(searchQuery.value, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.select_continent),
            contentDescription = "Cassy Mascot",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "Which countries are you\nplanning to go?",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        ActionButton(
            text = "Select Countries",
            onClick = { showDialog.value = true },
            modifier = Modifier.fillMaxWidth()
        )

        if (selectedCountries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Selected: ${selectedCountries.size} countries",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }

    if (showDialog.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = true) { showDialog.value = false },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(LightBlueBackground, RoundedCornerShape(24.dp))
                    .padding(24.dp)
                    .clickable(enabled = false) { }
            ) {
                Text(
                    text = "Select Countries",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    placeholder = {
                        Text("Search countries...",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 16.sp),
                    singleLine = true
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(300.dp)
                ) {
                    items(filteredCountries) { country ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (selectedCountries.contains(country))
                                        Color.White.copy(alpha = 0.2f)
                                    else
                                        Color.White.copy(alpha = 0.05f),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (selectedCountries.contains(country)) {
                                        selectedCountries.remove(country)
                                    } else {
                                        selectedCountries.add(country)
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = selectedCountries.contains(country),
                                onCheckedChange = {
                                    if (it) {
                                        selectedCountries.add(country)
                                    } else {
                                        selectedCountries.remove(country)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.White,
                                    uncheckedColor = Color.White.copy(alpha = 0.6f),
                                    checkmarkColor = LightBlueBackground
                                )
                            )
                            Text(
                                text = country,
                                color = Color.White,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            showDialog.value = false
                            searchQuery.value = ""
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    ActionButton(
                        text = "Confirm",
                        onClick = {
                            showDialog.value = false
                            searchQuery.value = ""
                            onCountriesSelected(selectedCountries.toList())
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FlightsBookingScreen(
    modifier: Modifier = Modifier,
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.select_continent),
            contentDescription = "Cassy Mascot",
            modifier = Modifier
                .size(180.dp)
                .padding(bottom = 32.dp)
        )

        Text(
            text = "Have you booked your\nflights already?",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActionButton(text = "Yes", onClick = onYes, modifier = Modifier.fillMaxWidth().height(72.dp))
            ActionButton(text = "No", onClick = onNo, modifier = Modifier.fillMaxWidth().height(72.dp))
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun TripAdvisoryCard(
    trip: TripAdvisory
) {
    val statusText = when (trip.status) {
        "NO-GO" -> "Do not"
        "CLEAR" -> "Go"
        "CAUTION" -> "Reconsider"
        else -> trip.status
    }

    val backgroundColor = when (trip.status) {
        "NO-GO" -> Color(0xFFFFEAE6)
        "CLEAR" -> Color(0xFFE8F5E9)
        "CAUTION" -> Color(0xFFFFF3E0)
        else -> Color(0xFFF5F5F5)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = trip.from + " → " + trip.to,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = trip.dateRange,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Box(
                modifier = Modifier
                    .background(trip.statusColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = trip.status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Text(
            text = statusText,
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = trip.statusColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LinearProgressIndicator(
                    progress = (trip.riskIndex / 100f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = trip.statusColor,
                    trackColor = Color.Gray.copy(alpha = 0.3f)
                )
            }

            Text(
                text = "risk index\n${trip.riskIndex}/100",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.End,
                lineHeight = 16.sp
            )
        }

        Text(
            text = trip.advisory,
            fontSize = 14.sp,
            color = Color.Black,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun SummaryScreen(
    modifier: Modifier = Modifier,
    onHome: () -> Unit
) {
    val trips = listOf(
        TripAdvisory(
            from = "Yerevan",
            to = "Beirut",
            dateRange = "Aug 14 – 21",
            status = "NO-GO",
            statusColor = Color(0xFFD32F2F),
            riskIndex = 86,
            advisory = "Active escalation on the corridor with airspace advisories. Cancel refundable bookings and delay travel."
        ),
        TripAdvisory(
            from = "Almaty",
            to = "Istanbul",
            dateRange = "Sep 03 – 10",
            status = "CLEAR",
            statusColor = Color(0xFF388E3C),
            riskIndex = 24,
            advisory = "Route is stable with normal advisories. Fares are steady — a good window to book."
        ),
        TripAdvisory(
            from = "Bishkek",
            to = "Tbilisi",
            dateRange = "Aug 12 – 19",
            status = "CAUTION",
            statusColor = Color(0xFFE67E22),
            riskIndex = 62,
            advisory = "Elevated regional tension and volatile fares. Travel is possible but hold a refundable fare and watch daily."
        )
    )

    val destinations = trips.map { it.to }.distinct()
    val selectedDestination = remember { mutableStateOf(destinations.firstOrNull() ?: "") }
    val filteredTrips = trips.filter { it.to == selectedDestination.value }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightBlueBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to EES",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Entry Exit System Advisory",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "Ensure your travel documents are valid. Register your entry and exit details before traveling.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = "Status: Ready to Travel",
                color = Color.Green,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Upcoming Trip",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Departure:", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Text("Ready", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Destination:", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Text("Ready", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Documents:", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Text("Complete", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Alerts",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("✓", color = Color.Green, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Column {
                    Text("All requirements met", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("Your travel is approved", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }

            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("!", color = Color.Yellow, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Column {
                    Text("Check travel advisories", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("Before departure", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("ℹ", color = Color.Cyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Column {
                    Text("Keep documents accessible", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text("During your journey", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "TRAVEL VERDICT",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(destinations) { destination ->
                Box(
                    modifier = Modifier
                        .background(
                            if (selectedDestination.value == destination)
                                Color.White
                            else
                                Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedDestination.value = destination }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = destination,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedDestination.value == destination) Color.Black else Color.White
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredTrips) { trip ->
                TripAdvisoryCard(trip = trip)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ActionButton(
            text = "Back to Home",
            onClick = onHome,
            modifier = Modifier.fillMaxWidth().height(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isPressed = remember { mutableStateOf(false) }

    OutlinedButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier
            .then(
                if (isPressed.value)
                    Modifier.graphicsLayer(scaleX = 0.95f, scaleY = 0.95f)
                else
                    Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.White),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White,
            containerColor = Color.Transparent
        ),
        interactionSource = remember { MutableInteractionSource() }
            .also { source ->
                LaunchedEffect(source) {
                    source.interactions.collect { interaction ->
                        when (interaction) {
                            is androidx.compose.foundation.interaction.PressInteraction.Press -> {
                                isPressed.value = true
                            }
                            is androidx.compose.foundation.interaction.PressInteraction.Release -> {
                                isPressed.value = false
                            }
                            is androidx.compose.foundation.interaction.PressInteraction.Cancel -> {
                                isPressed.value = false
                            }
                        }
                    }
                }
            }
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold
        )
    }
}

fun getEuropeanAirports(): List<Airport> {
    return listOf(
        Airport("CDG", "Paris", "France"),
        Airport("LHR", "London", "United Kingdom"),
        Airport("FRA", "Frankfurt", "Germany"),
        Airport("AMS", "Amsterdam", "Netherlands"),
        Airport("MAD", "Madrid", "Spain"),
        Airport("FCO", "Rome", "Italy"),
        Airport("IST", "Istanbul", "Turkey"),
        Airport("BCN", "Barcelona", "Spain"),
        Airport("BRU", "Brussels", "Belgium"),
        Airport("VIE", "Vienna", "Austria"),
        Airport("ZRH", "Zurich", "Switzerland"),
        Airport("MUC", "Munich", "Germany"),
        Airport("PRG", "Prague", "Czech Republic"),
        Airport("BUD", "Budapest", "Hungary"),
        Airport("WAW", "Warsaw", "Poland"),
        Airport("ATH", "Athens", "Greece"),
        Airport("LIS", "Lisbon", "Portugal"),
        Airport("DUB", "Dublin", "Ireland"),
        Airport("VCE", "Venice", "Italy"),
        Airport("BER", "Berlin", "Germany"),
        Airport("MIL", "Milan", "Italy"),
        Airport("TUN", "Turin", "Italy"),
        Airport("GVA", "Geneva", "Switzerland"),
        Airport("COP", "Copenhagen", "Denmark"),
        Airport("STO", "Stockholm", "Sweden"),
        Airport("HEL", "Helsinki", "Finland"),
        Airport("VNO", "Vilnius", "Lithuania"),
        Airport("RIX", "Riga", "Latvia"),
        Airport("TLL", "Tallinn", "Estonia"),
        Airport("BJS", "Bucharest", "Romania"),
        Airport("SJJ", "Sarajevo", "Bosnia"),
        Airport("ZAG", "Zagreb", "Croatia"),
        Airport("LJU", "Ljubljana", "Slovenia"),
        Airport("SKP", "Skopje", "Macedonia"),
        Airport("SOF", "Sofia", "Bulgaria"),
        Airport("KRK", "Kraków", "Poland"),
        Airport("GDN", "Gdańsk", "Poland"),
        Airport("WRO", "Wrocław", "Poland"),
        Airport("PZN", "Poznań", "Poland"),
        Airport("CFU", "Corfu", "Greece"),
        Airport("RHO", "Rhodes", "Greece"),
        Airport("JTR", "Santorini", "Greece"),
        Airport("IBZ", "Ibiza", "Spain"),
        Airport("AGP", "Málaga", "Spain"),
        Airport("SVQ", "Seville", "Spain"),
        Airport("BIO", "Bilbao", "Spain"),
        Airport("PMI", "Palma", "Spain"),
        Airport("ALC", "Alicante", "Spain"),
        Airport("VLC", "Valencia", "Spain"),
        Airport("TFS", "Tenerife South", "Spain"),
        Airport("ORY", "Paris Orly", "France"),
        Airport("LBG", "Paris Le Bourget", "France"),
        Airport("BVA", "Paris Beauvais", "France"),
        Airport("BOD", "Bordeaux", "France"),
        Airport("LYS", "Lyon", "France"),
        Airport("MRS", "Marseille", "France"),
        Airport("NCE", "Nice", "France"),
        Airport("TLS", "Toulouse", "France"),
        Airport("NRT", "Nantes", "France"),
        Airport("MPL", "Montpellier", "France"),
        Airport("AJA", "Ajaccio", "France"),
        Airport("BIA", "Bastia", "France"),
        Airport("GNB", "Grenoble", "France"),
        Airport("LIL", "Lille", "France"),
        Airport("SXB", "Strasbourg", "France"),
        Airport("BLE", "Basel", "Switzerland"),
        Airport("ANR", "Antwerp", "Belgium"),
        Airport("CHR", "Charleroi", "Belgium"),
        Airport("LGG", "Liège", "Belgium"),
        Airport("EIN", "Eindhoven", "Netherlands"),
        Airport("RTM", "Rotterdam", "Netherlands"),
        Airport("GRQ", "Groningen", "Netherlands"),
        Airport("MST", "Maastricht", "Netherlands"),
        Airport("DUS", "Düsseldorf", "Germany"),
        Airport("CGN", "Cologne", "Germany"),
        Airport("HAM", "Hamburg", "Germany"),
        Airport("HAJ", "Hanover", "Germany"),
        Airport("DRS", "Dresden", "Germany"),
        Airport("DTM", "Dortmund", "Germany"),
        Airport("FKB", "Karlsruhe", "Germany"),
        Airport("STR", "Stuttgart", "Germany"),
        Airport("NUE", "Nuremberg", "Germany"),
        Airport("INN", "Innsbruck", "Austria"),
        Airport("SZG", "Salzburg", "Austria"),
        Airport("LNZ", "Linz", "Austria"),
        Airport("GRZ", "Graz", "Austria"),
        Airport("KLU", "Klagenfurt", "Austria"),
        Airport("BRN", "Brno", "Czech Republic"),
        Airport("OKD", "Ostrava", "Czech Republic"),
        Airport("DEB", "Debrecen", "Hungary"),
        Airport("KRK", "Kraków", "Poland"),
        Airport("RZE", "Rzeszów", "Poland"),
        Airport("KTW", "Katowice", "Poland"),
        Airport("LCJ", "Łódź", "Poland"),
        Airport("SKG", "Thessaloniki", "Greece"),
        Airport("JMK", "Mykonos", "Greece"),
        Airport("ZTH", "Zante", "Greece"),
        Airport("PFO", "Paphos", "Cyprus"),
        Airport("LCA", "Larnaca", "Cyprus"),
        Airport("BOJ", "Burgas", "Bulgaria"),
        Airport("VAR", "Varna", "Bulgaria"),
        Airport("PIX", "Plovdiv", "Bulgaria"),
        Airport("OTP", "Bucharest", "Romania"),
        Airport("TSR", "Timișoara", "Romania"),
        Airport("CND", "Constanța", "Romania"),
        Airport("CLJ", "Cluj", "Romania"),
        Airport("IAI", "Iași", "Romania"),
        Airport("OMO", "Mostar", "Bosnia"),
        Airport("TZL", "Tuzla", "Bosnia"),
        Airport("DBV", "Dubrovnik", "Croatia"),
        Airport("SPU", "Split", "Croatia"),
        Airport("RJK", "Rijeka", "Croatia"),
        Airport("ZAD", "Zadar", "Croatia"),
        Airport("MBX", "Maribor", "Slovenia"),
        Airport("OHD", "Ohrid", "Macedonia"),
        Airport("BEG", "Belgrade", "Serbia"),
        Airport("NI", "Niš", "Serbia"),
        Airport("TIV", "Tivat", "Montenegro"),
        Airport("TGD", "Podgorica", "Montenegro"),
        Airport("PRN", "Pristina", "Kosovo"),
        Airport("ALB", "Tirana", "Albania"),
        Airport("TIR", "Tirana", "Albania"),
        Airport("BGY", "Bergamo", "Italy"),
        Airport("MXP", "Milan Malpensa", "Italy"),
        Airport("LIN", "Milan Linate", "Italy"),
        Airport("TRN", "Turin", "Italy"),
        Airport("CUF", "Cuneo", "Italy"),
        Airport("PMO", "Palermo", "Italy"),
        Airport("CTA", "Catania", "Italy"),
        Airport("NAP", "Naples", "Italy"),
        Airport("PSA", "Pisa", "Italy"),
        Airport("EIF", "Florence", "Italy"),
        Airport("BOL", "Bologna", "Italy"),
        Airport("VRN", "Verona", "Italy"),
        Airport("TRS", "Trieste", "Italy"),
        Airport("BRI", "Bari", "Italy"),
        Airport("BDS", "Brindisi", "Italy"),
        Airport("BLX", "Bolzano", "Italy"),
        Airport("REU", "Reus", "Spain"),
        Airport("TXL", "Barcelona", "Spain"),
        Airport("OSL", "Oslo", "Norway"),
        Airport("BGO", "Bergen", "Norway"),
        Airport("TRD", "Trondheim", "Norway"),
        Airport("BOO", "Bodø", "Norway"),
        Airport("SVG", "Stavanger", "Norway"),
        Airport("KUO", "Kuopio", "Finland"),
        Airport("TMP", "Tampere", "Finland"),
        Airport("TUR", "Turku", "Finland"),
        Airport("RVN", "Rovaniemi", "Finland"),
        Airport("OUL", "Oulu", "Finland"),
        Airport("JYV", "Jyväskylä", "Finland"),
        Airport("VAA", "Vaasa", "Finland"),
        Airport("AAL", "Aalborg", "Denmark"),
        Airport("AAT", "Aarhus", "Denmark"),
        Airport("BLL", "Billund", "Denmark"),
        Airport("ARN", "Stockholm", "Sweden"),
        Airport("GOT", "Gothenburg", "Sweden"),
        Airport("MMX", "Malmö", "Sweden"),
        Airport("VXO", "Växjö", "Sweden"),
        Airport("OER", "Örebro", "Sweden"),
        Airport("LIS", "Lisbon", "Portugal"),
        Airport("OPO", "Porto", "Portugal"),
        Airport("FNC", "Funchal", "Portugal"),
        Airport("PDL", "Ponta Delgada", "Portugal"),
        Airport("HOR", "Horta", "Portugal"),
        Airport("SKG", "Thessaloniki", "Greece"),
        Airport("KGS", "Kos", "Greece"),
        Airport("IEG", "Ioannina", "Greece"),
        Airport("MJT", "Mytilene", "Greece"),
        Airport("LXS", "Larissa", "Greece"),
        Airport("HER", "Heraklion", "Greece"),
        Airport("CHQ", "Chania", "Greece"),
        Airport("SYR", "Syros", "Greece")
    )
}

@Preview(showBackground = true)
@Composable
fun TravelCompanionFlowPreview() {
    EntryExitSystemEUTravelToEuropeTheme {
        TravelCompanionFlow()
    }
}
