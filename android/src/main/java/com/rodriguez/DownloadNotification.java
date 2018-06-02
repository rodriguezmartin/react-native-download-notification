
package com.rodriguez;

import com.facebook.react.bridge.ReactApplicationContext;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DownloadNotification {
  private final ReactApplicationContext reactContext;
  private final Context context;
  public int id;
  private String fileName;
  private Map<String, String> labels = new HashMap<String, String>();
  private Notification.Builder builder;
  private NotificationManager notiManager;
  private boolean firstReceiver = true;

  public DownloadNotification(ReactApplicationContext reactContext, NotificationManager notiManager, int id, Map<String, String> labels, String fileName) {
    this.reactContext = reactContext;
    this.context = reactContext.getApplicationContext();
    this.notiManager = notiManager;
    this.id = id;
    this.fileName = fileName;
    this.labels = labels;

    builder = new Notification.Builder(context)
      .setSmallIcon(reactContext.getResources().getIdentifier(labels.get("icon"), "mipmap", reactContext.getPackageName()))
      .setContentTitle(fileName)
      .setContentText(labels.get("downloading"))
      .setProgress(100, 0, true)
      .setDeleteIntent(newIntent("dismiss"))
      .setContentIntent(newIntent("cancel"));
    publish();
  }

  private PendingIntent newIntent(String type) {
    Intent intent = new Intent("DOWNLOAD");
    intent.putExtra("info", "{\"notiId\": "+ String.valueOf(id) +", \"intent\": \""+ type +"\"}");
    PendingIntent pendingIntent = PendingIntent.getBroadcast(reactContext, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    return pendingIntent;
  }

  public void publish() {
    notiManager.notify(id, builder.build());
  }

  public void updateProgress(int progress) {
    builder.setProgress(100, progress, false);
    publish();
  }

  public void finish(String state) {
    try{
      TimeUnit.SECONDS.sleep(1);
      builder.setContentText(labels.get(state));
      builder.setProgress(0, 0, false);
      builder.setContentIntent(Objects.equals(state,"completed") ? newIntent("open") : newIntent("dismiss"));
      builder.setAutoCancel(true);
      publish();
    }
    catch(Exception e){}
  }
}