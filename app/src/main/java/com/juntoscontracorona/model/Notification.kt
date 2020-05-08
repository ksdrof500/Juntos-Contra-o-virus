package com.juntoscontracorona.model

data class Notification(
    var idHelping: String = "",
    var name: String = "",
    var phone: String = "",
    var token: String = "",
    var idHelper: String = "",
    var tokenHelping: String = "",
    var helpConfirm: Boolean? = null
)