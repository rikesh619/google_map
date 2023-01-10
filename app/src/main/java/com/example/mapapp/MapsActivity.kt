package com.example.mapapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import com.example.mapapp.dataprovider.MapDataProvider
import com.example.mapapp.model.MapData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

        var currentMarker: Marker? = null
        private lateinit var mMap: GoogleMap
        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
        private lateinit var currentLocation: Location

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_maps)

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


            currentLocation()
        }

        private fun currentLocation() {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    7)

                return
            }

            val task = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener { location ->
                if (location != null) {
                    this.currentLocation = location
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }

            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray,
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                7 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentLocation()
                }
            }
        }

        override fun onMapReady(googleMap: GoogleMap) {
            mMap = googleMap

            val myLocation = LatLng(currentLocation.latitude, currentLocation.longitude)
            drawMarker(myLocation)

            mMap.setOnMapClickListener(object : GoogleMap.OnMapClickListener {
                override fun onMapClick(p0: LatLng) {

                    val newPosition = LatLng(p0.latitude, p0.longitude)

                    newMarker(newPosition)
                }

            })
        }

      

        private fun drawMarker(myLocation: LatLng) {
            val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.current)
            val icon = BitmapDescriptorFactory
                .fromBitmap(getCircleBitmap(bitmap, 250, this)!!)
            val markerOption = MarkerOptions().position(myLocation).title("$myLocation")
                .snippet(getAddress(myLocation.latitude, myLocation.longitude)).icon(icon)

            mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
            mMap.addMarker(markerOption)
        }


        private fun newMarker(newLocation: LatLng) {

            val markerOption = MarkerOptions().position(newLocation).title("$newLocation")
                .snippet(getAddress(newLocation.latitude, newLocation.longitude))
            mMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation))
            currentMarker = mMap.addMarker(markerOption)

        }



        private fun getAddress(latitude: Double, longitude: Double): String? {
            val geoCoder = Geocoder(this, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            return addresses[0].getAddressLine(0).toString()
        }

        private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
            val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
            vectorDrawable!!.setBounds(0,
                0,
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight)
            val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            vectorDrawable.draw(canvas)

            return BitmapDescriptorFactory.fromBitmap(bitmap.scale(100, 100, false))
        }

        private fun getCircleBitmap(bitmap: Bitmap, borderDips: Int, context: Context): Bitmap? {
            val borderSizePx: Int =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, borderDips.toFloat(),
                    context.resources.displayMetrics).toInt()
            val output = Bitmap.createBitmap(bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val color = Color.RED
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val borderColor = Color.parseColor("#87CEEB")
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawOval(rectF, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            paint.color = borderColor
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderSizePx.toFloat()
            canvas.drawOval(rectF, paint)
            bitmap.recycle()
            return output.scale(100, 100)
        }

    fun getmapData(context: Context): List<MapData> {

        lateinit var jsonString: String
        try {
            jsonString = context.assets.open("Places.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        val mapDataType = object : TypeToken<List<MapData>>() {}.type
        return Gson().fromJson(jsonString, mapDataType)
    }


}