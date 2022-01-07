package my.mobile.takeaplace.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import my.mobile.takeaplace.data.model.LoggedInUser
import my.mobile.takeaplace.data.model.RegisterUser
import my.mobile.takeaplace.ui.user.RegisteredUser

class DaftarRepository(val dataSource: DaftarDataSource) {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    // in-memory cache of the loggedInUser object
    private val _users = MutableLiveData<List<RegisterUser>>()
    val users: LiveData<List<RegisterUser>> = _users

    fun isRegistered(user: RegisteredUser): Boolean{
        users.value?.forEach {
            if (user.equals(it)) return true
        }
        return false
    }

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore

        scope.launch {
            _users.value = dataSource.getAllRegisteredUser()
        }

    }

//    fun unRegister(user: RegisteredUser) {
//
//        dataSource.unregister()
//    }

    suspend fun daftar(email: String, username: String, password: String): Result<RegisterUser> {
        // handle daftar
        val result = dataSource.daftar(email, username, password)

        if (result is Result.Success) {
            setRegisterData(result.data)
        }

        return result
    }

    private fun setRegisterData(registerUser: RegisterUser) {
        val list = _users.value as MutableList<RegisterUser>
        list.add(registerUser)

        _users.value = list
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}