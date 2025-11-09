package dev.training.ir_control.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.training.ir_control.BuildConfig
import dev.training.ir_control.data.PresetRepository
import dev.training.ir_control.data.dao.CommandDao
import dev.training.ir_control.data.dao.DeviceDao
import dev.training.ir_control.data.db.AppDatabase
import dev.training.ir_control.data.remote.RemoteApiService
import dev.training.ir_control.data.remote.RemoteDataSource
import dev.training.ir_control.ir.IrController
import dev.training.ir_control.ir.IrControllerImpl
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build()
    }

    @Provides fun provideDeviceDao(database: AppDatabase): DeviceDao = database.deviceDao()

    @Provides fun provideCommandDao(database: AppDatabase): CommandDao = database.commandDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        return OkHttpClient.Builder().addInterceptor(logging).build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
            Retrofit.Builder()
                    .baseUrl(BuildConfig.REMOTE_API_BASE_URL)
                    .client(client)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()

    @Provides
    @Singleton
    fun provideRemoteApiService(retrofit: Retrofit): RemoteApiService =
            retrofit.create(RemoteApiService::class.java)

    @Provides
    @Singleton
    fun providePresetRepository(
            deviceDao: DeviceDao,
            commandDao: CommandDao,
            remoteDataSource: RemoteDataSource
    ): PresetRepository = PresetRepository(deviceDao, commandDao, remoteDataSource)
}
