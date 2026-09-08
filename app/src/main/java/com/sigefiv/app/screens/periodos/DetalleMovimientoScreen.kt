@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.movimientos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Movimiento

private val VerdeSIGEFIV = Color(0xFF0B9F5A)
private val VerdeSIGEFIVOscuro = Color(0xFF087A46)
private val VerdeSuave = Color(0xFFE7F7EF)
private val FondoSIGEFIV = Color(0xFFF7F9FA)
private val Blanco = Color.White
private val TextoPrincipal = Color(0xFF1F2937)
private val GrisTexto = Color(0xFF64748B)
private val GrisBorde = Color(0xFFE2E8F0)
private val Rojo = Color(0xFFE63946)
private val RojoSuave = Color(0xFFFFECEE)
private val AzulSuave = Color(0xFFEAF3FF)
private val Azul = Color(0xFF2563EB)

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
    onMovimientosClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onMasClick: () -> Unit = {}
) {
    val esIngreso = movimiento.tipo.equals(
        "Ingreso",
        ignoreCase = true
    )

    val colorMovimiento = if (esIngreso) VerdeSIGEFIV else Rojo
    val fondoMovimiento = if (esIngreso) VerdeSuave else RojoSuave

    val nombrePeriodo = periodoNombre
        ?: "Período actual"

    val estadoPeriodo = periodoEstado
        ?: "Abierto"

    Scaffold(
        containerColor = FondoSIGEFIV,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Detalle del movimiento",
                            color = Blanco,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )

                        Text(
                            text = nombrePeriodo,
                            color = Color(0xFFE8F5E9),
                            fontSize = 13.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeSIGEFIV,
                    titleContentColor = Blanco,
                    navigationIconContentColor = Blanco
                )
            )
        },
        bottomBar = {
            DetalleBottomBar(
                onInicioClick = onInicioClick,
                onMovimientosClick = onMovimientosClick,
                onAsambleasClick = onAsambleasClick,
                onMasClick = onMasClick
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoSIGEFIV)
                .padding(innerPadding)
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                ResumenMovimientoCard(
                    movimiento = movimiento,
                    esIngreso = esIngreso,
                    colorMovimiento = colorMovimiento,
                    fondoMovimiento = fondoMovimiento
                )
            }

            item {
                InformacionGeneralCard(
                    movimiento = movimiento,
                    esIngreso = esIngreso,
                    colorMovimiento = colorMovimiento
                )
            }

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

            item {
                EstadoPeriodoCard(
                    estado = estadoPeriodo,
                    abierto = estadoPeriodo.equals(
                        "Abierto",
                        ignoreCase = true
                    )
                )
            }

            item {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
            }
        }
    }
}

@Composable
private fun ResumenMovimientoCard(
    movimiento: Movimiento,
    esIngreso: Boolean,
    colorMovimiento: Color,
    fondoMovimiento: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = Blanco,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = fondoMovimiento,
                    shape = RoundedCornerShape(18.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (esIngreso) {
                    Icons.Outlined.ArrowUpward
                } else {
                    Icons.Outlined.ArrowDownward
                },
                contentDescription = movimiento.tipo,
                tint = colorMovimiento,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(15.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = movimiento.tipo,
                color = colorMovimiento,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = movimiento.concepto,
                color = TextoPrincipal,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = movimiento.categoria
                    ?: "Sin categoría",
                color = GrisTexto,
                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = if (esIngreso) {
                "+ S/ %.2f".format(movimiento.monto)
            } else {
                "- S/ %.2f".format(movimiento.monto)
            },
            color = colorMovimiento,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InformacionGeneralCard(
    movimiento: Movimiento,
    esIngreso: Boolean,
    colorMovimiento: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = Blanco,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(
                horizontal = 18.dp,
                vertical = 18.dp
            )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = VerdeSIGEFIV,
                modifier = Modifier.size(23.dp)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = "Información general",
                color = TextoPrincipal,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        DetalleFila(
            icono = Icons.Outlined.CalendarMonth,
            titulo = "Fecha",
            valor = movimiento.fecha ?: "Sin fecha"
        )

        DetalleSeparador()

        DetalleFila(
            icono = if (esIngreso) {
                Icons.Outlined.ArrowUpward
            } else {
                Icons.Outlined.ArrowDownward
            },
            titulo = "Tipo",
            valor = movimiento.tipo,
            valorColor = colorMovimiento
        )

        DetalleSeparador()

        DetalleFila(
            icono = Icons.Outlined.Tag,
            titulo = "Categoría",
            valor = movimiento.categoria
                ?: "Sin categoría"
        )

        DetalleSeparador()

        DetalleFila(
            icono = Icons.Outlined.Payments,
            titulo = "Monto",
            valor = "S/ %.2f".format(movimiento.monto),
            valorColor = colorMovimiento
        )

        DetalleSeparador()

        DetalleFila(
            icono = Icons.Outlined.ReceiptLong,
            titulo = "Forma de pago",
            valor = movimiento.forma_pago
                ?: "No registrada"
        )

        DetalleSeparador()

        DetalleFila(
            icono = Icons.Outlined.Person,
            titulo = "Persona",
            valor = movimiento.persona
                ?: "No registrada"
        )

        DetalleSeparador()

        DetalleFila(
            icono = Icons.Outlined.Description,
            titulo = "Referencia",
            valor = movimiento.referencia
                ?: "—"
        )

        DetalleSeparador()

        DetalleFila(
            icono = Icons.Outlined.Visibility,
            titulo = "Comprobante",
            valor = if (
                movimiento.comprobante.isNullOrBlank()
            ) {
                "Sin comprobante"
            } else {
                "Comprobante registrado"
            }
        )

        DetalleSeparador()

        DetalleFila(
            icono = Icons.Outlined.Description,
            titulo = "Observaciones",
            valor = movimiento.observaciones
                ?: "—"
        )

        DetalleSeparador()

        DetalleFila(
            icono = Icons.Outlined.ReceiptLong,
            titulo = "N.º movimiento",
            valor = movimiento.numero
        )

        DetalleSeparador()

        DetalleFila(
            icono = Icons.Outlined.Info,
            titulo = "Estado",
            valor = movimiento.estado
        )
    }
}

@Composable
private fun DetalleFila(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    valor: String,
    valorColor: Color = TextoPrincipal
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = GrisTexto,
            modifier = Modifier.size(21.dp)
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = titulo,
            color = GrisTexto,
            fontSize = 14.sp,
            modifier = Modifier.width(104.dp)
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = valor,
            color = valorColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DetalleSeparador() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp)
            .height(1.dp)
            .background(GrisBorde)
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
    val disponiblePositivo = disponible >= 0
    val saldoPositivo = saldoCaja >= 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = VerdeSuave,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(18.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ReceiptLong,
                contentDescription = null,
                tint = VerdeSIGEFIV,
                modifier = Modifier.size(24.dp)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column {
                Text(
                    text = "Resumen del período",
                    color = VerdeSIGEFIVOscuro,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = periodoNombre,
                    color = GrisTexto,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ResumenDato(
                titulo = "Ingresos",
                valor = "S/ %.2f".format(ingresos),
                color = VerdeSIGEFIV
            )

            ResumenDato(
                titulo = "Egresos",
                valor = "S/ %.2f".format(egresos),
                color = Rojo
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ResumenDato(
                titulo = "Disponible",
                valor = "S/ %.2f".format(disponible),
                color = if (disponiblePositivo) {
                    VerdeSIGEFIV
                } else {
                    Rojo
                }
            )

            ResumenDato(
                titulo = "Saldo caja",
                valor = "S/ %.2f".format(saldoCaja),
                color = if (saldoPositivo) {
                    VerdeSIGEFIV
                } else {
                    Rojo
                }
            )
        }
    }
}

@Composable
private fun ResumenDato(
    titulo: String,
    valor: String,
    color: Color
) {
    Column(
        modifier = Modifier.width(145.dp)
    ) {
        Text(
            text = titulo,
            color = GrisTexto,
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = valor,
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EstadoPeriodoCard(
    estado: String,
    abierto: Boolean
) {
    val color = if (abierto) Azul else GrisTexto
    val fondo = if (abierto) AzulSuave else Color(0xFFF1F5F9)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = fondo,
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = color.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (abierto) {
                    Icons.Outlined.Visibility
                } else {
                    Icons.Outlined.Description
                },
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(25.dp)
            )
        }

        Spacer(
            modifier = Modifier.width(13.dp)
        )

        Column {
            Text(
                text = if (abierto) {
                    "Movimiento del período actual"
                } else {
                    "Período cerrado"
                },
                color = TextoPrincipal,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = if (abierto) {
                    "Este movimiento pertenece al período abierto."
                } else {
                    "Este movimiento pertenece a un período cerrado."
                },
                color = GrisTexto,
                fontSize = 13.sp
            )

            Text(
                text = "Estado: $estado",
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DetalleBottomBar(
    onInicioClick: () -> Unit,
    onMovimientosClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onMasClick: () -> Unit
) {
    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = VerdeSIGEFIV
    ) {

        NavigationBarItem(
            selected = false,
            onClick = onInicioClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Inicio"
                )
            },
            label = {
                Text("Inicio")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VerdeSIGEFIV,
                selectedTextColor = VerdeSIGEFIV,
                unselectedIconColor = Blanco,
                unselectedTextColor = Blanco,
                indicatorColor = Blanco
            )
        )

        NavigationBarItem(
            selected = true,
            onClick = onMovimientosClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.ReceiptLong,
                    contentDescription = "Movimientos"
                )
            },
            label = {
                Text("Movimientos")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VerdeSIGEFIV,
                selectedTextColor = VerdeSIGEFIV,
                unselectedIconColor = Blanco,
                unselectedTextColor = Blanco,
                indicatorColor = Blanco
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = onAsambleasClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = "Asambleas"
                )
            },
            label = {
                Text("Asambleas")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VerdeSIGEFIV,
                selectedTextColor = VerdeSIGEFIV,
                unselectedIconColor = Blanco,
                unselectedTextColor = Blanco,
                indicatorColor = Blanco
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = onMasClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Más"
                )
            },
            label = {
                Text("Más")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VerdeSIGEFIV,
                selectedTextColor = VerdeSIGEFIV,
                unselectedIconColor = Blanco,
                unselectedTextColor = Blanco,
                indicatorColor = Blanco
            )
        )
    }
}
