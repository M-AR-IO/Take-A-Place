package my.mobile.takeaplace.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import my.mobile.takeaplace.data.model.RegisterUser

@Dao
interface RegisterDBDao {
    @Insert
    suspend fun insert(registerUser: RegisterUser)

    @Query("SELECT * FROM tabel_pengguna_terdaftar ORDER BY userId DESC")
    suspend fun getAllUsers(): List<RegisterUser>

    @Query("SELECT * FROM tabel_pengguna_terdaftar WHERE username LIKE :username")
    suspend fun getUsername(username: String): RegisterUser?
}