package com.example.trainapp

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeRow(
    selectedDate: String,
    selectedTime: String,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val apiSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displaySdf = SimpleDateFormat("d MMM", Locale.ENGLISH)

    // Date Picker State
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = try {
            apiSdf.parse(selectedDate)?.time
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    )

    // Time Picker State
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime.split(":")[0].toIntOrNull() ?: 0,
        initialMinute = selectedTime.split(":")[1].toIntOrNull() ?: 0,
        is24Hour = true
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // DatePickerState uses UTC millis. Convert to local date string.
                        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        calendar.timeInMillis = millis
                        onDateSelected(apiSdf.format(calendar.time))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val formattedTime = "${timePickerState.hour.toString().padStart(2, '0')}:${timePickerState.minute.toString().padStart(2, '0')}"
                    onTimeSelected(formattedTime)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text(stringResource(R.string.btn_cancel)) }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val displayDate = try {
            val date = apiSdf.parse(selectedDate)
            if (date != null) displaySdf.format(date) else selectedDate
        } catch (e: Exception) {
            selectedDate
        }

        FancySelectionBox(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_date),
            value = displayDate,
            icon = Icons.Default.CalendarMonth,
            onClick = { showDatePicker = true }
        )
        FancySelectionBox(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_time),
            value = selectedTime,
            icon = Icons.Default.Schedule,
            onClick = { showTimePicker = true }
        )
    }
}

@Composable
fun FancySelectionBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarTypeSelector(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val carTypeOptions = listOf(
        stringResource(R.string.car_type_all),
        stringResource(R.string.car_type_tze_chiang),
        stringResource(R.string.car_type_puyuma),
        stringResource(R.string.car_type_local)
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.label_car_type)) },
            leadingIcon = { Icon(Icons.Default.DirectionsTransit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            carTypeOptions.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CityStationSelectorFancy(
    cityLabel: String,
    selectedCity: String,
    selectedStation: Station?,
    onCityClick: () -> Unit,
    onStationClick: () -> Unit,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = cityLabel,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // City Box
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onCityClick() }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = if (selectedCity.isEmpty()) stringResource(R.string.placeholder_city) else selectedCity,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedCity.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Station Box
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(enabled = selectedCity.isNotEmpty()) { onStationClick() }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = when {
                            selectedCity.isEmpty() -> stringResource(R.string.select_city_first)
                            selectedStation == null -> stringResource(R.string.placeholder_station)
                            else -> selectedStation.name
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedStation == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onSheetVisibilityChange: (Boolean) -> Unit = {}
) {
    val app = TrainApp.instance
    var originCity by remember { mutableStateOf("") }
    var originStation by remember { mutableStateOf<Station?>(null) }
    var destCity by remember { mutableStateOf("") }
    var destStation by remember { mutableStateOf<Station?>(null) }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val stf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val now = Date()

    var selectedDate by remember { mutableStateOf(sdf.format(now)) }
    var selectedTime by remember { mutableStateOf(stf.format(now)) }
    
    val allTypesLabel = stringResource(R.string.car_type_all)
    var selectedCarType by remember { mutableStateOf(allTypesLabel) }

    var searchResults by remember { mutableStateOf<List<TrainSchedule>?>(null) }
    var stationDataMap by remember { mutableStateOf(mapOf<String, List<Station>>()) }
    var isLoading by remember { mutableStateOf(false) }

    var favorites by remember { mutableStateOf(app.getFavorites()) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val citySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val stationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // State for City/Station sheets
    var showCitySheet by remember { mutableStateOf(false) }
    var showStationSheet by remember { mutableStateOf(false) }
    var selectionTarget by remember { mutableStateOf("origin") }

    // Update bottom bar visibility
    LaunchedEffect(showCitySheet, showStationSheet, showBottomSheet) {
        onSheetVisibilityChange(showCitySheet || showStationSheet || showBottomSheet)
    }

    LaunchedEffect(Unit) {
        TrainRepository.fetchAllStations { map -> stationDataMap = map }
    }

    val cityOrder = listOf(
        "基隆市", "臺北市", "新北市", "桃園市", "新竹縣", "新竹市", "苗栗縣", 
        "臺中市", "彰化縣", "南投縣", "雲林縣", "嘉義縣", "嘉義市", "臺南市", 
        "高雄市", "屏東縣", "宜蘭縣", "花蓮縣", "臺東縣"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Text(stringResource(R.string.nav_search), style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black))
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CityStationSelectorFancy(
                            cityLabel = stringResource(R.string.label_origin),
                            selectedCity = originCity,
                            selectedStation = originStation,
                            onCityClick = { selectionTarget = "origin"; showCitySheet = true },
                            onStationClick = { selectionTarget = "origin"; showStationSheet = true },
                            icon = Icons.Default.LocationOn
                        )
                        CityStationSelectorFancy(
                            cityLabel = stringResource(R.string.label_destination),
                            selectedCity = destCity,
                            selectedStation = destStation,
                            onCityClick = { selectionTarget = "dest"; showCitySheet = true },
                            onStationClick = { selectionTarget = "dest"; showStationSheet = true },
                            icon = Icons.Default.Flag
                        )
                    }
                    FilledIconButton(
                        onClick = {
                            val tC = originCity; val tS = originStation
                            originCity = destCity; originStation = destStation
                            destCity = tC; destStation = tS
                        },
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 24.dp).size(40.dp)
                    ) { Icon(Icons.AutoMirrored.Filled.CompareArrows, "Invert") }
                }
            }

            item { DateTimeRow(selectedDate, selectedTime, {selectedDate = it}, {selectedTime = it}) }
            item { CarTypeSelector(selectedCarType) {selectedCarType = it} }

            item {
                Row(modifier = Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                    val isFavorite = favorites.any { it.originId == originStation?.id && it.destId == destStation?.id }
                    Button(
                        onClick = {
                            if (originStation != null && destStation != null) {
                                isLoading = true
                                TrainRepository.searchTrains(originStation!!.id, destStation!!.id, selectedDate, selectedTime, selectedCarType) {
                                    searchResults = it; isLoading = false; showBottomSheet = true
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        enabled = originStation != null && destStation != null && !isLoading,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading) CircularProgressIndicator(Modifier.size(24.dp), MaterialTheme.colorScheme.onPrimary)
                        else { Icon(Icons.Default.Search, null); Spacer(Modifier.width(8.dp)); Text(stringResource(R.string.btn_search), fontWeight = FontWeight.Bold) }
                    }
                    FilledTonalIconButton(
                        onClick = {
                            if (originStation != null && destStation != null) {
                                val route = FavoriteRoute(originStation!!.id, originStation!!.name, destStation!!.id, destStation!!.name)
                                if (isFavorite) app.removeFavorite(route) else app.addFavorite(route)
                                favorites = app.getFavorites()
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        enabled = originStation != null && destStation != null,
                        shape = RoundedCornerShape(16.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = if (isFavorite) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                    ) { Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
    }

    if (showCitySheet) {
        val sortedCities = stationDataMap.keys.toList().sortedWith(compareBy { city ->
            val index = cityOrder.indexOf(city)
            if (index != -1) index else Int.MAX_VALUE
        })
        GridSelectionSheet(
            title = stringResource(R.string.placeholder_city),
            items = sortedCities,
            onItemSelected = {
                if (selectionTarget == "origin") { originCity = it; originStation = null } 
                else { destCity = it; destStation = null }
                showCitySheet = false
                showStationSheet = true
            },
            onDismiss = { showCitySheet = false },
            sheetState = citySheetState
        )
    }

    if (showStationSheet) {
        val currentCity = if (selectionTarget == "origin") originCity else destCity
        val stations = stationDataMap[currentCity] ?: emptyList()
        // Sort stations by ID as a heuristic for north-to-south within a city
        val sortedStations = stations.sortedBy { it.id }
        GridSelectionSheet(
            title = stringResource(R.string.placeholder_station),
            items = sortedStations.map { it.name },
            onItemSelected = { name ->
                val station = sortedStations.find { it.name == name }
                if (selectionTarget == "origin") originStation = station else destStation = station
                showStationSheet = false
            },
            onDismiss = { showStationSheet = false },
            sheetState = stationSheetState
        )
    }

    if (showBottomSheet) {
        SearchResultSheet(
            originName = originStation?.name ?: "",
            destName = destStation?.name ?: "",
            searchResults = searchResults,
            onDismiss = { showBottomSheet = false },
            sheetState = sheetState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridSelectionSheet(
    title: String,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    val importantStations = remember {
        setOf(
            "基隆", "七堵", "南港", "臺北", "板橋", "桃園", "中壢", "新竹", "竹南", "苗栗",
            "豐原", "臺中", "彰化", "員林", "斗六", "嘉義", "新營", "臺南", "岡山", "新左營",
            "高雄", "屏東", "潮州", "宜蘭", "羅東", "花蓮", "玉里", "臺東"
        )
    }

    TrainAppModalSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    val isImportant = importantStations.contains(item)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.3f)
                            .clickable { onItemSelected(item) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isImportant) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = if (isImportant) FontWeight.ExtraBold else FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center,
                                color = if (isImportant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListSelectionSheet(
    title: String,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    TrainAppModalSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(items) { item ->
                    ListItem(
                        headlineContent = { Text(item, style = MaterialTheme.typography.bodyLarge) },
                        modifier = Modifier.clickable { onItemSelected(item) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }
        }
    }
}
