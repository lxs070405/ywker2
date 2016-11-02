package com.y.w.ywker.fragment;

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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.y.w.ywker.ConstValues;
import com.y.w.ywker.R;
import com.y.w.ywker.YwkerApplication;
import com.y.w.ywker.activity.ActivityOrderReplayDetails;
import com.y.w.ywker.activity.BindDevicesActivity;
import com.y.w.ywker.adapters.AdapterReplayOrder;
import com.y.w.ywker.entry.DevicesEntry;
import com.y.w.ywker.entry.ReplayMsgEntry;
import com.y.w.ywker.entry.UserEntry;
import com.y.w.ywker.entry.YBasicNameValuePair;
import com.y.w.ywker.utils.ActivityManager;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lxs on 16/4/20.
 * 回复工单
 */
public class FragmentOrderReplay extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
//    @Bind(R.id.order_chat_order_title)
//    TextView orderChatOrderTitle;
//    @Bind(R.id.order_chat_order_status)
//    TextView orderChatOrderStatus;
//    @Bind(R.id.order_chat_order_level)
//    TextView orderChatOrderLevel;
//    @Bind(R.id.order_chat_top_view)
//    LinearLayout orderChatTopView;
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
    @Bind(R.id.order_chat_root_layout)
    RelativeLayout orderChatRootLayout;
    @Bind(R.id.mic_image)
    ImageView micImage;
    @Bind(R.id.recording_hint)
    TextView recordingHint;
    @Bind(R.id.view_voice)
    LinearLayout viewTalk;
    @Bind(R.id.order_chat_view_more_view)
    View chatViewMore;
    @Bind(R.id.order_details_replay_btn_send)
    Button orderDetailsReplayBtnSend;

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

    //定位得到的地址
    private String locationArr = "";

    //压缩后上传的文件名称
    private String nowUpLoadFileName = "";

    /**
     * 存储加载进来的数据,多次加载时按照倒叙进行插入
     */

    private List<ReplayMsgEntry> msgLists = new ArrayList<ReplayMsgEntry>();

    /**
     * 网络管理类
     */
    private YHttpManagerUtils httpManagerUtils;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private String orderid = "";

    private String orderTitle = "";
    private String orderStatus = "";
    private String orderLevel = "";
    private String userName = "";

    private String video_name = "_now_ywk_tmp" + System.currentTimeMillis() + ".mp4";
    private String pic_name = "_now_ywk_tmp" + System.currentTimeMillis() + ".jpg";
    private String voice_name = "_now_ywk_tmp" + System.currentTimeMillis() + ".mp3";

    private AdapterReplayOrder adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createCameraTempFile(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_order_replay, container, false);
        ButterKnife.bind(this, v);
        orderid = getArguments().getString("order_id");
        orderTitle = getArguments().getString("order_title");
        orderStatus = getArguments().getString("order_status");
        orderLevel = getArguments().getString("order_level");
        animationDrawable = (AnimationDrawable) micImage.getBackground();
        animationDrawable.setOneShot(false);
        String userJson = OfflineDataManager.getInstance(getContext()).getUser();
        Gson gson = new Gson();
        UserEntry userEntry = gson.fromJson(userJson, UserEntry.class);
        if (userEntry != null) {
            userName = userEntry.getUserName();
        }
//        if (orderLevel == null || orderLevel.equals("")) {
//            orderChatOrderLevel.setVisibility(View.GONE);
//        } else {
//            orderChatOrderLevel.setVisibility(View.VISIBLE);
//            orderChatOrderLevel.setText(orderLevel);
//        }
//        if (orderStatus == null || orderStatus.equals("")) {
//            orderChatOrderStatus.setVisibility(View.GONE);
//        } else {
//            orderChatOrderStatus.setVisibility(View.VISIBLE);
//            orderChatOrderStatus.setText(orderStatus);
//        }

//        orderChatOrderTitle.setText(orderTitle );
        manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        PathUtils.getInstance().initDirs(null, "ywker", getContext());
        voiceRecorder = new VoiceRecorder(mHandler);
        wakeLock = ((PowerManager) getActivity().getSystemService(Context.POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ywker");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutCommSwipeRefreshRecyclerview.setLayoutManager(layoutManager);
        layoutCommSwipeRefresh.setOnRefreshListener(this);
        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        layoutCommSwipeRefresh.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        layoutCommSwipeRefresh.setDistanceToTriggerSync(200);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        layoutCommSwipeRefresh.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小
        adapter = new AdapterReplayOrder(getContext(),"gongdan");
        layoutCommSwipeRefreshRecyclerview.setAdapter(adapter);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initReplay();
        loadData();
        setLisentner();
        return v;
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

    private void setLisentner() {
        orderChatEdt.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEND) {
                            String msg = orderChatEdt.getText().toString();
                            if (msg != null && !msg.equals("")) {
                                startReplay(REPLAY_TYPE.MSG, msg, 0);
                                return true;
                            } else {
                                Toast.makeText(getContext(), "要发送的消息不能为空", Toast.LENGTH_SHORT).show();
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
                        startReplay(REPLAY_TYPE.MSG, msg, 0);
                        return true;
                    }
                }
                return false;
            }
        });

        orderChatBtnVoice.setOnTouchListener(new PressToSpeakListen());
    }

    /**
     * 发送位置信息
     */
    public void sendADR() {
        LOG.e(getContext(), "发送位置信息");
        replaysStatus = 0;
        locationService.start();
    }

    private int minChatId = 0;

    /**
     * -1 正常状态
     * 0 正在回复状态
     */
    private int replaysStatus = -1;


    public void reloadData() {
        minChatId = 0;
        loadData();
    }

    private void loadData() {

        if (orderid == null || orderid.equals("")) {
            orderid = ((ActivityOrderReplayDetails) getActivity()).getOrderid();
        }

        if (orderid == null || orderid.equals("")) {
            Toast.makeText(getContext(), "工单id为空", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 加载数据
         */
        String url = "";
        if (msgLists != null && msgLists.isEmpty()) {//首次加载
            url = String.format(ConstValues.GET_ORDER_REPLAY_LIST, "0", orderid);
        } else {
            //查找列表中最小的
            url = String.format(ConstValues.GET_ORDER_REPLAY_LIST, minChatId + "", orderid);
        }

        httpManagerUtils = new YHttpManagerUtils(getActivity(), url, new MyHandler(this), getClass().getSimpleName());
        httpManagerUtils.startRequest();
    }


    //响应键盘内容
    public TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (charSequence.length() == 0) {
                orderDetailsReplayBtnSend.setVisibility(View.GONE);
                orderChatBtnAdd.setVisibility(View.VISIBLE);
            } else {
                orderDetailsReplayBtnSend.setVisibility(View.VISIBLE);
                orderChatBtnAdd.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    class MyHandler extends Handler {

        WeakReference<Fragment> mFragmentReference;

        public MyHandler(Fragment fragment) {
            mFragmentReference = new WeakReference<Fragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

            replaysStatus = -1;
            Fragment fragment = mFragmentReference.get();

            if (fragment != null) {

                if (layoutCommSwipeRefresh != null) {
                    layoutCommSwipeRefresh.setRefreshing(false);
                }

                switch (msg.what) {
                    case ConstValues.MSG_FAILED:
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            Toast.makeText(getContext(), "回复工单失败", Toast.LENGTH_SHORT).show();
                        } else if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.GET) {
                            Toast.makeText(getContext(), "已经全部加载完毕", Toast.LENGTH_SHORT).show();
                            layoutCommSwipeRefresh.setEnabled(false);
                        } else if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.UPLOAD) {
                            Toast.makeText(getContext(), "上传文件失败", Toast.LENGTH_SHORT).show();
                            layoutCommSwipeRefresh.setEnabled(false);
                        }
                        break;
                    case ConstValues.MSG_ERROR:
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            Toast.makeText(getContext(), "回复工单失败", Toast.LENGTH_SHORT).show();
                        } else if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.GET) {
                            Toast.makeText(getContext(), "已经全部加载完毕", Toast.LENGTH_SHORT).show();
                            layoutCommSwipeRefresh.setEnabled(false);
                        } else if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.UPLOAD) {
                            Toast.makeText(getContext(), "上传文件失败", Toast.LENGTH_SHORT).show();
                            layoutCommSwipeRefresh.setEnabled(false);
                        }

                        break;
                    case ConstValues.MSG_NET_INAVIABLE:
                        break;
                    case ConstValues.MSG_JSON_FORMAT_WRONG:
                        break;
                    case ConstValues.MSG_SUCESS:
                        if (msg.obj == null) {
                            return;
                        }
//                        LOG.e(getContext(), "返回信息 = " + (String) msg.obj);
                        if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.POST) {
                            String type = map.get("Type");
                            if (type.equals("ADR") || type.equals("ASSET") || type.equals("Character")) {
                                //直接添加信息,构造ReplayMsgEntry
                                ReplayMsgEntry entry = new ReplayMsgEntry();
                                if (type.equals("ADR") || type.equals("Character")) {
                                    entry.setSendDetail(map.get("Detail"));
                                } else if (type.equals("ASSET") && enteryDevices != null) {
                                    entry.setSendDetail(enteryDevices.getAssetName() + ","
                                            + enteryDevices.getBrandName() + ","
                                            + enteryDevices.getModelName());
                                }
                                entry.setMessageType(map.get("Type"));
                                entry.setUserName(userName);
                                entry.setUserRole("");
                                entry.setSendTime(TimeUtils.getTime(System.currentTimeMillis()));
                                adapter.addEntry(entry);
                                msgLists.add(entry);
                                layoutCommSwipeRefreshRecyclerview.scrollToPosition(msgLists.size() - 1);
                                orderChatEdt.setText("");

                            } else if (!type.equals("")) {
                                //上传文件
                                if (type.equals("VIDEO")) {
                                    nowUpLoadFileName = PathUtils.getInstance().getVideoPath() + "/" + video_name;
                                    httpManagerUtils.upLoadFile(nowUpLoadFileName, ConstValues.UP_LOAD_FILE);
                                } else if (type.equals("VOICE")) {
                                    nowUpLoadFileName = voice_name;
                                    httpManagerUtils.upLoadFile(nowUpLoadFileName, ConstValues.UP_LOAD_FILE);
                                } else if (type.equals("PIC")) {
                                    nowUpLoadFileName = pic_name;//压缩完毕后的图片
                                    Log.e("lxs", "handleMessage: nowUpLoadFileName "+ nowUpLoadFileName );
                                    httpManagerUtils.upLoadFile(nowUpLoadFileName, ConstValues.UP_LOAD_FILE);
                                }
                            }

                        } else if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.GET) {
                            handleData((String) msg.obj);
                        } else if (httpManagerUtils.getHttpMethod() == YHttpManagerUtils.UPLOAD) {
                            String result = (String) msg.obj;
                            if (result != null && result.equals("sucess")) {
                                Toast.makeText(getContext(), "文件发送成功", Toast.LENGTH_SHORT).show();
                                ReplayMsgEntry entry = new ReplayMsgEntry();
                                entry.setFileDown(nowUpLoadFileName);
                                entry.setMessageType(map.get("Type"));
                                entry.setUserName(userName);
                                entry.setUserRole("");
                                entry.setSendTime(TimeUtils.getTime(System.currentTimeMillis()));
                                adapter.addEntry(entry);
                                msgLists.add(entry);
                                layoutCommSwipeRefreshRecyclerview.scrollToPosition(msgLists.size() - 1);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void handleData(String json) {
        if (minChatId == 0) {
            msgLists.clear();
            if (adapter != null) {
                adapter.removeAll();
            }
        }
//        LOG.e(getContext(), "要处理工单回复详细的信息 = : \n" + json);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ReplayMsgEntry>>() {
        }.getType();
        List<ReplayMsgEntry> _list = gson.fromJson(json, type);
        if (_list != null && !_list.isEmpty()) {
            //查找最小的id
            Collections.sort(_list, new Comparator<ReplayMsgEntry>() {
                @Override
                public int compare(ReplayMsgEntry lhs, ReplayMsgEntry rhs) {
                    return lhs.getID() - rhs.getID();
                }
            });
            for (ReplayMsgEntry e : _list) {
                LOG.e(getContext(), e.getID() + "");
            }
            minChatId = _list.get(0).getID();
            boolean isScrollEnd = false;
            if (msgLists.isEmpty()) {
                isScrollEnd = true;
            }
            msgLists.addAll(_list);

            //更新adapter
            adapter.addAllEntry(_list);
            if (isScrollEnd) {
                layoutCommSwipeRefreshRecyclerview.scrollToPosition(msgLists.size() - 1);
            }

        }
    }

    @OnClick({R.id.order_chat_btn_add, R.id.order_chat_btn_device,
            R.id.order_chat_btn_gps, R.id.order_chat_btn_photo,
            R.id.order_chat_btn_keybord, R.id.order_chat_btn_pic,
            R.id.order_chat_btn_voice, R.id.order_chat_btn_voicebord,
            R.id.order_details_replay_btn_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_details_replay_btn_send:
                //发送内容
                String msg = orderChatEdt.getText().toString();
                if (msg != null && !msg.equals("")) {
                    startReplay(REPLAY_TYPE.MSG, msg, 0);
                } else {
                    Toast.makeText(getContext(), "要发送的消息不能为空", Toast.LENGTH_SHORT).show();
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
                LOG.e(getContext(), "发送位置信息");
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
            case R.id.order_chat_btn_photo://拍照
                Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent1.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent1, REQUEST_CAPTURE);
                break;
            case R.id.order_chat_btn_pic://拍照
                //跳转到调用系统图库
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
//                uploadHeadImage();
//                selectPicFromCamera();
                break;
            case R.id.order_chat_btn_voice://录音

                break;
            case R.id.order_chat_btn_voicebord://切换录音输入
                orderChatEdt.setVisibility(View.GONE);
                orderChatBtnVoice.setVisibility(View.VISIBLE);
                orderChatBtnVoicebord.setVisibility(View.GONE);
                orderChatBtnKeybord.setVisibility(View.VISIBLE);
                manager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((ActivityOrderReplayDetails) getActivity()).setMenuVisiable(true);
//        if (replaysStatus == -1){//正常状态
//            minChatId = 0;
//            loadData();
//        }

    }

    /**
     * 开启定位
     */
    @Override
    public void onStart() {
        super.onStart();
        locationService = ((YwkerApplication) (getActivity().getApplication())).locationService;
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
     */
    private void sendVoice(String filePath, String fileName, String length,boolean isResend) {
        LOG.e(getContext(), "发送录音文件...");
        LOG.e(getContext(), "filepath = " + filePath + ",fileName = " + fileName);
        voice_name = filePath;
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getContext(), "录音失败", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            startReplay(REPLAY_TYPE.VOICE, fileName, file.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 照相获取图片
     */
    public void selectPicFromCamera() {
        if (!Utils.isExitsSdcard()) {
            Toast.makeText(getContext(), "SD卡不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        pic_name = "_now_ywk_tmp" + System.currentTimeMillis() + ".jpg";
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
                    animationDrawable.start();
                    if (!Utils.isExitsSdcard()) {
                        String st4 = getResources().getString(
                                R.string.Send_voice_need_sdcard_support);
                        Toast.makeText(getContext(), st4, Toast.LENGTH_SHORT)
                                .show();
                        return false;
                    }
                    try {
                        v.setPressed(true);
                        wakeLock.acquire();
                        viewTalk.setVisibility(View.VISIBLE);
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        voiceRecorder.startRecording(null, voiceRecordName,
                                getContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                        v.setPressed(false);
                        if (wakeLock.isHeld())
                            wakeLock.release();
                        if (voiceRecorder != null)
                            voiceRecorder.discardRecording();
                        viewTalk.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), R.string.recoding_fail,
                                Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    return true;
                case MotionEvent.ACTION_MOVE: {
                    if (event.getY() < 0) {
                        recordingHint
                                .setText(getString(R.string.release_to_cancel));
                        recordingHint.setBackgroundResource(R.drawable.recording_hint_bg);
                    } else {
                        recordingHint
                                .setText(getString(R.string.move_up_to_cancel));
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        animationDrawable.start();
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    v.setPressed(false);
                    viewTalk.setVisibility(View.INVISIBLE);
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
                                        voiceRecorder.getVoiceFileName(voiceRecordName),
                                        Integer.toString(length), false);
                            } else if (length == -1) {
                                Toast.makeText(getContext(), st1,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), st2,
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), st3,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    return true;
                default:
                    viewTalk.setVisibility(View.INVISIBLE);
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
                LOG.e(getActivity(), locationArr);
                if (replaysStatus == 0) {
                    if (locationArr.equals("")) {
                        replaysStatus = -1;
                        Toast.makeText(getContext(), "定位失败", Toast.LENGTH_SHORT).show();
                    } else {
                        startReplay(REPLAY_TYPE.ADDR, locationArr, 0);
                    }
                }
                locationService.stop();
            }
        }
    };

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
//            case ConstValues.RESULT_FOR_PICKER_CAMERA:
//                LOG.e(getContext(), "视频录制完毕");
//                File file = new File(PathUtils.getInstance().getVideoPath(), video_name);
//                if (file.exists()) {
//                    startReplay(REPLAY_TYPE.VIDEO, video_name, file.length());
//                } else {
//                    LOG.e(getContext(), video_name + " 视频不存在");
//                }
//                break;
//            case ConstValues.RESULT_FOR_PICKER_IMG:
//                LOG.e(getContext(), "图片拍摄完毕");
//                File fileImg = new File(PathUtils.getInstance().getImagePath(), pic_name);
//                if (fileImg.exists()) {
//                    //开始压缩
//                    String commpressName = "_now_ywk_tmp" + System.currentTimeMillis() + ".jpg";
//                    String compressPath = PictureUtils.compressPic(fileImg.getPath(), commpressName);
//                    pic_name = compressPath;
//                    File compressFile = new File(pic_name);
//                    if (!compressFile.exists()) {
//                        Log.e("TAG", pic_name + "  -- 压缩文件不存在");
//                        return;
//                    }
//                    startReplay(REPLAY_TYPE.PIC, commpressName, compressFile.length());
//                } else {
//                    Log.e("TAG", "拍摄后的图片:" + pic_name + " 图片不存在");
//                }
//                break;
            case ConstValues.RESULT_FOR_DEVICES_REPLAY_ORDER:
                if (data == null) {
                    return;
                }
                String result = data.getStringExtra("devices_replay_result");
                Log.e("lxs", "onActivityResult: "+result );
                Gson gson = new Gson();
                enteryDevices = gson.fromJson(result, DevicesEntry.class);
                if (enteryDevices != null) {
                    startReplay(REPLAY_TYPE.DEVICE, enteryDevices.getID() + "", 0);
                }
                ActivityManager.getInstance().finshActivities(BindDevicesActivity.class);

                break;
            case REQUEST_CAPTURE: //调用系统相机返回
//                if (resultCode == -1) {
                    String cropImagePath = getRealFilePathFromUri(getContext(), Uri.fromFile(tempFile));
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
                        startReplay(REPLAY_TYPE.PIC, commpressName, compressFile.length());
//                        compressFile.delete();
                    } else {
                        Log.e("TAG", "拍摄后的图片:" + pic_name + " 图片不存在");
                    }
//                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
//                if (resultCode == -1) {
                String cropImagePath1  = getRealFilePathFromUri(getContext(), data.getData());
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
                    startReplay(REPLAY_TYPE.PIC, commpressName, compressFile.length());
//                        compressFile.delete();
                } else {
                    Log.e("TAG", "拍摄后的图片:" + pic_name + " 图片不存在");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private HashMap<String, String> map = new HashMap<String, String>();

    /**
     * 初始化回复的map
     */
    private void initReplay() {
        map.put("MainID", OfflineDataManager.getInstance(getActivity()).getMainID());
        map.put("SheetID", orderid);
        map.put("FileLength", "0");
        map.put("SendTime", "");
        map.put("Type", "");
        map.put("Detail", "");
        map.put("SendID", OfflineDataManager.getInstance(getActivity()).getUserID());
    }

    /**
     * 回复工单的类型
     */
    enum REPLAY_TYPE {
        PIC,//图片
        VIDEO,//视频
        VOICE,//语音
        MSG,//文本
        ADDR,//地址
        DEVICE//设备
    }

    /**
     * 开始回复工单
     * @param type
     * @param msg
     */
    private void startReplay(REPLAY_TYPE type, String msg, long size) {
        if (type == REPLAY_TYPE.MSG && (msg == null || msg.equals(""))) {
            return;
        }
        /**
         * 填充时间
         */
        map.put("SendTime", TimeUtils.getTime(System.currentTimeMillis()));
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
        httpManagerUtils.setUrl(ConstValues.REPLAY_ORDERS_URL);
        httpManagerUtils.startPostRequest(map);
    }
}
