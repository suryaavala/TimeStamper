package com.sardox.timestamper


import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sardox.timestamper.Managers.DataManager
import com.sardox.timestamper.dialogs.*
import com.sardox.timestamper.objects.Category
import com.sardox.timestamper.objects.QuickNote
import com.sardox.timestamper.objects.Timestamp
import com.sardox.timestamper.recyclerview.CategoryAdapter
import com.sardox.timestamper.recyclerview.TimestampsAdapter
import com.sardox.timestamper.types.JetTimestamp
import com.sardox.timestamper.types.JetUUID
import com.sardox.timestamper.types.PhysicalLocation
import com.sardox.timestamper.utils.*
import com.sardox.timestamper.utils.Constants.Analytics.Events.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, UserActionInterface, CategoryUpdatesInterface, TimestampsCountListenerInterface {

    companion object {
        private const val RC_READWRITE = 9863
        private const val RC_LOCATION = 9862
    }

    private var unfilteredTimestamps: HashMap<JetUUID, Timestamp> = hashMapOf()
    private var categories: MutableList<Category> = mutableListOf()
    private var metrics = DisplayMetrics()

    private val dataManager: DataManager  by lazy { DataManager(this) }
    private val icons: List<TimestampIcon> by lazy { Utils.getStockIcons() }
    private var appSettings: AppSettings = AppSettings()
    private val adapterCategory: CategoryAdapter by lazy { CategoryAdapter(categories, this, icons) }
    private val mFusedLocationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    private val timestampsAdapter: TimestampsAdapter  by lazy { TimestampsAdapter(categories, metrics, icons, this, this, this, appSettings) }

    private var lastSelectedCategory: Category = Category.Default


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("srdx", "-----------NEW RUN--------------")
        super.onCreate(savedInstanceState)
       // setTheme(R.style.AppThemeDark)
        loadUserSettings()

        if (appSettings.shouldUseDarkTheme){
            setTheme(R.style.AppThemeDark)
        }

        windowManager.defaultDisplay.getMetrics(metrics)        // !! TODO CONFIGURATION CHANGE!!!
        setContentView(R.layout.activity_main)
        toolbar_text.setText(R.string.app_name)
        setSupportActionBar(toolbar_top)
        setupDrawer()
        initApp()
        fab.setOnClickListener { createNewTimestamp() }
        logEvent(SHOW_MAIN_ON_CREATE)
    }

    override fun onStart() {
        super.onStart()
        logEvent(SHOW_MAIN_ON_START)
    }

    private fun createNewTimestamp() {
        logEvent(Constants.Analytics.Events.NEW_TIMESTAMP)
        val newTimestamp = Timestamp(
                JetTimestamp.now(),
                PhysicalLocation.Default,
                lastSelectedCategory.categoryID,
                JetUUID.randomUUID())

        unfilteredTimestamps[newTimestamp.identifier] = newTimestamp
        timestampsAdapter.add(newTimestamp)
        if (appSettings.showNoteAddDialog) editNote(newTimestamp)
        if (appSettings.shouldUseGps && hasGPSpermission()) {
            try {
                mFusedLocationClient.lastLocation.addOnSuccessListener { lastKnownLocation ->
                    if (lastKnownLocation == null) {
                        Toast.makeText(this@MainActivity, getString(R.string.null_location), Toast.LENGTH_SHORT).show()
                    } else {
                        val physicalLocation = PhysicalLocation(lastKnownLocation.latitude, lastKnownLocation.longitude)
                        newTimestamp.physicalLocation = physicalLocation
                    }
                }.addOnFailureListener { Toast.makeText(this@MainActivity, getString(R.string.null_location), Toast.LENGTH_SHORT).show() }
            } catch (e: SecurityException) {
                Log.e("SecurityException", "SecurityException during getLastLocation")
                logEvent(Constants.Analytics.Events.SECURITY_EXCEPTION)
            }

        }
        scrollViewTop()
        if (!appSettings.showNoteAddDialog) {
            Snackbar.make(recyclerView_timestamps, getString(R.string.new_timestamp_created) + " in " + lastSelectedCategory.name, Snackbar.LENGTH_LONG).show()
        }
    }


    override fun onUserAction(action: UserAction) {
        with(action.timestamp) {
            when (action.actionType) {
                ActionType.REMOVE_TIMESTAMP -> removeTimestamp(this)
                ActionType.EDIT_NOTE -> editNote(this)
                ActionType.EDIT_TIME -> pickTime(this)
                ActionType.EDIT_DATE -> pickDate(this)

                ActionType.CHANGE_CATEGORY -> changeCategory(this)
                ActionType.SHOW_MAP -> showTimestampOnMap(this)
                ActionType.SELECTED -> {
                    invalidateOptionsMenu()
                    if (action.count == 0) {
                        toolbar_text.text = lastSelectedCategory.name
                    } else {
                        toolbar_text.text = "${action.count} selected"
                        if (action.count == 2) showToastWithTimeDifference()
                    }
                }
                ActionType.SHARE_TIMESTAMP, null -> {
                }
            }
        }
    }

    override fun onCategoryRemove(selectedCategory: Category) {
        if (categories.size == 1) {
            Snackbar.make(recyclerView_timestamps, "Default category can not be deleted..  \uD83D\uDE44", Snackbar.LENGTH_SHORT).show()
        } else {
            onCategoryRemoved(selectedCategory)
        }
    }

    override fun onCategoryChanged(selectedCategory: Category) {
        Log.d("srdx", "selected_category: " + selectedCategory.name + " #" + selectedCategory.categoryID)
        lastSelectedCategory = selectedCategory
        timestampsAdapter.clearSelection()
        filterTimestampsByCategory(lastSelectedCategory)
        toolbar_text.text = selectedCategory.name
        toggleBackdrop()
    }

    override fun onCountChanged(count: Int) {
        when (count) {
            0 -> empty_list.visibility = View.VISIBLE
            else -> empty_list.visibility = View.GONE
        }
    }

    private fun showToastWithTimeDifference() {
        Snackbar.make(recyclerView_timestamps, "Time difference: ${ timestampsAdapter.timeDifferenceBetweenTwoSelectedTimestamps}", Snackbar.LENGTH_LONG).show()
    }

    override fun onCategoryAdded() {
        showAddNewCategoryDialog()
    }

    private fun initRecyclerView() {
        with(recyclerView_categories) {
            setHasFixedSize(true)
            adapter = adapterCategory
            layoutManager = LinearLayoutManager(this@MainActivity).apply { orientation = LinearLayoutManager.VERTICAL }
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(VerticalSpaceItemDecoration(10))
        }

        with(recyclerView_timestamps) {
            adapter = timestampsAdapter
            layoutManager = LinearLayoutManager(this@MainActivity).apply { orientation = LinearLayoutManager.VERTICAL; reverseLayout = false }
            itemAnimator = DefaultItemAnimator()
            //addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply { setDrawable(ContextCompat.getDrawable(context, R.drawable.timestamps_divider)!!) })

            setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity) {
                override fun onSwipeRight() {
                    selectNextCategory()
                }

                override fun onSwipeLeft() {
                    selectPreviousCategory()
                }
            })
        }

        filterTimestampsByCategory(Category.Default)
    }

    private fun selectPreviousCategory() {
        val currentIndex = categories.indexOf(lastSelectedCategory)
        if (currentIndex < categories.size) {
            lastSelectedCategory = categories[currentIndex + 1]
            adapterCategory.setSelectedCategory(lastSelectedCategory)
            adapterCategory.notifyDataSetChanged()
            filterTimestampsByCategory(categories[currentIndex + 1])
            toolbar_text.text = lastSelectedCategory.name
        }
    }

    private fun selectNextCategory() {
        val currentIndex = categories.indexOf(lastSelectedCategory)
        if (currentIndex > 0) {
            lastSelectedCategory = categories[currentIndex - 1]
            adapterCategory.setSelectedCategory(lastSelectedCategory)
            adapterCategory.notifyDataSetChanged()
            filterTimestampsByCategory(categories[currentIndex - 1])
            toolbar_text.text = lastSelectedCategory.name
        }
    }

    private fun filterTimestampsByCategory(selectedCategory: Category) {
        timestampsAdapter.removeAll()
        timestampsAdapter.add(Utils.filterTimestampsByCategory(unfilteredTimestamps, selectedCategory))
    }

    private fun scrollViewTop() {
        recyclerView_timestamps.smoothScrollToPosition(0)
    }

    private var isCollapsed: Boolean = true

    private fun toggleBackdrop() {
        if (isCollapsed) {
            recyclerView_timestamps_container.animate().translationY(recyclerView_categories.height.toFloat())
            toolbar_text.text = "Categories"
            recyclerView_categories.scrollToPosition(adapterCategory.selectedCategoryPosition())
        } else {
            toolbar_text.text = lastSelectedCategory.name
            recyclerView_timestamps_container.animate().translationY(0f)
        }
        isCollapsed = !isCollapsed
        invalidateOptionsMenu()
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (isCollapsed) {
            menu?.findItem(R.id.action_category_toggle_backdrop)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_list_white)
            menu?.findItem(R.id.action_timestamps_delete)?.isVisible = timestampsAdapter.hasSelectedTimestamps()
        } else {
            menu?.findItem(R.id.action_category_toggle_backdrop)?.let { it.icon = ContextCompat.getDrawable(this, R.drawable.ic_close_white) }
        }

        if (appSettings.shouldUseDarkTheme){
            menu?.findItem(R.id.action_category_toggle_backdrop)?.icon?.setTint(Color.WHITE)
            menu?.findItem(R.id.action_timestamps_delete)?.icon?.setTint(Color.WHITE)
        } else {
            menu?.findItem(R.id.action_category_toggle_backdrop)?.icon?.setTint(ContextCompat.getColor(this, R.color.colorAccentLightTheme))
            menu?.findItem(R.id.action_timestamps_delete)?.icon?.setTint(ContextCompat.getColor(this, R.color.colorAccentLightTheme))
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_category_toggle_backdrop -> toggleBackdrop()
            R.id.action_timestamps_delete -> {
                if (timestampsAdapter.hasSelectedTimestamps()) {
                    removeGroupOfTimestamps()
                }
            }
        }
        return true
    }

    private fun removeGroupOfTimestamps() { //TODO!!!!
        logEvent(Constants.Analytics.Events.REMOVE_GROUP)
        val copyOfRemovedItems = timestampsAdapter.deepCopyOfSelectedTimestamps
        unfilteredTimestamps.keys.removeAll(timestampsAdapter.selectedTimestampsUUIDs)
        timestampsAdapter.removeSelectedTimestamps() //may be not necessary since we already removed from hasmap
        timestampsAdapter.clearSelection()
        Snackbar.make(recyclerView_timestamps, copyOfRemovedItems.size.toString() + " timestamps were removed", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    logEvent(Constants.Analytics.Events.REMOVE_GROUP_UNDO)
                    timestampsAdapter.add(copyOfRemovedItems)
                    unfilteredTimestamps.putAll(Utils.listToHashMap(copyOfRemovedItems))
                    timestampsAdapter.notifyDataSetChanged()
                }
                .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.showSettings -> {
                logEvent(Constants.Analytics.Events.SHOW_SETTINGS)
                SettingsDialog(this) { applyUserSettings() }
            }
            R.id.action_export -> {
                exportTimestampsToCsv()
            }
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun pickDate(timestampToUpdate: Timestamp) {
        logEvent(Constants.Analytics.Events.EDIT_DATE)
        MyDatePickerDialog(this, timestampToUpdate) { onTimeChanged(timestampToUpdate, it) }
    }

    private fun pickTime(timestampToUpdate: Timestamp) {
        logEvent(Constants.Analytics.Events.EDIT_TIME)
        MyTimePickerDialog(this, timestampToUpdate, { onTimeChanged(timestampToUpdate, it) }, appSettings.use24hrFormat)
    }

    private fun onTimeChanged(timestampToUpdate: Timestamp, updatedDate: JetTimestamp) {
        unfilteredTimestamps[timestampToUpdate.identifier]!!.timestamp = updatedDate
        timestampsAdapter.updateTimestamp(timestampToUpdate)
    }

    private fun editNote(timestamp: Timestamp) {
        logEvent(Constants.Analytics.Events.EDIT_NOTE)
        EditNoteDialog(this, timestamp, appSettings) { onNoteEdited(timestamp, it) }
    }

    private fun onNoteEdited(timestamp: Timestamp, newNote: String) {
        unfilteredTimestamps[timestamp.identifier]!!.note = newNote
        timestampsAdapter.updateTimestamp(timestamp)
        appSettings.quickNotes.addNote(QuickNote(JetTimestamp.now(), newNote))
        dataManager.writeUserNotes(appSettings)
    }

    private fun changeCategory(timestamp: Timestamp) {
        logEvent(Constants.Analytics.Events.EDIT_CATEGORY)
        CategoryListBottomSheet(this, "Move to", categories, icons) { onCategoryChanged(timestamp, it) }
    }

    private fun onCategoryChanged(timestamp: Timestamp, newCategory: Category) {
        unfilteredTimestamps[timestamp.identifier]!!.setCategory_identifier(newCategory.categoryID)
        when {
            lastSelectedCategory.categoryID == newCategory.categoryID || lastSelectedCategory == Category.Default -> timestampsAdapter.updateTimestamp(timestamp)
            else -> timestampsAdapter.remove(timestamp)
        }
    }

    private fun removeTimestamp(timestampToRemove: Timestamp) {
        logEvent(Constants.Analytics.Events.TIMESTAMP_REMOVE)
        timestampsAdapter.remove(timestampToRemove)
        unfilteredTimestamps.remove(timestampToRemove.identifier)
    }

    private fun showTimestampOnMap(timestamp: Timestamp) {
        logEvent(Constants.Analytics.Events.OPEN_MAP)
        when {
            timestamp.physicalLocation != null -> try {
                val uri = String.format("geo:0,0?q=%s(%s)", timestamp.physicalLocation.toSimpleCommaString(), timestamp.note)
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
            } catch (ex: ActivityNotFoundException) {
                Snackbar.make(recyclerView_timestamps, "Please install a maps application", Snackbar.LENGTH_SHORT).show()
            } catch (ex: NullPointerException) {
            }
        }
    }

    private fun showAddNewCategoryDialog() {
        logEvent(Constants.Analytics.Events.ADD_NEW_CATEGORY)
        AddCategoryDialog(this, icons) { onNewCategoryCreated(it) }
    }

    private fun onNewCategoryCreated(newCategory: Category) {
        if (newCategory.name.isEmpty()) {
            Snackbar.make(recyclerView_timestamps, "Category name can't be empty \uD83E\uDD14", Snackbar.LENGTH_SHORT).show()
        } else {
            categories.add(newCategory)
            sortCategories()
            logEvent(Constants.Analytics.Events.NEW_CATEGORY)

            lastSelectedCategory = newCategory
            adapterCategory.setSelectedCategory(newCategory)
            adapterCategory.notifyDataSetChanged()

            recyclerView_categories.smoothScrollToPosition(adapterCategory.itemCount) //scrolling to new category
            filterTimestampsByCategory(newCategory)
            saveCategories()
            Utils.updateGridWidget(applicationContext)
        }
        toggleBackdrop()
    }

    private fun onCategoryRemoved(categoryToRemove: Category) {
        when (categoryToRemove) {
            Category.Default -> Snackbar.make(recyclerView_timestamps, R.string.cant_be_removed, Snackbar.LENGTH_LONG).show()
            else -> showDeleteConfirmDialog(categoryToRemove)
        }
    }

    private fun showDeleteConfirmDialog(categoryToRemove: Category) {
        logEvent(Constants.Analytics.Events.REMOVE_CATEGORY)
        ConfirmRemoveCategoryDialog(this, Consumer { isConfirmed ->
            if (isConfirmed) {
                resetWidgetCategoryToNoneIfNeeded(categoryToRemove)
                Utils.removeTimestampsByCategory(unfilteredTimestamps, categoryToRemove)
                categories.remove(categoryToRemove)
                resetView()
                saveCategories()
                Utils.updateGridWidget(applicationContext)
                toggleBackdrop()
            }
        })
    }

    private fun resetView() {
        recyclerView_categories.smoothScrollToPosition(0) //scrolling to default category
        lastSelectedCategory = Category.Default        //switch to default category
        adapterCategory.setSelectedCategory(Category.Default)
        adapterCategory.notifyDataSetChanged()
        filterTimestampsByCategory(lastSelectedCategory)
    }

    private fun resetWidgetCategoryToNoneIfNeeded(category: Category) {
        when {
            dataManager.readDefaultCategoryForWidget() == category.categoryID -> dataManager.saveDefaultCategoryForWidget(AppSettings.NO_DEFAULT_CATEGORY)
        }
    }

    private fun hasGPSpermission(): Boolean =
            when {
                EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION) -> true
                else -> {
                    Log.d("srdx", "ACCESS_FINE_LOCATION permission not granted. Requesting Permissions...")
                    EasyPermissions.requestPermissions(this, "To save your location, Timestamper needs the access to Location services",
                            RC_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    false
                }
            }

    private fun exportTimestampsToCsv() {
        logEvent(Constants.Analytics.Events.EXPORT_TIMESTAMPS)
        when {
            EasyPermissions.hasPermissions(this, *Constants.STORAGE_PERMS) -> emailCSV(dataManager.exportToCSV(lastSelectedCategory, ArrayList(unfilteredTimestamps.values), categories))
            else -> {
                Log.d("srdx", " EasyPermissions not granted. Requesting Permissions...")
                EasyPermissions.requestPermissions(this, "App requires your permission to save and export CSV file",
                        RC_READWRITE, *Constants.STORAGE_PERMS)
            }
        }
    }

    private fun emailCSV(file: File?) {
        Utils.emailCSV(this, file)
    }

    private fun logEvent(event: String) {
        AppInstance.firebaseAnalytics.logEvent(event, Bundle())
    }

    private fun logEvent(event: String, bundle: Bundle) {
        AppInstance.firebaseAnalytics.logEvent(event, bundle)
    }

    private fun applyUserSettings() {
        loadUserSettings()
        timestampsAdapter.updateAppSettings(appSettings)
        timestampsAdapter.notifyDataSetChanged()
    }

    private fun sortCategories() {
        val newList = categories
        newList.remove(Category.Default)
        newList.sortBy { it.name }
        newList.add(0, Category.Default)
        categories = newList
    }

    private fun initApp() {
        loadTimestamps()
        loadCategories()
        initRecyclerView()
    }

    /**
     * Must be called before setupDrawer()
     */
    private fun loadUserSettings() {
        appSettings = dataManager.loadUserSettings()
        sendSettingsToAnalytics()
    }

    private fun sendSettingsToAnalytics() {
        val bundle = Bundle()
        bundle.putBoolean("quickNotesEnabled", appSettings.shouldUseQuickNotes)
        bundle.putBoolean("autoKeyboardEnabled", appSettings.shouldShowKeyboardInAddNote)
        bundle.putBoolean("millisecondsEnabled", appSettings.shouldShowMillis)
        bundle.putBoolean("noteAddDialogEnabled", appSettings.showNoteAddDialog)
        bundle.putBoolean("amPmFormatEnabled", appSettings.use24hrFormat)
        bundle.putBoolean("gpsEnabled", appSettings.shouldUseGps)
        bundle.putBoolean("useDarkTheme", appSettings.shouldUseDarkTheme)
        logEvent(Constants.Analytics.Events.SETTINGS_LOADED, bundle)
    }

    private fun loadCategories() {
        categories = dataManager.readCategories()
        sortCategories()
        sendCategoriesCountToAnalytics()
    }

    private fun sendCategoriesCountToAnalytics() {
        val bundle = Bundle()
        bundle.putInt("categoriesTotal", categories.size)
        logEvent(Constants.Analytics.Events.TOTAL_CATEGORIES, bundle)
    }

    private fun loadTimestamps() {
        unfilteredTimestamps = dataManager.readTimestamps()
        sendTimestampsCountToAnalytics()
    }

    private fun sendTimestampsCountToAnalytics() {
        val bundle = Bundle()
        bundle.putInt("timestampsTotal", unfilteredTimestamps.size)
        logEvent(Constants.Analytics.Events.TOTAL_TIMESTAMPS, bundle)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.category_menu, menu)
        return true
    }

    private fun setupDrawer() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar_top, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val header = navigationView.getHeaderView(0)
        val phraseOfTheDay = header.findViewById<TextView>(R.id.phraseOfTheDay)
        phraseOfTheDay.text = Utils.getPhraseOfTheDay(this)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        logEvent(SHOW_MAIN_ON_RESUME)
    }

    private fun saveData() {
        Log.d("srdx", "saving data...")
        dataManager.writeTimestamps(unfilteredTimestamps)
        dataManager.clearWidgetTimestamps()
        saveCategories()
    }

    private fun saveCategories() {
        dataManager.writeCategories(categories)
    }

    override fun onBackPressed() {
        if (timestampsAdapter.hasSelectedTimestamps()) {
            timestampsAdapter.clearSelectionAndUpdateView()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * while app is in background, user can create timestamps with widget. when user back in app, we need to read them and add them to recycler view
     */
    private fun loadData() {
        if (timestampsAdapter.hasSelectedTimestamps()) {
            timestampsAdapter.clearSelectionAndUpdateView()
        }
        val widgetTimestamps = dataManager.readWidgetTimestamps()
        if (!widgetTimestamps.isEmpty()) {
            unfilteredTimestamps.putAll(widgetTimestamps)
            filterTimestampsByCategory(lastSelectedCategory)
        }
    }
}
