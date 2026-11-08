package com.sigefiv.app.ui.components

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

private val Blanco = Color(0xFFFFFFFF)

@Composable
fun AppBottomBar(
    pantallaActual: String, // Ejemplo: "Inicio", "Asamblea", "Negocios", "Periodos", "MiCuenta"
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onEmprendimientosClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {}
) {
    val primaryColor = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    NavigationBar(
        containerColor = primaryColor,
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
            label = { Text(text = "Inicio") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = Blanco,
                indicatorColor = Blanco,
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
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
            label = { Text(text = "Asamblea") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = Blanco,
                indicatorColor = Blanco,
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
            )
        )

        // 3. NEGOCIOS (EMPRENDIMIENTOS)
        NavigationBarItem(
            selected = pantallaActual == "Negocios",
            onClick = onEmprendimientosClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Storefront,
                    contentDescription = "Negocios"
                )
            },
            label = { Text(text = "Negocios") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = Blanco,
                indicatorColor = Blanco,
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
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
            label = { Text(text = "Periodos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = Blanco,
                indicatorColor = Blanco,
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
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
            label = { Text(text = "Mi cuenta") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = primaryColor,
                selectedTextColor = Blanco,
                indicatorColor = Blanco,
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
            )
        )
    }
}