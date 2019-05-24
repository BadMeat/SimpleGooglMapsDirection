package com.example.mapsterusapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext


class SecondoMapsActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val TAG = "so47492459"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondo_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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

        val barcelona = LatLng(41.385064, 2.173403)
        mMap.addMarker(MarkerOptions().position(barcelona).title("Marker in Barcelona"))

        val madrid = LatLng(40.416775, -3.70379)
        mMap.addMarker(MarkerOptions().position(madrid).title("Marker in Madrid"))

        val zaragoza = LatLng(41.648823, -0.889085)

        //Define list to get all latlng for the route
        val path = mutableListOf<LatLng>()


        //Execute Directions API request
        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyCR9ZYk1wcoMsPHjuOqFXliP8Pk39e9EQ0")
            .build()
        val req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379")
        try {
            val res = req.await()

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.isNotEmpty()) {
                val route = res.routes[0]

                if (route.legs != null) {
                    for (i in route.legs.indices) {
                        val leg = route.legs[i]
                        if (leg.steps != null) {
                            for (j in leg.steps.indices) {
                                val step = leg.steps[j]
                                if (step.steps != null && step.steps.isNotEmpty()) {
                                    for (k in step.steps.indices) {
                                        val step1 = step.steps[k]
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            val coords1 = points1.decodePath()
                                            for (coord1 in coords1) {
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        val coords = points.decodePath()
                                        for (coord in coords) {
                                            path.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(SecondoMapsActivity().TAG, ex.localizedMessage)
        }


        //Draw the polyline
        if (path.size > 0) {
            val opts = PolylineOptions().addAll(path).color(Color.BLUE).width(5f)
            mMap.addPolyline(opts)
        }

        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6f))
    }
}
