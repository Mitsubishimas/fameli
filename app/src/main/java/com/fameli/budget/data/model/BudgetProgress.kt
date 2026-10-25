package com.fameli.budget.data.model

data class BudgetProgress(
    val budgetId: Long,
    val categoryName: String,
    val limitAmount: Double,
    val month: String,
    val spent: Double,
    val remaining: Double,
    val progress: Float,
    val isExceeded: Boolean,
    val overAmount: Double,
    val alertThreshold: Float
) {
    val isNearLimit: Boolean
        get() = !isExceeded && progress >= alertThreshold
}
