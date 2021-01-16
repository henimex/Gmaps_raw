package com.devhen.gmapsv2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locM: LocationManager
    private lateinit var locL: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(dinleyici)

        //konumGoster()
        //39.43897930577555, 31.52645556956694
        /*val sivrihisar = LatLng(39.43897930577555, 31.52645556956694)
        mMap.addMarker(MarkerOptions().position(sivrihisar).title("HenimeX Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sivrihisar, 15f))*/

        locM = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locL = object : LocationListener{
            override fun onLocationChanged(location: Location) {
                //println("Lat:${location.latitude} Len: ${location.longitude}")
                val guncelKonum = LatLng(location.latitude,location.longitude)
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(guncelKonum).title("Güncel Konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(guncelKonum,15f))

                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())

                try {
                    val adresList = geocoder.getFromLocation(location.latitude,location.longitude,1)
                    if (adresList.size > 0){
                        println(adresList.get(0).toString())
                    }

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locM.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locL)
            val sonBilinenKonum = locM.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonBilinenKonum != null){
                val sonBilinenLatLng = LatLng(sonBilinenKonum.latitude,sonBilinenKonum.longitude)
                mMap.addMarker(MarkerOptions().position(sonBilinenLatLng).title("Son Bilinen Konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatLng,15f))
            }
        }
    }

    fun konumGoster(googleMap: GoogleMap) {
        val sivrihisar = LatLng(39.43897930577555, 31.52645556956694)
        mMap.addMarker(MarkerOptions().position(sivrihisar).title("HenimeX Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sivrihisar, 15f))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1){
            if (grantResults.size > 0){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locM.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locL)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val dinleyici = object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {
            mMap.clear()
            val geocoder = Geocoder(this@MapsActivity,Locale.getDefault())
            if (p0 != null){
                var adres = ""
                try {
                    val adresListesi = geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if (adresListesi.size > 0){
                        if (adresListesi.get(0).thoroughfare != null){
                            adres += adresListesi.get(0).thoroughfare
                            if (adresListesi.get(0).subThoroughfare != null){
                                adres += adresListesi.get(0).subThoroughfare
                            }
                        }
                        adresListesi.get(0).thoroughfare
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }

                mMap.addMarker(MarkerOptions().position(p0).title(adres))
            }
        }

    }
}