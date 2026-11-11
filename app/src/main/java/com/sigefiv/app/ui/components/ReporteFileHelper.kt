package com.sigefiv.app.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

object ReporteFileHelper {

    suspend fun guardarYCompartir(
        context: Context,
        body: ResponseBody,
        nombreArchivo: String,
        mimeType: String
    ) {
        try {
            val carpeta = File(context.cacheDir, "reportes").apply {
                if (!exists()) mkdirs()
            }
            val archivo = File(carpeta, nombreArchivo)

            // Escribir bytes en disco (hilo IO)
            withContext(Dispatchers.IO) {
                body.byteStream().use { input ->
                    FileOutputStream(archivo).use { output ->
                        input.copyTo(output)
                    }
                }
            }

            // Usamos la misma autoridad de FileProvider que tu app tiene configurada
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, archivo)

            withContext(Dispatchers.Main) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = mimeType
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(intent, "Compartir reporte con:"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error al abrir el archivo: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}