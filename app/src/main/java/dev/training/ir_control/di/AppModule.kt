package dev.training.ir_control.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.training.ir_control.data.PresetRepository
import dev.training.ir_control.data.dao.CommandDao
import dev.training.ir_control.data.dao.DeviceDao
import dev.training.ir_control.data.db.AppDatabase
import dev.training.ir_control.ir.IrController
import dev.training.ir_control.ir.IrControllerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideIrController(@ApplicationContext ctx: Context): IrController = IrControllerImpl(ctx)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "ir_control_database")
            .fallbackToDestructiveMigration(false)
                .build()
    }

    @Provides fun provideDeviceDao(database: AppDatabase): DeviceDao = database.deviceDao()

    @Provides fun provideCommandDao(database: AppDatabase): CommandDao = database.commandDao()

    @Provides
    @Singleton
    fun providePresetRepository(deviceDao: DeviceDao, commandDao: CommandDao): PresetRepository =
            PresetRepository(deviceDao, commandDao)
}
