@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.movimientos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ColorIngreso = Color(0xFF16A34A)
private val ColorEgreso = Color(0xFFDC2626)

@Composable
fun NuevoMovimientoScreen(
    onIngresoClick: () -> Unit,
    onEgresoClick: () -> Unit,
    onCerrarClick: () -> Unit
) {
    NuevoMovimientoBottomSheet(
        onDismiss = onCerrarClick,
        onIngresoClick = onIngresoClick,
        onEgresoClick = onEgresoClick
    )
}

@Composable
fun NuevoMovimientoBottomSheet(
    onDismiss: () -> Unit,
    onIngresoClick: () -> Unit,
    onEgresoClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cabecera del BottomSheet
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nuevo movimiento",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Selecciona el tipo de operación que deseas registrar",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tarjeta de Ingreso
            OpcionMovimientoItem(
                titulo = "Nuevo ingreso",
                descripcion = "Registrar dinero recibido en la comunidad o en caja",
                colorAcento = ColorIngreso,
                icono = Icons.Outlined.ArrowUpward,
                onClick = onIngresoClick
            )

            // Tarjeta de Egreso
            OpcionMovimientoItem(
                titulo = "Nuevo egreso",
                descripcion = "Registrar un pago, salida o gasto operativo",
                colorAcento = ColorEgreso,
                icono = Icons.Outlined.ArrowDownward,
                onClick = onEgresoClick
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Botón Cancelar
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Cancelar",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun OpcionMovimientoItem(
    titulo: String,
    descripcion: String,
    colorAcento: Color,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(colorAcento.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorAcento,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = descripcion,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = colorAcento.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}