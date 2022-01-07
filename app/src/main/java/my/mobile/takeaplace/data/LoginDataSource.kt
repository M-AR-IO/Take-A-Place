package my.mobile.takeaplace.data

import android.content.Context
import my.mobile.takeaplace.data.database.RegisterDB
import my.mobile.takeaplace.data.database.RegisterDBDao
import my.mobile.takeaplace.data.model.LoggedInUser
import my.mobile.takeaplace.util.Pengaturan
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(context: Context) {
    private val pengaturan: Pengaturan
    private val dao: RegisterDBDao

    init {
        pengaturan = Pengaturan(context)
        val db = RegisterDB.getInstance(context)
        dao = db.registerDBDao()
    }

    fun isLogedIn(username: String) = pengaturan.loggedInUsername !== "" && username == pengaturan.loggedInUsername

    fun loggedInUsername(): String = pengaturan.loggedInUsername ?: ""

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val registeredUser = dao.getUsername(username)
            if ( registeredUser != null){
                if (registeredUser.password.equals(password)){
                    pengaturan.loggedInUsername = registeredUser.username
                    val user = LoggedInUser(registeredUser.username)
                    return Result.Success(user)
                } else {
                    return Result.Error(IOException("Wrong password!"))
                }
            } else {
                return Result.Error(IOException("Username not registered!"))
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication

        pengaturan.loggedInUsername = ""
    }
}