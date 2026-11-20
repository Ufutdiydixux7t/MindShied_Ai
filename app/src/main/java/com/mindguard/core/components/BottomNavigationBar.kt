package com.mindguard.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mindguard.core.theme.Primary

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .border(BorderStroke(1.dp, Primary.copy(alpha = 0.16f)), RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            tonalElevation = 3.dp
        ) {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                containerColor = Color.Transparent,
                contentColor = Primary,
                tonalElevation = 0.dp,
                windowInsets = NavigationBarDefaults.windowInsets
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
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Primary,
    unselectedIconColor = Color.Gray,
    selectedTextColor = Primary,
    unselectedTextColor = Color.Gray,
    indicatorColor = Primary.copy(alpha = 0.1f)
)
