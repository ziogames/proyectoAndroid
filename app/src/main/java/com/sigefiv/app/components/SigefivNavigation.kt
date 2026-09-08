package com.sigefiv.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.Menu

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


/*
|--------------------------------------------------------------------------
| MENÚ LATERAL
|--------------------------------------------------------------------------
*/

@Composable
fun SigefivDrawer(

    onCerrar: () -> Unit,

    onInicioClick: () -> Unit,

    onMovimientosClick: () -> Unit,

    onAsambleasClick: () -> Unit,

    onPeriodosClick: () -> Unit

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

            Text(
                text = "SIGEFIV",
                color = Turquesa,
                fontSize = 25.sp
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )


            OpcionDrawer(
                icono =
                    Icons.Outlined.Home,

                titulo =
                    "Inicio",

                onClick = {
                    onCerrar()
                    onInicioClick()
                }
            )


            OpcionDrawer(
                icono =
                    Icons.Outlined.AccountBalanceWallet,

                titulo =
                    "Movimientos",

                onClick = {
                    onCerrar()
                    onMovimientosClick()
                }
            )


            OpcionDrawer(
                icono =
                    Icons.Outlined.Groups,

                titulo =
                    "Asambleas",

                onClick = {
                    onCerrar()
                    onAsambleasClick()
                }
            )


            OpcionDrawer(
                icono =
                    Icons.Outlined.Assignment,

                titulo =
                    "Períodos contables",

                onClick = {
                    onCerrar()
                    onPeriodosClick()
                }
            )


            OpcionDrawer(
                icono =
                    Icons.Outlined.ReceiptLong,

                titulo =
                    "Recibos"
            )


            OpcionDrawer(
                icono =
                    Icons.Outlined.Assignment,

                titulo =
                    "Reportes"
            )


            OpcionDrawer(
                icono =
                    Icons.Outlined.Campaign,

                titulo =
                    "Comunicados"
            )


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            OpcionDrawer(
                icono =
                    Icons.Outlined.Settings,

                titulo =
                    "Configuración"
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| OPCIÓN DRAWER
|--------------------------------------------------------------------------
*/

@Composable
private fun OpcionDrawer(

    icono: ImageVector,

    titulo: String,

    seleccionada: Boolean = false,

    onClick: () -> Unit = {}

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


/*
|--------------------------------------------------------------------------
| BARRA INFERIOR
|--------------------------------------------------------------------------
*/

@Composable
fun SigefivBottomBar(

    onInicioClick: () -> Unit,

    onMovimientosClick: () -> Unit,

    onAsambleasClick: () -> Unit,

    onMasClick: () -> Unit

) {

    NavigationBar(

        containerColor =
            FondoTarjeta
    ) {

        NavigationBarItem(

            selected = false,

            onClick =
                onInicioClick,

            icon = {

                Icon(

                    imageVector =
                        Icons.Outlined.Home,

                    contentDescription =
                        "Inicio"
                )
            },

            label = {

                Text(
                    text =
                        "Inicio"
                )
            }
        )


        NavigationBarItem(

            selected = false,

            onClick =
                onMovimientosClick,

            icon = {

                Icon(

                    imageVector =
                        Icons.Outlined.AccountBalanceWallet,

                    contentDescription =
                        "Movimientos"
                )
            },

            label = {

                Text(
                    text =
                        "Movimientos"
                )
            }
        )


        NavigationBarItem(

            selected = false,

            onClick =
                onAsambleasClick,

            icon = {

                Icon(

                    imageVector =
                        Icons.Outlined.Groups,

                    contentDescription =
                        "Asambleas"
                )
            },

            label = {

                Text(
                    text =
                        "Asambleas"
                )
            }
        )


        NavigationBarItem(

            selected = false,

            onClick =
                onMasClick,

            icon = {

                Icon(

                    imageVector =
                        Icons.Outlined.Menu,

                    contentDescription =
                        "Más"
                )
            },

            label = {

                Text(
                    text =
                        "Más"
                )
            }
        )
    }
}