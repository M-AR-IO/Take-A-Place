package my.mobile.takeaplace.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import my.mobile.takeaplace.BuildConfig
import my.mobile.takeaplace.R
import my.mobile.takeaplace.util.Converter

abstract class MapboxModel: Fragment(), OnMapReadyCallback, LocationEngineCallback<LocationEngineResult> {
    var mapboxMap: MapboxMap? = null
    private val MAP_STYLE = Style.TRAFFIC_DAY
    private val markers = mutableListOf<LatLng>()

    private lateinit var locationEngine: LocationEngine

    companion object {
        const val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
        const val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5

        const val MAX_MARKER_TO_SHOW = 2

        private const val SOURCE_ID = "source-id"
        private const val LAYER_ID = "layer-id"
        private const val ICON_KEY = "icon-key"

        const val ICON_SIZE = 0.4f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init mapbox with token
        Mapbox.getInstance(requireContext(), BuildConfig.MAPBOX_TOKEN)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap?.setStyle(MAP_STYLE){
            enableLocationComponent(it)
        }
        this.mapboxMap?.addOnMapLongClickListener { point: LatLng ->
            addMarker(point)
            true
        }
    }
    fun initMapLocationComponent(){
        mapboxMap?.getStyle {
            enableLocationComponent(it)
        }
    }

    @SuppressLint("MissingPermission")
    protected fun enableLocationComponent(loadedMapStyle: Style){
        if(PermissionsManager.areLocationPermissionsGranted(context)){
            val locationComponent = mapboxMap!!.locationComponent

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(requireContext(),loadedMapStyle)
                .useDefaultLocationEngine(false)
                .build()

            locationComponent.activateLocationComponent(locationComponentActivationOptions)

            locationComponent.isLocationComponentEnabled = true

            locationComponent.cameraMode = CameraMode.TRACKING

            locationComponent.renderMode = RenderMode.COMPASS

            initLocationEngine()
        }
    }
    @SuppressLint("MissingPermission")
    private fun initLocationEngine(){
        locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())
        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
            .build()
        locationEngine.requestLocationUpdates(request,this, Looper.getMainLooper())
        locationEngine.getLastLocation(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(this::locationEngine.isInitialized){
            locationEngine.removeLocationUpdates(this)
        }
    }

    protected fun addMarker(point: LatLng){
        var maksMarker: Int = MAX_MARKER_TO_SHOW
        if (PermissionsManager.areLocationPermissionsGranted(context)){
            val lastLocation = mapboxMap!!.locationComponent.lastKnownLocation

            if (lastLocation != null){
                maksMarker -= 1
            }
        }

        while (markers.size >= maksMarker){
            markers.removeFirst()
        }

        markers.add(point)

        showMarkers()
    }
    protected fun showMarkers(){
        Converter.bitmapFromDrawableRes(requireContext(), R.drawable.ic_marker)?.let {
            val simbolLayer = ArrayList<Feature>()
            markers.forEach {
                simbolLayer.add(Feature.fromGeometry(Point.fromLngLat(it.longitude,it.latitude)))
            }

            mapboxMap?.setStyle(
                Style.Builder().fromUri(MAP_STYLE)
                    .withImage(ICON_KEY,it)
                    .withSource(GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(simbolLayer)))
                    .withLayer(
                        SymbolLayer(LAYER_ID, SOURCE_ID).withProperties(
                            PropertyFactory.iconImage(ICON_KEY), PropertyFactory.iconSize(ICON_SIZE),
                            PropertyFactory.iconAllowOverlap(true), PropertyFactory.iconIgnorePlacement(true)
                    ))
            )
        }
    }

    /**
     * OnLocationChange listener
     */
    override fun onSuccess(result: LocationEngineResult) {
        val location = result.lastLocation ?: return

        if (mapboxMap != null && result.lastLocation != null){
            mapboxMap?.locationComponent?.forceLocationUpdate(location)
        }
    }

    override fun onFailure(exception: Exception) {
        Toast.makeText(context,exception.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}