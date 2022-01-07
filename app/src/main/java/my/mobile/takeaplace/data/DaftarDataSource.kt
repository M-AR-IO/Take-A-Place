package my.mobile.takeaplace.data

import android.app.Application
import android.content.Context
import kotlinx.coroutines.*
import my.mobile.takeaplace.data.database.RegisterDB
import my.mobile.takeaplace.data.database.RegisterDBDao
import my.mobile.takeaplace.data.model.LoggedInUser
import my.mobile.takeaplace.data.model.RegisterUser
import my.mobile.takeaplace.util.Pengaturan
import java.io.IOException
import java.util.*

class DaftarDataSource(context: Context) {
    private val pengaturan: Pengaturan
    private val dao: RegisterDBDao


    init {
        pengaturan = Pengaturan(context)
        val db = RegisterDB.getInstance(context)
        dao = db.registerDBDao()
    }

    suspend fun getAllRegisteredUser() = dao.getAllUsers()

    suspend fun daftar(email: String, username: String, password: String): Result<RegisterUser> {
        try {
            // TODO: register user
            val registeredUser = dao.getUsername(username)
            if (registeredUser == null){
                val user = RegisterUser( email = email, username = username, password = password)
                dao.insert(user)
                pengaturan.loggedInUsername = username
                return Result.Success(user)
            } else {
                return Result.Error(IOException("Username already exist!"))
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error sign up", e))
        }
    }

//    fun logout() {
//        // TODO: revoke authentication
//    }
}