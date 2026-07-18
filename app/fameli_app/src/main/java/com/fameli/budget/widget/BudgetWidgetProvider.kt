package com.fameli.budget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.fameli.budget.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BudgetWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_budget)
            views.setTextViewText(R.id.widget_balance, "Fameli")
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
