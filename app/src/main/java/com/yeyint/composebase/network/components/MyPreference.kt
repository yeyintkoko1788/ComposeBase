package com.yeyint.composebase.network.components

import android.content.Context
import android.content.SharedPreferences
import com.yeyint.composebase.R
import com.yeyint.composebase.network.showLogE
import com.yeyint.composebase.uitls.LOCALE_EN
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import javax.inject.Inject

class MyPreference @Inject constructor(private val context : Context){
    private val PREFERENCES_NAME = "MyCommPreference"

    private val sharedPreferences : SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME , Context.MODE_PRIVATE)

    fun clear(){
        sharedPreferences.edit().clear().apply()
    }

    fun logout(){
        sharedPreferences.edit().remove(context.getString(KEYS.ACCESS_TOKEN.label)).apply()
        sharedPreferences.edit().remove(context.getString(KEYS.REFRESH_TOKEN.label)).apply()
        sharedPreferences.edit().remove(context.getString(KEYS.USER_ID.label)).apply()
    }

    fun delete(key : KEYS) {
        sharedPreferences.edit().putString(context.getString(key.label) , key.defaultValue).apply()
    }

    fun delete(key : KEYB) {
        sharedPreferences.edit().putBoolean(context.getString(key.label) , key.defaultValue).apply()
    }

    fun delete(key : KEYI) {
        sharedPreferences.edit().putInt(context.getString(key.label) , key.defaultValue).apply()
    }

    fun save(key : KEYS, value : String) {
        sharedPreferences.edit().putString(context.getString(key.label) , value).commit()
    }

    fun load(key : KEYS) : String? {
        return sharedPreferences.getString(context.getString(key.label) , key.defaultValue)
    }

    fun save(key : KEYI, value : Int) {
        sharedPreferences.edit().putInt(context.getString(key.label) , value).commit()
    }

    fun load(key : KEYI) : Int {
        return sharedPreferences.getInt(context.getString(key.label) , key.defaultValue)
    }

    fun save(key : KEYB, value : Boolean) {
        sharedPreferences.edit().putBoolean(context.getString(key.label) , value).commit()
    }

    fun load(key : KEYB) : Boolean {
        return sharedPreferences.getBoolean(context.getString(key.label) , key.defaultValue)
    }

    fun delete(key : KEYL) {
        sharedPreferences.edit().putLong(context.getString(key.label) , key.defaultValue).apply()
    }

    fun save(key : KEYL, value : Long) {
        sharedPreferences.edit().putLong(context.getString(key.label) , value).commit()
    }

    fun load(key : KEYL) : Long {
        return sharedPreferences.getLong(context.getString(key.label) , key.defaultValue)
    }

    fun save(key : KEYAS, value : ArrayList<String>){
        // creating a new variable for gson.
        showLogE("beforeJson:$value")
        val gson = Gson()

        // getting data from gson and storing it in a string.
        val json : String = gson.toJson(value)
        showLogE("saveJson:$json")
        sharedPreferences.edit().putString(context.getString(key.label),json).apply()
    }

    fun load(key : KEYAS) : ArrayList<String>{
        // creating a variable for gson.
        val gson = Gson()

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        val json = sharedPreferences.getString(context.getString(key.label) , key.defaultValue)
        showLogE("jsonString:$json")

        // below line is to get the type of our array list.
        val type : Type = object : TypeToken<ArrayList<String?>?>() {}.type

        return gson.fromJson(json , type) ?: ArrayList()
    }

    enum class KEYS(val label : Int , val defaultValue : String) {
        ACCESS_TOKEN(R.string.pref_access_token, ""),
        REFRESH_TOKEN(R.string.pref_refresh_token, ""),
        SECRET_HEADER_KEY(R.string.pref_secret_header_key, ""),
        USER_ID(R.string.pref_user_id, ""),
        SPLASH_SCREEN_PHOTO(R.string.pref_splash_screen_photo, ""),
        ONBOARDING_SCREEN_PHOTO(R.string.pref_onboarding_screen_photo, ""),
        WELCOME_SCREEN_PHOTO(R.string.pref_welcome_screen_photo, ""),
        APP_THEME_PHOTO(R.string.pref_app_theme_photo, ""),
        HOME_SCREEN_PHOTO(R.string.pref_home_screen_photo, ""),
        USER_CITY(R.string.pref_user_city, ""),
        USER_COUNTRY(R.string.pref_user_country, ""),
        CHAT_BACKGROUND(R.string.pref_chat_background, ""),
        LOCALE(R.string.pref_locale, LOCALE_EN),
        MY_COLLECTION(R.string.pref_my_collection, ""),
        PUSHY_TOKEN(R.string.pref_push_token, ""),
        CURRENT_LOCATION_LAT(R.string.pref_current_location_lat, ""),
        CURRENT_LOCATION_LONG(R.string.pref_current_location_long, ""),
        CURRENT_LOCATION_NAME(R.string.pref_current_location_name, ""),
        USER_PERMISSION(R.string.pref_user_permission, ""),
        NOTIFICATION_SETTING(R.string.pref_notification_setting,""),
        SELECTED_NOTIFICATION_URL(R.string.pref_notification_sound_url, ""),
        VERSION_UPDATE(R.string.pref_version_update, ""),
    }

    enum class KEYB(val label : Int , val defaultValue : Boolean) {
        IS_ADMIN(R.string.pref_is_admin, false),
        IS_NOTIFICATION_ASKED(R.string.pref_is_notification_ask, false),
        IS_FIRST_TIME(R.string.pref_is_first_time, true),
        IS_PHONE_LOGIN_AVAILABLE(R.string.pref_is_phone_login_available, true),
        IS_EMAIL_LOGIN_AVAILABLE(R.string.pref_is_email_login_available, true),

        IS_GUEST_MODE(R.string.pref_is_guest, false)

    }

    enum class KEYI(val label : Int , val defaultValue : Int) {

    }

    enum class KEYL(val label : Int , val defaultValue : Long) {

    }

    enum class KEYAS(val label : Int, val defaultValue : String){

    }


}