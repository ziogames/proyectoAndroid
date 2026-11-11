package com.sigefiv.app.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

private val ColorInactivo = Color(0xFF64748B)

@Composable
fun AppBottomBar(
    pantallaActual: String,
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onEmprendimientosClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {}
) {
    val primaryColor = SeasonalColors.primary(SeasonalTheme.getSeason())
    val indicadorColor = primaryColor.copy(alpha = 0.12f)

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
            thickness = 0.8.dp
        )

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            // 1. INICIO
            NavigationBarItem(
                selected = pantallaActual == "Inicio",
                onClick = onInicioClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Inicio"
                    )
                },
                label = {
                    Text(
                        text = "Inicio",
                        fontSize = 11.sp,
                        fontWeight = if (pantallaActual == "Inicio") FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = indicadorColor,
                    unselectedIconColor = ColorInactivo,
                    unselectedTextColor = ColorInactivo
                )
            )

            // 2. ASAMBLEA
            NavigationBarItem(
                selected = pantallaActual == "Asamblea",
                onClick = {
                    Log.d("ASAMBLEA_DEBUG", "CLICK EN BOTON ASAMBLEA")
                    onAsambleasClick()
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Groups,
                        contentDescription = "Asamblea"
                    )
                },
                label = {
                    Text(
                        text = "Asamblea",
                        fontSize = 11.sp,
                        fontWeight = if (pantallaActual == "Asamblea") FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = indicadorColor,
                    unselectedIconColor = ColorInactivo,
                    unselectedTextColor = ColorInactivo
                )
            )

            // 3. NEGOCIOS
            NavigationBarItem(
                selected = pantallaActual == "Negocios",
                onClick = onEmprendimientosClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Storefront,
                        contentDescription = "Negocios"
                    )
                },
                label = {
                    Text(
                        text = "Negocios",
                        fontSize = 11.sp,
                        fontWeight = if (pantallaActual == "Negocios") FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = indicadorColor,
                    unselectedIconColor = ColorInactivo,
                    unselectedTextColor = ColorInactivo
                )
            )

            // 4. PERIODOS
            NavigationBarItem(
                selected = pantallaActual == "Periodos",
                onClick = onPeriodosClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = "Periodos"
                    )
                },
                label = {
                    Text(
                        text = "Periodos",
                        fontSize = 11.sp,
                        fontWeight = if (pantallaActual == "Periodos") FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = indicadorColor,
                    unselectedIconColor = ColorInactivo,
                    unselectedTextColor = ColorInactivo
                )
            )

            // 5. MI CUENTA
            NavigationBarItem(
                selected = pantallaActual == "MiCuenta",
                onClick = onMiCuentaClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Mi cuenta"
                    )
                },
                label = {
                    Text(
                        text = "Mi cuenta",
                        fontSize = 11.sp,
                        fontWeight = if (pantallaActual == "MiCuenta") FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = primaryColor,
                    selectedTextColor = primaryColor,
                    indicatorColor = indicadorColor,
                    unselectedIconColor = ColorInactivo,
                    unselectedTextColor = ColorInactivo
                )
            )
        }
    }
}