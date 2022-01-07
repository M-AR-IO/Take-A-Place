package my.mobile.takeaplace.ui.user

data class DaftarResult (
    val success: RegisteredUser? = null,
    val error: Int? = null,
    val errorMessage: String = ""
)