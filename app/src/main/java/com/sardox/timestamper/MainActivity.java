package com.sardox.timestamper;


import android.Manifest;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.Managers.TimeStampManager;
import com.sardox.timestamper.PickerFragments.JetTimePicker;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.recyclerview.MyRecyclerViewAdapter;
import com.sardox.timestamper.recyclerview.MyRecyclerViewAdapterCategory;
import com.sardox.timestamper.recyclerview.MyRecyclerViewIconPicker;

import com.sardox.timestamper.types.JetDuration;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.types.PhysicalLocation;
import com.sardox.timestamper.types.TimestampFormat;
import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.utils.Consumer;
import com.sardox.timestamper.utils.TimestampIcon;
import com.sardox.timestamper.utils.UserAction;
import com.sardox.timestamper.utils.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int RC_READWRITE = 9863;
    private TimeStampManager timeStampManager;
    private DataManager dataManager;
    private AppSettings appSettings;

    private Consumer<UserAction> userActionCallback;
    public RecyclerView recyclerViewTimestamps;
    public MyRecyclerViewAdapter adapter;

    public RecyclerView recyclerViewCategory;
    public MyRecyclerViewAdapterCategory adapterCategory;

    private Category lastSelectedCategory = Category.Default;

    private HashMap<JetUUID, Timestamp> unfilteredTimestamps;
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
            Log.v("srdx", " EasyPermissions not granted. Requesting Permissions...");
            EasyPermissions.requestPermissions(this, "App needs to write to storage",
                    RC_READWRITE, perms);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Timestamp newTimestamp = timeStampManager.createTimestamp(lastSelectedCategory);
                unfilteredTimestamps.put(newTimestamp.getIdentifier(), newTimestamp); //adding timestamp to main list
                adapter.add(newTimestamp);

                if (appSettings.isShowNoteAddDialog()) edit_note(newTimestamp);
                if (appSettings.isUse_gps()) {
                    getGPSCoordinates(new Consumer<PhysicalLocation>() {
                        @Override
                        public void accept(PhysicalLocation physicalLocation) {
                            Log.v("srdx", "setPhysicalLocation");
                            if (physicalLocation == null)
                                physicalLocation = PhysicalLocation.Default;
                            newTimestamp.setPhysicalLocation(physicalLocation);
                        }
                    });
                }

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

        setup_user_action_callbacks();

        initRecyclerView();
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

    private void setup_user_action_callbacks() {
        userActionCallback = new Consumer<UserAction>() {
            @Override
            public void accept(UserAction action) {
                Timestamp timestamp = action.getTimestamp();
                switch (action.getActionType()) {
                    case REMOVE:
                        remove_timestamp(timestamp);
                        break;
                    case EDIT_NOTE:
                        edit_note(timestamp);
                        break;
                    case EDIT_TIME:
                        pick_time(timestamp);
                        break;
                    case EDIT_DATE:
                        pick_date(timestamp);
                        break;
                    case SHARE:
                        //share_timestamp(timestamp);
                        break;
                    case CHANGE_CATEGORY:
                        change_category(timestamp);
                        break;
                    case MAP_TO:
                        show_timestamp_on_map(timestamp);
                        break;
                }
            }
        };
    }

    private void initRecyclerView() {

        Consumer<Category> categoryUpdate = new Consumer<Category>() {
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
        adapter = new MyRecyclerViewAdapter(categories, metrics, icons, getApplicationContext(), userActionCallback, appSettings);
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

    private void getGPSCoordinates(final Consumer<PhysicalLocation> consumer) {
        final int GPS_REQUEST_TIMEOUT = 1000 * 10;
        Log.v("srdx", "getGPSCoordinates");
        Looper myLooper = Looper.myLooper();

        final LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.v("srdx", "onLocationChanged: " + location.getLatitude());
                mlocManager.removeUpdates(this);
                PhysicalLocation physicalLocation = new PhysicalLocation(location.getLatitude(), location.getLongitude());
                consumer.accept(physicalLocation);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Snackbar.make(recyclerViewTimestamps, "Looks like your GPS if Off", Snackbar.LENGTH_LONG).show();
                mlocManager.removeUpdates(this);
            }
        };
        mlocManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, myLooper);

        final Handler myHandler = new Handler(myLooper);
        myHandler.postDelayed(new Runnable() {
            public void run() {
                mlocManager.removeUpdates(locationListener);
                Snackbar.make(recyclerViewTimestamps, "Unable to get your location", Snackbar.LENGTH_LONG).show();
            }
        }, GPS_REQUEST_TIMEOUT);
    }


    private void filterTimestamps(Category selectedCategory) {
        adapter.removeAll();
        if (selectedCategory.equals(Category.Default)) {
            adapter.add(unfilteredTimestamps.values());
            return;
        }
        List<Timestamp> sortedTimestamps = new ArrayList<>();
        for (Timestamp timestamp : unfilteredTimestamps.values()) {
            if (timestamp.getCategoryId().equals(selectedCategory.getCategoryID()))
                sortedTimestamps.add(timestamp);
        }
        adapter.add(sortedTimestamps);
    }


    public void saveData() {
        Log.v("srdx", "saving data...");
        dataManager.writeSettings(appSettings);
        dataManager.writeTimestamps(unfilteredTimestamps);
        dataManager.writeCategories(categories);
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
                adapter.notifyDataSetChanged();
                return true;
            }
            case R.id.checkable_menu_use24hr: {
                appSettings.setUse24hrFormat(!appSettings.isUse24hrFormat());
                ((Switch) item.getActionView()).toggle();
                adapter.notifyDataSetChanged();
                return true;
            }

            case R.id.action_category_add: {
                add_new_category_dialog(getCurrentFocus());
                break;
            }
            case R.id.action_category_delete: {
                if (categories.size() == 1) {
                    Snackbar.make(recyclerViewTimestamps, "No categories left..", Snackbar.LENGTH_SHORT).show();
                    break;
                } else remove_category(getCurrentFocus());
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void show_timestamp_on_map(Timestamp timestamp) {

        String uri = "geo:0,0?q=" + timestamp.getPhysicalLocation().toSimpleCommaString() + "(" + timestamp.getNote() + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        try {
           getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Snackbar.make(recyclerViewTimestamps, "Please install a maps application", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void pick_date(final Timestamp old_timestamp) {
        final int old_year = old_timestamp.format(TimestampFormat.Year);
        final int old_month = old_timestamp.format(TimestampFormat.Month);
        final int old_day = old_timestamp.format(TimestampFormat.Day);

        DatePickerDialog jetDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int new_year, int new_month, int new_day) {
                Log.v("srdx", "date is picked: " + new_year + " " + (new_month + 1) + " " + new_day);

                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(old_timestamp.getTimestamp().toMilliseconds());
                c.set(new_year, new_month, new_day);
                JetTimestamp updatedDate = JetTimestamp.fromMilliseconds(c.getTimeInMillis());
                c.clear();

                unfilteredTimestamps.get(old_timestamp.getIdentifier()).setTimestamp(updatedDate);
                adapter.updateTimestamp(old_timestamp);
            }
        }, old_year, old_month, old_day);

        jetDatePicker.show();
    }

    private void pick_time(final Timestamp old_timestamp) {
        final int old_hrs24 = old_timestamp.format(TimestampFormat.HRS24);
        final int old_min = old_timestamp.format(TimestampFormat.MIN);

        final long old_dif_in_millis = (old_hrs24 * 60 + old_min) * 60 * 1000;

        TimePickerDialog timePickerDialog = new JetTimePicker(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int new_hr, int new_min) {
                Log.v("srdx", "time is picked: " + new_hr + ":" + new_min);
                final long new_dif_in_millis = (new_hr * 60 + new_min) * 60 * 1000;
                long delta = new_dif_in_millis - old_dif_in_millis;

                JetTimestamp updatedDate = old_timestamp.getTimestamp();
                Log.v("srdx", "delta " + delta);

                updatedDate = updatedDate.add(JetDuration.fromMilliseconds(delta));

                unfilteredTimestamps.get(old_timestamp.getIdentifier()).setTimestamp(updatedDate);
                adapter.updateTimestamp(old_timestamp);
            }
        }, old_hrs24, old_min, appSettings.isUse24hrFormat());

        timePickerDialog.show();
    }

    private void edit_note(final Timestamp old_timestamp) {
        View v = recyclerViewTimestamps;
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        View viewInflated = LayoutInflater.from(v.getContext()).inflate(R.layout.text_input_note, null, false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        input.setText(old_timestamp.getNote());
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                unfilteredTimestamps.get(old_timestamp.getIdentifier()).setNote(input.getText().toString());
                adapter.updateTimestamp(old_timestamp);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        builder.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }  // input dialog -- for a note

    private void change_category(final Timestamp old_timestamp) {
        View v = recyclerViewTimestamps;
        AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
        b.setTitle("Move to");
        List<String> quickCategories = new ArrayList<>();//

        for (int a = 0; a < categories.size(); a++) {
            quickCategories.add(categories.get(a).getName());
        }

        b.setItems(quickCategories.toArray(new String[quickCategories.size()]), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int clickedPos) {
                dialog.dismiss();
                JetUUID move_to_category = categories.get(clickedPos).getCategoryID();

                unfilteredTimestamps.get(old_timestamp.getIdentifier()).setCategory_identifier(move_to_category);
                if (lastSelectedCategory.getCategoryID().equals(move_to_category))
                    adapter.updateTimestamp(old_timestamp);
                else adapter.remove(old_timestamp);
            }

        });

        b.show();
    }

    private void remove_timestamp(Timestamp timestamp) {
        adapter.remove(timestamp);
        unfilteredTimestamps.remove(timestamp.getIdentifier());
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
                Category newCategory = new Category(input.getText().toString(), JetUUID.randomUUID(), lastAdapterPosition);
                categories.add(newCategory);

                lastSelectedCategory = newCategory;
                adapterCategory.setSelected_category(newCategory);
                adapterCategory.notifyDataSetChanged();

                recyclerViewCategory.smoothScrollToPosition(adapterCategory.getItemCount()); //scrolling to new category
                filterTimestamps(newCategory);

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
    }                // input dialog --   add  new category

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
                JetUUID categoryToRemove = categories.get(clickedPos + 1).getCategoryID();

                // remove all timestamps  that belongs to this category
                List<JetUUID> itemsToRemove = new ArrayList<>();

                for (Timestamp timestamp : unfilteredTimestamps.values()) {
                    if (timestamp.getCategoryId().equals(categoryToRemove))
                        itemsToRemove.add(timestamp.getIdentifier());  //what to remove
                }

                for (JetUUID id : itemsToRemove) {
                    unfilteredTimestamps.remove(id); //removing
                }

                //remove category
                categories.remove(clickedPos + 1);

                recyclerViewCategory.smoothScrollToPosition(0); //scrolling to default category
                lastSelectedCategory = Category.Default;        //switch to default category
                adapterCategory.setSelected_category(lastSelectedCategory);
                adapterCategory.notifyDataSetChanged();
                filterTimestamps(lastSelectedCategory);
            }

        });

        b.show();

    }                       // spinner dialog -- delete  a category
}
