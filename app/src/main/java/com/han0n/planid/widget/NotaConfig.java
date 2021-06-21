package com.han0n.planid.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.han0n.planid.R;

public class NotaConfig extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_listado);
            Intent intent = new Intent(context, NotaService.class);
            views.setRemoteAdapter(R.id.widgetStackView,intent);
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_listado);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onEnabled(Context context) {
        Toast.makeText(context, context.getResources().getText(R.string.widget_cargando), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Toast.makeText(context, context.getResources().getText(R.string.widget_eliminado), Toast.LENGTH_LONG).show();
    }



}