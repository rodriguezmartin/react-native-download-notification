
package com.rodriguez;

import com.facebook.react.bridge.ReactApplicationContext;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;
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
  private NotificationCompat.Builder builder;
  private NotificationManager notiManager;
  private boolean firstReceiver = true;
  private static final String NOTIFICATION_CHANNEL_ID = "download-notification-channel-id";

  public DownloadNotification(ReactApplicationContext reactContext, NotificationManager notiManager, int id, Map<String, String> labels, String fileName) {
    this.reactContext = reactContext;
    this.context = reactContext.getApplicationContext();
    this.notiManager = notiManager;
    this.id = id;
    this.fileName = fileName;
    this.labels = labels;

    checkOrCreateChannel(notificationManager);

    builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
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

  private static boolean channelCreated = false;
  private void checkOrCreateChannel(NotificationManager manager) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
      return;
    if (channelCreated)
      return;
    if (manager == null)
      return;

    Bundle bundle = new Bundle();

    int importance = NotificationManager.IMPORTANCE_HIGH;

    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Download Notification", importance);

    channel.setDescription("Contains all download notifications");
    channel.enableLights(true);
    channel.enableVibration(true);

    manager.createNotificationChannel(channel);
    channelCreated = true;
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