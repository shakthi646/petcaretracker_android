package com.ksp.petcaretracker.provider

import android.content.Context
import com.ksp.corelibrary.coreInterface.DefaultAppConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DefaultAppConfigProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : DefaultAppConfig {

    override fun getAppName(): String = APP_NAME

    override fun getDatabaseName(): String = DB_NAME

    override fun getPrivacyPolicyUrl(): String = PRIVACY_POLICY_URL

    override fun getTermsAndConditionsUrl(): String = TERMS_AND_CONDITIONS_URL

    companion object {
        const val APP_NAME = "PetCare+"
        const val DB_NAME = "petcare_db"
        const val PRIVACY_POLICY_URL = ""
        const val TERMS_AND_CONDITIONS_URL = ""
    }
}
