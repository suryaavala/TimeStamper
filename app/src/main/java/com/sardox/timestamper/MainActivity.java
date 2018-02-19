package com.sardox.timestamper;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.dialogs.AddCategoryDialog;
import com.sardox.timestamper.dialogs.ChangeCategoryDialog;
import com.sardox.timestamper.dialogs.ConfirmRemoveCategoryDialog;
import com.sardox.timestamper.dialogs.EditNoteDialog;
import com.sardox.timestamper.dialogs.MyDatePickerDialog;
import com.sardox.timestamper.dialogs.MyTimePickerDialog;
import com.sardox.timestamper.dialogs.PickToRemoveCategoryDialog;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.recyclerview.CategoryAdapter;
import com.sardox.timestamper.recyclerview.TimestampsAdapter;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.types.PhysicalLocation;
import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.utils.Constants;
import com.sardox.timestamper.utils.Consumer;
import com.sardox.timestamper.utils.OnSwipeTouchListener;
import com.sardox.timestamper.utils.TimestampIcon;
import com.sardox.timestamper.utils.UserAction;
import com.sardox.timestamper.utils.Utils;
import com.sardox.timestamper.utils.VerticalSpaceItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private HashMap<JetUUID, Timestamp> unfilteredTimestamps;
    private List<Category> categories;
    private List<TimestampIcon> icons;

    private Consumer<UserAction> userActionCallback;
    final int RC_READWRITE = 9863;

    private DataManager dataManager;
    private AppSettings appSettings;

    public CategoryAdapter adapterCategory;
    public TimestampsAdapter timestampsAdapter;
    public RecyclerView recyclerViewCategory;
    public RecyclerView recyclerViewTimestamps;

    private Category lastSelectedCategory = Category.Default;
    private Tracker mTracker;

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    public void saveData() {
        Log.d("srdx", "saving data...");
        dataManager.writeUserSettings(appSettings);
        dataManager.writeTimestamps(unfilteredTimestamps);
        dataManager.writeCategories(categories);
        dataManager.clearWidgetTimestamps();
    }

    /**
     * while app is in background, user can create timestamps with widget. when user back in app, we need to read them and add them to recycler view
     */
    private void loadData() {
        if (dataManager == null || unfilteredTimestamps == null) return;
        HashMap<JetUUID, Timestamp> widgetTimestamps = dataManager.readWidgetTimestamps();
        if (!widgetTimestamps.isEmpty()) {
            unfilteredTimestamps.putAll(widgetTimestamps);
            filterTimestampsByCategory(lastSelectedCategory);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("srdx", "-----------NEW RUN--------------");
        mTracker = ((Application) getApplication()).getDefaultTracker();
        mTracker.setScreenName(Constants.Analytics.Screens.MainScreen);
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.APP_LAUNCH);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dataManager = new DataManager(this);
        loadUserSettings();
        setupDrawer(toolbar);
        initApp();
        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.fab);
        actionButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.NEW_TIMESTAMP);
        for (int i=0; i<1; i++) {
            final Timestamp newTimestamp = new Timestamp(
                    JetTimestamp.now(),
                    PhysicalLocation.Default,
                    lastSelectedCategory.getCategoryID(),
                    JetUUID.randomUUID());

            unfilteredTimestamps.put(newTimestamp.getIdentifier(), newTimestamp);
            timestampsAdapter.add(newTimestamp);
            if (appSettings.shouldShowNoteAddDialog()) editNote(newTimestamp);
            if (appSettings.shouldUseGps()) {
                if (hasGPSpermission()) {
                    getGPSCoordinates(new Consumer<PhysicalLocation>() {
                        @Override
                        public void accept(PhysicalLocation physicalLocation) {
                            Log.d("srdx", "setPhysicalLocation");
                            if (physicalLocation == null) {
                                physicalLocation = PhysicalLocation.Default;
                            }
                            newTimestamp.setPhysicalLocation(physicalLocation);
                        }
                    });
                }
            }
        }
        scrollViewTop();
        Snackbar.make(view, getString(R.string.new_timestamp_created) + " in " + lastSelectedCategory.getName(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @AfterPermissionGranted(RC_READWRITE)
    private void initApp() {
        initIcons();
        loadTimestamps();
        loadCategories();
        setupUserActionCallbacks();
        initRecyclerView();
    }

    /**
     * Must be called before setupDrawer()
     **/
    private void loadUserSettings() {
        appSettings = dataManager.loadUserSettings();
    }

    private void loadCategories() {
        categories = dataManager.readCategories();
    }

    private void loadTimestamps() {
        unfilteredTimestamps = dataManager.readTimestamps();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_menu, menu);
        return true;
    }

    private void setupDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        TextView phraseOfTheDay = (TextView) header.findViewById(R.id.phraseOfTheDay);
        phraseOfTheDay.setText(Utils.getPhraseOfTheDay(this));

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().findItem(R.id.checkable_menu_auto_note)
                .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_auto_note).getActionView()).setChecked(appSettings.shouldShowNoteAddDialog());
        navigationView.getMenu().findItem(R.id.checkable_menu_auto_note).getActionView().setClickable(false);

        navigationView.getMenu().findItem(R.id.checkable_menu_showMillis)
                .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_showMillis).getActionView()).setChecked(appSettings.shouldShowMillis());
        navigationView.getMenu().findItem(R.id.checkable_menu_showMillis).getActionView().setClickable(false);

        navigationView.getMenu().findItem(R.id.checkable_menu_useGPS)
                .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_useGPS).getActionView()).setChecked(appSettings.shouldUseGps());
        navigationView.getMenu().findItem(R.id.checkable_menu_useGPS).getActionView().setClickable(false);

        navigationView.getMenu().findItem(R.id.checkable_menu_use24hr)
                .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_use24hr).getActionView()).setChecked(appSettings.shouldUse24hrFormat());
        navigationView.getMenu().findItem(R.id.checkable_menu_use24hr).getActionView().setClickable(false);
    }

    private void setupUserActionCallbacks() {
        userActionCallback = new Consumer<UserAction>() {
            @Override
            public void accept(UserAction action) {
                Timestamp timestamp = action.getTimestamp();
                switch (action.getActionType()) {
                    case REMOVE:
                        removeTimestamp(timestamp);
                        break;
                    case EDIT_NOTE:
                        editNote(timestamp);
                        break;
                    case EDIT_TIME:
                        pickTime(timestamp);
                        break;
                    case EDIT_DATE:
                        pickDate(timestamp);
                        break;
                    case SHARE:
                        break;
                    case CHANGE_CATEGORY:
                        changeCategory(timestamp);
                        break;
                    case MAP_TO:
                        showTimestampOnMap(timestamp);
                        break;
                }
            }
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecyclerView() {
        Consumer<Category> categoryUpdate = new Consumer<Category>() {
            @Override
            public void accept(Category selectedCategory) {
                Log.d("srdx", "selected_category: " + selectedCategory.getName() + " #" + selectedCategory.getCategoryID());
                lastSelectedCategory = selectedCategory;
                filterTimestampsByCategory(lastSelectedCategory);
            }
        };

        adapterCategory = new CategoryAdapter(categories, categoryUpdate, this);

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
        timestampsAdapter = new TimestampsAdapter(categories, metrics, icons, this, userActionCallback, appSettings);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerViewTimestamps.setAdapter(timestampsAdapter);
        recyclerViewTimestamps.addItemDecoration(new VerticalSpaceItemDecoration(10));
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewTimestamps.setLayoutManager(linearLayoutManager);
        recyclerViewTimestamps.setItemAnimator(itemAnimator);

        recyclerViewTimestamps.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeRight() {
                int current_index = categories.indexOf(lastSelectedCategory);
                if (current_index>0){
                    lastSelectedCategory = categories.get(current_index-1);
                    adapterCategory.setSelectedCategory(lastSelectedCategory);
                    adapterCategory.notifyDataSetChanged();
                    filterTimestampsByCategory(categories.get(current_index-1));
                }

            }
            public void onSwipeLeft() {
                int current_index = categories.indexOf(lastSelectedCategory);
                if (current_index<categories.size()){
                    lastSelectedCategory = categories.get(current_index+1);
                    adapterCategory.setSelectedCategory(lastSelectedCategory);
                    adapterCategory.notifyDataSetChanged();
                    filterTimestampsByCategory(categories.get(current_index+1));
                }

            }
        });
        filterTimestampsByCategory(Category.Default);
    }

    private void initIcons() {
        icons = Utils.getStockIcons();
    }

    private void filterTimestampsByCategory(Category selectedCategory) {
        if (lastSelectedCategory == null) {
            lastSelectedCategory = Category.Default;
        }
        timestampsAdapter.removeAll();
        timestampsAdapter.add(Utils.filterTimestampsByCategory(unfilteredTimestamps, selectedCategory));
    }

    private void scrollViewTop() {
        recyclerViewTimestamps.smoothScrollToPosition(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_category_add: {
                showAddNewCategoryDialog();
                break;
            }
            case R.id.action_category_delete: {
                if (categories.size() == 1) {
                    Snackbar.make(recyclerViewTimestamps, "No categories left..", Snackbar.LENGTH_SHORT).show();
                } else {
                    showRemoveCategoryDialog();
                }
                break;
            }
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.checkable_menu_showMillis: {
                appSettings.setShowMillis(!appSettings.shouldShowMillis());
                ((Switch) item.getActionView()).toggle();
                timestampsAdapter.notifyDataSetChanged();
                return true;
            }
            case R.id.checkable_menu_use24hr: {
                appSettings.setUse24hrFormat(!appSettings.shouldUse24hrFormat());
                ((Switch) item.getActionView()).toggle();
                timestampsAdapter.notifyDataSetChanged();
                return true;
            }
            case R.id.action_export: {
                exportTimestampsToCsv();
                break;
            }
            case R.id.checkable_menu_auto_note: {
                ((Switch) item.getActionView()).toggle();
                appSettings.setShowNoteAddDialog(!appSettings.shouldShowNoteAddDialog());
                return true;
            }
            case R.id.checkable_menu_useGPS: {
                appSettings.setShouldUseGps(!appSettings.shouldUseGps());
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

    private void pickDate(final Timestamp timestampToUpdate) {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.EDIT_DATE);
        new MyDatePickerDialog(this, timestampToUpdate, new Consumer<JetTimestamp>() {
            @Override
            public void accept(JetTimestamp updatedDate) {
                unfilteredTimestamps.get(timestampToUpdate.getIdentifier()).setTimestamp(updatedDate);
                timestampsAdapter.updateTimestamp(timestampToUpdate);
            }
        }, appSettings.shouldUse24hrFormat());
    }

    private void pickTime(final Timestamp timestampToUpdate) {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.EDIT_TIME);
        new MyTimePickerDialog(this, timestampToUpdate, new Consumer<JetTimestamp>() {
            @Override
            public void accept(JetTimestamp updatedDate) {
                unfilteredTimestamps.get(timestampToUpdate.getIdentifier()).setTimestamp(updatedDate);
                timestampsAdapter.updateTimestamp(timestampToUpdate);
            }
        }, appSettings.shouldUse24hrFormat());
    }

    private void editNote(final Timestamp timestamp) {
        new EditNoteDialog(this, timestamp, new Consumer<String>() {
            @Override
            public void accept(String newNote) {
                unfilteredTimestamps.get(timestamp.getIdentifier()).setNote(newNote);
                timestampsAdapter.updateTimestamp(timestamp);
            }
        });
    }

    private void changeCategory(final Timestamp timestamp) {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.EDIT_CATEGORY);
        new ChangeCategoryDialog(this, categories, new Consumer<JetUUID>() {
            @Override
            public void accept(JetUUID newCategoryId) {
                unfilteredTimestamps.get(timestamp.getIdentifier()).setCategory_identifier(newCategoryId);
                if (lastSelectedCategory.getCategoryID().equals(newCategoryId) || lastSelectedCategory.equals(Category.Default))
                    timestampsAdapter.updateTimestamp(timestamp);
                else timestampsAdapter.remove(timestamp);
            }
        });
    }

    private void removeTimestamp(Timestamp timestampToRemove) {
        timestampsAdapter.remove(timestampToRemove);
        unfilteredTimestamps.remove(timestampToRemove.getIdentifier());
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.TIMESTAMP_REMOVE);
    }

    private void showTimestampOnMap(Timestamp timestamp) {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.OPEN_MAP);
        if (timestamp.getPhysicalLocation() != null) {
            final String uri = String.format("geo:0,0?q=%s(%s)", timestamp.getPhysicalLocation().toSimpleCommaString(), timestamp.getNote());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Snackbar.make(recyclerViewTimestamps, "Please install a maps application", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void showAddNewCategoryDialog() {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.ADD_NEW_CATEGORY);
        new AddCategoryDialog(this, icons, new Consumer<Category>() {
            @Override
            public void accept(Category newCategory) {
                categories.add(newCategory);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(Constants.Analytics.Events.ACTION)
                        .setAction(Constants.Analytics.Events.NEW_CATEGORY)
                        .setLabel(newCategory.getName().toLowerCase())
                        .build());

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(Constants.Analytics.Events.ACTION)
                        .setAction(Constants.Analytics.Events.ICON_PICKED)
                        .setLabel(icons.get(newCategory.getIcon_id()).getDescription().toLowerCase())
                        .build());

                lastSelectedCategory = newCategory;
                adapterCategory.setSelectedCategory(newCategory);
                adapterCategory.notifyDataSetChanged();

                recyclerViewCategory.smoothScrollToPosition(adapterCategory.getItemCount()); //scrolling to new category
                filterTimestampsByCategory(newCategory);
            }
        });
    }

    private void showRemoveCategoryDialog() {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.REMOVE_CATEGORY);
        new PickToRemoveCategoryDialog(this, categories, new Consumer<Category>() {
            @Override
            public void accept(Category categoryToRemove) {
                if (categoryToRemove.equals(Category.Default)) {
                    Snackbar.make(recyclerViewTimestamps, R.string.cant_be_removed, Snackbar.LENGTH_LONG).show();
                } else {
                    showDeleteConfirmDialog(categoryToRemove);
                }
            }
        });
    }

    private void showDeleteConfirmDialog(final Category categoryToRemove) {
        new ConfirmRemoveCategoryDialog(this, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isConfirmed) {
                if (isConfirmed) {
                    resetWidgetCategoryToNoneIfNeeded(categoryToRemove);
                    Utils.removeTimestampsByCategory(unfilteredTimestamps, categoryToRemove);
                    categories.remove(categoryToRemove);
                    resetView();
                }
            }
        });
    }

    private void resetView() {
        recyclerViewCategory.smoothScrollToPosition(0); //scrolling to default category
        lastSelectedCategory = Category.Default;        //switch to default category
        adapterCategory.setSelectedCategory(Category.Default);
        adapterCategory.notifyDataSetChanged();
        filterTimestampsByCategory(lastSelectedCategory);
    }

    private void resetWidgetCategoryToNoneIfNeeded(Category category) {
        JetUUID defaultCategory = dataManager.readDefaultCategoryForWidget();
        if (defaultCategory.equals(category.getCategoryID())) {
            dataManager.saveDefaultCategoryForWidget(AppSettings.NO_DEFAULT_CATEGORY);
        }
    }

    private void getGPSCoordinates(final Consumer<PhysicalLocation> consumer) {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.LOCATION_RECORDED);
        final Looper myLooper = Looper.myLooper();
        final LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Handler myHandler = new Handler(myLooper);
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("srdx", "onLocationChanged: " + location.getLatitude());
                mlocManager.removeUpdates(this);
                PhysicalLocation physicalLocation = new PhysicalLocation(location.getLatitude(), location.getLongitude());
                consumer.accept(physicalLocation);
                myHandler.removeCallbacksAndMessages(null);
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
        myHandler.postDelayed(new Runnable() {
            public void run() {
                mlocManager.removeUpdates(locationListener);
                Location location = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    consumer.accept(new PhysicalLocation(location.getLatitude(), location.getLongitude()));
                } else {
                    Snackbar.make(recyclerViewTimestamps, "Unable to get your location", Snackbar.LENGTH_LONG).show();
                }
            }
        }, Constants.GPS_REQUEST_TIMEOUT);
    }

    private boolean hasGPSpermission() {
        final int RC_LOCATION = 9863;
        final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(this, perms)) {
            return true;
        } else {
            Log.d("srdx", " EasyPermissions not granted. Requesting Permissions...");
            EasyPermissions.requestPermissions(this, "To save your location, Timestamper needs the access to Location services",
                    RC_LOCATION, perms);
            return false;
        }
    }

    private void exportTimestampsToCsv() {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.EXPORT_TIMESTAMPS);
        if (EasyPermissions.hasPermissions(this, Constants.STORAGE_PERMS)) {
            emailCSV(dataManager.exportToCSV(lastSelectedCategory, new ArrayList<>(unfilteredTimestamps.values()), categories));
        } else {
            Log.d("srdx", " EasyPermissions not granted. Requesting Permissions...");
            EasyPermissions.requestPermissions(this, "App needs to write to storage",
                    RC_READWRITE, Constants.STORAGE_PERMS);
        }
    }

    private void emailCSV(File file) {
        Utils.emailCSV(this, file);
    }
}
