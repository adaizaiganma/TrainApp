package com.example.trainapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DateTimeRow(
    date: String,
    time: String,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val formattedDate = "$year-${(month + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val formattedTime = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
            onTimeSelected(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FancySelectionBox(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_date),
            value = date,
            icon = Icons.Default.CalendarMonth,
            onClick = { datePickerDialog.show() }
        )
        FancySelectionBox(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_time),
            value = time,
            icon = Icons.Default.Schedule,
            onClick = { timePickerDialog.show() }
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
    Surface(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            carTypeOptions.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityStationSelectorFancy(
    cityLabel: String,
    selectedCity: String,
    selectedStation: Station?,
    stationMap: Map<String, List<Station>>,
    onCitySelected: (String) -> Unit,
    onStationSelected: (Station?) -> Unit,
    icon: ImageVector
) {
    var expandedCity by remember { mutableStateOf(false) }
    var expandedStation by remember { mutableStateOf(false) }

    val availableStations = stationMap[selectedCity] ?: emptyList()
    val cities = stationMap.keys.toList().sorted()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = cityLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // City Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCity,
                onExpandedChange = { expandedCity = !expandedCity },
                modifier = Modifier.weight(0.4f)
            ) {
                OutlinedTextField(
                    value = selectedCity,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(stringResource(R.string.placeholder_city), fontSize = 12.sp) },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                ExposedDropdownMenu(expanded = expandedCity, onDismissRequest = { expandedCity = false }) {
                    cities.forEach { city ->
                        DropdownMenuItem(text = { Text(city) }, onClick = { onCitySelected(city); expandedCity = false; onStationSelected(null) })
                    }
                }
            }

            // Station Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedStation,
                onExpandedChange = { if (selectedCity.isNotEmpty()) expandedStation = !expandedStation },
                modifier = Modifier.weight(0.6f)
            ) {
                OutlinedTextField(
                    value = selectedStation?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(stringResource(R.string.placeholder_station), fontSize = 12.sp) },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedCity.isNotEmpty(),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
                ExposedDropdownMenu(expanded = expandedStation, onDismissRequest = { expandedStation = false }) {
                    availableStations.forEach { station ->
                        DropdownMenuItem(text = { Text(station.name) }, onClick = { expandedStation = false; onStationSelected(station) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    val app = TrainApp.instance
    var originCity by remember { mutableStateOf("") }
    var originStation by remember { mutableStateOf<Station?>(null) }
    var destCity by remember { mutableStateOf("") }
    var destStation by remember { mutableStateOf<Station?>(null) }

    var selectedDate by remember { mutableStateOf("2026-01-19") }
    var selectedTime by remember { mutableStateOf("12:00") }
    
    val allTypesLabel = stringResource(R.string.car_type_all)
    var selectedCarType by remember { mutableStateOf(allTypesLabel) }

    var searchResults by remember { mutableStateOf<List<TrainSchedule>?>(null) }
    var stationDataMap by remember { mutableStateOf(mapOf<String, List<Station>>()) }
    var isLoading by remember { mutableStateOf(false) }

    var favorites by remember { mutableStateOf(app.getFavorites()) }
    var selectedRoute by remember { mutableStateOf<FavoriteRoute?>(null) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        TrainRepository.fetchAllStations { map -> stationDataMap = map }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.search_header),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Station Selection Card
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CityStationSelectorFancy(
                            cityLabel = stringResource(R.string.label_origin),
                            selectedCity = originCity,
                            selectedStation = originStation,
                            stationMap = stationDataMap,
                            onCitySelected = { originCity = it },
                            onStationSelected = { originStation = it },
                            icon = Icons.Default.LocationOn
                        )

                        CityStationSelectorFancy(
                            cityLabel = stringResource(R.string.label_destination),
                            selectedCity = destCity,
                            selectedStation = destStation,
                            stationMap = stationDataMap,
                            onCitySelected = { destCity = it },
                            onStationSelected = { destStation = it },
                            icon = Icons.Default.Flag
                        )
                    }

                    // Swap Button Floating
                    IconButton(
                        onClick = {
                            val tempCity = originCity
                            val tempStation = originStation
                            originCity = destCity
                            originStation = destStation
                            destCity = tempCity
                            destStation = tempStation
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 24.dp)
                            .offset(y = 0.dp)
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                            contentDescription = "Invert",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            item {
                DateTimeRow(
                    date = selectedDate,
                    time = selectedTime,
                    onDateSelected = { selectedDate = it },
                    onTimeSelected = { selectedTime = it }
                )
            }

            item {
                CarTypeSelector(selectedType = selectedCarType, onTypeSelected = { selectedCarType = it })
            }

            // Search and Favorite Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isFavorite = favorites.any { it.originId == originStation?.id && it.destId == destStation?.id }

                    // Search Button
                    Button(
                        onClick = {
                            if (originStation != null && destStation != null) {
                                selectedRoute = FavoriteRoute(originStation!!.id, originStation!!.name, destStation!!.id, destStation!!.name)
                                isLoading = true
                                TrainRepository.searchTrains(
                                    originId = originStation!!.id,
                                    destId = destStation!!.id,
                                    date = selectedDate,
                                    startTime = selectedTime,
                                    carTypeKeyword = selectedCarType
                                ) { results ->
                                    searchResults = results
                                    isLoading = false
                                    showBottomSheet = true
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = originStation != null && destStation != null && !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.btn_search), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    // Favorite Button
                    FilledTonalIconButton(
                        onClick = {
                            if (originStation != null && destStation != null) {
                                val route = FavoriteRoute(
                                    originId = originStation!!.id,
                                    originName = originStation!!.name,
                                    destId = destStation!!.id,
                                    destName = destStation!!.name
                                )
                                if (isFavorite) {
                                    app.removeFavorite(route)
                                } else {
                                    app.addFavorite(route)
                                }
                                favorites = app.getFavorites()
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        enabled = originStation != null && destStation != null,
                        shape = RoundedCornerShape(16.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (isFavorite) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Crossfade(targetState = isFavorite, animationSpec = tween(300)) { favorite ->
                            Icon(
                                imageVector = if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = stringResource(R.string.btn_add_favorite),
                                tint = if (favorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // --- FAVORITES SECTION ---
            if (favorites.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.favorites_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(favorites) { route ->
                    FavoriteRouteItemFancy(
                        route = route,
                        onClick = {
                            selectedRoute = route
                            isLoading = true
                            
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val stf = SimpleDateFormat("HH:mm", Locale.getDefault())
                            val now = Date()
                            
                            TrainRepository.searchTrains(
                                originId = route.originId,
                                destId = route.destId,
                                date = sdf.format(now),
                                startTime = stf.format(now),
                                carTypeKeyword = "All"
                            ) { results ->
                                searchResults = results
                                isLoading = false
                                showBottomSheet = true
                            }
                        },
                        onDelete = {
                            app.removeFavorite(route)
                            favorites = app.getFavorites()
                        }
                    )
                }
            }
        }
    }

    if (showBottomSheet && selectedRoute != null) {
        SearchResultSheet(
            originName = selectedRoute!!.originName,
            destName = selectedRoute!!.destName,
            searchResults = searchResults,
            onDismiss = { showBottomSheet = false },
            sheetState = sheetState
        )
    }
}

@Composable
fun FavoriteRouteItemFancy(
    route: FavoriteRoute,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Train,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "${route.originName} → ${route.destName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultSheet(
    originName: String,
    destName: String,
    searchResults: List<TrainSchedule>?,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.results_header),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = originName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp).padding(horizontal = 4.dp))
                        Text(
                            text = destName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (searchResults.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.no_results),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults) { train ->
                        TrainCard(train = train)
                    }
                }
            }
        }
    }
}
