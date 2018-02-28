package com.sardox.timestamper.utils;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Category;

import java.util.ArrayList;
import java.util.List;


public class StackWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<Category> widgetCategories = new ArrayList<>();
    private Context mContext;
    List<TimestampIcon> icons;

    public StackRemoteViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
    }

    private void readCategories() {
        if (mContext != null) {
            DataManager dataManager = new DataManager(mContext);
            widgetCategories = dataManager.readCategories();
        }
    }

    @Override
    public void onCreate() {
        icons = Utils.getStockIcons();
        readCategories();
    }

    @Override
    public void onDataSetChanged() {
        readCategories();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return widgetCategories.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        Category category = widgetCategories.get(position);
        rv.setTextViewText(R.id.categoryName, category.getName());
        rv.setImageViewResource(R.id.categoryIcon, icons.get(category.getIcon_id()).getDrawable_id());

        Bundle extras = new Bundle();
        extras.putInt(GridWidget.EXTRA_ITEM, position);
        extras.putString(GridWidget.CATEGORY_TO_ADD, category.getCategoryID().toString());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_grid_item, fillInIntent);
        return rv;
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
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}