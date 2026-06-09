package br.edu.unisatc.gearlog.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import br.edu.unisatc.gearlog.ui.navigation.GearLogScreen
import br.edu.unisatc.gearlog.ui.theme.JdmRed
import br.edu.unisatc.gearlog.ui.theme.PremiumMuted
import br.edu.unisatc.gearlog.ui.theme.PremiumCard

@Composable
fun GearLogBottomNav(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        GearLogScreen.Dashboard,
        GearLogScreen.Garage,
        GearLogScreen.History,
        GearLogScreen.Parts
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = PremiumCard,
        modifier = modifier
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to avoid building up a large stack
                        popUpTo(navController.graph.startDestinationId) {
                        }
                        // Avoid multiple copies of the same destination when reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = screen.icon, contentDescription = screen.title, modifier = Modifier.padding(2.dp))
                },
                label = { Text(screen.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = JdmRed,
                    selectedTextColor = JdmRed,
                    unselectedIconColor = PremiumMuted,
                    unselectedTextColor = PremiumMuted,
                    indicatorColor = Color.Transparent
                ),
                alwaysShowLabel = true
            )
        }
    }
}

