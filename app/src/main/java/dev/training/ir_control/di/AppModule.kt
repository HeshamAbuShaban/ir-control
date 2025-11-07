package dev.training.ir_control.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.training.ir_control.ir.IrController
import dev.training.ir_control.ir.IrControllerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideIrController(@ApplicationContext ctx: Context): IrController =
        IrControllerImpl(ctx)
}
