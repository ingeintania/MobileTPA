package com.example.mobiletpa.activities.ui.order

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mobiletpa.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentTwo : Fragment(), OnMapReadyCallback, LocationListener, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {
//
//    private val INITIAL_PERMS = arrayOf(
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.READ_CONTACTS
//    )

    private lateinit var mMap: GoogleMap

    private var latitude:Double=0.toDouble()
    private var longitude:Double=0.toDouble()

    private lateinit var mLastLocation: Location
    private var mMarker:Marker?=null

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    lateinit var i1 : ImageView

    companion object{
        private const val MY_PERMISSION_CODE: Int = 1000
    }

    lateinit var mGoogleMap: GoogleMap
    lateinit var mMapView : MapView
    lateinit var mView : View
    private val INITIAL_REQUEST = 1337

    lateinit var myPosition : CameraPosition

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_two, container, false)

        i1 = mView.findViewById(R.id.imageView)
        return mView;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMapView = mView.findViewById(R.id.map)

        if(mMapView!=null){
            mMapView.onCreate(null)
            mMapView.onResume()
            mMapView.getMapAsync(this)
        }


        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(checkLocationPermission()){
                buildLocationRequest()
                buildLocationCallBack()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.activity!!);
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            }
        }else{
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.activity!!);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }

    private fun checkLocationPermission():Boolean{
        if(ContextCompat.checkSelfPermission(this.activity!!, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this.activity!!, android.Manifest.permission.ACCESS_FINE_LOCATION))
                requestPermissions(arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE
                )
            else
                requestPermissions(arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE
                )
            return false
        }
        else
            return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MY_PERMISSION_CODE ->{
                if(grantResults.size>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this.activity!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        if(checkLocationPermission()){

                            buildLocationRequest()
                            buildLocationCallBack()

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.activity!!);
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

                            mMap.isMyLocationEnabled=true
                        }

                }else{
                    Toast.makeText(this.activity!!, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun buildLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun buildLocationCallBack(){
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations.get(p0.locations.size-1) //get last location

                if(mMarker != null){
                    mMarker!!.remove()
                }

                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude,longitude)
                val markerOptions = MarkerOptions().position(latLng).title("My Position").icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))

                //Move Camera
                mMarker = mMap.addMarker(markerOptions)
                mMap.addMarker(MarkerOptions().position(LatLng(-6.370325, 106.833352)).title("Pandawa").snippet("Jl. Margonda Raya No.397A, Pondok Cina, Kecamatan Beji, Kota Depok, Jawa Barat 16424"))
                mMap.addMarker(MarkerOptions().position(LatLng(-6.363187, 106.833359)).title("Cano").snippet("Jl. Margonda Raya No.495, Pondok Cina, Kecamatan Beji, Kota Depok, Jawa Barat 16424"))
                mMap.addMarker(MarkerOptions().position(LatLng(-6.370558, 106.833263)).title("Aladdin").snippet("Jl. Margonda Raya No.393, Pondok Cina, Kecamatan Beji, Kota Depok, Jawa Barat 16431"))

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))

                mMap.setOnCameraIdleListener {
                    GoogleMap.OnCameraIdleListener {
                        val center : LatLng = mMap.cameraPosition.target

                        if(mMarker!= null){
                            mMarker!!.remove()

                            mMarker = mMap.addMarker(MarkerOptions().position(center).title("New Position"))

                            var startLatLng = mMarker!!.position

                        }
                    }
                }

            }


        }
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this.activity!!, "Current location:\n" + p0, Toast.LENGTH_LONG).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

//        mMap.isMyLocationEnabled = true
//        mMap.uiSettings.isMyLocationButtonEnabled=true
//        mMap.setOnMyLocationButtonClickListener { true }
//        mMap.setOnMyLocationClickListener { true }

//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
//            if(ContextCompat.checkSelfPermission(this.activity!!, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
//                mMap.isMyLocationEnabled = true
//                mMap.uiSettings.isMyLocationButtonEnabled=true
//                mMap.setOnMyLocationButtonClickListener { true }
//                mMap.setOnMyLocationClickListener { true }
//
//            }
//        }
//        else{
//            mMap.isMyLocationEnabled=true
//            mMap.uiSettings.isMyLocationButtonEnabled=true
//            mMap.setOnMyLocationButtonClickListener { true }
//            mMap.setOnMyLocationClickListener { true }
//        }

        mMap.uiSettings.isZoomControlsEnabled=true
    }

    override fun onLocationChanged(p0: Location?) {

        mMap.setOnCameraIdleListener {
            GoogleMap.OnCameraIdleListener {
                val center : LatLng = mMap.cameraPosition.target

                if(mMarker!= null){
                    mMarker!!.remove()

                    mMarker = mMap.addMarker(MarkerOptions().position(center).title("New Position"))

                    var startLatLng = mMarker!!.position

                }
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        TODO("Not yet implemented")
    }

    override fun onStop() {
        buildLocationCallBack()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.activity!!)

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()

    }


}
