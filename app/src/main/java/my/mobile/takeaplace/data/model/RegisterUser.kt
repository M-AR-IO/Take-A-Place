package my.mobile.takeaplace.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabel_pengguna_terdaftar")
data class RegisterUser(
    @PrimaryKey(autoGenerate = true) var userId: Int = 0,
    @ColumnInfo(name = "username") var username: String = "",
    @ColumnInfo(name = "email") var email: String = "",
    @ColumnInfo(name = "password") var password: String = ""
)
