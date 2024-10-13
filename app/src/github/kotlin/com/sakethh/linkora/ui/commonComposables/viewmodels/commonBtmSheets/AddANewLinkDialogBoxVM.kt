package com.sakethh.linkora.ui.commonComposables.viewmodels.commonBtmSheets

import androidx.lifecycle.ViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sakethh.linkora.ui.screens.settings.SettingsPreference

class AddANewLinkDialogBoxVM: ViewModel() {

    init {
        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(SettingsPreference.isSendCrashReportsEnabled.value)
    }

}