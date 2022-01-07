package my.mobile.takeaplace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import my.mobile.takeaplace.databinding.ActivityStartBinding

class StartActivity: AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_TakeAPlace_NoActionBar)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}