package com.sigefiv.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FondoTarjeta =
    Color(0xFF0D3A45)

private val Turquesa =
    Color(0xFF00D4D4)

@Composable
fun AppBottomBar(
    pantallaActual: String,
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

            selected =
                pantallaActual == "Inicio",

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

            selected =
                pantallaActual == "Movimientos",

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

            selected =
                pantallaActual == "Asambleas",

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

            selected =
                pantallaActual == "Más",

            onClick =
                onMasClick,

            icon = {

                Icon(
                    imageVector =
                        Icons.Outlined.MoreHoriz,

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