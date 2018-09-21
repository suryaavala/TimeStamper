package com.sardox.timestamper

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
       // setPreferencesFromResource(R.xml.myprefs, rootKey);

        addPreferencesFromResource(R.xml.myprefs)
    }
}
