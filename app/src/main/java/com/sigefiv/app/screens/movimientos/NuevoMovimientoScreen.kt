@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.movimientos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV (ESTILO DASHBOARD)
|--------------------------------------------------------------------------
*/

private val FondoSIGEFIV = Color(0xFFF8FAFC)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val VerdeSuave = Color(0xFFDCFCE7)
private val RojoSuave = Color(0xFFFEE2E2)
private val Blanco = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisClaro = Color(0xFF64748B)
private val Verde = Color(0xFF15803D)
private val Rojo = Color(0xFFDC2626)

/*
|--------------------------------------------------------------------------
| NUEVO MOVIMIENTO
|--------------------------------------------------------------------------
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMovimientoScreen(
    onIngresoClick: () -> Unit,
    onEgresoClick: () -> Unit,
    onCerrarClick: () -> Unit
) {
    Scaffold(
        containerColor = FondoSIGEFIV,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Nuevo movimiento",
                            color = Blanco,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(1.dp))

                        Text(
                            text = "¿Qué deseas registrar?",
                            color = Blanco.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCerrarClick) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Cerrar",
                            tint = Blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SeasonalColors.primary(
                        SeasonalTheme.getSeason()
                    )
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoSIGEFIV)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /*
            |--------------------------------------------------------------------------
            | TEXTO INTRODUCTORIO
            |--------------------------------------------------------------------------
            */

            Text(
                text = "Selecciona el tipo de movimiento que deseas registrar en el sistema contable.",
                color = GrisClaro,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            /*
            |--------------------------------------------------------------------------
            | NUEVO INGRESO
            |--------------------------------------------------------------------------
            */

            TipoMovimientoCard(
                titulo = "Nuevo ingreso",
                descripcion = "Registrar dinero recibido en la comunidad o caja",
                color = Verde,
                fondoIcono = VerdeSuave,
                icono = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowUpward,
                        contentDescription = "Nuevo ingreso",
                        tint = Verde,
                        modifier = Modifier.size(28.dp)
                    )
                },
                onClick = onIngresoClick
            )

            /*
            |--------------------------------------------------------------------------
            | NUEVO EGRESO
            |--------------------------------------------------------------------------
            */

            TipoMovimientoCard(
                titulo = "Nuevo egreso",
                descripcion = "Registrar un pago, salida o gasto de operaciones",
                color = Rojo,
                fondoIcono = RojoSuave,
                icono = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDownward,
                        contentDescription = "Nuevo egreso",
                        tint = Rojo,
                        modifier = Modifier.size(28.dp)
                    )
                },
                onClick = onEgresoClick
            )

            Spacer(modifier = Modifier.weight(1f))

            /*
            |--------------------------------------------------------------------------
            | CANCELAR
            |--------------------------------------------------------------------------
            */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCerrarClick)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cancelar",
                    color = GrisClaro,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| TARJETA TIPO DE MOVIMIENTO
|--------------------------------------------------------------------------
*/

@Composable
private fun TipoMovimientoCard(
    titulo: String,
    descripcion: String,
    color: Color,
    fondoIcono: Color,
    icono: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /*
            |--------------------------------------------------------------------------
            | ICONO
            |--------------------------------------------------------------------------
            */

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = fondoIcono,
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                icono()
            }

            Spacer(modifier = Modifier.size(16.dp))

            /*
            |--------------------------------------------------------------------------
            | TEXTO
            |--------------------------------------------------------------------------
            */

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = titulo,
                    color = TextoPrincipal,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = descripcion,
                    color = GrisClaro,
                    fontSize = 12.sp
                )
            }

            /*
            |--------------------------------------------------------------------------
            | INDICADOR
            |--------------------------------------------------------------------------
            */

            Text(
                text = "›",
                color = color,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}