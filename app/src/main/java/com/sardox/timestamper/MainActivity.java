package com.sardox.timestamper;

import android.Manifest;
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
import android.support.annotation.NonNull;
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
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.dialogs.AddCategoryDialog;
import com.sardox.timestamper.dialogs.ConfirmRemoveCategoryDialog;
import com.sardox.timestamper.dialogs.EditNoteDialog;
import com.sardox.timestamper.dialogs.MyDatePickerDialog;
import com.sardox.timestamper.dialogs.MyTimePickerDialog;
import com.sardox.timestamper.dialogs.CategoryListBottomSheet;
import com.sardox.timestamper.dialogs.SettingsDialog;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.QuickNote;
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

import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    private Toolbar toolbar;
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
        dataManager.clearWidgetTimestamps();
        saveCategories();
    }

    private void saveCategories() {
        dataManager.writeCategories(categories);
    }

    @Override
    public void onBackPressed() {
        if (timestampsAdapter.hasSelectedTimestamps()) {
            timestampsAdapter.clearSelectionAndUpdateView();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * while app is in background, user can create timestamps with widget. when user back in app, we need to read them and add them to recycler view
     */
    private void loadData() {
        if (dataManager == null || unfilteredTimestamps == null) return;
        if (timestampsAdapter.hasSelectedTimestamps()) {
            timestampsAdapter.clearSelectionAndUpdateView();
        }
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
        mTracker.enableExceptionReporting(!BuildConfig.DEBUG);
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.APP_LAUNCH);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        dataManager = new DataManager(this);
        loadUserSettings();
        setupDrawer();
        initApp();
        FloatingActionButton actionButton = findViewById(R.id.fab);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTimestamp();
            }
        });
    }

    private void createNewTimestamp() {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.NEW_TIMESTAMP);
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
        scrollViewTop();
        if (!appSettings.shouldShowNoteAddDialog()) {
            Snackbar.make(recyclerViewTimestamps, getString(R.string.new_timestamp_created) + " in " + lastSelectedCategory.getName(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void applyUserSettings() {
        loadUserSettings();
        timestampsAdapter.updateAppSettings(appSettings);
        timestampsAdapter.notifyDataSetChanged();
    }

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

    private void setupDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView phraseOfTheDay = header.findViewById(R.id.phraseOfTheDay);
        phraseOfTheDay.setText(Utils.getPhraseOfTheDay(this));
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupUserActionCallbacks() {
        userActionCallback = new Consumer<UserAction>() {
            @Override
            public void accept(UserAction action) {
                Timestamp timestamp = action.getTimestamp();
                switch (action.getActionType()) {
                    case REMOVE_TIMESTAMP:
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
                    case SHARE_TIMESTAMP:
                        break;
                    case CHANGE_CATEGORY:
                        changeCategory(timestamp);
                        break;
                    case SHOW_MAP:
                        showTimestampOnMap(timestamp);
                        break;
                    case SELECTED:
                        if (action.getCount() == 0) {
                            toolbar.setTitle(R.string.app_name);
                        } else {
                            toolbar.setTitle(String.valueOf(action.getCount()));
                        }
                        break;
                }
            }
        };
    }

    private void initRecyclerView() {
        Consumer<Category> onCategorySelectedCallback = new Consumer<Category>() {
            @Override
            public void accept(Category selectedCategory) {
                Log.d("srdx", "selected_category: " + selectedCategory.getName() + " #" + selectedCategory.getCategoryID());
                lastSelectedCategory = selectedCategory;
                timestampsAdapter.clearSelection();
                filterTimestampsByCategory(lastSelectedCategory);
            }
        };
        Consumer<Void> onCategoryAddSelected = new Consumer<Void>() {
            @Override
            public void accept(Void var1) {
                 showAddNewCategoryDialog();
            }
        };

        adapterCategory = new CategoryAdapter(categories, onCategorySelectedCallback, onCategoryAddSelected, this);

        recyclerViewCategory = findViewById(R.id.recyclerViewCat);
        recyclerViewCategory.setAdapter(adapterCategory);
        recyclerViewCategory.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManagerCat = new LinearLayoutManager(this);
        linearLayoutManagerCat.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCategory.setLayoutManager(linearLayoutManagerCat);

        RecyclerView.ItemAnimator itemAnimatorCat = new DefaultItemAnimator();
        recyclerViewCategory.setItemAnimator(itemAnimatorCat);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        recyclerViewTimestamps = findViewById(R.id.recyclerView);
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
                selectNextCategory();
            }

            public void onSwipeLeft() {
                selectPreviousCategory();
            }
        });
        filterTimestampsByCategory(Category.Default);
    }

    private void selectPreviousCategory() {
        int current_index = categories.indexOf(lastSelectedCategory);
        if (current_index < categories.size()) {
            Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.SWIPE_TO_CHANGE_CATEGORY);
            lastSelectedCategory = categories.get(current_index + 1);
            adapterCategory.setSelectedCategory(lastSelectedCategory);
            adapterCategory.notifyDataSetChanged();
            filterTimestampsByCategory(categories.get(current_index + 1));
        }
    }

    private void selectNextCategory() {
        int current_index = categories.indexOf(lastSelectedCategory);
        if (current_index > 0) {
            Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.SWIPE_TO_CHANGE_CATEGORY);
            lastSelectedCategory = categories.get(current_index - 1);
            adapterCategory.setSelectedCategory(lastSelectedCategory);
            adapterCategory.notifyDataSetChanged();
            filterTimestampsByCategory(categories.get(current_index - 1));
        }
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
            case R.id.action_category_delete: {
                if (categories.size() == 1) {
                    Snackbar.make(recyclerViewTimestamps, "Default category can not be deleted..  \uD83D\uDE44", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (timestampsAdapter.hasSelectedTimestamps()) {
                        removeGroupOfTimestamps();
                    } else {
                        showRemoveCategoryDialog();
                    }
                }
                break;
            }
        }
        return true;
    }

    private void removeGroupOfTimestamps() {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.REMOVE_GROUP);
        final List<Timestamp> copyOfRemovedItems = timestampsAdapter.getDeepCopyOfSelectedTimestamps();
        final int size = copyOfRemovedItems.size();
        unfilteredTimestamps.keySet().removeAll(timestampsAdapter.getSelectedTimestampsUUIDs());
        timestampsAdapter.removeSelectedTimestamps(); //may be not necessary since we already removed from hasmap
        timestampsAdapter.clearSelection();
        Snackbar.make(recyclerViewTimestamps, size + " timestamps were removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.REMOVE_GROUP_UNDO);
                        timestampsAdapter.add(copyOfRemovedItems);
                        unfilteredTimestamps.putAll(Utils.listToHashMap(copyOfRemovedItems));
                        timestampsAdapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showSettings: {
                Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.SHOW_SETTINGS);
                dataManager.writeUserSettings(appSettings);
                new SettingsDialog(this, new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean var1) {
                        applyUserSettings();
                    }
                });
                break;
            }
            case R.id.action_export: {
                exportTimestampsToCsv();
                break;
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        });
    }

    private void pickTime(final Timestamp timestampToUpdate) {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.EDIT_TIME);
        new MyTimePickerDialog(this,
                timestampToUpdate, new Consumer<JetTimestamp>() {
            @Override
            public void accept(JetTimestamp updatedDate) {
                unfilteredTimestamps.get(timestampToUpdate.getIdentifier()).setTimestamp(updatedDate);
                timestampsAdapter.updateTimestamp(timestampToUpdate);
            }
        }, appSettings.shouldUse24hrFormat());
    }

    private void editNote(final Timestamp timestamp) {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.EDIT_NOTE);
        new EditNoteDialog(this,
                timestamp,
                appSettings,
                new Consumer<String>() {
                    @Override
                    public void accept(String newNote) {
                        unfilteredTimestamps.get(timestamp.getIdentifier()).setNote(newNote);
                        timestampsAdapter.updateTimestamp(timestamp);
                        QuickNote quickNote = new QuickNote(JetTimestamp.now(), newNote);
                        appSettings.addQuickNote(quickNote);
                    }
                });
    }

    private void changeCategory(final Timestamp timestamp) {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.EDIT_CATEGORY);
        new CategoryListBottomSheet(this, "Move to", categories, icons, new Consumer<Category>() {
            @Override
            public void accept(Category newCategory) {
                unfilteredTimestamps.get(timestamp.getIdentifier()).setCategory_identifier(newCategory.getCategoryID());
                if (lastSelectedCategory.getCategoryID().equals(newCategory.getCategoryID()) || lastSelectedCategory.equals(Category.Default))
                    timestampsAdapter.updateTimestamp(timestamp);
                else timestampsAdapter.remove(timestamp);
            }
        });
    }

    private void removeTimestamp(Timestamp timestampToRemove) {
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.TIMESTAMP_REMOVE);
        timestampsAdapter.remove(timestampToRemove);
        unfilteredTimestamps.remove(timestampToRemove.getIdentifier());
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
                if (newCategory.getName().isEmpty()) {
                    Snackbar.make(recyclerViewTimestamps, "Category name can't be empty \uD83E\uDD14", Snackbar.LENGTH_SHORT).show();
                } else {
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
                    saveCategories();
                    Utils.updateGridWidget(getApplicationContext());
                }
            }
        });
    }

    private void showRemoveCategoryDialog() {
        new CategoryListBottomSheet(this, getString(R.string.select_cat_to_remove), categories, icons, new Consumer<Category>() {
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
        Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.REMOVE_CATEGORY);
        new ConfirmRemoveCategoryDialog(this, new Consumer<Boolean>() {
            @Override
            public void accept(Boolean isConfirmed) {
                if (isConfirmed) {
                    resetWidgetCategoryToNoneIfNeeded(categoryToRemove);
                    Utils.removeTimestampsByCategory(unfilteredTimestamps, categoryToRemove);
                    categories.remove(categoryToRemove);
                    resetView();
                    saveCategories();
                    Utils.updateGridWidget(getApplicationContext());
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
        if (mlocManager == null) return;
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
                Snackbar.make(recyclerViewTimestamps, "Looks like your GPS if Off \uD83E\uDD14", Snackbar.LENGTH_LONG).show();
                mlocManager.removeUpdates(this);

            }
        };
        try {
            mlocManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, myLooper);
        } catch (SecurityException e) {
            Log.e("SecurityException", "SecurityException during requestSingleUpdate");
            Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.SECURITY_EXCEPTION);
        }

        myHandler.postDelayed(new Runnable() {
            public void run() {
                mlocManager.removeUpdates(locationListener);
                Location location = null;
                try {
                    location = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                } catch (SecurityException e) {
                    Log.e("SecurityException", "SecurityException during getLastKnownLocation");
                    Utils.sendEventToAnalytics(mTracker, Constants.Analytics.Events.SECURITY_EXCEPTION);
                }

                if (location != null) {
                    consumer.accept(new PhysicalLocation(location.getLatitude(), location.getLongitude()));
                } else {
                    Snackbar.make(recyclerViewTimestamps, "Unable to get your location \uD83D\uDE1E", Snackbar.LENGTH_LONG).show();
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
            Log.d("srdx", "ACCESS_FINE_LOCATION permission not granted. Requesting Permissions...");
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
            EasyPermissions.requestPermissions(this, "App requires your permission to save and export CSV file",
                    RC_READWRITE, Constants.STORAGE_PERMS);
        }
    }

    private void emailCSV(File file) {
        Utils.emailCSV(this, file);
    }
}
