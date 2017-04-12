package com.sardox.timestamper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.recyclerview.MyRecyclerViewAdapter;
import com.sardox.timestamper.recyclerview.MyRecyclerViewAdapterCategory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

public class MainActivity_old { /*extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SHARED_PREFS_USE_GPS = "gps";
    public static final String SHARED_PREFS_STAMPS = "stamps";
    public static final String SHARED_PREFS_CATS = "cats";
    public static final String SHARED_PREFS_USE24HR = "use24hr";
    public static final String SHARED_PREFS_AUTONOTE = "autoNote";
    public static final String SHARED_PREFS_USEDARK = "useDark";
    public static final String SHARED_PREFS_SHOW_MILLIS = "showMillis";
    public static final String SHARED_PREFS_MAX_ID = "maxID";
    public static final String SHARED_PREFS_MAXCAT_ID = "maxCatID";
    public static SharedPreferences mPrefs;

  //  public static SharedPreferences.Editor prefsEditor;
    public boolean use24hrFormat = false;            //default values
    public boolean useDark = true;                   //default values
    public boolean showNoteAddDialog = false;        //default values
    public boolean showMillis = false;               //default values
    public boolean use_gps = false;                  //default values

    public int maxListID = 0;                         //default values
    public int maxCatID = 0;                          //default values
    public int currentPhrase=0;
    public int LastCategoryFilter;

    public RecyclerView recyclerView;
    public static MyRecyclerViewAdapter adapter;

    public RecyclerView recyclerViewCat;
    public MyRecyclerViewAdapterCategory adapterCategory;

    public List<Category> CategoryList = new ArrayList<>();
    public final List<Timestamp> timestampList = new ArrayList<>();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LastCategoryFilter = 0;

        Log.e("stamper", "-----------NEW RUN--------------");

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        applyUserSetting();  //setting theme, setting, ets

        verifyStoragePermissions(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.new_timestamp_created), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Calendar c = Calendar.getInstance();

                maxListID++;                              // need to save it in Shared PREFS
                Timestamp timestamp = new Timestamp();
                timestamp.setCategoryID(LastCategoryFilter);
                timestamp.setSubtitle(getString(R.string.add_note_here));
                timestamp.setGps(getGPSCoordinates());

                timestamp.setStampID(maxListID);
                timestamp.setTime(c.getTimeInMillis());
                adapter.addStamp(timestamp);

               if(showNoteAddDialog) showDialogForNote(view, maxListID);
            }
             });


        View header = setupDrawer(toolbar);

         Random r = new Random();
         currentPhrase = r.nextInt(11 - 1) + 1;
         Log.e("stamper", "random: " + currentPhrase);
         String[] phrases = getResources().getStringArray(R.array.phrases);
         TextView phrase = (TextView) header.findViewById(R.id.phrase);
         phrase.setText(phrases[currentPhrase - 1]);


        //populateCategories();
        recyclerViewCat = (RecyclerView) findViewById(R.id.recyclerViewCat);
      //  adapterCategory = new MyRecyclerViewAdapterCategory(CategoryList, this);
        LinearLayoutManager linearLayoutManagerCat = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimatorCat = new DefaultItemAnimator();
        recyclerViewCat.setAdapter(adapterCategory);
        recyclerViewCat.setHasFixedSize(true);
        linearLayoutManagerCat.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewCat.setLayoutManager(linearLayoutManagerCat);
        recyclerViewCat.setItemAnimator(itemAnimatorCat);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                adapter.mStampsListCopy.remove(adapter.findStampPositionInListByID(adapter.mStampsList.get(viewHolder.getAdapterPosition()).getStampID()));
                adapter.mStampsList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Log.e("stamper", "swyped!!!");
                //TODO ADD UNDO
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        //populateRecords(timestampList);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
     //   adapter = new MyRecyclerViewAdapter(timestampList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);                                                  //setReverseLayout
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));                        //VerticalSpaceItemDecoration(10
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        adapter.fragmentmanager=getSupportFragmentManager();
       // getGPSCoordinates();


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
        ((Switch)  navigationView.getMenu().findItem(R.id.checkable_menu_auto_note).getActionView()).setChecked(showNoteAddDialog);
        navigationView.getMenu().findItem(R.id.checkable_menu_auto_note).getActionView().setClickable(false);

        navigationView.getMenu().findItem(R.id.checkable_menu_showMillis)
                .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_showMillis).getActionView()).setChecked(showMillis);
        navigationView.getMenu().findItem(R.id.checkable_menu_showMillis).getActionView().setClickable(false);


        navigationView.getMenu().findItem(R.id.checkable_menu_use_dark_theme)
           .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_use_dark_theme).getActionView()).setChecked(useDark);
        navigationView.getMenu().findItem(R.id.checkable_menu_use_dark_theme).getActionView().setClickable(false);

        navigationView.getMenu().findItem(R.id.checkable_menu_useGPS)
            .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_useGPS).getActionView()).setChecked(use_gps);
        navigationView.getMenu().findItem(R.id.checkable_menu_useGPS).getActionView().setClickable(false);

        navigationView.getMenu().findItem(R.id.checkable_menu_use24hr)
            .setActionView(new Switch(this));
        ((Switch) navigationView.getMenu().findItem(R.id.checkable_menu_use24hr).getActionView()).setChecked(use24hrFormat);
        navigationView.getMenu().findItem(R.id.checkable_menu_use24hr).getActionView().setClickable(false);
        return header;
    }

    private void applyUserSetting() {
        if (mPrefs.contains(SHARED_PREFS_USE_GPS))      use_gps = mPrefs.getBoolean(SHARED_PREFS_USE_GPS, false);
        if (mPrefs.contains(SHARED_PREFS_USEDARK))      useDark = mPrefs.getBoolean(SHARED_PREFS_USEDARK, true);
        if (useDark) setTheme(R.style.AppThemeCustomMaterialDark); else setTheme(R.style.AppThemeCustom);
        if (mPrefs.contains(SHARED_PREFS_SHOW_MILLIS)) showMillis = mPrefs.getBoolean(SHARED_PREFS_SHOW_MILLIS, false);
        if (mPrefs.contains(SHARED_PREFS_USE24HR))     use24hrFormat = mPrefs.getBoolean(SHARED_PREFS_USE24HR, false);
        if (mPrefs.contains(SHARED_PREFS_AUTONOTE))    showNoteAddDialog = mPrefs.getBoolean(SHARED_PREFS_AUTONOTE, false);
        if (mPrefs.contains(SHARED_PREFS_AUTONOTE))    showNoteAddDialog = mPrefs.getBoolean(SHARED_PREFS_AUTONOTE, false);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.checkable_menu_showMillis) {
            showMillis = !showMillis;
           // item.setChecked(showMillis);
            ((Switch) item.getActionView()).toggle();
            filterList();
            return true;
        }
        if (id == R.id.checkable_menu_use24hr) {
            use24hrFormat = !use24hrFormat;
            ((Switch) item.getActionView()).toggle();
            //item.setChecked(use24hrFormat);
            filterList();
            return true;
        }

        if (id == R.id.action_category_add) {
            showDialog(findViewById(R.id.recyclerViewCat));
            return true;
        }
        if (id == R.id.action_category_delete) {
            showSpinner(findViewById(R.id.recyclerView));
            return true;
        }
        if (id == R.id.action_export) {
            exportToCSV();
            return true;
        }

        if (id == R.id.checkable_menu_use_dark_theme) {

            useDark =!useDark;
            ((Switch) item.getActionView()).toggle();
            mPrefs.edit().putBoolean(SHARED_PREFS_USEDARK, useDark).commit();

            recreate();
            return true;
        }

        if (id == R.id.checkable_menu_auto_note) {
            ((Switch) item.getActionView()).toggle();
            showNoteAddDialog = !showNoteAddDialog;
            //item.setChecked(showNoteAddDialog);
            return true;
        }

        if (id == R.id.checkable_menu_useGPS) {

            use_gps =!use_gps;
            ((Switch) item.getActionView()).toggle();
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public List<Category> getCategoryList(){
       return adapterCategory.mCategoryList;
    }

    public Category getCategoryByID(int categoryID) {
        for (Category item : adapterCategory.mCategoryList) { //!!! added adapter
            if (item.getCategoryID() == categoryID) {
                //Log.e("stamper",  "category: " + item.getCategoryID());
                return item;
            }
        }
        return null;  //if category was deleted then we assign  a text: "to nofilter" but not changing cat id
    }

    public void filterList() { //i call this fron MyRecViewAdapter to filter by category when clicked
        adapter.sortByCategory(LastCategoryFilter);
    }

    private void populateCategories() {
        List<String> tempcatlist = Arrays.asList("No filter", "Sport", "Baby sleep", "Medication", "Work shifts");

        for (int i = 0; i < tempcatlist.size(); i++) {
            Category category = new Category();
            category.setName(tempcatlist.get(i));
            category.setCategoryID(i);
            adapterCategory.mCategoryList.add(category);

        }
        maxCatID = adapterCategory.mCategoryList.size();
        Log.e("stamper", "maxCatID after populateCategories: " + maxCatID);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public String getGPSCoordinates(){
        Log.e("gps", "use GPS: "+ use_gps);
        if (!use_gps) return "";
        final LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Location currentGeoLocation = mlocManager
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (currentGeoLocation==null) {
            Toast.makeText(this, R.string.null_location, Toast.LENGTH_LONG).show();
            return "";
        }

        Log.e("gps","getGPSCoordinates: " + String.valueOf(currentGeoLocation.getLatitude()) +  "," +  String.valueOf(currentGeoLocation.getLongitude() ));

        return  String.valueOf(currentGeoLocation.getLatitude() + "," +  String.valueOf(currentGeoLocation.getLongitude()));
    }

    public void exportToCSV() {
        String filename = "TimeStampExport.csv";
        final String newLine = "\n";
        final String newComma = ",";

        // int currentCategoryID = adapterCategory.mCategoryList.get(LastCategoryFilter).getCategoryID(); //added adapter
        String string =  getCategoryByID(LastCategoryFilter).getName() + newLine;  //aname of the category will be stored on 1st  line of csv file

        Calendar c = Calendar.getInstance();
        TimeZone tz = c.getTimeZone();

        for (Timestamp item : adapter.mStampsListCopy) {
            if (LastCategoryFilter == 0) {
                c.setTimeInMillis(item.getTime());
                String format = "EEE d MMM yyyy hh:mm:ss.SSS a z";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setTimeZone(tz);
                String dateForCSV = sdf.format(c.getTime());

                string += dateForCSV + newComma;
                string += item.getSubtitle() + newComma;
                string += getCategoryByID(item.getCategoryID()).getName();
                string += newLine;
            } else if (item.getCategoryID() == LastCategoryFilter) {

                c.setTimeInMillis(item.getTime());
                String format = "EEE d MMM yyyy HH:mm:ss.SSS a z";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setTimeZone(tz);
                String dateForCSV = sdf.format(c.getTime());
                string += dateForCSV + newComma;
                string += item.getSubtitle();
                string += newLine;
            }
        }
        Log.e("stamper", "string:" + string);


        //  File file = new File();
        File root = Environment.getExternalStorageDirectory();

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d("stamper", "sdcard mounted and writable");


            File dir = new File(root.getAbsolutePath());

            dir.getParentFile().mkdirs();

            try {

                File file = new File(dir, filename);
                FileOutputStream out = new FileOutputStream(file);
                out.write(string.getBytes());
                out.close();
                emailCSV(file);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("stamper", "FileNotFoundException");

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("stamper", "IOException");
            }
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.d("stamper", "sdcard mounted readonly");
        } else {
            Log.d("stamper", "sdcard state: " + state);
        }
    }

    private void emailCSV(File file) {

        Uri u1 = Uri.fromFile(file);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "My timestamps");
        sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
        sendIntent.setType("text/html");
        startActivity(sendIntent);

    }

    private void showDialog(View v) {  //new category dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        View viewInflated = LayoutInflater.from(v.getContext()).inflate(R.layout.text_input_cat, null, false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input_cat);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                maxCatID++;
                Category newCategorytoAdd = new Category(input.getText().toString(), maxCatID);
                adapterCategory.mCategoryList.add(newCategorytoAdd); //!!!
                int pos = adapterCategory.mCategoryList.size(); //!!!
                LastCategoryFilter = maxCatID; //error was here!!!
                filterList();
                adapterCategory.notifyDataSetChanged();
                //adapter.notifyDataSetChanged();
                recyclerViewCat.scrollToPosition(pos-1); // scroll to the end

                adapterCategory.selectedPos = pos-1;
                Log.e("stamper", "New category added: " +input.getText().toString() + " with id= " + maxCatID);

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


      //  builder.show();
        builder.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }                        // input dialog --   add  new category

    private void showSpinner(View v) {      //category deletion dialog
        AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
        b.setTitle("Select a category you want to delete");
        List<String> types = new ArrayList<>();//

        int a = 0;
        for (Category item : adapterCategory.mCategoryList) {
            if (a > 0) types.add(item.getName());
            a = 1; // dont want to show 0 item "no filter" sicne u cant delete it
        }

        b.setItems(types.toArray(new String[types.size()]), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int clickedPos) {
                dialog.dismiss();

                int oldID;
                oldID = adapterCategory.mCategoryList.get(clickedPos + 1).getCategoryID();
                adapterCategory.mCategoryList.remove(clickedPos + 1);

                for (Timestamp item : adapter.mStampsListCopy) {
                    if (item.getCategoryID() == oldID) item.setCategoryID(0);
                }


                LastCategoryFilter = 0;
                filterList();
                adapterCategory.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                recyclerViewCat.scrollToPosition(0);
                adapterCategory.selectedPos = 0;

            }

        });

        b.show();

    }                       // spinner dialog -- delete  a category

    private void showDialogForNote(View v, final int stampID) {     // input dialog -- add a note
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        View viewInflated = LayoutInflater.from(v.getContext()).inflate(R.layout.text_input_note, null, false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.mStampsListCopy.get(adapter.findStampPositionInListByID(stampID)).setSubtitle(input.getText().toString());
                adapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });



        builder.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void saveData() {

        Gson gson = new Gson();
        String json = gson.toJson(adapter.mStampsListCopy);
        mPrefs.edit().putString(SHARED_PREFS_STAMPS, json).commit();
        json = gson.toJson(adapterCategory.mCategoryList);
        mPrefs.edit().putString(SHARED_PREFS_CATS, json).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_SHOW_MILLIS, showMillis).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_USE24HR, use24hrFormat).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_AUTONOTE, showNoteAddDialog).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_USEDARK, useDark).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_USE_GPS, use_gps).commit();
        mPrefs.edit().putInt(SHARED_PREFS_MAX_ID, maxListID).commit();
        mPrefs.edit().putInt(SHARED_PREFS_MAXCAT_ID, maxCatID).commit();
    }

    public void loadData() {

        if (mPrefs.contains(SHARED_PREFS_CATS)) {
            Gson gson = new Gson();
            String json = mPrefs.getString(SHARED_PREFS_CATS, "");
            Type type = new TypeToken<List<Category>>() {
            }.getType();
            Log.e("stamper", "Loaded cats: " + json);
            adapterCategory.mCategoryList = gson.fromJson(json, type);
            filterList();
        } else {
            populateCategories(); //if there are no categories in shared prefs (means its a 1st run right after install) we populate with some template categories
            Log.e("stamper", "1st run right after install.  populating categories with templates...");
        }

        if (mPrefs.contains(SHARED_PREFS_STAMPS)) {
            Gson gson = new Gson();
            String json = mPrefs.getString(SHARED_PREFS_STAMPS, "");
            Log.e("stamper", "all stamps to string: "+json);
            Type type = new TypeToken<List<Timestamp>>() {
            }.getType();
            adapter.mStampsListCopy = gson.fromJson(json, type);
            filterList();
        }


        if (mPrefs.contains(SHARED_PREFS_MAX_ID)) {
            maxListID = mPrefs.getInt(SHARED_PREFS_MAX_ID, 0);
        }

        if (mPrefs.contains(SHARED_PREFS_MAXCAT_ID)) {
            maxCatID = mPrefs.getInt(SHARED_PREFS_MAXCAT_ID, 0);
        }
    }
*/
}
