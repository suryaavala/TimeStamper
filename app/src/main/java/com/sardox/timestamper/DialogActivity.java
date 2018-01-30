package com.sardox.timestamper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.types.JetUUID;

import java.util.ArrayList;
import java.util.List;

public class DialogActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Select default category");
        this.setFinishOnTouchOutside(true);
        setContentView(R.layout.activity_dialog);
        listView = (ListView) findViewById(R.id.list_categories);

        final DataManager dataManager = new DataManager(this);
        final List<Category> categoryList = dataManager.readCategories();
        List<String> quickCategories = new ArrayList<>();//

        for (int a = 0; a < categoryList.size(); a++) {
            quickCategories.add(categoryList.get(a).getName());
        }

        ListAdapter adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                quickCategories );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JetUUID selectedCategoryId = categoryList.get(position).getCategoryID();
                dataManager.saveDefaultCategoryForWidget(selectedCategoryId);
                Log.e("srdx", "saveDefaultCategoryForWidget: " + categoryList.get(position).getName());
                finishAffinity();
            }
        });
        listView.setAdapter(adapter);
    }
}
