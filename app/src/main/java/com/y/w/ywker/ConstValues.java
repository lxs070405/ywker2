package com.y.w.ywker;

/**
 * Created by lxs on 16/4/13.
 *
 * 常量类
 */
public class ConstValues {
    public static boolean isStarDr;//工单状态已完成后自动定位开关
    public static boolean GuZhangisFinish;//故障类型工单已完成前的限制
    public static boolean isStartRefsh;
    public static String UserID = "";//操作人
    public static final int GONGZUOJONGJIE = 2323;
    /**
     * 展示图片(最大的个数)
     */
    public static final int MAX_IMAGE_SIZE = 3;
    public static String planId;
    /**
     * 巡检执行ID
     */
    public static String PatrolRecordID;
    //客户组
    public static final int RESULT_FOR_PICKER_CLIENT_ROOT = 1;
    //客户组联系人
    public static final int RESULT_FOR_PICKER_CLIENT_CHILDREN = 2;

    //编辑title
    public static final int RESULT_FOR_PICKER_TITLE_EDT = 3;

    public static final int RESULT_FOR_PICKER_DES_EDT = 4;
    /**
     * 联系人页面返回
     */
    public static final int RESULT_FOR_CONTACTS = 44444;
    public static final int Image_REQUEST_CODE = 8686;

    public static final int RESULT_FOR_PICKER_SERVICES_ROOT = 5;

    public static final int RESULT_FOR_PICKER_SERVICES_CHILDREN = 6;

    public static final int RESULT_FOR_PICKER_ORDER_STATUS = 7;
    public static final int RESULT_FOR_PICKER_ORDER_TYPES = 8;
    public static final int RESULT_FOR_PICKER_ORDER_LEVEL = 9;
    public static final int RESULT_FOR_PICKER_WATCH_FOR = 10;

    public static final int RESULT_FOR_PICKER_CAMERA = 11;
    public static final int RESULT_FOR_PICKER_IMG = 12;
    public static final int RESULT_FOR_DEVICES_REPLAY_ORDER = 13;
    public static final int RESULT_FOR_DEVICES_PINPAI = 14;
    public static final int RESULT_FOR_DEVICES_XINGHAO = 15;
    public static final int RESULT_FOR_DEVICES_NAME = 16;
    public static final int RESULT_FOR_DEVICES_XULIEHAO = 22;
    public static final int RESULT_FOR_MODIFY_NAME = 17;
    public static final int RESULT_FOR_MODIFY_PHONE = 18;
    /**
     * 搜索
     */
    public static final int RESULT_SEARCH_CLIENT = 19;
    public static final int RESULT_SEARCH_DEVIDES = 20;
    public static final int RESULT_SEARCH_ORDER = 21;


    public static final int QR_CODE_REQUEST = 1;
    public static final int ZONGJIE = 22222;
    public static final int ADD_SHEBEI = 33333;
    public static final int QR_CODE_REQUEST_XULIEHAO = 11111;

    /**
     * HANDLER 发送的失败的空消息
     */
    public static final int MSG_FAILED = -1;

    public static final int MSG_JSON_FORMAT_WRONG = -2;
    /**
     * Handler发送的成功消息
     */
    public static final int MSG_SUCESS = 1;

    /**
     * Handler发送的访问失败
     */
    public static final int MSG_ERROR = -3;

    /**
     * 网络不可用
     */
    public static final int MSG_NET_INAVIABLE = 0;

    /**
     * 取消按钮
     */
    public static final int DIALOG_CANCLE = 0;
    /**
     * 确定按钮
     */
    public static final int DIALOG_SURE = 1;
    /**
     * 基础URL
     */
//    public static String BASE_URL = "http://123.56.153.172:8888/";
//    public static String BASE_URL = "http://123.56.153.172:8801/";
    public static String BASE_URL = "http://192.168.1.149:8801/";
//    public static String BASE_URL = "http://192.168.1.15:8801/";
//    public static String BASE_URL = "http://192.168.1.17:8801/";



    public static String GetNewVersionForApp =  BASE_URL+"AppUpgrade/GetNewVersionForApp";
    public static String GetNewVersionPath = BASE_URL+"AppUpgrade/GetNewVersionPath";
    /**
     * 登录URL
     */
    public static String LOGIN_URL = BASE_URL + "SBUser/UserLoging?userName=%s&password=%s";
    /**
     * 故障类型工单完成后检验是否和设备绑定url
     */
    public static String ISGUZHUANGFINISHED_URL = BASE_URL + "SheetList/GetSheetAssetInfo?SheetID=%s&MainID=%s";
    /**
     * 创建工单
     */
    public static String NEW_WORK_ORDER_URL = BASE_URL + "NewSheet/NewSheet";
    public static String NEW_weixiu_ORDER_URL = BASE_URL + "Repair/AddTask";

    /**
     * 创建设备
     */
    public static String NEW_ASEET_URL = BASE_URL + "AssetBind/AssetNew";
    /**
     * 获取受理组
     */
    public static String SERVICE_GROUP_URL = BASE_URL + "TeamList/TeamList?lMainID=%s";
    /**
     * 获取受理组受理人
     */
    public static String SERVICE_CONNTECT_URL = BASE_URL + "UserTeam/UserTeam?lTeamID=%s";
    public static String SHOULIREN_URL = BASE_URL + "TeamList/GetTeamAndMember?mainId=%s";
    public static String GET_ZONGJIE_DETEI =BASE_URL + "TSheet/GetSheetSummary?sheetId=%s";
    /**
     * 客户组
     */
    public static String CLIENT_GROUP_URL = BASE_URL + "ClientList/ClientList?lMainID=%s";
    /**
     * 客户组联系人
     */
    public static String CLIENT_CONNECTION_URL = BASE_URL + "ClientContactList/ClientContactList?ClientID=%s";
    /**
     * 主页URL
     */
    public static String HOME_URL = BASE_URL + "TSheet/GetSheetListByStatus?mainId=%s&userId=%s";
    public static String WEIXIU_HOME_URL = BASE_URL + "Repair/GetTypeAndCount?mainId=%s&userId=%s";
    public static String WEIXIU_ORDERDETAIL_URL = BASE_URL +"Repair/GetTaskDetails?taskId=%s";
    public static String WEIXIU_ORDERDPIC_URL = BASE_URL +"Repair/GetPicList?sourceId=%s&mainId=%s";
    public static String WEIXIU_ORDERDLINGJIAN_URL = BASE_URL +"Repair/GetTaskAssetDetails?taskId=%s&taskAssetId=%s";
    public static String WEIXIU_ORDERDjindu_URL = BASE_URL +"Repair/GetRepairSpeedList?mainId=%s";
    public static String WEIXIU_ORDERDDeleteSheBei_URL = BASE_URL +"Repair/DeleteTaskAsset?taskAssetId=%s";
    public static String WEIXIU_ORDERDzongjie_URL = BASE_URL +"Repair/GetUsedRepairSummary?mainId=%s";
    /**
     * 首次获取动态列表
     */
    public static String DYMANIC_URL = BASE_URL + "ChatList/GetDynamicLoad?lMainID=%s&lUserID=%s&lMessageID=%s";
    public static String isRed_URL = BASE_URL + "ChatList/UpdateMessageReadStatus?msgId=%s";
    /**
     * 下拉刷新获取最新数据
     */
    public static String DYNAMIC_LOAD_NEWS_URL = BASE_URL + "ChatList/GetDynamicDown?lMainID=%s&lUserID=%s&lMessageID=%s";


    /**
     * 获取联系人列表
     */
    public static String CONNTECTINON_URL = BASE_URL + "UserList/UserList?lMainID=%s";

    public static String WORK_ORDER_LIST_URL = BASE_URL + "SheetList/SheetList?lMainID=%s&lUserID=%s&strType=%s&strQuy=%s";
    public static String WEIXIU_ORDER_LIST_URL = BASE_URL + "Repair/GetTaskList?mainId=%s&userId=%s&type=%s";//&strQuy=%s
    /**
     * 工单搜索接口
     */
    public static String WORK_ORDER_SEARCH_URL = BASE_URL + "SheetList/SheetList?lMainID=%s&lUserID=%s&strType=%s&strQuy=%s";

    /**
     * 根据工单ID获取工单
     */
    public static String GET_ORDER_BYID = BASE_URL + "TSheet/GetSheetDetail?lSheetID=%s";
    /**
     * 根据mainid和userid planType 获取巡检列表
     */
    public static String GET_XUNJIAN_LIST = BASE_URL + "InspectPlan/GetInspectPlanList?mainid=%s&userid=%s&planType=%s&maxId=%s";
    /**
     * 根据mainid和userid planType 获取巡检列表
     */
    public static String GET_XUNJIAN_LISTDetail = BASE_URL + "InspectPlan/getInspectPlanDetail?planId=%s&mainId=%s";
    public static String GET_DETAIL_URL = BASE_URL + "InspectPlan/LookInspectAsset?AssetRecordId=%s";
    public static String GET_JIHUA_URL = BASE_URL + "InspectPlan/GetNextInspectData?PlanId=%s";
    public static String POST_START_XUNJIAN = BASE_URL + "InspectPlan/StartExecutePlan";
    public static String POST_OVER_XUNJIAN = BASE_URL + "InspectPlan/CompleteInspection";
    public static String POST_XUNJIANCHAKAN = BASE_URL + "InspectPlan/LookInspectAssetSave";
    public static String POST_ADD_XUNJIAN = BASE_URL + "InspectPlan/InspectionAddAsset";
    public static String GET_XUNJIAN_PIC = BASE_URL + "InspectPlan/GetPicList?recordAssetId=%s&mainId=%s";
    public static String GET_DEVICESINFO_PIC = BASE_URL+"Asset/GetPicList?assetId=%s&mainId=%s";
    public static String GET_DEVICETYPE_URL = BASE_URL + "Asset/GetAssetType?mainId=%s";
    public static String GET_XINGHAO_URL = BASE_URL + "Asset/GetModelList?mainId=%s&typeId=%s&brandId=%s";
    public static String GET_ASEETID_URL = BASE_URL + "Asset/GetAssetIdForBindAsset?mainId=%s&typeId=%s&brandId=%s&modelId=%s&clientId=%s";
    /**
     * 获取工单类型
     */
    public static String GET_ORDER_TYPE = BASE_URL + "TSheet/GetSheetType?lMainID=%s";
    public static String GET_GONGDANPIC_URL = BASE_URL+"TSheet/GetPicList?sheetId=%s&mainId=%s";
    /**
     * 获取工单状态和优先级
     * 0001 -- 代表工单状态   0003代表工单优先级
     */
    public static String GET_ORDER_STATUS = BASE_URL + "TSheet/GetDictionarie?strDicTypeCode=%s";

    /**
     * 根据条件查询客户
     */
    public static String SEARCH_CLIENT_URL = BASE_URL + "ClientList/QuyClientList?lMainID=%s&strClientName=%s";

    /**
     * 修改工单url
     */
    public static String ORDER_MODIFY_URL = BASE_URL + "SheetModify/SheetModify";
    public static String UPDATAWEIXIUORDER_STATUE_URL = BASE_URL + "Repair/ChangeTaskStatus";
    public static String UPDATAWEIXIUORDER_shouliren_URL = BASE_URL + "Repair/ChangeTaskInfo";
    public static String WEIXIU_ADDSHEBEI_URL = BASE_URL + "Repair/AddNewRepairAsset";

    /**
     * 获取未读信息个数
     */
    public static String Get_UNREAD_MSG_NUM = BASE_URL + "SBUser/GetMessageNotRead?lUserID=%s";

    /**
     * 获取未读信息列表
     */
    public static String Get_UNREAD_MSG_LIST = BASE_URL + "SBUser/GetMessageList?lUserID=%s";


    /**
     * 获取工单的回复的列表
     */

    public static String GET_ORDER_REPLAY_LIST = BASE_URL + "ChatList/ChatList?lChatID=%s&lSheetID=%s";
    public static String GET_weixiuORDER_REPLAY_LIST = BASE_URL + "Repair/ChatList?lChatID=%s&lSheetID=%s&dataType=%s";

    /**
     * 回复工单
     */
    public static String REPLAY_ORDERS_URL = BASE_URL + "Chat/Chat";
    public static String REPLAY_weixiuORDERS_URL = BASE_URL + "Repair/Chat";
    public static String POST_ADD_XINGHAO = BASE_URL + "Asset/AddModel";
    public static String POST_ZONGJIE_FINISH_URL = BASE_URL+"TSheet/SubmitSheetSummaryInfo";
    public static String POST_weixiuZONGJIE_FINISH_URL = BASE_URL+"Repair/AddNewTaskAssetPart";
    /**
     * 是否绑定设备
     */
    public static String BIND_DEVICES_COMPARE_URL = BASE_URL + "Asset/GetAsset?strQBCode=%s&MainID=%s";
    /**
     * 模糊搜索设备码
     */
    public static String SEARCH_DEVICES_COMPARE_URL = BASE_URL + "asset/GetQRCode?strQRCode=%s&MainID=%s";

    /**
     * 绑定设备
     */
    public static String BIND_DEVICES_URL = BASE_URL + "AssetBind/AssetBind";

    /**
     * 获取设备信息
     */

    public static String GET_DEVICES_INFO_URL = BASE_URL + "Asset/GetAssetBrandModel?lMainID=%s&lClientID=%s&lContactID=%s&strType=%s";
    public static String GET_MODE_URL = BASE_URL + "Asset/GetBrandList?mainId=%s";
    /**
     * 获取设备名称
     */

    public static String GET_DEVICES_NAME_URL = BASE_URL + "Asset/GetAssetQuy?lMainID=%s&lClientID=%s&lContactID=%s&lBrandID=%s&lModelID=%s&strAssetName=%s";

    /**
     * 上传文件
     */
    public static String UP_LOAD_FILE = BASE_URL + "Chat/FileUP";
    public static String weixiuUP_LOAD_FILE = BASE_URL + "Repair/FileUP";

    /**
     * 修改姓名,电话
     */

    public static String MODIFY_NAME_PHONE_URL = BASE_URL + "SBUser/ModifyUser";


    /**
     * 意见反馈
     */

    public static String FEED_BACK_URL = BASE_URL + "SBUser/Feedback";

    /**
     * 获取人员消息列表
     */

    public static String GET_MSG_LIST_URL = BASE_URL + "SBUser/GetMessageList?lUserID=%s";

    /**
     * 消息内容列表
     */

    public static String GET_MSG_DETAILS_URL = BASE_URL + "SBUser/GetMessageRecord?lUserID=%s&lSendID=%s";

    /**
     * 回复消息
     */

    public static String REPLAY_MSG_URL = BASE_URL + "SBUser/ReplyMessage";

    /**
     * 获取某一个人员详细信息
     */

    public static String GET_CONNECT_DETAILS_URL = BASE_URL + "SBUser/GetUser?lUserID=%s";


    /**
     * 修改密码
     */
    public static String MODIFY_PWD_URL = BASE_URL + "SBUser/UpdatePWD";
    public static String Reset_PWD_URL = BASE_URL + "SBUser/ResetPassword";
    public static String GET_Reset_PWD_URL_GET = BASE_URL + "SBUser/GetValidateCode?phone=%s";

    /**
     * 客户搜索接口
     */
    public static String CLIENT_SEARCH_URL = BASE_URL + "ClientList/QuyClientList?lMainID=%s&strClientName=%s";



    public static final int RESULT_FOR_DEVICES_TYPE = 24;
}
