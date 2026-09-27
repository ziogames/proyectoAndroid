package com.sigefiv.app

data class ComprobanteInterpretado(
    val tipo: TipoMovimientoDetectado,
    val monto: Double?,
    val persona: String?,
    val fecha: String?,
    val medioPago: String?,
    val numeroOperacion: String?,
    val textoOriginal: String
)

enum class TipoMovimientoDetectado {
    INGRESO,
    EGRESO,
    INDETERMINADO
}

object ComprobanteInterpreter {

    fun interpretar(texto: String): ComprobanteInterpretado {

        val textoNormalizado = texto
            .lowercase()
            .replace("¡", "")
            .replace("!", "")
            .trim()

        val tipo = when {
            textoNormalizado.contains("te yapearon") -> {
                TipoMovimientoDetectado.INGRESO
            }

            textoNormalizado.contains("yapeaste") -> {
                TipoMovimientoDetectado.EGRESO
            }

            else -> {
                TipoMovimientoDetectado.INDETERMINADO
            }
        }

        val monto = extraerMonto(textoNormalizado)
        val fecha = extraerFecha(texto)
        val persona = extraerPersona(texto, monto)
        val medioPago = extraerMedioPago(texto)
        val numeroOperacion = extraerNumeroOperacion(texto)

        return ComprobanteInterpretado(
            tipo = tipo,
            monto = monto,
            persona = persona,
            fecha = fecha,
            medioPago = medioPago,
            numeroOperacion = numeroOperacion,
            textoOriginal = texto
        )
    }

    private fun extraerMonto(texto: String): Double? {

        val patron = Regex(
            """(?:s/|s\/)\s*(\d+(?:[.,]\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        )

        val resultado = patron.find(texto)
            ?: return null

        return resultado
            .groupValues[1]
            .replace(",", ".")
            .toDoubleOrNull()
    }

    private fun extraerFecha(texto: String): String? {

        val meses = mapOf(
            "ene" to "01",
            "enero" to "01",
            "feb" to "02",
            "febrero" to "02",
            "mar" to "03",
            "marzo" to "03",
            "abr" to "04",
            "abril" to "04",
            "may" to "05",
            "mayo" to "05",
            "jun" to "06",
            "junio" to "06",
            "jul" to "07",
            "julio" to "07",
            "ago" to "08",
            "agosto" to "08",
            "sep" to "09",
            "sept" to "09",
            "set" to "09",
            "septiembre" to "09",
            "setiembre" to "09",
            "oct" to "10",
            "octubre" to "10",
            "nov" to "11",
            "noviembre" to "11",
            "dic" to "12",
            "diciembre" to "12"
        )

        val patron = Regex(
            """(\d{1,2})\s+([a-zA-Záéíóú]+)\.?\s+(\d{4})""",
            RegexOption.IGNORE_CASE
        )

        val resultado = patron.find(texto)
            ?: return null

        val dia = resultado.groupValues[1].padStart(2, '0')

        val mesTexto = resultado.groupValues[2]
            .lowercase()
            .removeSuffix(".")

        val anio = resultado.groupValues[3]

        val mes = meses[mesTexto]
            ?: return null

        return "$dia/$mes/$anio"
    }

    private fun extraerPersona(
        texto: String,
        monto: Double?
    ): String? {

        if (monto == null) {
            return null
        }

        val lineas = texto
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val patronMonto = Regex(
            """(?:s/|s\/)\s*\d+(?:[.,]\d{1,2})?""",
            RegexOption.IGNORE_CASE
        )

        val indiceMonto = lineas.indexOfFirst {
            patronMonto.containsMatchIn(it)
        }

        if (indiceMonto == -1) {
            return null
        }

        if (indiceMonto + 1 >= lineas.size) {
            return null
        }

        val posiblePersona = lineas[indiceMonto + 1]

        if (
            posiblePersona.equals("yape", ignoreCase = true) ||
            posiblePersona.equals("¡yapeaste!", ignoreCase = true) ||
            posiblePersona.equals("yapeaste", ignoreCase = true)
        ) {
            return null
        }

        return posiblePersona
    }

    /**
     * Detecta el medio o entidad desde la que se realizó
     * la operación.
     *
     * Ejemplos:
     * Yape
     * Plin
     * BIM
     * Dale
     */
    private fun extraerMedioPago(texto: String): String? {

        val textoNormalizado = texto
            .lowercase()
            .replace("¡", "")
            .replace("!", "")

        return when {
            Regex("""\byape\b""").containsMatchIn(textoNormalizado) -> {
                "Yape"
            }

            Regex("""\bplin\b""").containsMatchIn(textoNormalizado) -> {
                "Plin"
            }

            Regex("""\bbim\b""").containsMatchIn(textoNormalizado) -> {
                "BIM"
            }

            Regex("""\bdale\b""").containsMatchIn(textoNormalizado) -> {
                "Dale"
            }

            else -> {
                null
            }
        }
    }

    /**
     * Busca el número asociado al texto:
     *
     * Nro. de operación
     * Nro de operación
     * Número de operación
     * Numero de operacion
     *
     * No toma números pequeños como el código de seguridad.
     */
    private fun extraerNumeroOperacion(texto: String): String? {

        val lineas = texto
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val patronEtiquetaOperacion = Regex(
            """nro\.?\s*(?:de\s*)?operaci[oó]n|n[uú]mero\s*(?:de\s*)?operaci[oó]n""",
            RegexOption.IGNORE_CASE
        )

        val indiceOperacion = lineas.indexOfFirst {
            patronEtiquetaOperacion.containsMatchIn(it)
        }

        if (indiceOperacion == -1) {
            return null
        }

        /*
         * El OCR puede colocar varias líneas entre
         * "Nro. de operación" y el número.
         *
         * Por eso buscamos varias líneas hacia adelante.
         */
        val ultimaLinea = minOf(
            indiceOperacion + 20,
            lineas.size
        )

        for (i in (indiceOperacion + 1) until ultimaLinea) {

            val numeros = Regex("""\b\d{5,20}\b""")
                .findAll(lineas[i])

            for (numero in numeros) {
                return numero.value
            }
        }

        return null
    }
}