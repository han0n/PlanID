package com.han0n.planid.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class NotaService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new NotaProvider(this, intent);
    }
}