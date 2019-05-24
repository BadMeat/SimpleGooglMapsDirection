package com.example.mapsterusapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.CameraPosition


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, TaskLoadedCallback {

    private lateinit var mMap: GoogleMap

    private var place1: MarkerOptions? = null
    private var place2: MarkerOptions? = null
    var getDirection: Button? = null
    private var currentPolyline: Polyline? = null

    override fun onTaskDone(vararg values: Any) {
        currentPolyline?.remove()
        currentPolyline = mMap.addPolyline(values[0] as PolylineOptions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        place1 = MarkerOptions().position(LatLng(22.3039, 70.8022)).title("Location 1")
        place2 = MarkerOptions().position(LatLng(23.0225, 72.5714)).title("Location 2")

        getDirection?.setOnClickListener {
            FetchURL(baseContext).execute(getUrl(place1!!.position, place2!!.position, "driving"), "driving")
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d("mylog", "Added Markers")
        mMap.addMarker(place1)
        mMap.addMarker(place2)

        val googlePlex = CameraPosition.builder()
            .target(LatLng(22.7739, 71.6673))
            .zoom(7f)
            .bearing(0f)
            .tilt(45f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 5000, null)
    }

    private fun getUrl(origin: LatLng, dest: LatLng, directionMode: String): String {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Mode
        val mode = "mode=$directionMode"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$mode"
        // Output format
        val output = "json"
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=AIzaSyCR9ZYk1wcoMsPHjuOqFXliP8Pk39e9EQ0AIzaSyCR9ZYk1wcoMsPHjuOqFXliP8Pk39e9EQ0"
    }
}
