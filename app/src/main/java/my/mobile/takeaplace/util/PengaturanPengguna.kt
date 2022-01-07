package my.mobile.takeaplace.util

import android.content.Context

class PengaturanPengguna(context: Context, username: String): PengaturanBantuan(context, username) {
    var password by stringPref()
    var email by stringPref()
    var profileUri by stringPref()
}