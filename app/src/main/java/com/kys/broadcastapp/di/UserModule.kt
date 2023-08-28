package com.kys.broadcastapp.di

import com.kys.broadcastapp.repository.FirebaseRepository
import com.kys.broadcastapp.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UserModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        repository: FirebaseRepository,
        auth: FirebaseAuth
    ):UserRepository{
        return UserRepository(repository,auth)
    }
}