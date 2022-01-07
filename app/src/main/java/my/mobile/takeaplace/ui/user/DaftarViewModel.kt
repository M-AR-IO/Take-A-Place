package my.mobile.takeaplace.ui.user

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import my.mobile.takeaplace.R
import my.mobile.takeaplace.data.DaftarRepository
import my.mobile.takeaplace.data.Result

class DaftarViewModel(private val daftarRepository: DaftarRepository) : ViewModel() {
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    private val _daftarForm = MutableLiveData<DaftarFormState>()
    val daftarFormState: LiveData<DaftarFormState> = _daftarForm

    private val _daftarResult = MutableLiveData<DaftarResult>()
    val daftarResult: LiveData<DaftarResult> = _daftarResult


    fun daftar(email: String, username: String, password: String) {
        // can be launched in a separate asynchronous job
        scope.launch {
            val result = daftarRepository.daftar(email, username, password)

            if (result is Result.Success) {
                _daftarResult.value = DaftarResult(success = RegisteredUser(username = result.data.username, password = result.data.password))
            } else {
                _daftarResult.value = DaftarResult(error = R.string.daftar_failed, errorMessage = result.toString())
            }
        }
    }

    fun daftarDataChanged(email: String, username: String, password: String) {
        if (!isEmailValid(email)) {
            _daftarForm.value = DaftarFormState(emailError = R.string.invalid_email)
        } else if (!isUserNameValid(username)) {
            _daftarForm.value = DaftarFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _daftarForm.value = DaftarFormState(passwordError = R.string.invalid_password)
        } else {
            _daftarForm.value = DaftarFormState(isDataValid = true)
        }
    }

    // A placeholder email validation check
    private fun isEmailValid(email: String) = email.contains('@') && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // A placeholder username validation check
    private fun isUserNameValid(username: String) = username.isNotBlank() && username.length > 2

    // A placeholder password validation check
    private fun isPasswordValid(password: String) = password.length > 5
}