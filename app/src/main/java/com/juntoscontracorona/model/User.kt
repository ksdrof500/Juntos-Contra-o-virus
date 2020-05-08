package com.juntoscontracorona.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

data class User(
    var id: String = "",
    var image: String = "",
    var name: String = "",
    var phone: String = "",
    var help: Help? = null,
    var store: Boolean = false,
    var pharmacy: Boolean = false,
    var talk: Boolean = false,
    var latLong: GeoPoint? = null,
    var beingHelped: String? = "",
    var helpingCount: Int = 0,
    var token: String = "",
    var date: Timestamp? = null
): Serializable {
    constructor(id: String, image: String, name: String) : this(
        id, image, name, "", null, false, false, false, null, ""
    )
}

enum class Help {
    HELPER,
    HELPING
}