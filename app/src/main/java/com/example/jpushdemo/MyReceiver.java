package com.example.jpushdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.y.w.ywker.activity.ActivityMsgList;
import com.y.w.ywker.activity.ActivityOrderReplayDetails;
import com.y.w.ywker.activity.HomeActivity;
import com.y.w.ywker.R;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.utils.OfflineDataManager;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 *
 */

public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPushlxs";
	private Context ctx;
	//工单ID
	private String sheetId = "";
	private String order_type = "";
	private String order_status = "";
	private String order_title = "";
	//推送类型
	private String notify_type = "0";

	private String msg = "";
	@Override
	public void onReceive(Context context, Intent intent) {
		this.ctx = context;
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

			msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			Log.e(TAG, "收到的消息"+msg);
			/**
			 * 处理自定义消息
			 */
		    String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Log.e(TAG, "待处理消息"+message);
			if(!message.isEmpty()){
				try {
					/**
					 * 解析json
					 */
					JSONObject json = new JSONObject(message);
					sheetId = json.getString("SheetID");
					String ids = json.getString("IDS");
					String type = json.getString("Type");
					notify_type = type;

					order_status = json.getString("SheetStateCN");
					order_title = json.getString("SheetTitle");
					order_type = json.getString("TypeName");
					String megstr = bundle.getString(JPushInterface.EXTRA_MESSAGE);
					Log.e(TAG, "onReceive:接收到的消息内容详情 "+megstr );
					handleMsgData(ids,type,bundle.getString(JPushInterface.EXTRA_MESSAGE));
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}
			}
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        }
	}

	/**
	 * 根据
	 * @param ids 可接受人的ID
	 * @param type 类型
	 */
	private void handleMsgData(String ids,String type,String msgNotify){
		if (TextUtils.isEmpty(ids) || TextUtils.isEmpty(type) || TextUtils.isEmpty(msgNotify)){
			//do nothing
			return;
		}

		/**
		 * 判断开关是否开启
		 * 1工单状态
		 2工单回复
		 3工单操作
		 4站内消息
		 5系统消息
		 */

		String title = "";
		if (type.equals("1")){
			title = "工单状态改变了";
			if (!OfflineDataManager.getInstance(ctx).getOrderDynamicOn()){
				return;
			}
		}else if (type.equals("2")){
			title = "工单有新回复了";
			if (!OfflineDataManager.getInstance(ctx).getOrderReplayOn()){
				return;
			}
		}else if(type.equals("3")){
			title = "工单修改了";
			if (!OfflineDataManager.getInstance(ctx).getOrderHandleOn()){
				return;
			}
		}else if(type.equals("4")){
			title = "站内消息";
			if (!OfflineDataManager.getInstance(ctx).getInnerMsgOn()){
				return;
			}
		}else if(type.equals("5")){
			title = "系统消息";
			if (!OfflineDataManager.getInstance(ctx).getSystemMsgOn()){
				return;
			}
		}

		/**
		 * 判断是否是可接收人
		 */

		Gson gson = new Gson();

		/**
		 * 获取用户信息
		 */
		UserEntry userEntry = gson.fromJson(OfflineDataManager.getInstance(ctx).getUser(), UserEntry.class);

		boolean isReceiveAble = false;
		if (ids.contains(",") && userEntry != null){
			String []_ids = ids.split(",");
			for (String id : _ids){
				if (id.equals(userEntry.getID())){
					isReceiveAble = true;
					break;
				}
			}
		}else if (userEntry != null && ids.equals(userEntry.getID())){
			isReceiveAble = true;
		}

		if (isReceiveAble){
			if (!TextUtils.isEmpty(msg)){//判断推送来的标题是否为空
				title += ":";
				title += msg;
			}
			showNotification(title,msgNotify);
		}
	}

	/**
	 * notify
	 * @param title
	 * @param msg
     */
	private void showNotification(String title,String msg) {
			NotificationManager notificationManager = (NotificationManager) this.ctx.getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this.ctx)
							.setSmallIcon(R.drawable.logo)
							.setContentTitle(title)
							.setContentText(msg)
							.setNumber(3)
							.setDefaults(Notification.DEFAULT_SOUND)
							.setVibrate(new long[]{300, 100, 300, 100})
							.setAutoCancel(true);

			Intent resultIntent = new Intent();

			int result = -1;//判断跳转页面的类型
			try{
				result = Integer.parseInt(notify_type);
			}catch (Exception e) {
				notify_type = "";
			}

			if (TextUtils.isEmpty(notify_type) || result <= 0 || result > 5){
				resultIntent.setClass(this.ctx, HomeActivity.class);
				resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}else{
				if (result >= 1 && result <= 3){
					resultIntent.setClass(this.ctx, ActivityOrderReplayDetails.class);
					resultIntent.putExtra("order_id", sheetId);
					resultIntent.putExtra("order_title", order_title);
					resultIntent.putExtra("order_level", order_type);
					resultIntent.putExtra("order_status", order_status);
					resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				}else if(result == 4 || result == 5){
					resultIntent.setClass(this.ctx, ActivityMsgList.class);
					resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				}
			}

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.ctx);

			stackBuilder.addNextIntent(resultIntent);

			PendingIntent resultPendingIntent =
					stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT | Intent.FLAG_ACTIVITY_NEW_TASK);
			mBuilder.setContentIntent(resultPendingIntent);
			notificationManager.notify(1000,mBuilder.build());
		}
}
