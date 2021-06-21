package com.han0n.planid.widget;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.han0n.planid.datos.NotaMDL;
import com.han0n.planid.R;

import java.util.ArrayList;

public class NotaProvider implements RemoteViewsService.RemoteViewsFactory{

    private static ArrayList<NotaMDL> widgetListNotas = new ArrayList<>();
    Context context;
    Intent intent;

    /* Se usa cuando se cargan las notas en Listado */
    public static void setNotasWidget(ArrayList<NotaMDL> notas){
        NotaProvider.widgetListNotas = notas;
    }

    public NotaProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        //inicializarDatos();
        SystemClock.sleep(3000);
        if(widgetListNotas.size()==0){
            Toast.makeText(context, context.getResources().getText(R.string.widget_sin_notas), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onDataSetChanged() {
        //inicializarDatos();
        SystemClock.sleep(3000);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.d("AAA", "" + widgetListNotas.size());
        return widgetListNotas.size();
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_nota);
        String textoWidget = "<b>" + widgetListNotas.get(position).getActividad() + "</b>";

        if (widgetListNotas.get(position).getDescripcion().equals("")){

        }else {
            textoWidget += "<br>";
            if(widgetListNotas.get(position).getDescripcion().length()>99)
                textoWidget += "<i>" + widgetListNotas.get(position).getDescripcion()
                        .substring(0, 99) + "...</i>";
            else
                textoWidget += "<i>" + widgetListNotas.get(position).getDescripcion()+ "</i>";
        }
        views.setTextViewText(R.id.widgetTxtNota, Html.fromHtml(textoWidget));

        SystemClock.sleep(500);
        return views;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

}
