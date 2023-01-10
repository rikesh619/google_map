package com.example.mapapp.dataprovider

import android.content.Context
import android.util.Log
import com.example.mapapp.model.MapData
import com.google.gson.GsonBuilder

object MapDataProvider {

    private var mapData: ArrayList<MapData>? = null

    fun getmapData(context: Context): ArrayList<MapData> {
        if (mapData != null) {
            return mapData!!
        }

        var mapDataList = ArrayList<MapData>()
        val mapData = context.assets
            .open("Places.json")
            .bufferedReader()
            .use { it.readText() }
        val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

        mapDataList = arrayListOf()

        val mapDataObjects = gson.fromJson(mapData, Array<MapData>::class.java).toList()
        Log.i("MapDataProvider", "${mapDataObjects.map { it.id }}")
        mapDataList.addAll(mapDataObjects)

        return mapDataList
    }
}