@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.navigation

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.Asamblea
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.model.Notificacion
import com.sigefiv.app.data.repository.CajaRepository
import com.sigefiv.app.data.repository.ChatRepository
import com.sigefiv.app.data.repository.RolRepository
import com.sigefiv.app.data.repository.UsuarioRepository
import com.sigefiv.app.screens.asambleas.AsambleasScreen
import com.sigefiv.app.screens.asambleas.ConvocatoriaAsambleaScreen
import com.sigefiv.app.screens.asambleas.CrearAsambleaScreen
import com.sigefiv.app.screens.asambleas.DetalleAsambleaScreen
import com.sigefiv.app.screens.caja.CajaScreen
import com.sigefiv.app.screens.chat.ChatVecinalScreen
import com.sigefiv.app.screens.dashboard.DashboardScreen
import com.sigefiv.app.screens.movimientos.DetalleMovimientoScreen
import com.sigefiv.app.screens.movimientos.MovimientosScreen
import com.sigefiv.app.screens.movimientos.NuevoEgresoScreen
import com.sigefiv.app.screens.movimientos.NuevoIngresoScreen
import com.sigefiv.app.screens.movimientos.NuevoMovimientoScreen
import com.sigefiv.app.screens.actividad.ActividadScreen

import com.sigefiv.app.screens.notificaciones.EnviarNotificacionScreen
import com.sigefiv.app.screens.notificaciones.NotificacionesScreen
import com.sigefiv.app.screens.perfil.PerfilScreen
import com.sigefiv.app.screens.periodos.PeriodoDetalleScreen
import com.sigefiv.app.screens.periodos.PeriodosAnioScreen
import com.sigefiv.app.screens.periodos.PeriodosScreen
import com.sigefiv.app.screens.roles.EditarRolScreen
import com.sigefiv.app.screens.roles.RolesListScreen
import com.sigefiv.app.screens.sigi.SigiScreen
import com.sigefiv.app.screens.usuarios.UsuariosScreen
import com.sigefiv.app.ui.components.AppDrawer
import com.sigefiv.app.viewmodel.AsambleasViewModel
import com.sigefiv.app.viewmodel.CajaViewModel
import com.sigefiv.app.viewmodel.CategoriasViewModel
import com.sigefiv.app.viewmodel.ChatViewModel
import com.sigefiv.app.viewmodel.DashboardViewModel
import com.sigefiv.app.viewmodel.FcmPreferenciaViewModel
import com.sigefiv.app.viewmodel.NotificacionViewModel
import com.sigefiv.app.viewmodel.EnviarNotificacionViewModel
import com.sigefiv.app.viewmodel.LoginViewModel
import com.sigefiv.app.viewmodel.MovimientosViewModel
import com.sigefiv.app.viewmodel.PeriodoViewModel
import com.sigefiv.app.viewmodel.PeriodosViewModel
import com.sigefiv.app.viewmodel.PerfilViewModel
import com.sigefiv.app.viewmodel.RolViewModel
import com.sigefiv.app.viewmodel.UsuarioViewModel
import com.sigefiv.app.viewmodel.ActividadViewModel
import kotlinx.coroutines.launch
import com.sigefiv.app.screens.notificaciones.EnviarNotificacionScreen
import com.sigefiv.app.screens.notificaciones.NotificacionesScreen
import com.sigefiv.app.screens.notificaciones.ConfirmarNotificacionScreen
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.layout.fillMaxHeight


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
    EDITAR_ROL("roles"),
    CAJA("caja"),
    ACTIVIDAD("actividad"),
    NOTIFICACIONES("notificaciones"),
    ENVIAR_NOTIFICACION("notificaciones"),
    CONFIRMAR_NOTIFICACION("notificaciones")
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
    onThemeToggle: () -> Unit = {}
) {
    val sessionManager = remember { SessionManager(context) }
    val categoriasViewModel = remember { CategoriasViewModel(context) }
    val periodoViewModel = remember { PeriodoViewModel(context) }
    val periodosViewModel = remember { PeriodosViewModel(context) }
    val perfilViewModel = remember { PerfilViewModel(context) }
    val asambleasViewModel =
        remember { AsambleasViewModel(context.applicationContext as Application) }
    val usuarioViewModel =
        remember { UsuarioViewModel(UsuarioRepository(ApiClient.usuarioApi(context))) }
    val cajaViewModel = remember { CajaViewModel(CajaRepository(ApiClient.cajaApi(context))) }
    val actividadViewModel = remember {
        ActividadViewModel(
            context.applicationContext as Application
        )
    }

    val rolViewModel = remember { RolViewModel(RolRepository(ApiClient.rolApi(context))) }
    val chatViewModel = remember { ChatViewModel(ChatRepository(ApiClient.chatApi(context))) }
    val notificacionViewModel =
        remember { NotificacionViewModel(context) }
    val fcmPreferenciaViewModel =
        remember { FcmPreferenciaViewModel(context) }
    val enviarNotificacionViewModel =
        remember { EnviarNotificacionViewModel(context) }
    val notificacionesNoLeidas by
    notificacionViewModel.noLeidas.collectAsState()
    val usuarioActualId by sessionManager.userId.collectAsState(initial = null)
    val usuarioPerfil by perfilViewModel.usuario.collectAsState()
    val guardandoPerfil by perfilViewModel.guardando.collectAsState()
    val subiendoFotoPerfil by perfilViewModel.subiendoFoto.collectAsState()
    // Solo el rol Tesorero tiene acceso al módulo Movimientos.
    val puedeVerMovimientos =
        usuarioPerfil?.rol?.equals("Tesorero", ignoreCase = true) == true


    var notificacionTitulo by remember {
        mutableStateOf("")
    }

    var notificacionMensaje by remember {
        mutableStateOf("")
    }

    var notificacionTipo by remember {
        mutableStateOf("Aviso")
    }

    var notificacionDestinatario by remember {
        mutableStateOf("Todos los vecinos")
    }

    var notificacionCantidad by remember {
        mutableStateOf(0)
    }



    LaunchedEffect(Unit) {
        dashboardViewModel.cargarDashboard()
        perfilViewModel.cargarPerfil()
        notificacionViewModel.cargarNoLeidas()
    }

    val backStack = remember { mutableStateListOf(AppScreen.DASHBOARD) }
    val pantallaActual = backStack.lastOrNull() ?: AppScreen.DASHBOARD
    var mostrarZoeModal by remember {
        mutableStateOf(false)
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

    LaunchedEffect(asambleaIdNotificacion) {
        asambleaIdNotificacion?.let { id ->

            Log.d(
                "SIGEFIV_NOTIF",
                "NAVEGANDO A ASAMBLEA ID = $id"
            )

            asambleasViewModel.obtenerAsamblea(id) { asamblea ->

                Log.d(
                    "SIGEFIV_NOTIF",
                    "ASAMBLEA OBTENIDA = $asamblea"
                )

                if (asamblea != null) {

                    asambleaConvocatoria = asamblea

                    Log.d(
                        "SIGEFIV_NOTIF",
                        "ABRIENDO CONVOCATORIA"
                    )
                    backStack.clear()
                    backStack.add(AppScreen.DASHBOARD)
                    backStack.add(AppScreen.CONVOCATORIA_ASAMBLEA)
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
                        "movimientos" -> {
                            if (puedeVerMovimientos) {
                                navegarA(AppScreen.MOVIMIENTOS)
                            }
                        }

                        "chat" -> navegarA(AppScreen.CHAT)

                        "asambleas" -> {
                            asambleaSeleccionada = null
                            navegarA(AppScreen.ASAMBLEAS)
                        }

                        "periodos" -> navegarA(AppScreen.PERIODOS)
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
                        "notificaciones" -> navegarA(AppScreen.NOTIFICACIONES)
                        "actividad" -> {
                            actividadViewModel.cargarActividades()
                            navegarA(AppScreen.ACTIVIDAD)
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
                    fadeIn(animationSpec = tween(140)) togetherWith fadeOut(
                        animationSpec = tween(
                            140
                        )
                    )
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
                                navegarA(AppScreen.NOTIFICACIONES)
                            },

                            onMovimientosClick = {
                                if (puedeVerMovimientos) {
                                    navegarA(AppScreen.MOVIMIENTOS)
                                }
                            },
                            onAsambleasClick = { navegarA(AppScreen.ASAMBLEAS) },
                            onPeriodosClick = { navegarA(AppScreen.PERIODOS) },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            },
                            onSigiClick = {
                                mostrarZoeModal = true
                            },
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
                            onAsambleasClick = { navegarA(AppScreen.ASAMBLEAS) },
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
                            onAsambleasClick = { navegarA(AppScreen.ASAMBLEAS) },
                            onPeriodosClick = { navegarA(AppScreen.PERIODOS) },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            },
                            onNuevoMovimientoClick = { navegarA(AppScreen.NUEVO_MOVIMIENTO) },
                            onMovimientoClick = { mov ->
                                movimientoSeleccionado = mov
                                navegarA(AppScreen.DETALLE_MOVIMIENTO)
                            },
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            puedeCerrarPeriodo = usuarioPerfil?.rol
                                ?.equals("Tesorero", ignoreCase = true)
                                    == true,

                            onCerrarPeriodoClick = {
                                periodo?.let { periodoActual ->
                                    periodoViewModel.cerrarPeriodo(periodoActual.id) { exito, mensaje ->
                                        if (exito) {
                                            // El backend crea automáticamente el siguiente período.
                                            periodoViewModel.cargarPeriodoAbierto()

                                            // Actualizamos los movimientos mostrados.
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
                                onInicioClick = {
                                    navegarA(AppScreen.DASHBOARD, limpiarPila = true)
                                },
                                onAsambleasClick = {
                                    navegarA(AppScreen.ASAMBLEAS)
                                },
                                onPeriodosClick = {
                                    navegarA(AppScreen.PERIODOS)
                                },
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
                            onCerrarClick = { retroceder() },
                            onGuardar = { fecha, categoriaId, concepto, persona, formaPago, monto, referencia, observaciones ->
                                movimientosViewModel.crearMovimiento(
                                    fecha = fecha,
                                    categoriaId = categoriaId,
                                    concepto = concepto,
                                    persona = persona,
                                    formaPago = formaPago,
                                    monto = monto,
                                    referencia = referencia,
                                    observaciones = observaciones
                                ) { exito ->
                                    if (exito) retroceder()
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
                            onCerrarClick = { retroceder() },
                            onGuardar = { fecha, categoriaId, concepto, persona, formaPago, monto, referencia, observaciones ->
                                movimientosViewModel.crearMovimiento(
                                    fecha = fecha,
                                    categoriaId = categoriaId,
                                    concepto = concepto,
                                    persona = persona,
                                    formaPago = formaPago,
                                    monto = monto,
                                    referencia = referencia,
                                    observaciones = observaciones
                                ) { exito ->
                                    if (exito) retroceder()
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
                                onInicioClick = {
                                    navegarA(
                                        AppScreen.DASHBOARD,
                                        limpiarPila = true
                                    )
                                },
                                onMovimientosClick = { navegarA(AppScreen.PERIODOS) },
                                onAsambleasClick = { navegarA(AppScreen.ASAMBLEAS) },
                                onPeriodosClick = {
                                    perfilViewModel.cargarPerfil()
                                    navegarA(AppScreen.PERFIL)
                                },
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
                            onAsambleasClick = { navegarA(AppScreen.ASAMBLEAS) },
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
                            onMovimientosClick = { navegarA(AppScreen.ASAMBLEAS) },
                            onAsambleasClick = { navegarA(AppScreen.PERIODOS) },
                            onMasClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            },
                            onOpenDrawer = { scope.launch { drawerState.open() } }
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
                            onMovimientosClick = { navegarA(AppScreen.ASAMBLEAS) },
                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onMovimientosPrincipalClick = { navegarA(AppScreen.ASAMBLEAS) },
                            onAsambleasClick = { navegarA(AppScreen.ASAMBLEAS) },
                            onMiCuentaClick = {
                                perfilViewModel.cargarPerfil()
                                navegarA(AppScreen.PERFIL)
                            }
                        )
                    }

                    AppScreen.PERFIL -> {
                        PerfilScreen(
                            onBackClick = { retroceder() },
                            onInicioClick = { navegarA(AppScreen.DASHBOARD, limpiarPila = true) },
                            onAsambleasClick = { navegarA(AppScreen.ASAMBLEAS) },
                            onPeriodosClick = { navegarA(AppScreen.PERIODOS) },
                            onMiCuentaClick = { },
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
                            onNuevoRol = { },
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
                                    CircularProgressIndicator(color = Color(0xFF15803D))
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
                                            text = rolUiState.error
                                                ?: "Error al cargar detalle del rol",
                                            color = Color(0xFFDC2626),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { retroceder() },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(
                                                    0xFF15803D
                                                )
                                            )
                                        ) {
                                            Text("Volver a Roles", color = Color.White)
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

                    AppScreen.CAJA -> {
                        CajaScreen(
                            viewModel = cajaViewModel,
                            onBackClick = { retroceder() },
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                        )
                    }

                    AppScreen.ACTIVIDAD -> {
                        ActividadScreen(
                            viewModel = actividadViewModel,
                            onBack = {
                                retroceder()
                            }
                        )
                    }

                    AppScreen.NOTIFICACIONES -> {
                        NotificacionesScreen(
                            viewModel = notificacionViewModel,
                            fcmPreferenciaViewModel = fcmPreferenciaViewModel,
                            rol = usuarioPerfil?.rol,

                            onNuevaNotificacionClick = {
                                navegarA(AppScreen.ENVIAR_NOTIFICACION)
                            },

                            onNotificacionClick = { notificacion ->

                                when (notificacion.tipo.lowercase()) {

                                    "zoe" -> {
                                        val periodoId = notificacion.data
                                            ?.get("periodo_id")
                                            ?.toIntOrNull()

                                        if (periodoId != null) {
                                            periodoIdSeleccionado = periodoId

                                            navegarA(
                                                AppScreen.PERIODO_DETALLE
                                            )
                                        }
                                    }

                                    "ingreso" -> {

                                        val movimientoId =
                                            notificacion.data
                                                ?.get("movimiento_id")
                                                ?.toIntOrNull()

                                        if (movimientoId != null) {

                                            movimientosViewModel.obtenerMovimientoPorId(
                                                movimientoId
                                            ) { movimiento ->

                                                if (movimiento != null) {

                                                    movimientoSeleccionado =
                                                        movimiento



                                                    navegarA(
                                                        AppScreen.DETALLE_MOVIMIENTO
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    "egreso" -> {
                                        val movimientoId =
                                            notificacion.data
                                                ?.get("movimiento_id")
                                                ?.toIntOrNull()

                                        if (movimientoId != null) {
                                            movimientosViewModel.obtenerMovimientoPorId(
                                                movimientoId
                                            ) { movimiento ->
                                                if (movimiento != null) {
                                                    movimientoSeleccionado = movimiento

                                                    navegarA(
                                                        AppScreen.DETALLE_MOVIMIENTO
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    else -> {
                                        // Los demás tipos los implementaremos después.
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
                            onBackClick = {
                                retroceder()
                            },
                            onEnvioExitoso = { titulo,
                                               mensaje,
                                               tipo,
                                               destinatario,
                                               cantidad ->

                                notificacionTitulo = titulo
                                notificacionMensaje = mensaje
                                notificacionTipo = tipo
                                notificacionDestinatario = destinatario
                                notificacionCantidad = cantidad

                                navegarA(
                                    AppScreen.CONFIRMAR_NOTIFICACION
                                )
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

                                while (
                                    backStack.size > 1 &&
                                    backStack.last() != AppScreen.NOTIFICACIONES
                                ) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                            }
                        )
                    }


                }
            }
        }

        // MODAL DE ZOE INTELIGENTE
        if (mostrarZoeModal) {
            Dialog(
                onDismissRequest = {
                    mostrarZoeModal = false
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SigiScreen(
                        onBackClick = {
                            mostrarZoeModal = false
                        },
                        onOpenDrawer = {}
                    )
                }
            }
        }
    }
}