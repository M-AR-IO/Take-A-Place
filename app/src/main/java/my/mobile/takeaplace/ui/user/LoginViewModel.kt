package my.mobile.takeaplace.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import my.mobile.takeaplace.data.LoginRepository
import my.mobile.takeaplace.data.Result

import my.mobile.takeaplace.R

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        scope.launch {
            val result = loginRepository.login(username, password)

            if (result is Result.Success) {
                _loginResult.value = LoginResult(success = LoggedInUserView(result.data.username))
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed, errorMessage = result.toString())
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String) = username.isNotBlank() && username.length > 2

    // A placeholder password validation check
    private fun isPasswordValid(password: String) = password.length > 5
}