package com.sardox.timestamper;


import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.MenuItem;

import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.Managers.TimeStampManager;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.recyclerview.MyRecyclerViewAdapter;
import com.sardox.timestamper.recyclerview.MyRecyclerViewAdapterCategory;
import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.utils.VerticalSpaceItemDecoration;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int RC_READWRITE = 9863;
    private TimeStampManager timeStampManager;
    private DataManager dataManager;
    private AppSettings appSettings;

    public RecyclerView recyclerViewTimestamps;
    public MyRecyclerViewAdapter adapter;

    public RecyclerView recyclerViewCategory;
    public MyRecyclerViewAdapterCategory adapterCategory;

    private List<Timestamp> timestamps;
    private List<Category> categories;

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /*
    1. verify permissions
    2. check first start
    2-init.  apply default setting. populate default categories. add sample timestamp.
    2-repeat. read user setting.  apply user settings . read categories, timestamps
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("stamper", "-----------NEW RUN--------------");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this, perms)) {
            initApp();
        } else {
            Log.v("test", " EasyPermissions not granted. Requesting Permissions...");
            EasyPermissions.requestPermissions(this, "App needs to write to storage",
                    RC_READWRITE, perms);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.new_timestamp_created), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @AfterPermissionGranted(RC_READWRITE)
    private void initApp(){
        DataManager dataManager = new DataManager();

        appSettings =  dataManager.readSettings();
        timestamps = dataManager.readTimestamps();
        categories = dataManager.readCategories();

        initRecyclerView(timestamps, categories);
        //timeStampManager = new TimeStampManager();
    }

    private void initRecyclerView(List<Timestamp> timestamps, List<Category> categories) {

        adapterCategory = new MyRecyclerViewAdapterCategory(categories);

        recyclerViewCategory = (RecyclerView) findViewById(R.id.recyclerViewCat);
        recyclerViewCategory.setAdapter(adapterCategory);
        recyclerViewCategory.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManagerCat = new LinearLayoutManager(this);
        linearLayoutManagerCat.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCategory.setLayoutManager(linearLayoutManagerCat);

        RecyclerView.ItemAnimator itemAnimatorCat = new DefaultItemAnimator();
        recyclerViewCategory.setItemAnimator(itemAnimatorCat);


        recyclerViewTimestamps = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new MyRecyclerViewAdapter(timestamps);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerViewTimestamps.setAdapter(adapter);
        recyclerViewTimestamps.addItemDecoration(new VerticalSpaceItemDecoration(10));
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewTimestamps.setLayoutManager(linearLayoutManager);
        recyclerViewTimestamps.setItemAnimator(itemAnimator);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return true;
    }


    public void saveData() {
    }

    public void loadData() {
    }

}
