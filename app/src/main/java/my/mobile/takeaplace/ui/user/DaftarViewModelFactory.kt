package my.mobile.takeaplace.ui.user

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import my.mobile.takeaplace.data.DaftarDataSource
import my.mobile.takeaplace.data.DaftarRepository

class DaftarViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DaftarViewModel::class.java)){
            return DaftarViewModel(
                daftarRepository = DaftarRepository(
                    DaftarDataSource(context)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}