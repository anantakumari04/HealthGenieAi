package com.example.healthgenieai.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.healthgenieai.R
import com.example.healthgenieai.BuildConfig
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.*

class HospitalMapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_hospital_map, container, false)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(),  BuildConfig.MAPS_API_KEY)
        }

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        mMap.isMyLocationEnabled = true

        val fusedLocationClient = LocationServices
            .getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

            location?.let {

                val userLatLng = LatLng(it.latitude, it.longitude)

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14f))

                searchNearbyHospitals(userLatLng)

            }
        }
    }

    private fun searchNearbyHospitals(userLocation: LatLng) {

        val placesClient = Places.createClient(requireContext())

        val request = FindCurrentPlaceRequest.newInstance(
            listOf(com.google.android.libraries.places.api.model.Place.Field.NAME,
                com.google.android.libraries.places.api.model.Place.Field.LAT_LNG)
        )

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        placesClient.findCurrentPlace(request)
            .addOnSuccessListener { response ->

                for (placeLikelihood in response.placeLikelihoods) {

                    val place = placeLikelihood.place

                    if (place.name?.contains("Hospital", true) == true) {

                        place.latLng?.let {

                            mMap.addMarker(
                                MarkerOptions()
                                    .position(it)
                                    .title(place.name)
                                    .icon(
                                        BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_RED
                                        )
                                    )
                            )
                        }
                    }
                }
            }
    }
}