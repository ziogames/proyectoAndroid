package com.sigefiv.app.screens.emprendimiento

sealed interface EmprendimientoFormAction {
    data class UpdateNombre(val nombre: String) : EmprendimientoFormAction
    data class UpdatePalabrasClave(val palabrasClave: String) : EmprendimientoFormAction // 👈 Nuevo campo
    data class UpdateDescripcion(val descripcion: String) : EmprendimientoFormAction
    data class UpdateTelefono(val telefono: String) : EmprendimientoFormAction
    data class ToggleWhatsapp(val usar: Boolean) : EmprendimientoFormAction
    data class UpdateModality(val modalidad: String) : EmprendimientoFormAction
    data class UpdateZonaAtencion(val zona: String) : EmprendimientoFormAction
    data class UpdateHorario(val horario: String) : EmprendimientoFormAction
    data class AddPhoto(val uri: String) : EmprendimientoFormAction
    data class RemovePhoto(val index: Int) : EmprendimientoFormAction
    object RequestZoeDescription : EmprendimientoFormAction
    object SaveAsDraft : EmprendimientoFormAction
    object Publish : EmprendimientoFormAction
    object ClearMessages : EmprendimientoFormAction
}