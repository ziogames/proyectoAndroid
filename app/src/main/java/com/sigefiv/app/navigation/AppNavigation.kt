@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.navigation

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import com.sigefiv.app.ComprobanteInterpretado
import com.sigefiv.app.TipoMovimientoDetectado
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.api.EmprendimientoApi
import com.sigefiv.app.data.model.Asamblea
import com.sigefiv.app.data.model.EmprendimientoModel
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.repository.CajaRepository
import com.sigefiv.app.data.repository.ChatRepository
import com.sigefiv.app.data.repository.ConfiguracionRepository
import com.sigefiv.app.data.repository.EmprendimientoRepository
import com.sigefiv.app.data.repository.RolRepository
import com.sigefiv.app.data.repository.UsuarioRepository
import com.sigefiv.app.notifications.NotificacionEventBus
import com.sigefiv.app.screens.actividad.ActividadScreen
import com.sigefiv.app.screens.asambleas.AsambleasScreen
import com.sigefiv.app.screens.asambleas.ConvocatoriaAsambleaScreen
import com.sigefiv.app.screens.asambleas.CrearAsambleaScreen
import com.sigefiv.app.screens.asambleas.DetalleAsambleaScreen
import com.sigefiv.app.screens.bitacora.BitacoraScreen
import com.sigefiv.app.screens.caja.CajaScreen
import com.sigefiv.app.screens.chat.ChatVecinalScreen
import com.sigefiv.app.screens.configuracion.ConfiguracionScreen
import com.sigefiv.app.screens.dashboard.DashboardScreen
import com.sigefiv.app.screens.emprendimiento.DetalleEmprendimientoScreen
import com.sigefiv.app.screens.emprendimiento.EmprendimientoFormAction
import com.sigefiv.app.screens.emprendimiento.EmprendimientoFormScreen
import com.sigefiv.app.screens.emprendimiento.EmprendimientosFavoritosScreen
import com.sigefiv.app.screens.emprendimiento.EmprendimientosListScreen
import com.sigefiv.app.screens.emprendimiento.EmprendimientosScreen
import com.sigefiv.app.screens.emprendimiento.OpinionesScreen
import com.sigefiv.app.screens.incidencias.CrearReporteScreen
import com.sigefiv.app.screens.incidencias.GestionIncidenciasDirectivaScreen
import com.sigefiv.app.screens.incidencias.MisReportesVecinoScreen
import com.sigefiv.app.screens.mascota.MascotaBurbujaEvento
import com.sigefiv.app.screens.mascota.MascotaControlador
import com.sigefiv.app.screens.mascota.MascotaPreferencias
import com.sigefiv.app.screens.mascota.MascotaSigefiv
import com.sigefiv.app.screens.movimientos.DetalleMovimientoScreen
import com.sigefiv.app.screens.movimientos.MovimientosScreen
import com.sigefiv.app.screens.movimientos.NuevoEgresoScreen
import com.sigefiv.app.screens.movimientos.NuevoIngresoScreen
import com.sigefiv.app.screens.movimientos.NuevoMovimientoScreen
import com.sigefiv.app.screens.notificaciones.ConfirmarNotificacionScreen
import com.sigefiv.app.screens.notificaciones.EnviarNotificacionScreen
import com.sigefiv.app.screens.notificaciones.NotificacionesScreen
import com.sigefiv.app.screens.perfil.PerfilScreen
import com.sigefiv.app.screens.periodos.PeriodoDetalleScreen
import com.sigefiv.app.screens.periodos.PeriodosAnioScreen
import com.sigefiv.app.screens.periodos.PeriodosScreen
import com.sigefiv.app.screens.roles.EditarRolScreen
import com.sigefiv.app.screens.roles.NuevoRolScreen
import com.sigefiv.app.screens.roles.RolesListScreen
import com.sigefiv.app.screens.sigi.SigiScreen
import com.sigefiv.app.screens.usuarios.UsuariosScreen
import com.sigefiv.app.ui.components.AppDrawer
import com.sigefiv.app.viewmodel.ActividadViewModel
import com.sigefiv.app.viewmodel.AsambleasViewModel
import com.sigefiv.app.viewmodel.CajaViewModel
import com.sigefiv.app.viewmodel.CategoriasViewModel
import com.sigefiv.app.viewmodel.ChatViewModel
import com.sigefiv.app.viewmodel.ConfiguracionViewModel
import com.sigefiv.app.viewmodel.DashboardViewModel
import com.sigefiv.app.viewmodel.EmprendimientoFormViewModel
import com.sigefiv.app.viewmodel.EmprendimientoViewModel
import com.sigefiv.app.viewmodel.EmprendimientosListViewModel
import com.sigefiv.app.viewmodel.EnviarNotificacionViewModel
import com.sigefiv.app.viewmodel.FcmPreferenciaViewModel
import com.sigefiv.app.viewmodel.IncidenciasViewModel
import com.sigefiv.app.viewmodel.LoginViewModel
import com.sigefiv.app.viewmodel.MovimientosViewModel
import com.sigefiv.app.viewmodel.NotificacionViewModel
import com.sigefiv.app.viewmodel.PeriodoViewModel
import com.sigefiv.app.viewmodel.PeriodosViewModel
import com.sigefiv.app.viewmodel.PerfilViewModel
import com.sigefiv.app.viewmodel.RolViewModel
import com.sigefiv.app.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder
import kotlin.math.roundToInt
import com.sigefiv.app.viewmodel.ReportesViewModel
import com.sigefiv.app.screens.reportes.ReportesScreen

enum class AppScreen(val drawerRoute: String) {
    DASHBOARD("dashboard"),
    MOVIMIENTOS("movimientos"),
    DETALLE_MOVIMIENTO("movimientos"),
    NUEVO_MOVIMIENTO("movimientos"),
    NUEVO_INGRESO("movimientos"),
    NUEVO_EGRESO("movimientos"),
    ASAMBLEAS("asambleas"),
    NUEVA_ASAMBLEA("asambleas"),
    EDITAR_ASAMBLEA("asambleas"),
    DETALLE_ASAMBLEA("asambleas"),
    CONVOCATORIA_ASAMBLEA("asambleas"),
    PERIODOS("periodos"),
    PERIODOS_ANIO("periodos"),
    PERIODO_DETALLE("periodos"),
    SIGI("sigi"),
    CHAT("chat"),
    PERFIL("perfil"),
    USUARIOS("usuarios"),
    ROLES("roles"),
    NUEVO_ROL("roles"),
    EDITAR_ROL("roles"),
    CAJA("caja"),
    ACTIVIDAD("actividad"),
    BITACORA("bitacora"),
    NOTIFICACIONES("notificaciones"),
    ENVIAR_NOTIFICACION("notificaciones"),
    CONFIRMAR_NOTIFICACION("notificaciones"),
    MIS_EMPRENDIMIENTOS("perfil"),
    EMPRENDIMIENTO_FORM("perfil"),
    DIRECTORIO_EMPRENDIMIENTOS("dashboard"),
    FAVORITOS_EMPRENDIMIENTOS("dashboard"),
    DETALLE_EMPRENDIMIENTO("perfil"),
    OPINIONES_EMPRENDIMIENTO("perfil"),
    MIS_REPORTES_VECINO("mis_reportes"),
    GESTION_INCIDENCIAS_DIRECTIVA("gestion_incidencias"),
    CREAR_REPORTE("mis_reportes"),
    CONFIGURACION("configuracion"),
    REPORTES("reportes")
}

@Composable
fun AppNavigation(
    context: Context,
    dashboardViewModel: DashboardViewModel,
    movimientosViewModel: MovimientosViewModel,
    loginViewModel: LoginViewModel,
    asambleaIdNotificacion: Int? = null,
    notificacionIdNotificacion: Int? = null,
    darkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {},
    comprobanteConfirmado: ComprobanteInterpretado? = null,
    comprobanteUri: Uri? = null,
    onComprobanteProcesado: () -> Unit = {}
) {
    val sessionManager = remember { SessionManager(context) }
    val categoriasViewModel = remember { CategoriasViewModel(context) }
    val periodoViewModel = remember { PeriodoViewModel(context) }
    val periodosViewModel = remember { PeriodosViewModel(context) }
    val perfilViewModel = remember { PerfilViewModel(context) }

    val asambleasViewModel = remember { AsambleasViewModel(context.applicationContext as Application) }
    val usuarioViewModel = remember {
        UsuarioViewModel(
            UsuarioRepository(ApiClient.usuarioApi(context)),
            RolRepository(ApiClient.rolApi(context))
        )
    }
    val usuarioUiState by usuarioViewModel.uiState.collectAsState()
    val cajaViewModel = remember { CajaViewModel(CajaRepository(ApiClient.cajaApi(context))) }
    val actividadViewModel = remember { ActividadViewModel(context.applicationContext as Application) }
    val rolViewModel = remember { RolViewModel(RolRepository(ApiClient.rolApi(context))) }
    val chatViewModel = remember { ChatViewModel(ChatRepository(ApiClient.chatApi(context))) }
    val notificacionViewModel = remember { NotificacionViewModel(context) }
    val fcmPreferenciaViewModel = remember { FcmPreferenciaViewModel(context) }
    val enviarNotificacionViewModel = remember { EnviarNotificacionViewModel(context) }
    val notificacionesNoLeidas by notificacionViewModel.noLeidas.collectAsState()
    val usuarioActualId by sessionManager.userId.collectAsState(initial = null)
    val usuarioPerfil by perfilViewModel.usuario.collectAsState()
    val guardandoPerfil by perfilViewModel.guardando.collectAsState()
    val subiendoFotoPerfil by perfilViewModel.subiendoFoto.collectAsState()
    val permisosUsuario = usuarioPerfil?.permisos ?: emptyList()

    val emprendimientoApi: EmprendimientoApi = remember { ApiClient.emprendimientoApi(context) }
    val emprendimientoRepository: EmprendimientoRepository = remember { EmprendimientoRepository(emprendimientoApi, context) }
    val emprendimientosListViewModel: EmprendimientosListViewModel = remember { EmprendimientosListViewModel(emprendimientoRepository) }
    val emprendimientoFormViewModel: EmprendimientoFormViewModel = remember { EmprendimientoFormViewModel(context.applicationContext as Application) }
    val emprendimientoViewModel = remember {
        EmprendimientoViewModel(
            EmprendimientoRepository(ApiClient.emprendimientoApi(context), context)
        )
    }

    val incidenciasViewModel = remember { IncidenciasViewModel(context.applicationContext as Application) }
    val listaIncidenciasVecino by incidenciasViewModel.incidenciasVecino.collectAsState()
    val listaIncidenciasDirectiva by incidenciasViewModel.incidenciasDirectiva.collectAsState()
    val cargandoIncidencias by incidenciasViewModel.cargando.collectAsState()

    val configuracionRepository = remember { ConfiguracionRepository(ApiClient.configuracionApi(context)) }
    val configuracionViewModel = remember { ConfiguracionViewModel(configuracionRepository) }
    val configUiState by configuracionViewModel.uiState.collectAsState()

    fun puede(permiso: String): Boolean {
        return permisosUsuario.any { it.trim().equals(permiso, ignoreCase = true) }
    }

    val puedeVerMovimientos = puede("movimientos.index")

    var notificacionTitulo by remember { mutableStateOf("") }
    var notificacionMensaje by remember { mutableStateOf("") }
    var notificacionTipo by remember { mutableStateOf("Aviso") }
    var notificacionDestinatario by remember { mutableStateOf("Todos los vecinos") }
    var notificacionCantidad by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        dashboardViewModel.cargarDashboard()
        perfilViewModel.cargarPerfil()
        notificacionViewModel.cargarNoLeidas()
    }

    val backStack = remember { mutableStateListOf(AppScreen.DASHBOARD) }
    val pantallaActual = backStack.lastOrNull() ?: AppScreen.DASHBOARD
    var mostrarZoeModal by remember { mutableStateOf(false) }
    val zoeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val mascotaPreferencias = remember { MascotaPreferencias(context) }
    val mascotaEstado by MascotaControlador.estado.collectAsState()
    val eventoMascota by MascotaControlador.eventoUi.collectAsState()
    val mascotaVisible by mascotaPreferencias.visible.collectAsState(initial = true)
    var mascotaX by remember { mutableStateOf(0f) }
    var mascotaY by remember { mutableStateOf(0f) }
    var emprendimientoIdSeleccionado by remember { mutableStateOf<Int?>(null) }
    val mascotaScope = rememberCoroutineScope()
// Instancia del ViewModel para Reportes
    val reportesViewModel = remember {
        ReportesViewModel(ApiClient.reportesApi(context))
    }
    LaunchedEffect(Unit) {
        mascotaPreferencias.posicion.collect { posicion ->
            mascotaX = posicion.first
            mascotaY = posicion.second
        }
    }

    fun navegarA(pantalla: AppScreen, limpiarPila: Boolean = false) {
        if (limpiarPila) {
            backStack.clear()
            backStack.add(pantalla)
        } else if (pantallaActual != pantalla) {
            backStack.add(pantalla)
        }
    }

    fun retroceder(): Boolean {
        return if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            true
        } else {
            false
        }
    }

    BackHandler(enabled = backStack.size > 1) {
        retroceder()
    }

    var anioSeleccionado by remember { mutableStateOf(2026) }
    var periodoIdSeleccionado by remember { mutableStateOf(0) }
    var movimientoSeleccionado by remember { mutableStateOf<Movimiento?>(null) }
    var asambleaSeleccionada by remember { mutableStateOf<Asamblea?>(null) }
    var asambleaConvocatoria by remember { mutableStateOf<Asamblea?>(null) }
    var mostrarAvisoAsamblea by remember { mutableStateOf(false) }
    var mensajeAvisoAsamblea by remember { mutableStateOf("No hay asambleas programadas por el momento.") }

    if (mostrarAvisoAsamblea) {
        AlertDialog(
            onDismissRequest = { mostrarAvisoAsamblea = false },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )
            },
            title = {
                Text(
                    text = "Asambleas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = mensajeAvisoAsamblea,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarAvisoAsamblea = false },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Aceptar", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    fun abrirAsamblea() {
        if (puede("asambleas.index")) {
            asambleaSeleccionada = null
            navegarA(AppScreen.ASAMBLEAS)
        } else {
            asambleasViewModel.obtenerProximaAsamblea { asamblea ->
                if (asamblea != null) {
                    asambleaConvocatoria = asamblea
                    navegarA(AppScreen.CONVOCATORIA_ASAMBLEA)
                } else {
                    asambleasViewModel.obtenerAvisoSinAsamblea { mensaje ->
                        mensajeAvisoAsamblea = mensaje ?: "No hay asambleas programadas por el momento."
                        mostrarAvisoAsamblea = true
                    }
                }
            }
        }
    }
    fun abrirPeriodos() {
        if (puede("periodos.index")) {
            navegarA(AppScreen.PERIODOS)
        }
    }

    var comprobanteParaFormulario by remember { mutableStateOf<ComprobanteInterpretado?>(null) }

    LaunchedEffect(asambleaIdNotificacion) {
        asambleaIdNotificacion?.let { id ->
            asambleasViewModel.obtenerAsambleaPublica(id) { asamblea ->
                if (asamblea != null) {
                    asambleaConvocatoria = asamblea
                    backStack.clear()
                    backStack.add(AppScreen.DASHBOARD)
                    backStack.add(AppScreen.CONVOCATORIA_ASAMBLEA)
                } else {
                    mostrarAvisoAsamblea = true
                }
            }
        }
    }

    LaunchedEffect(notificacionIdNotificacion) {
        notificacionIdNotificacion?.let { id ->
            notificacionViewModel.marcarComoLeida(id)
        }
    }

    LaunchedEffect(pantallaActual, puedeVerMovimientos) {
        if (!puedeVerMovimientos &&
            (
                    pantallaActual == AppScreen.MOVIMIENTOS ||
                            pantallaActual == AppScreen.NUEVO_MOVIMIENTO ||
                            pantallaActual == AppScreen.NUEVO_INGRESO ||
                            pantallaActual == AppScreen.NUEVO_EGRESO
                    )
        ) {
            backStack.clear()
            backStack.add(AppScreen.DASHBOARD)
        }
    }

    LaunchedEffect(comprobanteConfirmado) {
        comprobanteConfirmado?.let { resultado ->
            comprobanteParaFormulario = resultado
            when (resultado.tipo) {
                TipoMovimientoDetectado.INGRESO -> navegarA(AppScreen.NUEVO_INGRESO)
                TipoMovimientoDetectado.EGRESO -> navegarA(AppScreen.NUEVO_EGRESO)
                TipoMovimientoDetectado.INDETERMINADO -> {}
            }
            onComprobanteProcesado()
        }
    }

    val categorias by categoriasViewModel.categorias.collectAsState()
    val cargandoCategorias by categoriasViewModel.cargando.collectAsState()
    val periodo by periodoViewModel.periodo.collectAsState()
    val cargandoPeriodo by periodoViewModel.cargando.collectAsState()
    val guardandoMovimiento by movimientosViewModel.guardando.collectAsState()
    val mensajeMovimiento by movimientosViewModel.mensaje.collectAsState()
    val periodoDetalle by periodosViewModel.periodoDetalle.collectAsState()
    val asambleasUiState by asambleasViewModel.uiState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentScreen = pantallaActual.drawerRoute,
                rol = usuarioPerfil?.rol,
                permisos = usuarioPerfil?.permisos ?: emptyList(),
                noLeidas = notificacionesNoLeidas,
                darkTheme = darkTheme,
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> navegarA(AppScreen.DASHBOARD, limpiarPila = true)
                        "movimientos" -> if (puedeVerMovimientos) navegarA(AppScreen.MOVIMIENTOS)
                        "chat" -> if (puede("chat.index")) navegarA(AppScreen.CHAT)
                        "asambleas" -> if (puede("asambleas.index")) {
                            asambleaSeleccionada = null
                            navegarA(AppScreen.ASAMBLEAS)
                        }
                        "periodos" -> if (puede("periodos.index")) navegarA(AppScreen.PERIODOS)
                        "sigi" -> navegarA(AppScreen.SIGI)
                        "perfil" -> {
                            perfilViewModel.cargarPerfil()
                            navegarA(AppScreen.PERFIL)
                        }
                        "usuarios" -> navegarA(AppScreen.USUARIOS)
                        "roles" -> {
                            rolViewModel.cargarRoles()
                            navegarA(AppScreen.ROLES)
                        }
                        "caja" -> navegarA(AppScreen.CAJA)
                        "notificaciones" -> if (puede("notificaciones.index")) navegarA(AppScreen.NOTIFICACIONES)
                        "mis_reportes" -> navegarA(AppScreen.MIS_REPORTES_VECINO)
                        "gestion_incidencias" -> navegarA(AppScreen.GESTION_INCIDENCIAS_DIRECTIVA)
                        "bitacora" -> if (puede("bitacora.index")) navegarA(AppScreen.BITACORA)
                        "actividad" -> {
                            actividadViewModel.cargarActividades()
                            navegarA(AppScreen.ACTIVIDAD)
                        }
                        "configuracion" -> {
                            if (puede("configuracion.index") || usuarioPerfil?.rol?.equals("administrador", true) == true) {
                                navegarA(AppScreen.CONFIGURACION)
                            }
                        }
                        "reportes" -> {
                            if (puede("reportes.index")) {
                                navegarA(AppScreen.REPORTES)
                            }
                        }
                    }
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    loginViewModel.cerrarSesion()
                }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = pantallaActual,
                transitionSpec = {
                    fadeIn(animationSpec = tween(140)) togetherWith fadeOut(animationSpec = tween(140))
                },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    AppScreen.DASHBOARD -> {
                        DashboardScreen(
                            context = context,
                            dashboardViewModel = dashboardViewModel,
                            movimientosViewModel = movimientosViewModel,
                            notificacionesNoLeidas = notificacionesNoLeidas,
                            onNotificacionesClick = {
                                if (puede("notificaciones.index")) {
                                    navegarA(AppScreen.NOTIFICACIONES)
                                }
                            },
                            onMovimientosClick = {
                                if (puedeVerMovimientos) {
                                    navegarA(AppScreen.MOVIMIENTOS)
                                }
                            },
                            onAsambleasClick = { abrirAsamblea() },
                            onEmprendimientosClick = {
                                navegarA(AppScreen.DIRECTORIO_EMPRENDIMIENTOS)
                            },
                            onPeriodosClick = {
                                if (puede("periodos.index")) {
                                    navegarA(AppScreen.PERIODOS)
                                }
                            },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            },
                            onSigiClick = { mostrarZoeModal = true },
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            darkTheme = darkTheme,
                            onThemeToggle = onThemeToggle
                        )
                    }

                    AppScreen.SIGI -> {
                        SigiScreen(
                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onMovimientosClick = {
                                if (puedeVerMovimientos) {
                                    navegarA(AppScreen.MOVIMIENTOS)
                                }
                            },
                            onAsambleasClick = { abrirAsamblea() },
                            onMasClick = { navegarA(AppScreen.PERFIL) },
                            onBackClick = { retroceder() },
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }

                    AppScreen.CHAT -> {
                        ChatVecinalScreen(
                            viewModel = chatViewModel,
                            usuarioActualId = usuarioActualId ?: 0,
                            onBackClick = { retroceder() }
                        )
                    }

                    AppScreen.MOVIMIENTOS -> {
                        MovimientosScreen(
                            movimientosViewModel = movimientosViewModel,
                            periodoViewModel = periodoViewModel,
                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onAsambleasClick = { abrirAsamblea() },
                            onEmprendimientosClick = { navegarA(AppScreen.DIRECTORIO_EMPRENDIMIENTOS) },
                            onPeriodosClick = { abrirPeriodos() },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            },
                            // 👇 Ahora van directo a las pantallas correspondientes
                            onNuevoIngresoClick = { navegarA(AppScreen.NUEVO_INGRESO) },
                            onNuevoEgresoClick = { navegarA(AppScreen.NUEVO_EGRESO) },
                            onMovimientoClick = { mov ->
                                movimientoSeleccionado = mov
                                navegarA(AppScreen.DETALLE_MOVIMIENTO)
                            },
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            puedeCerrarPeriodo = puede("periodos.cerrar"),
                            onCerrarPeriodoClick = {
                                periodo?.let { periodoActual ->
                                    periodoViewModel.cerrarPeriodo(periodoActual.id) { exito, _ ->
                                        if (exito) {
                                            periodoViewModel.cargarPeriodoAbierto()
                                            movimientosViewModel.cargarMovimientos()
                                        }
                                    }
                                }
                            }
                        )
                    }

                    AppScreen.DETALLE_MOVIMIENTO -> {
                        movimientoSeleccionado?.let { mov ->
                            DetalleMovimientoScreen(
                                movimiento = mov,
                                periodoNombre = mov.periodo?.nombre_completo
                                    ?: periodoDetalle?.nombre_completo
                                    ?: periodo?.nombre,
                                periodoEstado = mov.periodo?.estado
                                    ?: periodoDetalle?.estado
                                    ?: periodo?.estado,
                                ingresosPeriodo = mov.periodo?.total_ingresos
                                    ?: periodoDetalle?.total_ingresos,
                                egresosPeriodo = mov.periodo?.total_egresos
                                    ?: periodoDetalle?.total_egresos,
                                saldoDisponiblePeriodo = periodoDetalle?.saldo_disponible,
                                saldoCajaPeriodo = periodoDetalle?.saldo_caja,
                                onBackClick = { retroceder() },
                                onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                                onAsambleasClick = { abrirAsamblea() },
                                onEmprendimientosClick = { navegarA(AppScreen.DIRECTORIO_EMPRENDIMIENTOS) },
                                onPeriodosClick = { navegarA(AppScreen.PERIODOS) },
                                onMiCuentaClick = {
                                    perfilViewModel.cargarPerfil()
                                    navegarA(AppScreen.PERFIL)
                                }
                            )
                        } ?: run {
                            retroceder()
                        }
                    }

                    AppScreen.NUEVO_MOVIMIENTO -> {
                        NuevoMovimientoScreen(
                            onIngresoClick = { navegarA(AppScreen.NUEVO_INGRESO) },
                            onEgresoClick = { navegarA(AppScreen.NUEVO_EGRESO) },
                            onCerrarClick = { retroceder() }
                        )
                    }

                    AppScreen.NUEVO_INGRESO -> {
                        NuevoIngresoScreen(
                            categorias = categorias,
                            cargandoCategorias = cargandoCategorias,
                            periodoNombre = periodo?.nombre,
                            periodoAnio = periodo?.anio,
                            cargandoPeriodo = cargandoPeriodo,
                            guardando = guardandoMovimiento,
                            mensaje = mensajeMovimiento,
                            fechaInicial = comprobanteParaFormulario?.fecha?.let { convertirFechaComprobante(it) },
                            personaInicial = comprobanteParaFormulario?.persona,
                            formaPagoInicial = comprobanteParaFormulario?.medioPago,
                            montoInicial = comprobanteParaFormulario?.monto,
                            referenciaInicial = comprobanteParaFormulario?.numeroOperacion,
                            comprobanteUri = comprobanteUri,
                            onCerrarClick = { retroceder() },
                            onGuardar = { fecha, categoriaId, concepto, persona, formaPago, monto, referencia, observaciones, comprobanteUriParam ->
                                movimientosViewModel.crearMovimiento(
                                    fecha = fecha,
                                    categoriaId = categoriaId,
                                    concepto = concepto,
                                    persona = persona,
                                    formaPago = formaPago,
                                    monto = monto,
                                    referencia = referencia,
                                    observaciones = observaciones,
                                    comprobanteUri = comprobanteUriParam
                                ) { exito ->
                                    if (exito) {
                                        dashboardViewModel.cargarDashboard()
                                        movimientosViewModel.cargarMovimientos()
                                        retroceder()
                                    }
                                }
                            }
                        )
                    }

                    AppScreen.NUEVO_EGRESO -> {
                        NuevoEgresoScreen(
                            categorias = categorias,
                            cargandoCategorias = cargandoCategorias,
                            periodoNombre = periodo?.nombre,
                            periodoAnio = periodo?.anio,
                            cargandoPeriodo = cargandoPeriodo,
                            guardando = guardandoMovimiento,
                            mensaje = mensajeMovimiento,
                            fechaInicial = comprobanteParaFormulario?.fecha?.let { convertirFechaComprobante(it) },
                            personaInicial = comprobanteParaFormulario?.persona,
                            formaPagoInicial = comprobanteParaFormulario?.medioPago,
                            montoInicial = comprobanteParaFormulario?.monto,
                            referenciaInicial = comprobanteParaFormulario?.numeroOperacion,
                            comprobanteUri = comprobanteUri,
                            onCerrarClick = { retroceder() },
                            onGuardar = { fecha, categoriaId, concepto, persona, formaPago, monto, referencia, observaciones, comprobanteUriParam ->
                                movimientosViewModel.crearMovimiento(
                                    fecha = fecha,
                                    categoriaId = categoriaId,
                                    concepto = concepto,
                                    persona = persona,
                                    formaPago = formaPago,
                                    monto = monto,
                                    referencia = referencia,
                                    observaciones = observaciones,
                                    comprobanteUri = comprobanteUriParam
                                ) { exito ->
                                    if (exito) {
                                        dashboardViewModel.cargarDashboard()
                                        movimientosViewModel.cargarMovimientos()
                                        retroceder()
                                    }
                                }
                            }
                        )
                    }

                    AppScreen.ASAMBLEAS -> {
                        AsambleasScreen(
                            viewModel = asambleasViewModel,
                            onAsambleaClick = { asamblea ->
                                asambleaSeleccionada = asamblea
                                navegarA(AppScreen.DETALLE_ASAMBLEA)
                            },
                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onAsambleasClick = { },
                            onEmprendimientosClick = { navegarA(AppScreen.DIRECTORIO_EMPRENDIMIENTOS) },
                            onPeriodosClick = { navegarA(AppScreen.PERIODOS) },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            },
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onCrearClick = {
                                asambleaSeleccionada = null
                                navegarA(AppScreen.NUEVA_ASAMBLEA)
                            }
                        )
                    }
                    AppScreen.DETALLE_ASAMBLEA -> {
                        asambleaSeleccionada?.let { asamblea ->
                            DetalleAsambleaScreen(
                                asamblea = asamblea,
                                onBackClick = { retroceder() },
                                onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                                onMovimientosClick = { if (puedeVerMovimientos) navegarA(AppScreen.MOVIMIENTOS) },
                                onAsambleasClick = { abrirAsamblea() },
                                onPeriodosClick = { abrirPeriodos() },
                                onOpenDrawer = { scope.launch { drawerState.open() } },
                                onEditarClick = { navegarA(AppScreen.EDITAR_ASAMBLEA) }
                            )
                        } ?: run {
                            retroceder()
                        }
                    }

                    AppScreen.CONVOCATORIA_ASAMBLEA -> {
                        asambleaConvocatoria?.let { asamblea ->
                            ConvocatoriaAsambleaScreen(
                                asamblea = asamblea,
                                onBackClick = { retroceder() }
                            )
                        } ?: run {
                            retroceder()
                        }
                    }

                    AppScreen.NUEVA_ASAMBLEA -> {
                        CrearAsambleaScreen(
                            creando = asambleasUiState.creando,
                            error = asambleasUiState.error,
                            onBackClick = { retroceder() },
                            onGuardar = { request ->
                                asambleasViewModel.crearAsamblea(request = request) { exito ->
                                    if (exito) {
                                        asambleasViewModel.cargarAsambleas()
                                        retroceder()
                                    }
                                }
                            }
                        )
                    }

                    AppScreen.EDITAR_ASAMBLEA -> {
                        asambleaSeleccionada?.let { asamblea ->
                            CrearAsambleaScreen(
                                asamblea = asamblea,
                                creando = asambleasUiState.actualizando,
                                error = asambleasUiState.error,
                                onBackClick = { retroceder() },
                                onGuardar = { request ->
                                    asambleasViewModel.actualizarAsamblea(
                                        id = asamblea.id,
                                        request = request
                                    ) { exito ->
                                        if (exito) {
                                            asambleasViewModel.cargarAsambleas()
                                            retroceder()
                                        }
                                    }
                                }
                            )
                        } ?: run {
                            retroceder()
                        }
                    }

                    AppScreen.PERIODOS -> {
                        PeriodosScreen(
                            periodosViewModel = periodosViewModel,
                            onBackClick = { retroceder() },
                            onAnioClick = { anio ->
                                anioSeleccionado = anio
                                navegarA(AppScreen.PERIODOS_ANIO)
                            },
                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onAsambleasClick = { abrirAsamblea() },
                            onEmprendimientosClick = { navegarA(AppScreen.DIRECTORIO_EMPRENDIMIENTOS) },
                            onPeriodosClick = { },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            },
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }

                    AppScreen.PERIODOS_ANIO -> {
                        PeriodosAnioScreen(
                            periodosViewModel = periodosViewModel,
                            anio = anioSeleccionado,
                            onBackClick = { retroceder() },
                            onPeriodoClick = { periodoId ->
                                periodoIdSeleccionado = periodoId
                                navegarA(AppScreen.PERIODO_DETALLE)
                            },
                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onAsambleasClick = { abrirAsamblea() },
                            onEmprendimientosClick = { navegarA(AppScreen.DIRECTORIO_EMPRENDIMIENTOS) },
                            onPeriodosClick = { navegarA(AppScreen.PERIODOS) },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            }
                        )
                    }

                    AppScreen.PERIODO_DETALLE -> {
                        PeriodoDetalleScreen(
                            periodosViewModel = periodosViewModel,
                            periodoId = periodoIdSeleccionado,
                            onBackClick = { retroceder() },
                            onMovimientoClick = { mov ->
                                movimientoSeleccionado = mov
                                navegarA(AppScreen.DETALLE_MOVIMIENTO)
                            },
                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onAsambleasClick = { abrirAsamblea() },
                            onEmprendimientosClick = { navegarA(AppScreen.DIRECTORIO_EMPRENDIMIENTOS) },
                            onPeriodosClick = { navegarA(AppScreen.PERIODOS) },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            },

                        )
                    }

                    AppScreen.PERFIL -> {
                        PerfilScreen(
                            onBackClick = { retroceder() },
                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onAsambleasClick = { abrirAsamblea() },
                            onEmprendimientosClick = { navegarA(AppScreen.DIRECTORIO_EMPRENDIMIENTOS) },
                            onPeriodosClick = {
                                if (puede("periodos.index")) {
                                    navegarA(AppScreen.PERIODOS)
                                }
                            },
                            onMiCuentaClick = { },
                            onMisEmprendimientosClick = { navegarA(AppScreen.MIS_EMPRENDIMIENTOS) },
                            nombre = usuarioPerfil?.name ?: "Usuario",
                            seudonimo = usuarioPerfil?.seudonimo,
                            email = usuarioPerfil?.email ?: "",
                            telefono = usuarioPerfil?.telefono,
                            dni = usuarioPerfil?.dni,
                            direccion = usuarioPerfil?.direccion,
                            rol = usuarioPerfil?.rol,
                            foto = usuarioPerfil?.foto,
                            metodoAcceso = usuarioPerfil?.metodo_acceso,
                            guardando = guardandoPerfil,
                            subiendoFoto = subiendoFotoPerfil,
                            cargandoPassword = usuarioUiState.cambiandoPassword,
                            onActualizarPassword = { actual, nueva, confirmar, onExito ->
                                usuarioViewModel.actualizarPassword(
                                    currentPassword = actual,
                                    newPassword = nueva,
                                    newPasswordConfirmation = confirmar,
                                    onExito = onExito
                                )
                            },
                            onGuardarPerfil = { seudonimo ->
                                perfilViewModel.actualizarPerfil(
                                    seudonimo = seudonimo,
                                    telefono = usuarioPerfil?.telefono,
                                    dni = usuarioPerfil?.dni,
                                    direccion = usuarioPerfil?.direccion
                                )
                            },
                            onSeleccionarFoto = { uri: Uri ->
                                perfilViewModel.actualizarFoto(uri)
                            }
                        )
                    }

                    AppScreen.USUARIOS -> {
                        UsuariosScreen(
                            viewModel = usuarioViewModel,
                            onBackClick = { retroceder() }
                        )
                    }

                    AppScreen.ROLES -> {
                        RolesListScreen(
                            viewModel = rolViewModel,
                            onBackClick = { retroceder() },
                            onNuevoRol = {
                                rolViewModel.cargarPermisosParaNuevoRol()
                                navegarA(AppScreen.NUEVO_ROL)
                            },
                            onEditarRol = { rolSeleccionado ->
                                rolViewModel.cargarDetalleRol(rolSeleccionado.id)
                                navegarA(AppScreen.EDITAR_ROL)
                            },
                            onEliminarRol = { }
                        )
                    }

                    AppScreen.EDITAR_ROL -> {
                        val rolUiState by rolViewModel.uiState.collectAsState()
                        val rol = rolUiState.rolEnEdicion

                        when {
                            rolUiState.cargando -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            rolUiState.error != null -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = rolUiState.error ?: "Error al cargar detalle del rol",
                                            color = MaterialTheme.colorScheme.error,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { retroceder() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        ) {
                                            Text("Volver a Roles")
                                        }
                                    }
                                }
                            }

                            rol != null -> {
                                EditarRolScreen(
                                    rol = rol,
                                    guardando = rolUiState.guardando,
                                    onBackClick = { retroceder() },
                                    onGuardar = { nuevoNombre, permisosSeleccionados ->
                                        rolViewModel.guardarRol(
                                            rol.id,
                                            nuevoNombre,
                                            permisosSeleccionados
                                        ) { exito ->
                                            if (exito) retroceder()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    AppScreen.NUEVO_ROL -> {
                        val rolUiState by rolViewModel.uiState.collectAsState()

                        NuevoRolScreen(
                            permisosAgrupados = rolUiState.permisosDisponibles,
                            cargandoPermisos = rolUiState.cargandoPermisos,
                            guardando = rolUiState.guardando,
                            error = rolUiState.error,
                            onBackClick = { retroceder() },
                            onReintentar = {
                                rolViewModel.cargarPermisosParaNuevoRol()
                            },
                            onCrearRol = { nombre, permisos ->
                                rolViewModel.crearRol(nombre, permisos) { exito ->
                                    if (exito) {
                                        retroceder()
                                    }
                                }
                            }
                        )
                    }

                    AppScreen.CAJA -> {
                        CajaScreen(
                            viewModel = cajaViewModel,
                            onBackClick = { retroceder() },
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            darkTheme = darkTheme
                        )
                    }

                    AppScreen.BITACORA -> {
                        BitacoraScreen(
                            onBackClick = { retroceder() }
                        )
                    }

                    AppScreen.ACTIVIDAD -> {
                        ActividadScreen(
                            viewModel = actividadViewModel,
                            onBack = { retroceder() }
                        )
                    }

                    AppScreen.NOTIFICACIONES -> {
                        NotificacionesScreen(
                            viewModel = notificacionViewModel,
                            fcmPreferenciaViewModel = fcmPreferenciaViewModel,
                            rol = usuarioPerfil?.rol,
                            mascotaVisible = mascotaVisible,
                            onMascotaVisibleChange = { visible ->
                                mascotaScope.launch {
                                    mascotaPreferencias.guardarVisible(visible)
                                }
                            },
                            onNuevaNotificacionClick = {
                                navegarA(AppScreen.ENVIAR_NOTIFICACION)
                            },
                            onNotificacionClick = { notificacion ->
                                when (notificacion.tipo.lowercase()) {
                                    "asamblea" -> {
                                        val asambleaId = notificacion.data?.get("asamblea_id")?.toIntOrNull()
                                        if (asambleaId != null) {
                                            asambleasViewModel.obtenerAsambleaPublica(asambleaId) { asamblea ->
                                                if (asamblea != null) {
                                                    asambleaConvocatoria = asamblea
                                                    navegarA(AppScreen.CONVOCATORIA_ASAMBLEA)
                                                }
                                            }
                                        }
                                    }
                                    "zoe" -> {
                                        val periodoId = notificacion.data?.get("periodo_id")?.toIntOrNull()
                                        if (periodoId != null) {
                                            periodoIdSeleccionado = periodoId
                                            navegarA(AppScreen.PERIODO_DETALLE)
                                        }
                                    }
                                    "ingreso", "egreso" -> {
                                        val movimientoId = notificacion.data?.get("movimiento_id")?.toIntOrNull()
                                        if (movimientoId != null) {
                                            movimientosViewModel.obtenerMovimientoPorId(movimientoId) { movimiento ->
                                                if (movimiento != null) {
                                                    movimientoSeleccionado = movimiento
                                                    navegarA(AppScreen.DETALLE_MOVIMIENTO)
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            onBackClick = { retroceder() }
                        )
                    }

                    AppScreen.ENVIAR_NOTIFICACION -> {
                        EnviarNotificacionScreen(
                            viewModel = enviarNotificacionViewModel,
                            usuarioViewModel = usuarioViewModel,
                            onBackClick = { retroceder() },
                            onEnvioExitoso = { titulo, mensaje, tipo, destinatario, cantidad ->
                                notificacionTitulo = titulo
                                notificacionMensaje = mensaje
                                notificacionTipo = tipo
                                notificacionDestinatario = destinatario
                                notificacionCantidad = cantidad
                                navegarA(AppScreen.CONFIRMAR_NOTIFICACION)
                            }
                        )
                    }

                    AppScreen.CONFIRMAR_NOTIFICACION -> {
                        ConfirmarNotificacionScreen(
                            titulo = notificacionTitulo,
                            mensaje = notificacionMensaje,
                            tipo = notificacionTipo,
                            destinatario = notificacionDestinatario,
                            cantidadDestinatarios = notificacionCantidad,
                            onBackClick = {
                                enviarNotificacionViewModel.limpiarResultado()
                                retroceder()
                            },
                            onConfirmarEnvio = {
                                enviarNotificacionViewModel.limpiarResultado()
                                while (backStack.size > 1 && backStack.last() != AppScreen.NOTIFICACIONES) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                            }
                        )
                    }

                    AppScreen.DIRECTORIO_EMPRENDIMIENTOS -> {
                        EmprendimientosScreen(
                            viewModel = emprendimientoViewModel,
                            onItemClick = { emprendimientoId ->
                                emprendimientoIdSeleccionado = emprendimientoId
                                navegarA(AppScreen.DETALLE_EMPRENDIMIENTO)
                            },
                            onFavoritosClick = {
                                navegarA(AppScreen.FAVORITOS_EMPRENDIMIENTOS)
                            },
                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onAsambleasClick = { abrirAsamblea() },
                            onEmprendimientosClick = { /* Ya estás aquí */ },
                            onPeriodosClick = { abrirPeriodos() },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            },
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }

                    AppScreen.FAVORITOS_EMPRENDIMIENTOS -> {
                        LaunchedEffect(Unit) {
                            emprendimientoViewModel.cargarFavoritos()
                        }

                        EmprendimientosFavoritosScreen(
                            viewModel = emprendimientoViewModel,
                            onBackClick = { retroceder() },
                            onItemClick = { id ->
                                emprendimientoIdSeleccionado = id
                                navegarA(AppScreen.DETALLE_EMPRENDIMIENTO)
                            },
                            onContactarWhatsapp = { telefono ->
                                val mensaje = "Hola, vi tu negocio en mis favoritos de SIGEFIV y quisiera hacer una consulta."
                                val mensajeCodificado = URLEncoder.encode(mensaje, "UTF-8")
                                val telefonoLimpio = telefono.replace(Regex("[^0-9]"), "")
                                val telefonoConPais = if (telefonoLimpio.length == 9) "51$telefonoLimpio" else telefonoLimpio
                                val url = "https://api.whatsapp.com/send?phone=$telefonoConPais&text=$mensajeCodificado"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            onLlamar = { telefono ->
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefono"))
                                context.startActivity(intent)
                            }
                        )
                    }

                    AppScreen.MIS_EMPRENDIMIENTOS -> {
                        LaunchedEffect(Unit) {
                            emprendimientosListViewModel.cargarMisEmprendimientos()
                        }

                        val state by emprendimientosListViewModel.state.collectAsState()

                        EmprendimientosListScreen(
                            state = state,
                            onBackClick = { retroceder() },
                            onNuevoEmprendimientoClick = {
                                emprendimientoFormViewModel.resetForm()
                                navegarA(AppScreen.EMPRENDIMIENTO_FORM)
                            },
                            onEditarEmprendimientoClick = { id ->
                                emprendimientoFormViewModel.cargarEmprendimiento(id)
                                navegarA(AppScreen.EMPRENDIMIENTO_FORM)
                            },
                            onItemClick = { id ->
                                emprendimientoIdSeleccionado = id.toInt()
                                navegarA(AppScreen.DETALLE_EMPRENDIMIENTO)
                            },
                            onCambiarEstadoClick = { id, nuevoEstado ->
                                emprendimientosListViewModel.cambiarEstado(id, nuevoEstado)
                            },
                            onEliminarEmprendimientoClick = { id ->
                                emprendimientosListViewModel.eliminarEmprendimiento(id)
                            }
                        )
                    }

                    AppScreen.EMPRENDIMIENTO_FORM -> {
                        val state by emprendimientoFormViewModel.state.collectAsState()

                        val imagePickerLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.GetContent()
                        ) { uri: Uri? ->
                            uri?.let { selectedUri ->
                                emprendimientoFormViewModel.onAction(
                                    EmprendimientoFormAction.AddPhoto(selectedUri.toString())
                                )
                            }
                        }

                        EmprendimientoFormScreen(
                            state = state,
                            onAction = { action -> emprendimientoFormViewModel.onAction(action) },
                            onPickImageClick = {
                                imagePickerLauncher.launch("image/*")
                            },
                            onNavigateBack = { retroceder() }
                        )
                    }

                    AppScreen.DETALLE_EMPRENDIMIENTO -> {
                        val listaPublica = emprendimientoViewModel.emprendimientos
                        val statePersonal by emprendimientosListViewModel.state.collectAsState()

                        val negocioPublico = listaPublica.find { it.id == emprendimientoIdSeleccionado }
                        val negocioPersonal = statePersonal.emprendimientos.find { it.id.toInt() == emprendimientoIdSeleccionado }

                        LaunchedEffect(emprendimientoIdSeleccionado) {
                            emprendimientoIdSeleccionado?.let {
                                emprendimientoViewModel.cargarOpiniones(it)
                            }
                        }

                        val opinionesData = emprendimientoViewModel.opinionesState

                        val modeloDetalle = when {
                            negocioPublico != null -> {
                                EmprendimientoModel(
                                    id = negocioPublico.id,
                                    nombre = negocioPublico.nombre,
                                    descripcion = negocioPublico.descripcion,
                                    categoria = negocioPublico.categoria,
                                    telefono = negocioPublico.telefono,
                                    showWhatsapp = negocioPublico.showWhatsapp,
                                    modalidad = negocioPublico.modalidad,
                                    zonaAtencion = negocioPublico.zonaAtencion,
                                    horario = negocioPublico.horario,
                                    esFavorito = negocioPublico.esFavorito,
                                    photos = negocioPublico.photos
                                )
                            }
                            negocioPersonal != null -> {
                                EmprendimientoModel(
                                    id = negocioPersonal.id.toInt(),
                                    nombre = negocioPersonal.nombre,
                                    descripcion = negocioPersonal.description ?: "",
                                    categoria = "General",
                                    telefono = negocioPersonal.phone,
                                    showWhatsapp = negocioPersonal.showWhatsapp,
                                    modalidad = negocioPersonal.modality,
                                    zonaAtencion = negocioPersonal.attentionZone,
                                    horario = negocioPersonal.schedule,
                                    esFavorito = false,
                                    photos = emptyList()
                                )
                            }
                            else -> null
                        }

                        if (modeloDetalle != null) {
                            DetalleEmprendimientoScreen(
                                emprendimiento = modeloDetalle,
                                promedio = opinionesData?.promedio ?: 0.0f,
                                totalOpiniones = opinionesData?.total ?: 0,
                                onBackClick = { retroceder() },
                                onOpenOpiniones = {
                                    navegarA(AppScreen.OPINIONES_EMPRENDIMIENTO)
                                },
                                onContactarWhatsapp = {
                                    val mensaje = "Hola, vi tu negocio '${modeloDetalle.nombre}' en SIGEFIV y quisiera consultar más información."
                                    val mensajeCodificado = URLEncoder.encode(mensaje, "UTF-8")
                                    val telefonoLimpio = modeloDetalle.telefono.replace(Regex("[^0-9]"), "")
                                    val telefonoConPais = if (telefonoLimpio.length == 9) "51$telefonoLimpio" else telefonoLimpio
                                    val url = "https://api.whatsapp.com/send?phone=$telefonoConPais&text=$mensajeCodificado"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                },
                                onLlamar = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${modeloDetalle.telefono}"))
                                    context.startActivity(intent)
                                }
                            )
                        } else {
                            LaunchedEffect(Unit) {
                                retroceder()
                            }
                        }
                    }

                    AppScreen.OPINIONES_EMPRENDIMIENTO -> {
                        val listaPublica = emprendimientoViewModel.emprendimientos
                        val statePersonal by emprendimientosListViewModel.state.collectAsState()

                        val negocioPublico = listaPublica.find { it.id == emprendimientoIdSeleccionado }
                        val negocioPersonal = statePersonal.emprendimientos.find { it.id.toInt() == emprendimientoIdSeleccionado }
                        val nombreNegocio = negocioPublico?.nombre ?: negocioPersonal?.nombre ?: "Emprendimiento"

                        val opinionesData = emprendimientoViewModel.opinionesState
                        val cargandoOps = emprendimientoViewModel.cargandoOpiniones

                        LaunchedEffect(emprendimientoIdSeleccionado) {
                            emprendimientoIdSeleccionado?.let {
                                emprendimientoViewModel.cargarOpiniones(it)
                            }
                        }

                        val opinionesList = opinionesData?.opiniones ?: emptyList()
                        val totalOps = opinionesData?.total ?: 1

                        val distribucionReal = (5 downTo 1).associateWith { estrella ->
                            if (totalOps > 0) {
                                val count = opinionesList.count { it.calificacion == estrella }
                                ((count.toFloat() / totalOps) * 100).toInt()
                            } else {
                                0
                            }
                        }

                        OpinionesScreen(
                            nombreNegocio = nombreNegocio,
                            promedioEstrellas = opinionesData?.promedio ?: 0.0f,
                            totalOpiniones = opinionesData?.total ?: 0,
                            distribucionPorcentaje = distribucionReal,
                            opiniones = opinionesList,
                            userOpinion = opinionesData?.userOpinion,
                            cargando = cargandoOps,
                            onBackClick = { retroceder() },
                            onEnviarOpinion = { calificacion, comentario ->
                                emprendimientoIdSeleccionado?.let { id ->
                                    emprendimientoViewModel.enviarOpinion(id, calificacion, comentario) {}
                                }
                            }
                        )
                    }

                    AppScreen.MIS_REPORTES_VECINO -> {
                        LaunchedEffect(Unit) {
                            incidenciasViewModel.cargarMisReportesParaVecino()
                        }

                        MisReportesVecinoScreen(
                            listaIncidencias = listaIncidenciasVecino,
                            isRefreshing = cargandoIncidencias,
                            onRefresh = { incidenciasViewModel.cargarMisReportesParaVecino() },
                            onNuevoReporteClick = { navegarA(AppScreen.CREAR_REPORTE) },
                            onBackClick = { retroceder() }
                        )
                    }

                    AppScreen.CREAR_REPORTE -> {
                        var imagenSeleccionadaUri by remember { mutableStateOf<Uri?>(null) }
                        var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
                        val authority = "${context.packageName}.fileprovider"

                        val tomarFotoLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.TakePicture()
                        ) { exito ->
                            if (exito) {
                                tempPhotoUri?.let { uri -> imagenSeleccionadaUri = uri }
                            }
                        }

                        val galeriaLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.GetContent()
                        ) { uri ->
                            uri?.let { imagenSeleccionadaUri = it }
                        }

                        CrearReporteScreen(
                            onEnviarReporte = { titulo, categoria, mensaje, uri ->
                                incidenciasViewModel.crearIncidencia(
                                    titulo = titulo,
                                    categoria = categoria,
                                    mensaje = mensaje,
                                    imagenUri = uri,
                                    onResultado = { exito, _ ->
                                        if (exito) retroceder()
                                    }
                                )
                            },
                            onTomarFoto = {
                                val archivoTemp = File(context.cacheDir, "incidencia_camara_${System.currentTimeMillis()}.jpg")
                                val uriTemp = FileProvider.getUriForFile(context, authority, archivoTemp)
                                tempPhotoUri = uriTemp
                                tomarFotoLauncher.launch(uriTemp)
                            },
                            onElegirGaleria = {
                                galeriaLauncher.launch("image/*")
                            },
                            imagenUri = imagenSeleccionadaUri,
                            onEliminarImagen = { imagenSeleccionadaUri = null },
                            cargando = cargandoIncidencias,
                            onBackClick = { retroceder() }
                        )
                    }

                    AppScreen.GESTION_INCIDENCIAS_DIRECTIVA -> {
                        LaunchedEffect(Unit) {
                            incidenciasViewModel.cargarIncidenciasDirectiva()
                        }

                        GestionIncidenciasDirectivaScreen(
                            listaIncidencias = listaIncidenciasDirectiva,
                            isRefreshing = cargandoIncidencias,
                            onCambiarEstado = { idIncidencia, nuevoEstado ->
                                incidenciasViewModel.cambiarEstadoIncidencia(idIncidencia, nuevoEstado, esVistaDirectiva = true)
                            },
                            onBackClick = { retroceder() }
                        )
                    }

                    AppScreen.CONFIGURACION -> {
                        LaunchedEffect(Unit) {
                            configuracionViewModel.cargarConfiguracion()
                        }

                        ConfiguracionScreen(
                            configuracionInicial = configUiState.configuracion,
                            cargando = configUiState.cargando || configUiState.guardando,
                            onGuardarConfiguracion = { configActualizada ->
                                configuracionViewModel.guardarConfiguracion(configActualizada)
                            },
                            onBackClick = { retroceder() }
                        )
                    }
                    AppScreen.REPORTES -> {
                        ReportesScreen(
                            viewModel = reportesViewModel,
                            darkTheme = darkTheme,
                            onOpenDrawer = { scope.launch { drawerState.open() } },

                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onAsambleasClick = { abrirAsamblea() },
                            onEmprendimientosClick = { navegarA(AppScreen.DIRECTORIO_EMPRENDIMIENTOS) },
                            onPeriodosClick = { abrirPeriodos() },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            }
                        )
                    }
                }
            }

            // ============================================================
            // MASCOTA GLOBAL DE ZOE
            // ============================================================
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val mascotaSize = 78.dp
                val margen = 12.dp
                val density = androidx.compose.ui.platform.LocalDensity.current

                val limiteIzquierdo = with(density) {
                    -(maxWidth - mascotaSize - margen * 2).toPx()
                }

                val limiteSuperior = with(density) {
                    -(maxHeight - mascotaSize - margen * 2).toPx()
                }

                if (mascotaVisible) {
                    if (eventoMascota != null) {
                        MascotaBurbujaEvento(
                            evento = eventoMascota,
                            visible = true,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(margen)
                                .zIndex(21f)
                                .offset {
                                    IntOffset(
                                        mascotaX.coerceIn(limiteIzquierdo, 0f).roundToInt(),
                                        (mascotaY - with(density) { mascotaSize.toPx() })
                                            .coerceIn(limiteSuperior, 0f)
                                            .roundToInt()
                                    )
                                }
                        )
                    }

                    MascotaSigefiv(
                        estado = mascotaEstado,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(margen)
                            .zIndex(20f)
                            .offset {
                                IntOffset(
                                    mascotaX.coerceIn(limiteIzquierdo, 0f).roundToInt(),
                                    mascotaY.coerceIn(limiteSuperior, 0f).roundToInt()
                                )
                            },
                        onDrag = { dx, dy ->
                            mascotaX = (mascotaX + dx).coerceIn(limiteIzquierdo, 0f)
                            mascotaY = (mascotaY + dy).coerceIn(limiteSuperior, 0f)
                        },
                        onDragEnd = {
                            mascotaScope.launch {
                                mascotaPreferencias.guardarPosicion(mascotaX, mascotaY)
                            }
                        },
                        onClick = { mostrarZoeModal = true }
                    )
                }
            }
        }

        // ============================================================
        // MODAL DE ZOE INTELIGENTE
        // ============================================================
        if (mostrarZoeModal) {
            ModalBottomSheet(
                onDismissRequest = { mostrarZoeModal = false },
                sheetState = zoeSheetState,
                sheetGesturesEnabled = false,
                containerColor = Color.Transparent, // 👈 Cambiar a Transparent
                tonalElevation = 0.dp,              // 👈 Sin elevación opaca de fondo
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                dragHandle = null
            )  {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.95f)
                ) {
                    SigiScreen(
                        onBackClick = { mostrarZoeModal = false },
                        onOpenDrawer = {}
                    )

                    val mascotaSize = 78.dp
                    val margen = 12.dp
                    val density = androidx.compose.ui.platform.LocalDensity.current

                    val limiteIzquierdo = with(density) {
                        -(maxWidth - mascotaSize - margen * 2).toPx()
                    }

                    val limiteSuperior = with(density) {
                        -(maxHeight - mascotaSize - margen * 2).toPx()
                    }

                    if (mascotaVisible) {
                        if (eventoMascota != null) {
                            MascotaBurbujaEvento(
                                evento = eventoMascota,
                                visible = true,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(margen)
                                    .zIndex(11f)
                                    .offset {
                                        IntOffset(
                                            x = mascotaX.coerceIn(limiteIzquierdo, 0f).roundToInt(),
                                            y = (mascotaY - with(density) { mascotaSize.toPx() })
                                                .coerceIn(limiteSuperior, 0f)
                                                .roundToInt()
                                        )
                                    }
                            )
                        }

                        MascotaSigefiv(
                            estado = mascotaEstado,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(margen)
                                .zIndex(10f)
                                .offset {
                                    IntOffset(
                                        mascotaX.coerceIn(limiteIzquierdo, 0f).roundToInt(),
                                        mascotaY.coerceIn(limiteSuperior, 0f).roundToInt()
                                    )
                                },
                            onDrag = { dx, dy ->
                                mascotaX = (mascotaX + dx).coerceIn(limiteIzquierdo, 0f)
                                mascotaY = (mascotaY + dy).coerceIn(limiteSuperior, 0f)
                            },
                            onDragEnd = {
                                mascotaScope.launch {
                                    mascotaPreferencias.guardarPosicion(mascotaX, mascotaY)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun convertirFechaComprobante(fecha: String): String? {
    val partes = fecha.split("/")
    if (partes.size != 3) return null
    val dia = partes[0]
    val mes = partes[1]
    val anio = partes[2]
    return "$anio-$mes-$dia"
}