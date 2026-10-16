package com.fit10.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fit10.app.core.security.DatabaseKeyManager
import com.fit10.app.data.db.converter.Fit10TypeConverters
import com.fit10.app.data.db.dao.ExerciseDao
import com.fit10.app.data.db.dao.ProgramDao
import com.fit10.app.data.db.dao.ReminderDao
import com.fit10.app.data.db.dao.UserProfileDao
import com.fit10.app.data.db.dao.WorkoutDao
import com.fit10.app.data.db.dao.WorkoutResultDao
import com.fit10.app.data.db.entity.ExerciseEntity
import com.fit10.app.data.db.entity.ProgramDayEntity
import com.fit10.app.data.db.entity.ProgramEntity
import com.fit10.app.data.db.entity.ReminderEntity
import com.fit10.app.data.db.entity.UserPlanEntity
import com.fit10.app.data.db.entity.UserProfileEntity
import com.fit10.app.data.db.entity.WorkoutEntity
import com.fit10.app.data.db.entity.WorkoutExerciseEntity
import com.fit10.app.data.db.entity.WorkoutResultEntity
import com.fit10.app.data.db.migration.SqlCipherMigrationHelper
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [
        UserProfileEntity::class,
        ExerciseEntity::class,
        WorkoutEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutResultEntity::class,
        ProgramEntity::class,
        ProgramDayEntity::class,
        UserPlanEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Fit10TypeConverters::class)
abstract class Fit10Database : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutResultDao(): WorkoutResultDao
    abstract fun programDao(): ProgramDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "fit10.db"

        @Volatile
        private var instance: Fit10Database? = null

        fun getInstance(
            context: Context,
            customPassphrase: ByteArray? = null
        ): Fit10Database {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context.applicationContext, customPassphrase).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context, customPassphrase: ByteArray?): Fit10Database {
            val passphrase = customPassphrase ?: DatabaseKeyManager.getOrCreateDatabasePassphrase(context)
            SqlCipherMigrationHelper.migrateIfPlaintext(context, DATABASE_NAME, passphrase)

            val openHelperFactory = SupportOpenHelperFactory(passphrase)
            return Room.databaseBuilder(
                context,
                Fit10Database::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(openHelperFactory)
                .build()
        }

        fun resetInstance() {
            instance = null
        }
    }
}
