@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.movimientos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.sigefiv.app.ServidorConfig
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.ui.components.AppBottomBar

private val ColorIngreso = Color(0xFF16A34A)
private val ColorEgreso = Color(0xFFDC2626)

@Composable
fun DetalleMovimientoScreen(
    movimiento: Movimiento,
    periodoNombre: String? = null,
    periodoEstado: String? = null,
    ingresosPeriodo: Double? = null,
    egresosPeriodo: Double? = null,
    saldoDisponiblePeriodo: Double? = null,
    saldoCajaPeriodo: Double? = null,
    onBackClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onEmprendimientosClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val esIngreso = movimiento.tipo.equals("Ingreso", ignoreCase = true)
    val colorMovimiento = if (esIngreso) ColorIngreso else ColorEgreso

    val nombrePeriodo = periodoNombre ?: "Período actual"
    val estadoPeriodo = periodoEstado ?: "Abierto"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Detalle de movimiento",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "N.º ${movimiento.numero} · $nombrePeriodo",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            AppBottomBar(
                pantallaActual = "Movimientos",
                onInicioClick = onInicioClick,
                onAsambleasClick = onAsambleasClick,
                onEmprendimientosClick = onEmprendimientosClick,
                onPeriodosClick = onPeriodosClick,
                onMiCuentaClick = onMiCuentaClick
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Tarjeta Principal Tipo Voucher (Monto + Concepto + Estado)
            item {
                HeaderMontoCard(
                    movimiento = movimiento,
                    esIngreso = esIngreso,
                    colorMovimiento = colorMovimiento,
                    primaryColor = primaryColor
                )
            }

            // Información General detallada
            item {
                InformacionGeneralCard(
                    movimiento = movimiento,
                    esIngreso = esIngreso,
                    colorMovimiento = colorMovimiento,
                    primaryColor = primaryColor
                )
            }

            // Resumen financiero del período si está disponible
            if (
                ingresosPeriodo != null ||
                egresosPeriodo != null ||
                saldoDisponiblePeriodo != null ||
                saldoCajaPeriodo != null
            ) {
                item {
                    ResumenPeriodoCard(
                        periodoNombre = nombrePeriodo,
                        ingresos = ingresosPeriodo ?: 0.0,
                        egresos = egresosPeriodo ?: 0.0,
                        disponible = saldoDisponiblePeriodo
                            ?: ((ingresosPeriodo ?: 0.0) - (egresosPeriodo ?: 0.0)),
                        saldoCaja = saldoCajaPeriodo
                            ?: ((ingresosPeriodo ?: 0.0) - (egresosPeriodo ?: 0.0))
                    )
                }
            }

            // Estado del período contable
            item {
                EstadoPeriodoCard(
                    estado = estadoPeriodo,
                    abierto = estadoPeriodo.equals("Abierto", ignoreCase = true)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HeaderMontoCard(
    movimiento: Movimiento,
    esIngreso: Boolean,
    colorMovimiento: Color,
    primaryColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, colorMovimiento.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono Circular
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(colorMovimiento.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (esIngreso) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                    contentDescription = movimiento.tipo,
                    tint = colorMovimiento,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Monto Grande
            Text(
                text = if (esIngreso) "+ S/ %.2f".format(movimiento.monto) else "- S/ %.2f".format(movimiento.monto),
                color = colorMovimiento,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Concepto
            Text(
                text = movimiento.concepto,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Badges: Categoría y Tipo
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = primaryColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = movimiento.categoria ?: "Sin categoría",
                        color = primaryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = colorMovimiento.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, colorMovimiento.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = movimiento.tipo.uppercase(),
                        color = colorMovimiento,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InformacionGeneralCard(
    movimiento: Movimiento,
    esIngreso: Boolean,
    colorMovimiento: Color,
    primaryColor: Color
) {
    var mostrarComprobanteModal by remember { mutableStateOf(false) }

    val urlComprobante = remember(movimiento.comprobante) {
        movimiento.comprobante?.let { comp ->
            if (comp.startsWith("http")) comp
            else "${ServidorConfig.API_BASE_URL.substringBefore("/api/")}/storage/$comp"
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Detalles de la operación",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            DetalleItemFila(
                icono = Icons.Outlined.CalendarMonth,
                etiqueta = "Fecha",
                valor = movimiento.fecha ?: "Sin fecha"
            )

            DetalleDivisor()

            DetalleItemFila(
                icono = Icons.Outlined.Person,
                etiqueta = "Persona / Vecino",
                valor = movimiento.persona?.ifBlank { "No registrada" } ?: "No registrada"
            )

            DetalleDivisor()

            DetalleItemFila(
                icono = Icons.Outlined.Payments,
                etiqueta = "Forma de pago",
                valor = movimiento.forma_pago ?: "No registrada"
            )

            DetalleDivisor()

            DetalleItemFila(
                icono = Icons.Outlined.Tag,
                etiqueta = "N.º Operación / Ref.",
                valor = movimiento.referencia?.ifBlank { "—" } ?: "—"
            )

            DetalleDivisor()

            DetalleItemFila(
                icono = Icons.Outlined.Description,
                etiqueta = "Observaciones",
                valor = movimiento.observaciones?.ifBlank { "—" } ?: "—"
            )

            DetalleDivisor()

            DetalleItemFila(
                icono = Icons.Outlined.ReceiptLong,
                etiqueta = "Registro contable",
                valor = "Movimiento N.º ${movimiento.numero}"
            )

            // Sección de Comprobante
            if (!urlComprobante.isNullOrBlank()) {
                DetalleDivisor()
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Comprobante adjunto",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    onClick = { mostrarComprobanteModal = true },
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = urlComprobante,
                            contentDescription = "Comprobante",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Comprobante / Voucher",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Toca para ver o ampliar en pantalla completa",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Modal para zoom y vista completa del comprobante
    if (mostrarComprobanteModal && !urlComprobante.isNullOrBlank()) {
        Dialog(
            onDismissRequest = { mostrarComprobanteModal = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                var escala by remember { mutableFloatStateOf(1f) }
                var desplazamientoX by remember { mutableFloatStateOf(0f) }
                var desplazamientoY by remember { mutableFloatStateOf(0f) }

                val estadoTransformacion = rememberTransformableState { zoom, pan, _ ->
                    escala = (escala * zoom).coerceIn(1f, 5f)
                    desplazamientoX += pan.x
                    desplazamientoY += pan.y
                }

                AsyncImage(
                    model = urlComprobante,
                    contentDescription = "Comprobante ampliado",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 40.dp)
                        .graphicsLayer {
                            scaleX = escala
                            scaleY = escala
                            translationX = desplazamientoX
                            translationY = desplazamientoY
                        }
                        .transformable(estadoTransformacion),
                    contentScale = ContentScale.Fit
                )

                IconButton(
                    onClick = { mostrarComprobanteModal = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Cerrar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun DetalleItemFila(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    valorColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = etiqueta,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            modifier = Modifier.width(120.dp)
        )

        Text(
            text = valor,
            color = valorColor ?: MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DetalleDivisor() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
        thickness = 0.8.dp
    )
}

@Composable
private fun ResumenPeriodoCard(
    periodoNombre: String,
    ingresos: Double,
    egresos: Double,
    disponible: Double,
    saldoCaja: Double
) {
    val balancePositivo = disponible >= 0

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Balance del período",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = periodoNombre,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ResumenDatoItem("Ingresos", "S/ %.2f".format(ingresos), ColorIngreso)
                ResumenDatoItem("Egresos", "S/ %.2f".format(egresos), ColorEgreso)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ResumenDatoItem(
                    "Disponible",
                    "S/ %.2f".format(disponible),
                    if (balancePositivo) ColorIngreso else ColorEgreso
                )
                ResumenDatoItem(
                    "Saldo en caja",
                    "S/ %.2f".format(saldoCaja),
                    if (saldoCaja >= 0) ColorIngreso else ColorEgreso
                )
            }
        }
    }
}

@Composable
private fun ResumenDatoItem(
    titulo: String,
    valor: String,
    color: Color
) {
    Column(modifier = Modifier.width(135.dp)) {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = valor,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EstadoPeriodoCard(
    estado: String,
    abierto: Boolean
) {
    val colorEstado = if (abierto) ColorIngreso else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colorEstado.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (abierto) Icons.Outlined.CheckCircle else Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = colorEstado,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = if (abierto) "Período activo y editable" else "Período cerrado",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (abierto) "Estado contable: Abierto" else "Estado contable: Cerrado",
                    color = colorEstado,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}