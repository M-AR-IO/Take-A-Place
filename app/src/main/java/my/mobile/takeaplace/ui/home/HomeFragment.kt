package my.mobile.takeaplace.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.Annotation
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import my.mobile.takeaplace.R
import my.mobile.takeaplace.databinding.FragmentHomeBinding
import my.mobile.takeaplace.lokasi.LocationChangeCallback

class HomeFragment : Fragment(), OnMapReadyCallback, PermissionsListener {
    lateinit var mapboxMap: MapboxMap

    private lateinit var permissionManager: PermissionsManager
    private var mintaijinLokasi: Boolean = false

    private lateinit var locationEngine: LocationEngine
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5
    private val callback: LocationChangeCallback = LocationChangeCallback(this)

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

//        Mapbox.getInstance(requireContext(),BuildConfig.MAPBOX_TOKEN)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // jangan minta ijin lokasi jika sudah diijinkan
        mintaijinLokasi = !PermissionsManager.areLocationPermissionsGranted(context)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if(this::locationEngine.isInitialized){
            locationEngine.removeLocationUpdates(callback)
        }
        binding.mapView.onDestroy()
        _binding = null
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        this.mapboxMap.setStyle(Style.TRAFFIC_DAY) {
            enableLocationComponent(it)
        }

        this.mapboxMap.addOnMapLongClickListener { point: LatLng ->
            addMarker(point)
            true
        }
    }

    private fun addMarker(latLng: LatLng){
        bitmapFromDrawableRes(requireContext(),R.drawable.ic_marker)?.let {
            val simbolLayer = ArrayList<Feature>()
            simbolLayer.add(Feature.fromGeometry(Point.fromLngLat(latLng.longitude,latLng.latitude)))
            mapboxMap.setStyle(
                Style.Builder().fromUri(Style.TRAFFIC_DAY)
                    .withImage(ICON_KEY,it)
                    .withSource(GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(simbolLayer)))
                    .withLayer(SymbolLayer(LAYER_ID, SOURCE_ID).withProperties(iconImage(ICON_KEY), iconSize(0.4f),
                        iconAllowOverlap(true), iconIgnorePlacement(true)
                    ))
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun enableLocationComponent(loadedMapStyle: Style){
        if(PermissionsManager.areLocationPermissionsGranted(context)){
            val locationComponent = mapboxMap.locationComponent

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(requireContext(),loadedMapStyle)
                .useDefaultLocationEngine(false)
                .build()

            locationComponent.activateLocationComponent(locationComponentActivationOptions)

            locationComponent.isLocationComponentEnabled = true

            locationComponent.cameraMode = CameraMode.TRACKING

            locationComponent.renderMode = RenderMode.COMPASS

            initLocationEngine()
        } else if (mintaijinLokasi){
//            permissionManager = PermissionsManager(this)
//            permissionManager.requestLocationPermissions(requireActivity())
        }
    }
    @SuppressLint("MissingPermission")
    private fun initLocationEngine(){
        locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())
        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
            .build()
        locationEngine.requestLocationUpdates(request,callback, Looper.getMainLooper())
        locationEngine.getLastLocation(callback)
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        permissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults)
//    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(requireContext(), R.string.user_location_permission_explanation,Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        mintaijinLokasi = false
        mapboxMap.getStyle{
            enableLocationComponent(it)
        }

        if(false) {
            Toast.makeText(requireContext(),R.string.user_location_permission_not_granted,Toast.LENGTH_LONG).show()
//            finish()
        }
    }

    /**
     * LIFECYCLE
     */

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        binding.mapView.onDestroy()
//    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) = convertDrawableToBitmap(AppCompatResources.getDrawable(context,resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap?{
        if (sourceDrawable == null) return null
        return if (sourceDrawable is BitmapDrawable){
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0,0,canvas.width,canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
    companion object{
        private const val SOURCE_ID = "source-id"
        private const val LAYER_ID = "layer-id"
        private const val ICON_KEY = "icon-key"
    }
}