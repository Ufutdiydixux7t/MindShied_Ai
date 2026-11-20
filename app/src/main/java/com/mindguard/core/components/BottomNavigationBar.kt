package com.mindguard.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mindguard.core.theme.Primary

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 7.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp,
        tonalElevation = 2.dp
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth().height(68.dp),
            containerColor = Color.Transparent,
            contentColor = Primary,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                label = { Text("الرئيسية") },
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                colors = navigationItemColors()
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.BarChart, contentDescription = "الإحصائيات") },
                label = { Text("الإحصائيات") },
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                colors = navigationItemColors()
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Shield, contentDescription = "الحماية") },
                label = { Text("الحماية") },
                selected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                colors = navigationItemColors()
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.FamilyRestroom, contentDescription = "العائلة") },
                label = { Text("العائلة") },
                selected = selectedTab == 3,
                onClick = { onTabSelected(3) },
                colors = navigationItemColors()
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Menu, contentDescription = "المزيد") },
                label = { Text("المزيد") },
                selected = selectedTab == 4,
                onClick = { onTabSelected(4) },
                colors = navigationItemColors()
            )
        }
    }
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Primary,
    unselectedIconColor = Color.Gray,
    selectedTextColor = Primary,
    unselectedTextColor = Color.Gray,
    indicatorColor = Primary.copy(alpha = 0.1f)
)
