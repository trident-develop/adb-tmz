package com.trident.library

import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.trident.library.constants.Constants
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
import com.trident.library.constants.Constants.autoZone
import com.trident.library.constants.Constants.autoZone_key
import com.trident.library.constants.Constants.battery
import com.trident.library.constants.Constants.battery_key
import com.trident.library.constants.Constants.campaignId
import com.trident.library.constants.Constants.campaign_id_key
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
import com.trident.library.constants.Constants.secure_get_parametr
import com.trident.library.constants.Constants.secure_key
import com.trident.library.constants.Constants.source
import com.trident.library.constants.Constants.source_key
import com.trident.library.constants.Constants.vpn
import com.trident.library.constants.Constants.vpn_key
import com.trident.library.storage.Repository
import com.trident.library.storage.persistroom.LinkDatabase
import java.util.jar.Manifest

object Utils {

    //processing final url
    fun processDataToFinalUrl(){
        finalUrl = finalUrl +
                "?$secure_get_parametr=$secure_key" +
                "&$dev_tmz_key=$devTmz" +
                "&$adb_key=$adb" +
                "&$autoZone_key=$autoZone" +
                "&$vpn_key=$vpn" +
                "&$battery_key=$battery" +
                "&$model_key=$model" +
                "&$manufacture_key=$manufacture" +
                "&$lockpin_key=$lockpin" +
                "&$gadid_key=$gadid" +
                "&$deeplink_key=$deeplink" +
                "&$source_key=$source" +
                "&$af_id_key=$afId" +
                "&$ad_id_key=$adId" +
                "&$adset_id_key=$adsetId" +
                "&$campaign_id_key=$campaignId" +
                "&$app_campaign_key=$appCampaign" +
                "&$adset_key=$adset" +
                "&$adgroup_key=$adgroup" +
                "&$orig_cost_key=$origCost" +
                "&$af_siteid_key=$afSteid"
    }



    //checking data on null vars and rearrange them
     fun rearrangeData() {
        if (adId.isEmpty()) adId = "null"

        if (adsetId.isEmpty()) adsetId = "null"

        if (campaignId.isEmpty()) campaignId = "null"

        if (appCampaign.isEmpty()) appCampaign = "null"

        if (adset.isEmpty()) adset = "null"

        if (adgroup.isEmpty()) adgroup = "null"

        if (origCost.isEmpty()) origCost = "null"

        if (afSteid.isEmpty()) afSteid = "null"

        if (battery.isEmpty()) battery = "null"

        if (vpn.isEmpty()) vpn = "null"

        if (model.isEmpty()) model = "null"

        if (manufacture.isEmpty()) manufacture = "null"

        if (gadid.isEmpty()) gadid = "null"

        if (deeplink.isEmpty()) deeplink = "null"

        if (source.isEmpty()) source = "null"

        if (afId.isEmpty()) afId = "null"

        if (devTmz.isEmpty()) devTmz = "null"
    }

    //creating repository instance
     fun createRepoInstance(context: Context): Repository {
        if (Constants.repository == null){
            return Repository(LinkDatabase.getDatabase(context).linkDao())
        } else {
            return Constants.repository as Repository
        }
    }


    fun collectData(){


    }


    fun collectLocation(activity: AppCompatActivity){


    }

}