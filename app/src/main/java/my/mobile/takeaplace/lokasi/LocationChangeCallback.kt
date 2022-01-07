package my.mobile.takeaplace.lokasi

import android.widget.Toast
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import my.mobile.takeaplace.ui.home.HomeFragment
import java.lang.Exception
import java.lang.ref.WeakReference

class LocationChangeCallback(
    fragment: HomeFragment?
) : LocationEngineCallback<LocationEngineResult>{
    private val activityWeakReference: WeakReference<HomeFragment>

    override fun onSuccess(result: LocationEngineResult) {
        val activity: HomeFragment? = activityWeakReference.get()

        if (activity != null){
            val location = result.lastLocation ?: return

//            Toast.makeText(activity.context,String)

            if (activity.mapboxMap != null && result.lastLocation != null){
                activity.mapboxMap.locationComponent.forceLocationUpdate(result.lastLocation)
            }
        }
    }

    override fun onFailure(exception: Exception) {
        val activity: HomeFragment? = activityWeakReference.get()
        if (activity != null){
            Toast.makeText(activity.context,exception.localizedMessage,Toast.LENGTH_SHORT).show()
        }
    }

    init {
        activityWeakReference = WeakReference(fragment)
    }

}