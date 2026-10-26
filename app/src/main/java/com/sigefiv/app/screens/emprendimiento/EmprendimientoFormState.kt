package com.sigefiv.app.screens.emprendimiento

data class EmprendimientoFormState(
    val id: Long? = null,
    val nombre: String = "",
    val palabrasClave: String = "", // 👈 Reemplaza a categoriaId
    val descripcion: String = "",
    val telefono: String = "",
    val usarWhatsapp: Boolean = true,
    val modalidad: String = "ambos", // local_emprendedor, domicilio_cliente, ambos, virtual
    val zonaAtencion: String = "",
    val horario: String = "",
    val status: String = "draft", // draft, published
    val fotos: List<String> = emptyList(), // URIs locales o rutas de imágenes
    val isLoading: Boolean = false,
    val isGeneratingWithZoe: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

// Ya no necesitamos CategoriaUi, pero puedes dejarlo comentado o borrarlo si no se usa en otra parte.

