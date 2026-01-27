// Filename: AppBars.kt
package com.example.trainapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

// --- 1. Data Models for Navigation ---
data class BottomNavItem(
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

// --- 2. The Menu Configuration ---
val bottomNavItems = listOf(
    BottomNavItem(R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(R.string.nav_search, Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem(R.string.nav_settings, Icons.Filled.Settings, Icons.Outlined.Settings)
)

// --- 4. The Bottom Bar Component ---

@Composable
fun MyBottomBar(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar {
        bottomNavItems.forEachIndexed { index, item ->
            val isSelected = selectedItemIndex == index

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                label = { Text(stringResource(id = item.labelResId)) },

                // === ADD THIS LINE ===
                // This tells Android: "Hide text if not selected"
                alwaysShowLabel = false,
                // =====================

                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(id = item.labelResId)
                    )
                }
            )
        }
    }
}