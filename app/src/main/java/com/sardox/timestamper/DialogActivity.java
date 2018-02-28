package com.sardox.timestamper;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.utils.Utils;

import java.util.List;

public class DialogActivity extends AppCompatActivity {
    ListView listView;
    int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        setTitle("Select default category");
        this.setFinishOnTouchOutside(true);
        setContentView(R.layout.activity_dialog);
        listView = findViewById(R.id.list_categories);

        final DataManager dataManager = new DataManager(this);
        final List<Category> allCategories = dataManager.readCategories();
        List<String> quickCategories = Utils.getListOfCategories(allCategories);

        ListAdapter adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                quickCategories);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JetUUID selectedCategoryId = allCategories.get(position).getCategoryID();
                dataManager.saveDefaultCategoryForWidget(selectedCategoryId);
                Log.v("srdx", "saveDefaultCategoryForWidget: " + allCategories.get(position).getName());
                launchReturnIntent();
            }
        });
        listView.setAdapter(adapter);
    }

    private void launchReturnIntent() {
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
