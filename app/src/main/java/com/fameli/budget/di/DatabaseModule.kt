package com.fameli.budget.di

import android.content.Context
import androidx.room.Room
import com.fameli.budget.data.local.FameliDatabase
import com.fameli.budget.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FameliDatabase =
        Room.databaseBuilder(context, FameliDatabase::class.java, "fameli_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideTransactionDao(db: FameliDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideCategoryDao(db: FameliDatabase): CategoryDao = db.categoryDao()
    @Provides fun provideBudgetDao(db: FameliDatabase): BudgetDao = db.budgetDao()
    @Provides fun provideTaskDao(db: FameliDatabase): TaskDao = db.taskDao()
    @Provides fun provideGoalDao(db: FameliDatabase): GoalDao = db.goalDao()
    @Provides fun provideShoppingDao(db: FameliDatabase): ShoppingDao = db.shoppingDao()
}
