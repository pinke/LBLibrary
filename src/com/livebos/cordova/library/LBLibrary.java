package com.livebos.cordova.library;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import org.apache.cordova.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

/**
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class LBLibrary extends CordovaPlugin {

    protected static final String LOG_TAG = "LiveBOSLib";
    private static final String EXIT_EVENT = "exit";
    private static final String LOAD_START_EVENT = "loadstart";
    private static final String LOAD_STOP_EVENT = "loadstop";

    private WebView inAppWebView;
    private CallbackContext callbackContext;

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArry of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return A PluginResult object with a status and message.
     */
    public boolean execute(final String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        this.callbackContext = callbackContext;
        if (action.equals("launch")) { //启动app
            final String activityName = args.getString(0);
            final String serviceURL = args.getString(1);
            Log.d(LOG_TAG, "target activity name = " + activityName);
            Log.d(LOG_TAG, " serviceURL = " + serviceURL);
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(serviceURL.equals("samples")){
                            openSample();
                            return;
                        }
                        Intent intent = new Intent(activityName);
                        intent.setData(Uri.parse(serviceURL));
                        cordova.getActivity().startActivity(intent); //判断安装及版本等
                    } catch (android.content.ActivityNotFoundException e) {
                        LOG.e(LOG_TAG, "Error  " + serviceURL + ": " + e.toString());
                    }
                }
            });
        } else if(action.equals("close")) {
            closeApp();
        } else {
            return false;
        }
        return true;
    }

    private void closeApp() {
            this.cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   //TODO: close app
                }
            });

            try {
                JSONObject obj = new JSONObject();
                obj.put("type", EXIT_EVENT);
                sendUpdate(obj, false);
            } catch (JSONException ex) {
                Log.d(LOG_TAG, "Should never happen");
            }
    }

    /**
     * Create a new plugin success result and send it back to JavaScript
     *
     * @param obj a JSONObject contain event payload information
     */
    private void sendUpdate(JSONObject obj, boolean keepCallback) {
        sendUpdate(obj, keepCallback, PluginResult.Status.OK);
    }

    /**
     * Create a new plugin result and send it back to JavaScript
     *
     * @param obj a JSONObject contain event payload information
     * @param status the status code to return to the JavaScript environment
     */
    private void sendUpdate(JSONObject obj, boolean keepCallback, PluginResult.Status status) {
        if (callbackContext != null) {
            PluginResult result = new PluginResult(status, obj);
            result.setKeepCallback(keepCallback);
            callbackContext.sendPluginResult(result);
            if (!keepCallback) {
                callbackContext = null;
            }
        }
    }
    /**
     * //TODO: test
     * 检查系统应用程序，并打开
     */
    private void openSample(){
        //应用过滤条件
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager mPackageManager = cordova.getActivity().getApplicationContext().getPackageManager();
        List<ResolveInfo> mAllApps = mPackageManager.queryIntentActivities(mainIntent, 0);
        //按报名排序
        Collections.sort(mAllApps, new ResolveInfo.DisplayNameComparator(mPackageManager));

        for(ResolveInfo res : mAllApps){
            //该应用的包名和主Activity
            String pkg = res.activityInfo.packageName;
            String cls = res.activityInfo.name;

            // 打开QQ
//            if(pkg.contains("qq")){
            if(pkg.contains("rebsabbubg")){//sample
                ComponentName componet = new ComponentName(pkg, cls);
                Intent intent = new Intent();
                intent.setComponent(componet);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cordova.getActivity().startActivity(intent);
            }
        }
    }

}
