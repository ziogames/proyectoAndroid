package com.sigefiv.app.screens.mascota

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.sigefiv.app.R

@Composable
fun MascotaSigefiv(
    estado: MascotaEstado = MascotaEstado.NORMAL,
    modifier: Modifier = Modifier,
    onDrag: (Float, Float) -> Unit = { _, _ -> },
    onDragEnd: () -> Unit = {},
    onClick: () -> Unit = {}
) {

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.zoe_chatbot)
    )

    // ============================================================
    // VELOCIDAD DE LA ANIMACIÓN LOTTIE
    // ============================================================

    val velocidad = when (estado) {
        MascotaEstado.NORMAL -> 1.0f
        MascotaEstado.SALUDANDO -> 1.20f
        MascotaEstado.FELIZ -> 1.18f
        MascotaEstado.PENSANDO -> 1.30f
        MascotaEstado.ATENTA -> 1.12f
        MascotaEstado.DURMIENDO -> 0.65f
        MascotaEstado.CELEBRANDO -> 1.45f
    }

    val progreso by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = velocidad
    )

    // ============================================================
    // MOTOR DE MOVIMIENTO DE ZOE
    // ============================================================

    val infiniteTransition = rememberInfiniteTransition(
        label = "zoe_motion"
    )

    val duracionMovimiento = when (estado) {
        MascotaEstado.NORMAL -> 2200
        MascotaEstado.SALUDANDO -> 900
        MascotaEstado.FELIZ -> 700
        MascotaEstado.PENSANDO -> 600
        MascotaEstado.ATENTA -> 1200
        MascotaEstado.DURMIENDO -> 3200
        MascotaEstado.CELEBRANDO -> 450
    }

    val movimiento by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duracionMovimiento,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoe_bob"
    )

    // ============================================================
    // REBOTE VERTICAL
    // ============================================================

    val amplitudVertical = when (estado) {
        MascotaEstado.NORMAL -> 1.2f
        MascotaEstado.SALUDANDO -> 2.5f
        MascotaEstado.FELIZ -> 4.0f
        MascotaEstado.PENSANDO -> 2.0f
        MascotaEstado.ATENTA -> 1.0f
        MascotaEstado.DURMIENDO -> 0.5f
        MascotaEstado.CELEBRANDO -> 5.0f
    }

    val movimientoY = movimiento * amplitudVertical

    // ============================================================
    // MOVIMIENTO LATERAL
    // ============================================================

    val movimientoX by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (estado) {
                    MascotaEstado.NORMAL -> 3000
                    MascotaEstado.SALUDANDO -> 750
                    MascotaEstado.FELIZ -> 900
                    MascotaEstado.PENSANDO -> 650
                    MascotaEstado.ATENTA -> 850
                    MascotaEstado.DURMIENDO -> 4000
                    MascotaEstado.CELEBRANDO -> 500
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoe_sway"
    )

    val amplitudHorizontal = when (estado) {
        MascotaEstado.NORMAL -> 0.6f
        MascotaEstado.SALUDANDO -> 1.8f
        MascotaEstado.FELIZ -> 1.4f
        MascotaEstado.PENSANDO -> 1.2f
        MascotaEstado.ATENTA -> 2.2f
        MascotaEstado.DURMIENDO -> 0.3f
        MascotaEstado.CELEBRANDO -> 2.8f
    }

    val movimientoXFinal = movimientoX * amplitudHorizontal

    // ============================================================
    // INCLINACIÓN
    // ============================================================

    val inclinacion by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (estado) {
                    MascotaEstado.NORMAL -> 2800
                    MascotaEstado.SALUDANDO -> 900
                    MascotaEstado.FELIZ -> 700
                    MascotaEstado.PENSANDO -> 600
                    MascotaEstado.ATENTA -> 1300
                    MascotaEstado.DURMIENDO -> 3500
                    MascotaEstado.CELEBRANDO -> 450
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zoe_rotation"
    )

    val rotacion = when (estado) {
        MascotaEstado.NORMAL -> inclinacion * 1f
        MascotaEstado.SALUDANDO -> inclinacion * 3f
        MascotaEstado.FELIZ -> inclinacion * 2.5f
        MascotaEstado.PENSANDO -> inclinacion * 2f
        MascotaEstado.ATENTA -> inclinacion * 1.5f
        MascotaEstado.DURMIENDO -> inclinacion * 0.5f
        MascotaEstado.CELEBRANDO -> inclinacion * 4f
    }

    // ============================================================
    // ESCALA
    // ============================================================

    val escalaObjetivo = when (estado) {
        MascotaEstado.NORMAL -> 1.0f
        MascotaEstado.SALUDANDO -> 1.03f
        MascotaEstado.FELIZ -> 1.08f
        MascotaEstado.PENSANDO -> 1.02f
        MascotaEstado.ATENTA -> 1.04f
        MascotaEstado.DURMIENDO -> 0.96f
        MascotaEstado.CELEBRANDO -> 1.12f
    }

    val escala by animateFloatAsState(
        targetValue = escalaObjetivo,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "zoe_scale"
    )

    // ============================================================
    // ZOE
    // ============================================================

    Box(
        modifier = modifier
            .size(78.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = onDragEnd,
                    onDragCancel = onDragEnd,
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(
                            dragAmount.x,
                            dragAmount.y
                        )
                    }
                )
            }
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progreso },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = movimientoXFinal
                    translationY = movimientoY
                    rotationZ = rotacion
                    scaleX = escala
                    scaleY = escala
                }
        )
    }
}
