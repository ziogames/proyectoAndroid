package com.sigefiv.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FondoSIGEFIV =
    Color(0xFF062A34)

private val FondoTarjeta =
    Color(0xFF0D3A45)

private val Turquesa =
    Color(0xFF00D4D4)

private val Blanco =
    Color(0xFFFFFFFF)

private val GrisClaro =
    Color(0xFFD9E5E7)


@Composable
fun SigefivDrawer(
    onInicioClick: () -> Unit,
    onMovimientosClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onPeriodosClick: () -> Unit,
    onRecibosClick: () -> Unit = {},
    onReportesClick: () -> Unit = {},
    onComunicadosClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    seleccion: String = "Inicio"
) {

    ModalDrawerSheet(

        drawerContainerColor =
            FondoSIGEFIV

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp,
                        vertical = 28.dp
                    )
        ) {

            /*
            |--------------------------------------------------------------------------
            | ENCABEZADO
            |--------------------------------------------------------------------------
            */

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(

                            color =
                                FondoTarjeta,

                            shape =
                                RoundedCornerShape(
                                    18.dp
                                )
                        )
                        .padding(
                            18.dp
                        )
            ) {

                Column {

                    Text(

                        text =
                            "SIGEFIV",

                        color =
                            Turquesa,

                        fontSize =
                            25.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    Text(

                        text =
                            "Administrador",

                        color =
                            Blanco,

                        fontSize =
                            15.sp,

                        fontWeight =
                            FontWeight.Medium
                    )


                    Text(

                        text =
                            "Sistema de Gestión Financiera Vecinal",

                        color =
                            GrisClaro,

                        fontSize =
                            11.sp
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            /*
            |--------------------------------------------------------------------------
            | INICIO
            |--------------------------------------------------------------------------
            */

            OpcionDrawer(

                icono =
                    Icons.Outlined.Home,

                titulo =
                    "Inicio",

                seleccionada =
                    seleccion == "Inicio",

                onClick =
                    onInicioClick
            )


            /*
            |--------------------------------------------------------------------------
            | MOVIMIENTOS
            |--------------------------------------------------------------------------
            */

            OpcionDrawer(

                icono =
                    Icons.Outlined.AccountBalanceWallet,

                titulo =
                    "Movimientos",

                seleccionada =
                    seleccion == "Movimientos",

                onClick =
                    onMovimientosClick
            )


            /*
            |--------------------------------------------------------------------------
            | ASAMBLEAS
            |--------------------------------------------------------------------------
            */

            OpcionDrawer(

                icono =
                    Icons.Outlined.Groups,

                titulo =
                    "Asambleas",

                seleccionada =
                    seleccion == "Asambleas",

                onClick =
                    onAsambleasClick
            )


            /*
            |--------------------------------------------------------------------------
            | PERÍODOS CONTABLES
            |--------------------------------------------------------------------------
            */

            OpcionDrawer(

                icono =
                    Icons.Outlined.Assignment,

                titulo =
                    "Períodos contables",

                seleccionada =
                    seleccion == "Períodos",

                onClick =
                    onPeriodosClick
            )


            /*
            |--------------------------------------------------------------------------
            | RECIBOS
            |--------------------------------------------------------------------------
            */

            OpcionDrawer(

                icono =
                    Icons.Outlined.ReceiptLong,

                titulo =
                    "Recibos",

                seleccionada =
                    seleccion == "Recibos",

                onClick =
                    onRecibosClick
            )


            /*
            |--------------------------------------------------------------------------
            | REPORTES
            |--------------------------------------------------------------------------
            */

            OpcionDrawer(

                icono =
                    Icons.Outlined.Assignment,

                titulo =
                    "Reportes",

                seleccionada =
                    seleccion == "Reportes",

                onClick =
                    onReportesClick
            )


            /*
            |--------------------------------------------------------------------------
            | COMUNICADOS
            |--------------------------------------------------------------------------
            */

            OpcionDrawer(

                icono =
                    Icons.Outlined.Campaign,

                titulo =
                    "Comunicados",

                seleccionada =
                    seleccion == "Comunicados",

                onClick =
                    onComunicadosClick
            )


            Spacer(
                modifier =
                    Modifier.height(
                        10.dp
                    )
            )


            /*
            |--------------------------------------------------------------------------
            | CONFIGURACIÓN
            |--------------------------------------------------------------------------
            */

            OpcionDrawer(

                icono =
                    Icons.Outlined.Settings,

                titulo =
                    "Configuración",

                seleccionada =
                    seleccion == "Configuración",

                onClick =
                    onConfiguracionClick
            )
        }
    }
}


@Composable
private fun OpcionDrawer(
    icono: ImageVector,
    titulo: String,
    seleccionada: Boolean = false,
    onClick: () -> Unit
) {

    NavigationDrawerItem(

        label = {

            Text(

                text =
                    titulo,

                fontSize =
                    15.sp
            )
        },

        selected =
            seleccionada,

        onClick =
            onClick,

        icon = {

            Icon(

                imageVector =
                    icono,

                contentDescription =
                    titulo
            )
        },

        modifier =
            Modifier.padding(
                vertical = 3.dp
            ),

        colors =
            NavigationDrawerItemDefaults.colors(

                selectedContainerColor =
                    Turquesa.copy(
                        alpha = 0.15f
                    ),

                selectedIconColor =
                    Turquesa,

                selectedTextColor =
                    Blanco,

                unselectedIconColor =
                    GrisClaro,

                unselectedTextColor =
                    GrisClaro
            )
    )
}