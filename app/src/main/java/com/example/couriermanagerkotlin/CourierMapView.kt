package com.example.couriermanagerkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

var mapView: MapView? = null

class CourierMapView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courier_map_view)

        mapView = findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS)
    }

    
}