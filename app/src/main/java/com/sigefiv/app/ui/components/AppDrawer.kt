package com.sigefiv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
|--------------------------------------------------------------------------
| PALETA DE COLORES - SIDEBAR SIGEFIV
|--------------------------------------------------------------------------
*/
private val VerdePrincipal = Color(0xFF15803D)
private val VerdeFondoSidebar = Color(0xFFF0FDF4)
private val VerdeSeleccionadoContainer = Color(0xFFDCFCE7)
private val VerdeTextoSeleccionado = Color(0xFF15803D)
private val TextoSeccion = Color(0xFF166534)
private val TextoInactivo = Color(0xFF334155)
private val TextoDeshabilitado = Color(0xFF94A3B8)
private val Blanco = Color(0xFFFFFFFF)

@Composable
fun AppDrawer(
    currentScreen: String,
    rol: String? = null,
    permisos: List<String> = emptyList(),
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    // =========================================================
    // LÓGICA DE PERMISOS (@can / RBAC de Spatie)
    // =========================================================
    val esAdmin = rol?.trim()?.equals("Administrador", ignoreCase = true) == true

    fun puede(permiso: String): Boolean {
        if (esAdmin) return true
        return permisos.contains(permiso)
    }

    fun puedeCualquiera(vararg listaPermisos: String): Boolean {
        if (esAdmin) return true
        return listaPermisos.any { permisos.contains(it) }
    }

    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = VerdeFondoSidebar
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // =========================================================
            // CABECERA VERDE ESMERALDA
            // =========================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerdePrincipal)
                    .padding(
                        top = 36.dp,
                        bottom = 20.dp,
                        start = 20.dp,
                        end = 20.dp
                    )
            ) {
                Text(
                    text = "SIGEFIV",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Blanco
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Sistema de Gestión Financiera",
                    style = MaterialTheme.typography.bodySmall,
                    color = Blanco.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Grupo Residencial 21",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBBF7D0),
                    fontWeight = FontWeight.Bold
                )

                if (!rol.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Blanco.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = rol,
                            color = Blanco,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // =========================================================
            // INICIO (@can('dashboard'))
            // =========================================================
            if (puede("dashboard")) {
                DrawerSectionTitle("INICIO")

                DrawerItem(
                    title = "Panel principal",
                    icon = Icons.Default.Home,
                    route = "dashboard",
                    currentScreen = currentScreen,
                    onNavigate = onNavigate
                )
            }

            // =========================================================
            // COMUNIDAD
            // =========================================================
            DrawerSectionTitle("COMUNIDAD")

            DrawerItem(
                title = "Chat Vecinal",
                icon = Icons.Default.Chat,
                route = "chat",
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )

            // @can('asambleas.index')
            if (puede("asambleas.index")) {
                DrawerItem(
                    title = "Asambleas",
                    icon = Icons.Default.Campaign,
                    route = "asambleas",
                    currentScreen = currentScreen,
                    onNavigate = onNavigate
                )
            }

            // =========================================================
            // ASISTENTE
            // =========================================================
            DrawerSectionTitle("ASISTENTE")

            NavigationDrawerItem(
                label = {
                    Text(
                        text = "ZOE",
                        fontWeight = FontWeight.Bold
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "ZOE"
                    )
                },
                badge = {
                    Text(
                        text = "IA",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                selected = currentScreen == "sigi",
                onClick = {
                    onNavigate("sigi")
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 2.dp
                ),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = VerdeSeleccionadoContainer,
                    selectedTextColor = VerdeTextoSeleccionado,
                    selectedIconColor = VerdeTextoSeleccionado,
                    unselectedContainerColor = Color.Transparent,
                    unselectedTextColor = TextoInactivo,
                    unselectedIconColor = VerdePrincipal
                )
            )

            // =========================================================
            // CONTABILIDAD
            // =========================================================
            val tieneAccesoContable = puedeCualquiera("reportes.index", "movimientos.index", "caja.index")

            if (tieneAccesoContable) {
                DrawerSectionTitle("CONTABILIDAD")

                if (puede("reportes.index")) {
                    DrawerItem(
                        title = "Reportes",
                        icon = Icons.Default.Assessment,
                        route = "reportes",
                        currentScreen = currentScreen,
                        onNavigate = onNavigate,
                        disponible = false
                    )
                }

                if (puede("movimientos.index")) {
                    DrawerItem(
                        title = "Movimientos",
                        icon = Icons.Default.SwapHoriz,
                        route = "movimientos",
                        currentScreen = currentScreen,
                        onNavigate = onNavigate
                    )

                    DrawerItem(
                        title = "Períodos",
                        icon = Icons.Default.Timeline,
                        route = "periodos",
                        currentScreen = currentScreen,
                        onNavigate = onNavigate
                    )
                }

                if (puede("caja.index")) {
                    DrawerItem(
                        title = "Caja",
                        icon = Icons.Default.AccountBalanceWallet,
                        route = "caja",
                        currentScreen = currentScreen,
                        onNavigate = onNavigate,
                        disponible = true
                    )
                }
            }

            // =========================================================
            // ADMINISTRACIÓN
            // =========================================================
            val tieneAccesoAdmin = puedeCualquiera(
                "usuarios.index",
                "roles.index",
                "configuracion",
                "categorias.index",
                "bitacora.index",
                "actividad.index"
            )

            if (tieneAccesoAdmin) {
                DrawerSectionTitle("ADMINISTRACIÓN")

                if (puede("usuarios.index")) {
                    DrawerItem(
                        title = "Usuarios",
                        icon = Icons.Default.People,
                        route = "usuarios",
                        currentScreen = currentScreen,
                        onNavigate = onNavigate,
                        disponible = true
                    )
                }

                if (puede("roles.index")) {
                    DrawerItem(
                        title = "Roles",
                        icon = Icons.Default.Security,
                        route = "roles",
                        currentScreen = currentScreen,
                        onNavigate = onNavigate,
                        disponible = true
                    )
                }

                if (puede("configuracion")) {
                    DrawerItem(
                        title = "Configuración",
                        icon = Icons.Default.Settings,
                        route = "configuracion",
                        currentScreen = currentScreen,
                        onNavigate = onNavigate,
                        disponible = false
                    )
                }

                if (puede("categorias.index")) {
                    DrawerItem(
                        title = "Categorías",
                        icon = Icons.Default.Folder,
                        route = "categorias",
                        currentScreen = currentScreen,
                        onNavigate = onNavigate,
                        disponible = false
                    )
                }

                if (puede("bitacora.index")) {
                    DrawerItem(
                        title = "Bitácora",
                        icon = Icons.Default.MenuBook,
                        route = "bitacora",
                        currentScreen = currentScreen,
                        onNavigate = onNavigate,
                        disponible = false
                    )
                }

                if (puede("actividad.index")) {
                    DrawerItem(
                        title = "Actividad",
                        icon = Icons.Default.Timeline,
                        route = "actividad",
                        currentScreen = currentScreen,
                        onNavigate = onNavigate,
                        disponible = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(
                color = Color(0xFFCBD5E1),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // =========================================================
            // SISTEMA
            // =========================================================
            DrawerSectionTitle("SISTEMA")

            DrawerItem(
                title = "Mi Cuenta",
                icon = Icons.Default.AccountCircle,
                route = "perfil",
                currentScreen = currentScreen,
                onNavigate = onNavigate,
                disponible = true
            )

            Spacer(modifier = Modifier.height(4.dp))

            // =========================================================
            // CERRAR SESIÓN
            // =========================================================
            NavigationDrawerItem(
                label = {
                    Text(
                        text = "Cerrar sesión",
                        fontWeight = FontWeight.Medium
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Cerrar sesión"
                    )
                },
                selected = false,
                onClick = onLogout,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedTextColor = MaterialTheme.colorScheme.error,
                    unselectedIconColor = MaterialTheme.colorScheme.error,
                    unselectedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = TextoSeccion,
        modifier = Modifier.padding(
            start = 24.dp,
            top = 14.dp,
            bottom = 4.dp
        )
    )
}

@Composable
private fun DrawerItem(
    title: String,
    icon: ImageVector,
    route: String,
    currentScreen: String,
    onNavigate: (String) -> Unit,
    disponible: Boolean = true
) {
    val isSelected = currentScreen == route

    NavigationDrawerItem(
        label = {
            Text(
                text = title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title
            )
        },
        selected = isSelected,
        onClick = {
            if (disponible) {
                onNavigate(route)
            }
        },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(
            horizontal = 12.dp,
            vertical = 2.dp
        ),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = VerdeSeleccionadoContainer,
            selectedTextColor = VerdeTextoSeleccionado,
            selectedIconColor = VerdeTextoSeleccionado,
            unselectedContainerColor = Color.Transparent,
            unselectedTextColor = if (disponible) TextoInactivo else TextoDeshabilitado,
            unselectedIconColor = if (disponible) VerdePrincipal else TextoDeshabilitado
        )
    )
}