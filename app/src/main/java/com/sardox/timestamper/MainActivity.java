package com.sardox.timestamper;


import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;

import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.Managers.TimeStampManager;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.recyclerview.MyRecyclerViewAdapter;
import com.sardox.timestamper.recyclerview.MyRecyclerViewAdapterCategory;
import com.sardox.timestamper.recyclerview.MyRecyclerViewIconPicker;
import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.utils.Consumer;
import com.sardox.timestamper.utils.TimestampIcon;
import com.sardox.timestamper.utils.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int RC_READWRITE = 9863;
    private TimeStampManager timeStampManager;
    private DataManager dataManager;
    private AppSettings appSettings;

    private Consumer<Category> categoryUpdate;
    public RecyclerView recyclerViewTimestamps;
    public MyRecyclerViewAdapter adapter;

    public RecyclerView recyclerViewCategory;
    public MyRecyclerViewAdapterCategory adapterCategory;

    private Category lastSelectedCategory = Category.Default;

    private List<Timestamp> unfilteredTimestamps;
    private List<Category> categories;

    private List<TimestampIcon> icons;

    @Override
    protected void onResume() {
        super.onResume();
        // loadData();
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
        Log.e("stamper", "-----------NEW RUN--------------");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataManager = new DataManager(getApplicationContext());
        appSettings = dataManager.readSettings();

        //   if (appSettings.isUseDark()) setTheme(R.style.AppThemeCustomMaterialDark); else setTheme(R.style.AppThemeCustom);
        setupDrawer(toolbar);

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
                Timestamp newTimestamp = timeStampManager.createTimestamp(lastSelectedCategory);
                unfilteredTimestamps.add(newTimestamp); //adding timestamp to main list
                adapter.add(newTimestamp);

                Snackbar.make(view, getString(R.string.new_timestamp_created) + " in " + lastSelectedCategory.getName(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @AfterPermissionGranted(RC_READWRITE)
    private void initApp() {
        init_icons();
        unfilteredTimestamps = dataManager.readTimestamps();
        categories = dataManager.readCategories();

        initRecyclerView(unfilteredTimestamps, categories);
        timeStampManager = new TimeStampManager();
    }

    private void init_icons() {
        icons = new ArrayList<>();
        icons.add(new TimestampIcon(R.drawable.category_default, "Default", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_baby, "Baby", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_sport, "Sport", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_home, "Home", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_love, "Favorite", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_pill, "Pills", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_sleep, "Sleep", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_car, "Car", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_food, "Food", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_map, "Map", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_phone, "Phone", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_timer, "Timer", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_wallet, "Money", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_weather, "Weather", icons.size()));
    }

    private View setupDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        navigationView.getMenu().findItem(R.id.checkable_menu_auto_note)
                .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_auto_note).getActionView()).setChecked(appSettings.isShowNoteAddDialog());
        navigationView.getMenu().findItem(R.id.checkable_menu_auto_note).getActionView().setClickable(false);

        navigationView.getMenu().findItem(R.id.checkable_menu_showMillis)
                .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_showMillis).getActionView()).setChecked(appSettings.isShowMillis());
        navigationView.getMenu().findItem(R.id.checkable_menu_showMillis).getActionView().setClickable(false);

//        navigationView.getMenu().findItem(R.id.checkable_menu_use_dark_theme)
//                .setActionView(new Switch(this));
//        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_use_dark_theme).getActionView()).setChecked(appSettings.isUseDark());
//        navigationView.getMenu().findItem(R.id.checkable_menu_use_dark_theme).getActionView().setClickable(false);

        navigationView.getMenu().findItem(R.id.checkable_menu_useGPS)
                .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_useGPS).getActionView()).setChecked(appSettings.isUse_gps());
        navigationView.getMenu().findItem(R.id.checkable_menu_useGPS).getActionView().setClickable(false);

        navigationView.getMenu().findItem(R.id.checkable_menu_use24hr)
                .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_use24hr).getActionView()).setChecked(appSettings.isUse24hrFormat());
        navigationView.getMenu().findItem(R.id.checkable_menu_use24hr).getActionView().setClickable(false);
        return header;
    }

    private void initRecyclerView(List<Timestamp> unfilteredTimestamps, List<Category> categories) {

        categoryUpdate = new Consumer<Category>() {
            @Override
            public void accept(Category selectedCategory) {
                Log.e("stamper", "selected_category: " + selectedCategory.getName() + " #" + selectedCategory.getCategoryID());
                lastSelectedCategory = selectedCategory;
                filterTimestamps(lastSelectedCategory);
            }
        };


        adapterCategory = new MyRecyclerViewAdapterCategory(categories, categoryUpdate, getApplicationContext());

        recyclerViewCategory = (RecyclerView) findViewById(R.id.recyclerViewCat);
        recyclerViewCategory.setAdapter(adapterCategory);
        recyclerViewCategory.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManagerCat = new LinearLayoutManager(this);
        linearLayoutManagerCat.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCategory.setLayoutManager(linearLayoutManagerCat);

        RecyclerView.ItemAnimator itemAnimatorCat = new DefaultItemAnimator();
        recyclerViewCategory.setItemAnimator(itemAnimatorCat);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        recyclerViewTimestamps = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new MyRecyclerViewAdapter(categories, metrics, icons, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerViewTimestamps.setAdapter(adapter);
        recyclerViewTimestamps.addItemDecoration(new VerticalSpaceItemDecoration(10));
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewTimestamps.setLayoutManager(linearLayoutManager);
        recyclerViewTimestamps.setItemAnimator(itemAnimator);

        filterTimestamps(Category.Default);

    }

    private void add_new_category_dialog(View v) {//new category dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        View viewInflated = LayoutInflater.from(v.getContext()).inflate(R.layout.new_category, null, false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input_cat);

        final MyRecyclerViewIconPicker iconPicker = new MyRecyclerViewIconPicker(icons, new Consumer<TimestampIcon>() {
            @Override
            public void accept(TimestampIcon icon) {
                input.setText(icon.getDescription());
            }
        }, getApplicationContext());
        RecyclerView iconRecycler = (RecyclerView) viewInflated.findViewById(R.id.recyclerView_icon);
        iconRecycler.setAdapter(iconPicker);
        iconRecycler.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManagerCat = new LinearLayoutManager(this);
        linearLayoutManagerCat.setOrientation(LinearLayoutManager.HORIZONTAL);
        iconRecycler.setLayoutManager(linearLayoutManagerCat);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int lastAdapterPosition = iconPicker.getLastSelected(); // which icon was selected
                categories.add(new Category(input.getText().toString(), categories.size(), lastAdapterPosition));
                iconPicker.destroy();
                dialog.cancel();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                iconPicker.destroy();
                dialog.cancel();
            }
        });

        builder.show();
    }                        // input dialog --   add  new category

    private void remove_category(View v) {      //category deletion dialog
        AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
        b.setTitle("Select a category you want to delete");
        List<String> quickCategories = new ArrayList<>();//

        for (int a = 1; a < categories.size(); a++) {
            quickCategories.add(categories.get(a).getName());
        }

        b.setItems(quickCategories.toArray(new String[quickCategories.size()]), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int clickedPos) {
                dialog.dismiss();
                int categoryToRemove = categories.get(clickedPos + 1).getCategoryID();

                // remove all timestamps  that belongs to this category
                List<Timestamp> itemsToRemove = new ArrayList<>();
                for (Timestamp timestamp : unfilteredTimestamps) {
                    if (timestamp.getCategoryId() == categoryToRemove) itemsToRemove.add(timestamp);
                }
                unfilteredTimestamps.removeAll(itemsToRemove);

                //remove category
                categories.remove(clickedPos + 1);

                adapterCategory.notifyItemRemoved(clickedPos + 1);

                //switch to default category
                lastSelectedCategory = Category.Default;
                filterTimestamps(lastSelectedCategory);
            }

        });

        b.show();

    }                       // spinner dialog -- delete  a category

    private void filterTimestamps(Category selectedCategory) {
        adapter.removeAll();
        if (selectedCategory.getCategoryID() == Category.Default.getCategoryID()) {
            adapter.add(unfilteredTimestamps);
            return;
        }
        List<Timestamp> sortedTimestamps = new ArrayList<>();
        for (Timestamp timestamp : unfilteredTimestamps) {
            if (timestamp.getCategoryId() == selectedCategory.getCategoryID())
                sortedTimestamps.add(timestamp);
        }
        adapter.add(sortedTimestamps);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.checkable_menu_showMillis: {
                appSettings.setShowMillis(!appSettings.isShowMillis());
                ((Switch) item.getActionView()).toggle();
                return true;
            }
            case R.id.checkable_menu_use24hr: {
                appSettings.setUse24hrFormat(!appSettings.isUse24hrFormat());
                ((Switch) item.getActionView()).toggle();
                return true;
            }

            case R.id.action_category_add: {
                add_new_category_dialog(getCurrentFocus());
                // add_new_category_dialog(findViewById(R.id.recyclerViewCat));
                break;
            }
            case R.id.action_category_delete: {
                remove_category(getCurrentFocus());
                break;
            }
            case R.id.action_export: {
                // exportToCSV();
                break;
            }
//       case R.id.checkable_menu_use_dark_theme) {
//            appSettings.setUseDark(!appSettings.isUseDark());
//            ((Switch) item.getActionView()).toggle();
//            recreate();
//            return true;
//        }
            case R.id.checkable_menu_auto_note: {
                ((Switch) item.getActionView()).toggle();
                appSettings.setShowNoteAddDialog(!appSettings.isShowNoteAddDialog());
                return true;
            }

            case R.id.checkable_menu_useGPS: {

                appSettings.setUse_gps(!appSettings.isUse_gps());
                ((Switch) item.getActionView()).toggle();
                return true;
            }
        }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

    public void saveData() {
        Log.v("stamper", "writing appSettings");
        dataManager.writeSettings(appSettings);
    }


    public void loadData() {
        //Log.v("stamper", "reading appSettings");
        // appSettings = dataManager.readSettings();
        //   applyAppSettings(appSettings);
    }



}
