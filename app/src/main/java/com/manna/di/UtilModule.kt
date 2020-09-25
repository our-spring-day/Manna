package com.manna.di

import com.manna.picker.ImageLoadManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object UtilModule {

    @ActivityScoped
    @Provides
    fun provideImageLoadManager(): ImageLoadManager {
        return ImageLoadManager()
    }
}