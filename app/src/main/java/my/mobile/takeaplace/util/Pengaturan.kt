package my.mobile.takeaplace.util

import android.content.Context

class Pengaturan(context: Context): PengaturanBantuan(context, NAMA) {
    companion object{
        const val NAMA = "Pengaturan untuk aplikasi"
    }


    var usernames by stringSetPref()

    var loggedInUsername by stringPref()


}