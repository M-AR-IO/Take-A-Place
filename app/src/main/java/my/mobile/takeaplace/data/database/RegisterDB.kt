package my.mobile.takeaplace.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import my.mobile.takeaplace.data.model.RegisterUser

@Database(entities = arrayOf(RegisterUser::class), version = 1, exportSchema = false)
abstract class RegisterDB:RoomDatabase() {
    abstract fun registerDBDao(): RegisterDBDao

    companion object{
        private var INSTANCE: RegisterDB? = null

        fun getInstance(context: Context): RegisterDB{
            synchronized(this){
                var instance = INSTANCE
                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RegisterDB::class.java,
                        "db_pengguna"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}