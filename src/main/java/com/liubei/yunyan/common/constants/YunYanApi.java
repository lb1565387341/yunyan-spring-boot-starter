package com.liubei.yunyan.common.constants;

/**
 * @Author lyf15
 * @Description
 * @Date 2023/8/2917:16
 */
public class YunYanApi {
    /**
     * 获取TOKEN地址
     */
    public static final String GET_ACCESS_TOKEN_URI = "/open/oauth/getAccessToken";
    public static final String GET_REGIN_WITH_GROUP_LIST = "/open/token/device/getReginWithGroupList";
    public static final String  GET_DEVICE_LIST="/open/token/device/getDeviceList";
    public static final String  GET_DEVICE_LIST_NEW="/open/token/device/getAllDeviceListNew";
    public static final String  GET_DEVICE_BY_DEVICE_CODE="/open/token/vcpTree/getDeviceByDeviceCode";

    // ========== 云转mp4相关接口 ==========
    /**
     * 提交云转存MP4任务
     */
    public static final String CLOUD_START_TRANS_CODEC_TASK = "/open/token/vpaas/videoClip/startTransCodecTask";
    /**
     * 获取转码（MP4）后云存文件的下载地址
     */
    public static final String CLOUD_GET_MP4_DOWNLOAD_URL = "/open/token/vpaas/videoClip/getDownloadUrl";
    /**
     * 获取云存转码处理任务信息
     */
    public static final String CLOUD_GET_TASK_INFO = "/open/token/vpaas/videoClip/getTaskInfo";

    // ========== 云回看相关接口 ==========
    /**
     * 获取云回看文件列表
     */
    public static final String GET_DEVICE_FILE_LIST = "/open/token/cloud/getCloudFileList";

    /**
     * 获取云回看文件下载地址
     */
    public static final String GET_DEVICE_FILE_DOWNLOAD_URL = "/open/token/cloud/getFileUrlById";
}
