package com.example.mapapp.model

enum class MarkerType(var value: String){
    Shop("shop"),
    Bank("bank"),
    Gym("gym"),
    Unknown("unknown");

    companion object {
        infix fun from(value: String): MarkerType? =
            MarkerType.values().firstOrNull { it.value == value }
    }
}

