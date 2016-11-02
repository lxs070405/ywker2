package com.y.w.ywker.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.y.w.ywker.R;
import com.y.w.ywker.activity.HomeActivity;
import com.y.w.ywker.utils.HttpNetUtils;
import com.y.w.ywker.utils.OfflineDataManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 更新app服务
 * @author lxs
 * 2016年9月22日
 */
public class UpdateService extends Service {

	private String apkurl ;
	private String apkPath;
	private String apkName;
	private boolean canceled = false;
	private NotificationManager manager;
	private Notification notification;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			apkPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/update";
			apkName = "eweic.apk";
			OfflineDataManager offlineDataManager = OfflineDataManager.getInstance(this);
			apkurl = offlineDataManager.getAppPath();
			registerBroader();
			setUpNotifiction();

			new Thread(new DownApkRunnable(apkurl)).start();
		} else {
			Toast.makeText(UpdateService.this, "SD卡不存在", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 创建通知
	 */
	private void setUpNotifiction() {
		manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		int icon = R.drawable.logo;
		CharSequence tickerText = "开始下载";
		long when = System.currentTimeMillis();
		notification = new Notification(icon, tickerText, when);

		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notify_update_layout);
		contentView.setTextViewText(R.id.name, "eweic正在下载中");
		
		Intent canceledIntent = new Intent("canceled");
		canceledIntent.putExtra("canceled", "canceled");
		PendingIntent canceledPendingIntent = PendingIntent.getBroadcast(
				UpdateService.this, 1, canceledIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		contentView.setOnClickPendingIntent(R.id.cancle, canceledPendingIntent);
		notification.contentView = contentView;
//
		Intent intent = new Intent(UpdateService.this, HomeActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(
				UpdateService.this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;

		manager.notify(0, notification);// 发送通知
	}

	/**
	 * 取消接收者
	 */
	class CanceledReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("canceled".equals(intent.getStringExtra("canceled"))) {
				canceled = true;
				manager.cancel(0);
				stopSelf();
			}
		}

	}

	/**
	 * 注册广播
	 */
	public void registerBroader() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("canceled");
		receiver = new CanceledReceiver();
		registerReceiver(receiver, filter);
	}
	CanceledReceiver receiver;
	/**
	 * 下载apk
	 */
	class DownApkRunnable implements Runnable {
		String url ;
		public DownApkRunnable(String apkurl) {
			url = apkurl;
		}

		@Override
		public void run() {
			Log.e("lxs", "run: 版本路径地址"+url );
			downloadApk(url);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private long laterate = 0;

	private void downloadApk(String apkurlurl) {
		Request request = new Request.Builder().url(String.format(apkurlurl)).build();
		HttpNetUtils.enqueue(request, new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(Response response) throws IOException {
				InputStream in = response.body().byteStream();
				long length = response.body().contentLength();
				long count = 0;
				File apkPathFile = new File(apkPath);
				if (!apkPathFile.exists()) {
					apkPathFile.mkdir();
				}
				File apkFile = new File(apkPath, apkName);
				FileOutputStream os = new FileOutputStream(apkFile);
				byte[] buffer = new byte[1024];
				do {
					int numread = in.read(buffer);
					count += numread;
					long progress = (long) (((float) count / length) * 100);// 得到当前进度
					Log.e("lxs", "downloadApk:progress "+progress );
					if (progress >= laterate + 1) {// 只有当前进度比上一次进度大于等于1，才可以更新进度
						laterate = progress;
						Log.e("lxs", "downloadApk: "+laterate );
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = (int)progress;
						handler.sendMessage(msg);
					}
					if (numread <= 0) {// 下载完毕
						handler.sendEmptyMessage(2);
						canceled = true;
						laterate = progress;
						Log.e("lxs", "downloadApk:下载完毕 "+laterate );
						break;
					}
					os.write(buffer, 0, numread);
				} while (!canceled);// 如果没有被取消
				in.close();
				os.close();
			}
		});



	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:// 更新进度
				int progress = msg.arg1;
				if (progress < 100) {
					RemoteViews contentView = notification.contentView;
					contentView.setTextViewText(R.id.tv_progress, progress
							+ "%");

					contentView.setProgressBar(R.id.progressbar, 100, progress,
							false);
				} else {// 下载完成，停止服务
					stopSelf();
				}
				manager.notify(0, notification);
				break;
			case 2:// 安装apk
				manager.cancel(0);
				installApk();
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 安装apk
	 */
	private void installApk() {
		File apkFile = new File(apkPath, apkName);
		if (!apkFile.exists()) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.parse("file://" + apkFile.toString()),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
}
