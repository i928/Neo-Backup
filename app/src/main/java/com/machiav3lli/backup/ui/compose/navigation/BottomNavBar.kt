package com.machiav3lli.backup.ui.compose.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.machiav3lli.backup.NAV_MAIN
import com.machiav3lli.backup.NAV_PREFS
import com.machiav3lli.backup.preferences.pref_toolbarOpacity

@Composable
fun BottomNavBar(page: Int = NAV_MAIN, navController: NavController) {
    val items = when (page) {
        NAV_PREFS -> listOf(
            NavItem.UserPrefs,
            NavItem.ServicePrefs,
            NavItem.AdvancedPrefs,
            NavItem.ToolsPrefs,
        )
        else      -> listOf(
            NavItem.Home,
            NavItem.Backup,
            NavItem.Restore,
            NavItem.Scheduler,
        )
    }

    NavigationBar(
        modifier = Modifier
            .padding(
                start = 8.dp,
                end = 8.dp,
                bottom = 8.dp,
            )
            .clip(MaterialTheme.shapes.large),
        containerColor = MaterialTheme.colorScheme.surface
            .copy(alpha = pref_toolbarOpacity.value / 100f),
        tonalElevation = 0.dp,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentDestination == item.destination

            NavBarItem(
                selected = selected,
                icon = item.icon,
                labelId = item.title,
                onClick = {
                    navController.navigate(item.destination) {
                        navController.currentDestination?.id?.let {
                            popUpTo(it) {
                                inclusive = true
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}