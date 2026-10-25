package com.fameli.budget.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE budgets ADD COLUMN categoryName TEXT NOT NULL DEFAULT ''")

        db.execSQL("""
            DELETE FROM budgets WHERE id NOT IN (
                SELECT MIN(id) FROM budgets GROUP BY cloudId
            ) AND cloudId != ''
        """.trimIndent())

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_budgets_cloudId ON budgets(cloudId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_month_categoryName ON budgets(month, categoryName)")
    }
}
