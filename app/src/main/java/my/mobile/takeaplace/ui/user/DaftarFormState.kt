package my.mobile.takeaplace.ui.user

data class DaftarFormState (
    val emailError: Int? = null,
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)