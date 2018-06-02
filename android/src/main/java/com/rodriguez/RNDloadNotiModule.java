
package com.rodriguez;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import android.content.BroadcastReceiver;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.app.NotificationManager;

import java.util.Map;
import java.util.HashMap;

import org.json.JSONObject;


public class RNDloadNotiModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private NotificationManager notiManager;
  private DownloadNotification[] notifications = new DownloadNotification[100];
  private int lastNotiId = -1;
  private Map<String, String> labels = new HashMap<String, String>();

  private void sendEvent(String eventName, String msg) {
    reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, msg);
  }

  public RNDloadNotiModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    notiManager = (NotificationManager) reactContext.getSystemService(reactContext.getApplicationContext().NOTIFICATION_SERVICE);
    reactContext.registerReceiver(new BroadcastReceiver(){
      @Override
      public void onReceive(Context context, Intent intent) {
        try{
          JSONObject info = new JSONObject(intent.getStringExtra("info"));
          sendEvent("DownloadNotification"+ info.getString("notiId"), info.getString("intent"));
        } catch (Exception e){}
      }
    }, new IntentFilter("DOWNLOAD"));
    labels.put("icon", "rndn_icon");
    labels.put("downloading", "Downloading");
    labels.put("completed", "Download completed");
    labels.put("failed", "Download failed");
    labels.put("cancelled", "Download cancelled");
  }

  @ReactMethod
  public void setup(final ReadableMap options){
    new Thread(new Runnable() {
      @Override
      public void run() {
        if(options.hasKey("icon")){
          labels.put("icon", options.getString("icon"));
        }
        if(options.hasKey("downloadingLabel")){
          labels.put("downloading", options.getString("downloadingLabel"));
        }
        if(options.hasKey("completedLabel")){
          labels.put("completed", options.getString("completedLabel"));
        }
        if(options.hasKey("failedLabel")){
          labels.put("failed", options.getString("failedLabel"));
        }
        if(options.hasKey("cancelledLabel")){
          labels.put("cancelled", options.getString("cancelledLabel"));
        }
      }
    }).start();
  }

  @ReactMethod
  public void create(final String fileName, final Promise promise){
    new Thread(new Runnable() {
      @Override
      public void run() {
        lastNotiId++;
        notifications[lastNotiId] = new DownloadNotification(reactContext, notiManager, lastNotiId, labels, fileName);
        promise.resolve(lastNotiId);
      }
    }).start();
  }

  @ReactMethod
  public void updateProgress(final int notiId, final int progress, final Promise promise){
    new Thread(new Runnable() {
      @Override
      public void run() {
        notifications[notiId].updateProgress(progress);
        promise.resolve(true);
      }
    }).start();
  }

  @ReactMethod
  public void finish(final int notiId, final String state, final Promise promise){
    new Thread(new Runnable() {
      @Override
      public void run() {
        notifications[notiId].finish(state);
        promise.resolve(true);
      }
    }).start();
  }

  @Override
  public String getName() {
    return "DownloadNotification";
  }
}
