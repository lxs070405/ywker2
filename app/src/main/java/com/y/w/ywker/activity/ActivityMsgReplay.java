package com.y.w.ywker.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.service.LocationService;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.YwkerApplication;
import com.y.w.ywker.adapters.AdapterReplayMsg;
import com.y.w.ywker.entry.DevicesEntry;
import com.y.w.ywker.entry.MsgDetailsEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.LOG;
import com.y.w.ywker.utils.OfflineDataManager;
import com.y.w.ywker.utils.PathUtils;
import com.y.w.ywker.utils.PictureUtils;
import com.y.w.ywker.utils.TimeUtils;
import com.y.w.ywker.utils.Utils;
import com.y.w.ywker.utils.VoiceRecorder;
import com.y.w.ywker.utils.YHttpManagerUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/5/5.
 *
 * 消息回复页面
 */

public class ActivityMsgReplay extends SuperActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.order_chat_btn_pic)
    ImageView orderChatBtnPic;
    @Bind(R.id.order_chat_btn_photo)
    ImageView orderChatBtnPhoto;
    @Bind(R.id.order_chat_btn_gps)
    ImageView orderChatBtnGps;
    @Bind(R.id.order_chat_btn_device)
    ImageView orderChatBtnDevice;
    @Bind(R.id.order_chat_btn_keybord)
    ImageView orderChatBtnKeybord;
    @Bind(R.id.order_chat_btn_voicebord)
    ImageView orderChatBtnVoicebord;
    @Bind(R.id.order_chat_btn_voice)
    TextView orderChatBtnVoice;
    @Bind(R.id.order_chat_edt)
    EditText orderChatEdt;
    @Bind(R.id.order_chat_btn_add)
    ImageView orderChatBtnAdd;
    @Bind(R.id.order_chat_bottom_view)
    LinearLayout orderChatBottomView;
    @Bind(R.id.layout_comm_swipe_refresh_recyclerview)
    RecyclerView layoutCommSwipeRefreshRecyclerview;
    @Bind(R.id.layout_comm_swipe_refresh)
    SwipeRefreshLayout layoutCommSwipeRefresh;
    @Bind(R.id.mic_image)
    ImageView micImage;
    @Bind(R.id.recording_hint)
    TextView recordingHint;
    @Bind(R.id.view_voice)
    LinearLayout viewVoice;
    @Bind(R.id.order_chat_root_layout)
    RelativeLayout orderChatRootLayout;
    @Bind(R.id.msg_chat_view_more_view)
    View chatViewMore;
    @Bind(R.id.layout_comm_toolbar_title)
    TextView layoutCommToolbarTitle;
    @Bind(R.id.msg_replay_show_img)
    ImageView msgReplayShowImg;
    @Bind(R.id.msg_replay_show_img_layout)
    RelativeLayout msgReplayShowImgLayout;
    @Bind(R.id.msg_replay_btn_send)
    Button msgReplayBtnSend;

    private String myUserName = "";
    private String myUserId = "";

    String nowUpLoadFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createCameraTempFile(savedInstanceState);
        setContentView(R.layout.activity_msg_replay);
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        PathUtils.getInstance().initDirs(null, "ywker", this);
        voiceRecorder = new VoiceRecorder(mHandler);
        /**
         * 初始化动画
         */
        animationDrawable = (AnimationDrawable) micImage.getBackground();
        animationDrawable.setOneShot(false);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ywker");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutCommSwipeRefreshRecyclerview.setLayoutManager(layoutManager);
        layoutCommSwipeRefresh.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        layoutCommSwipeRefresh.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        layoutCommSwipeRefresh.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        layoutCommSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小

        orderChatEdt.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEND) {
                            String msg = orderChatEdt.getText().toString();
                            if (msg != null && !msg.equals("")) {
                                startReplay(REPLAY_MSG_TYPE.MSG, msg, 0);
                                return true;
                            }
                        }
                        return false;
                    }
                });

        orderChatEdt.addTextChangedListener(watcher);
        orderChatEdt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String msg = orderChatEdt.getText().toString();
                    if (msg != null && !msg.equals("")) {
                        startReplay(REPLAY_MSG_TYPE.MSG, msg, 0);
                        return true;
                    }
                }
                return false;
            }
        });
        orderChatBtnVoice.setOnTouchListener(new PressToSpeakListen());
        sendId = getIntent().getStringExtra("send_id");
        userName = getIntent().getStringExtra("send_name");
        myUserId = OfflineDataManager.getInstance(this).getUserID();
        Log.e(getClass().getSimpleName(),"userid = " + myUserId);

        if (userName != null && !userName.equals("")) {
            layoutCommToolbarTitle.setText(userName);
        }
        if (sendId != null && !sendId.equals("")) {
            initReplay();
            loadData();
            adapter = new AdapterReplayMsg(this, userName, "");
            layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    /**
     * 定位服务
     */
    private LocationService locationService;
    /**
     * 输入法管理
     */
    private InputMethodManager manager;
    /**
     * 录音动画
     */
    private AnimationDrawable animationDrawable;
    /**
     * 录音管理
     */
    private VoiceRecorder voiceRecorder;
    private PowerManager.WakeLock wakeLock;

    private String voiceRecordName = "voice_record";
    private String videoRecordName = "video_record";

    private String locationArr = "";

    private HashMap<String, String> map = new HashMap<String, String>();

    /**
     * 存储加载进来的数据,多次加载时按照倒叙进行插入
     */

    private List<MsgDetailsEntry> msgLists = new ArrayList<MsgDetailsEntry>();

    /**
     * 网络管理类
     */
    private YHttpManagerUtils httpManagerUtils;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    /**
     * 发送者ID
     */
    private String sendId = "";

    /**
     * 发送者名称
     */
    private String userName = "";

    private String video_name = "_now_ywk_tmp" + System.currentTimeMillis() + ".mp4";
    private String pic_name = "_now_ywk_tmp" + System.currentTimeMillis() + ".jpg";
    private String voice_name = "_now_ywk_tmp" + System.currentTimeMillis() + ".amr";


    private AdapterReplayMsg adapter;

    private int minChatId = 0;

    /**
     * -1 正常状态
     * 0 正在回复状态
     */
    private int replaysStatus = -1;

    private void loadData() {

        if (sendId == null || sendId.equals("")) {
            Toast.makeText(this, "发送者id为空", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 加载数据
         */
        String userId = OfflineDataManager.getInstance(this).getUserID();
        String url = String.format(ConstValues.GET_MSG_DETAILS_URL, userId, sendId);

        httpManagerUtils = new YHttpManagerUtils(this, url, new MyHandler(this), getClass().getSimpleName());
        httpManagerUtils.startRequest();
    }

    //响应键盘内容
    public TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (charSequence != null) {
                if (charSequence.length() == 0){
                    orderChatBtnAdd.setVisibility(View.VISIBLE);
                    msgReplayBtnSend.setVisibility(View.GONE);
                }else{
                    orderChatBtnAdd.setVisibility(View.GONE);
                    msgReplayBtnSend.setVisibility(View.VISIBLE);
                }
            } else {
                orderChatBtnAdd.setVisibility(View.VISIBLE);
                msgReplayBtnSend.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };


    class MyHandler extends Handler {

        WeakReference<AppCompatActivity> mFragmentReference;

        public MyHandler(AppCompatActivity fragment) {
            mFragmentReference = new WeakReference<AppCompatActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            replaysStatus = -1;
            AppCompatActivity fragment = mFragmentReference.get();

            if (fragment != null) {

                if (layoutCommSwipeRefresh != null) {
                    layoutCommSwipeRefresh.setRefreshing(false);
                }

                switch (msg.what) {
                    case ConstValues.MSG_FAILED:

                        break;
                    case ConstValues.MSG_ERROR:

                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            Toast.makeText(ActivityMsgReplay.this, "回复消息失败", Toast.LENGTH_SHORT).show();
                        } else if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.GET) {
                            Toast.makeText(ActivityMsgReplay.this, "已经全部加载完毕", Toast.LENGTH_SHORT).show();
                            layoutCommSwipeRefresh.setEnabled(false);
                        }

                        break;
                    case ConstValues.MSG_NET_INAVIABLE:

                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:

                        break;
                    case ConstValues.MSG_SUCESS:

                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            String type = map.get("Type");
                            if (type.equals("ADR") || type.equals("Asset") || type.equals("Character")) {
                                MsgDetailsEntry entry = new MsgDetailsEntry();
                                entry.setMessageType(map.get("Type"));
                                entry.setReceiveID(sendId);
                                entry.setSendID(Integer.parseInt(myUserId));
                                entry.setSendDetail(map.get("Detail"));
                                entry.setSendTime(TimeUtils.getTime(System.currentTimeMillis()));
                                adapter.addEntry(entry);
                                msgLists.add(entry);
                                layoutCommSwipeRefreshRecyclerview.scrollToPosition(msgLists.size() - 1);
                                orderChatEdt.setText("");
                            } else if (!type.equals("")) {
                                //上传文件
                                if (type.equals("VIDEO")) {
                                    LOG.e(ActivityMsgReplay.this, "up load video");
                                    nowUpLoadFileName = PathUtils.getInstance().getVideoPath() + "/" + video_name;
                                    httpManagerUtils.setUrl(ConstValues.UP_LOAD_FILE);
                                    httpManagerUtils.upLoadFile(nowUpLoadFileName,ConstValues.UP_LOAD_FILE);
                                } else if (type.equals("VOICE")) {
                                    nowUpLoadFileName = voice_name;
                                    httpManagerUtils.setUrl(ConstValues.UP_LOAD_FILE);
                                    LOG.e(ActivityMsgReplay.this, "up load voice");
                                    httpManagerUtils.upLoadFile(voice_name,ConstValues.UP_LOAD_FILE);
                                } else if (type.equals("PIC")) {
                                    LOG.e(ActivityMsgReplay.this, "up load pic");
                                    nowUpLoadFileName = pic_name;
                                    httpManagerUtils.setUrl(ConstValues.UP_LOAD_FILE);
                                    httpManagerUtils.upLoadFile(nowUpLoadFileName,ConstValues.UP_LOAD_FILE);
                                }
                            }

                        } else if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.GET) {
                            handleData((String) msg.obj);
                        } else if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.UPLOAD) {
                            LOG.e(ActivityMsgReplay.this, "返回信息 = " + (String) msg.obj);
                            String result = (String) msg.obj;
                            if (result != null && result.equals("sucess")) {
                                Toast.makeText(ActivityMsgReplay.this, "文件发送成功", Toast.LENGTH_SHORT).show();
                                MsgDetailsEntry entry = new MsgDetailsEntry();
                                entry.setFileDown(nowUpLoadFileName);
                                entry.setMessageType(map.get("Type"));
                                entry.setReceiveID(sendId);
                                entry.setSendID(Integer.parseInt(myUserId));
                                entry.setSendTime(TimeUtils.getTime(System.currentTimeMillis()));
                                adapter.addEntry(entry);
                                msgLists.add(entry);
                                layoutCommSwipeRefreshRecyclerview.scrollToPosition(msgLists.size() - 1);
                            }
                        }
                        break;
                }
            }
        }
    }

    private void handleData(String json) {
        LOG.e(this, json);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<MsgDetailsEntry>>() {
        }.getType();
        List<MsgDetailsEntry> _list = gson.fromJson(json, type);
        msgLists.clear();
        msgLists.addAll(_list);
        //更新adapter
        adapter.removeAll();
        adapter.addAllEntry(_list);
        layoutCommSwipeRefreshRecyclerview.scrollToPosition(msgLists.size() - 1);
    }

    @OnClick({R.id.order_chat_btn_add, R.id.order_chat_btn_device, R.id.order_chat_btn_gps,
            R.id.order_chat_btn_keybord, R.id.order_chat_btn_photo, R.id.order_chat_btn_pic,
            R.id.order_chat_btn_voice, R.id.order_chat_btn_voicebord, R.id.msg_replay_show_img,
            R.id.msg_replay_btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.msg_replay_btn_send:
                String msg = orderChatEdt.getText().toString();
                if (msg != null && !msg.equals("")) {
                    startReplay(REPLAY_MSG_TYPE.MSG, msg, 0);
                }else{
                    Toast.makeText(this,"发送信息为空",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.order_chat_btn_add:
                if (chatViewMore.getVisibility() == View.VISIBLE) {
                    chatViewMore.setVisibility(View.GONE);
                } else if (chatViewMore.getVisibility() == View.GONE) {
                    chatViewMore.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.order_chat_btn_device://设备信息
                Utils.start_ActivityResult(this, BindDevicesActivity.class, ConstValues.RESULT_FOR_DEVICES_REPLAY_ORDER, new YBasicNameValuePair[]{
                        new YBasicNameValuePair("fromSource", "2")
                });
                break;
            case R.id.order_chat_btn_gps://位置信息
                LOG.e(this, "发送位置信息");
                replaysStatus = 0;
                locationService.start();
                break;
            case R.id.order_chat_btn_keybord://切换文字输入

                orderChatEdt.setVisibility(View.VISIBLE);
                orderChatBtnVoice.setVisibility(View.GONE);

                orderChatBtnVoicebord.setVisibility(View.VISIBLE);
                orderChatBtnKeybord.setVisibility(View.GONE);
                orderChatEdt.requestFocus();
                manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                break;
            case R.id.order_chat_btn_photo://视频
//                selectedVideoFromCamera();
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent1, REQUEST_CAPTURE);
                break;
            case R.id.order_chat_btn_pic://相册选择图片发送
//                selectPicFromCamera();
                //跳转到调用系统图库
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
                break;
            case R.id.order_chat_btn_voice://录音

                break;
            case R.id.order_chat_btn_voicebord://切换录音输入
                orderChatEdt.setVisibility(View.GONE);
                orderChatBtnVoice.setVisibility(View.VISIBLE);
                orderChatBtnVoicebord.setVisibility(View.GONE);
                orderChatBtnKeybord.setVisibility(View.VISIBLE);
                manager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                break;
            case R.id.msg_replay_back:
                finish();
            case R.id.msg_replay_show_img:
                msgReplayShowImgLayout.setVisibility(View.GONE);
                break;
        }
    }


    public void showImg(String url) {
        if (msgReplayShowImgLayout.getVisibility() == View.GONE) {
            msgReplayShowImgLayout.setVisibility(View.VISIBLE);
        }
        Glide.with(this).load(url).placeholder(R.drawable.photo_default).error(R.drawable.photo_default).into(msgReplayShowImg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        locationService = ((YwkerApplication) (this.getApplication())).locationService;
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    /**
     * 发送语音
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(String filePath, String fileName, String length,boolean isResend) {
        LOG.e(this, "发送录音文件...");
        LOG.e(this, "filepath = " + filePath + ",fileName = " + fileName);
        voice_name = filePath;
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, "录音失败", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            startReplay(REPLAY_MSG_TYPE.VOICE, fileName, file.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if (!Utils.isExitsSdcard()) {
            Toast.makeText(this, "SD卡不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        File cameraFile = new File(PathUtils.getInstance().getImagePath(), pic_name);
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                ConstValues.RESULT_FOR_PICKER_IMG);
    }

    /**
     * 调用视频
     */
    public void selectedVideoFromCamera() {

        Intent intent = new Intent();
        intent.setAction("android.media.action.VIDEO_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        File file = new File(PathUtils.getInstance().getVideoPath(), video_name);
        if (file.exists()) {
            file.delete();
        }
        Uri uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, ConstValues.RESULT_FOR_PICKER_CAMERA);
    }

    /**
     * 信息回复
     */
    public void sendMsg() {

    }

    /**
     * 按住说话listener
     */
    class PressToSpeakListen implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    LOG.e(ActivityMsgReplay.this, "DOWN...开始动画");
                    animationDrawable.start();
                    if (!Utils.isExitsSdcard()) {
                        String st4 = getResources().getString(
                                R.string.Send_voice_need_sdcard_support);
                        Toast.makeText(ActivityMsgReplay.this, st4, Toast.LENGTH_SHORT)
                                .show();
                        return false;
                    }

                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        viewVoice.setVisibility(View.VISIBLE);
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(null, voiceRecordName,
                                ActivityMsgReplay.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        viewVoice.setVisibility(View.INVISIBLE);
                        Toast.makeText(ActivityMsgReplay.this, R.string.recoding_fail,
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    LOG.e(ActivityMsgReplay.this, "MOVE...");
                    if (event.getY() < 0) {
                        recordingHint
                                .setText(getString(R.string.release_to_cancel));
                        recordingHint
                                .setBackgroundResource(R.drawable.recording_hint_bg);
                    } else {
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        animationDrawable.start();
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    LOG.e(ActivityMsgReplay.this, "UP...");
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    v.setPressed(false);
                    viewVoice.setVisibility(View.INVISIBLE);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (event.getY() < 0) {
                        voiceRecorder.discardRecording();
                    } else {

                        String st1 = getResources().getString(
                                R.string.Recording_without_permission);
                        String st2 = getResources().getString(
                                R.string.The_recording_time_is_too_short);
                        String st3 = getResources().getString(
                                R.string.send_failure_please);
                        try {
                            int length = voiceRecorder.stopRecoding();
                            if (length > 0) {
                                sendVoice(voiceRecorder.getVoiceFilePath(),
                                        voiceRecorder
                                                .getVoiceFileName(voiceRecordName),
                                        Integer.toString(length), false);
                            } else if (length == -1) {
                                Toast.makeText(ActivityMsgReplay.this, st1,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivityMsgReplay.this, st2,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ActivityMsgReplay.this, st3,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                default:
                    viewVoice.setVisibility(View.INVISIBLE);
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    return false;
            }
        }
    }

    @Override
    public void onPause() {
        if (wakeLock.isHeld())
            wakeLock.release();
        super.onPause();
    }

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    locationArr = location.getAddrStr();
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    locationArr = location.getAddrStr();
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    locationArr = location.getAddrStr();
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    locationArr = "";
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    locationArr = "";
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    locationArr = "";
                }
                LOG.e(ActivityMsgReplay.this, locationArr);
                if (replaysStatus == 0) {
                    if (locationArr.equals("")) {
                        replaysStatus = -1;
                        Toast.makeText(ActivityMsgReplay.this, "定位失败", Toast.LENGTH_SHORT).show();
                    } else {
                        startReplay(REPLAY_MSG_TYPE.ADDR, locationArr, 0);
                    }
                }
                locationService.stop();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
//            case ConstValues.RESULT_FOR_PICKER_CAMERA:
//                LOG.e(this, "视频录制完毕");
//                File file = new File(PathUtils.getInstance().getVideoPath(), video_name);
//                if (file.exists()) {
//                    startReplay(REPLAY_MSG_TYPE.VIDEO, video_name, file.length());
//                } else {
//                    LOG.e(this, video_name + " 视频不存在");
//                }
//                break;
//            case ConstValues.RESULT_FOR_PICKER_IMG:
//                LOG.e(this, "图片拍摄完毕");
//                File fileImg = new File(PathUtils.getInstance().getImagePath(), pic_name);
//                if (fileImg.exists()) {
//                    //开始压缩
//                    String commpressName = "_now_ywk_tmp" + System.currentTimeMillis() + ".jpg";
//                    String compressPath = PictureUtils.compressPic(fileImg.getPath(), commpressName);
//                    pic_name = compressPath;
//                    File compressFile = new File(pic_name);
//                    if (!compressFile.exists()){
//                        Log.e("TAG",pic_name + "  -- 压缩文件不存在");
//                        return;
//                    }
//                    startReplay(REPLAY_MSG_TYPE.PIC, commpressName, compressFile.length());
//                } else {
//                    LOG.e(this, pic_name + " 图片不存在");
//                }
//                break;
            case ConstValues.RESULT_FOR_DEVICES_REPLAY_ORDER:
                if (data == null) {
                    return;
                }
                String result = data.getStringExtra("devices_replay_result");
                startReplay(REPLAY_MSG_TYPE.DEVICE, result, 0);

                break;
            case REQUEST_CAPTURE: //调用系统相机返回
                String cropImagePath = getRealFilePathFromUri(ActivityMsgReplay.this, Uri.fromFile(tempFile));
                Log.e("lxs", "onActivityResult:图片 路径" + cropImagePath);

                File fileImg1 = new File(cropImagePath);
                if (fileImg1.exists()) {
                    //开始压缩
                    String commpressName = "_now_ywk_tmp" + System.currentTimeMillis() + ".jpg";
                    String compressPath = PictureUtils.compressPic(fileImg1.getPath(), commpressName);
                    pic_name = compressPath;
                    File compressFile = new File(pic_name);
                    if (!compressFile.exists()) {
                        Log.e("TAG", pic_name + "  -- 压缩文件不存在");
                        return;
                    }
                    startReplay(REPLAY_MSG_TYPE.PIC, commpressName, compressFile.length());
                } else {
                    Log.e("TAG", "拍摄后的图片:" + pic_name + " 图片不存在");
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                String cropImagePath1  = getRealFilePathFromUri(ActivityMsgReplay.this, data.getData());
                File fileImg2 = new File(cropImagePath1);
                if (fileImg2.exists()) {
                    //开始压缩
                    String commpressName = "_now_ywk_tmp" + System.currentTimeMillis() + ".jpg";
                    String compressPath = PictureUtils.compressPic(fileImg2.getPath(), commpressName);
                    pic_name = compressPath;
                    File compressFile = new File(pic_name);
                    if (!compressFile.exists()) {
                        Log.e("TAG", pic_name + "  -- 压缩文件不存在");
                        return;
                    }
                    startReplay(REPLAY_MSG_TYPE.PIC, commpressName, compressFile.length());
                } else {
                    Log.e("TAG", "拍摄后的图片:" + pic_name + " 图片不存在");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 创建调用系统照相机待存储的临时文件
     */
    private void createCameraTempFile(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("tempFile")) {
            tempFile = (File) savedInstanceState.getSerializable("tempFile");
        } else {
            tempFile = new File(checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"),
                    System.currentTimeMillis() + ".jpg");

        }
    }

    /**
     * 检查文件是否存在
     */
    private static String checkDirPath(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return "";
        }
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dirPath;
    }

    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //调用照相机返回图片临时文件
    private File tempFile;
    /**
     * 定义设备实体(设备回复时使用到)
     */
    private DevicesEntry enteryDevices;

    public static String getRealFilePathFromUri(final Context context, final Uri uri) {

        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    /**
     * 初始化回复的map
     */
    private void initReplay() {

        //测试userID
        String userId = OfflineDataManager.getInstance(this).getUserID();
//        userId = "2";
        map.put("MainID", OfflineDataManager.getInstance(this).getMainID());
        map.put("UserID", userId);
        map.put("ReceiveID", sendId);
        map.put("FileLength", "");
        map.put("Type", "");
        map.put("Detail", "");
    }

    /**
     * 回复消息的类型
     */

    enum REPLAY_MSG_TYPE {
        PIC,//图片
        VIDEO,//视频
        VOICE,//语音
        MSG,//文本
        ADDR,//地址
        DEVICE//设备
    }

    /**
     * 开始回复工单
     *
     * @param type
     * @param msg
     */
    private void startReplay(REPLAY_MSG_TYPE type, String msg, long size) {

        if (type == REPLAY_MSG_TYPE.MSG && (msg == null || msg.equals(""))) {
            return;
        }
        /**
         * 填充类型
         */
        switch (type) {
            case VIDEO:
                map.put("Type", "VIDEO");
                map.put("FileLength", size + "");
                break;
            case VOICE:
                map.put("Type", "VOICE");
                map.put("FileLength", size + "");
                break;
            case PIC:
                map.put("Type", "PIC");
                map.put("FileLength", size + "");
                break;
            case ADDR:
                map.put("Type", "ADR");
                break;
            case DEVICE:
                map.put("Type", "ASSET");
                break;
            case MSG:
                map.put("Type", "Character");
                break;
        }
        /**
         * 填充内容
         */
        map.put("Detail", msg);
        httpManagerUtils.setUrl(ConstValues.REPLAY_MSG_URL);
        httpManagerUtils.startPostRequest(map);
    }

    @Override
    public void onBackPressed() {
        if (msgReplayShowImgLayout.getVisibility() == View.VISIBLE){
            msgReplayShowImgLayout.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.msg_replay_back)
    public void onBackFinish(){
        finish();
    }
}
