package com.example.mapapp.model

import com.google.android.gms.maps.model.Marker
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MapData {

    @SerializedName("id")
    var id : Int? = null

    @SerializedName("type")
    @Expose
    var type :String? = null

    val markerType: MarkerType
        get() {
            return MarkerType.from(type ?: "") ?: MarkerType.Unknown
        }

    @SerializedName("name")
    @Expose
    var name: String?= null

    @SerializedName("latitude")
    @Expose
    var latitude: Double?= null

    @SerializedName("longitude")
    @Expose
    var longitude: Double?= null





}