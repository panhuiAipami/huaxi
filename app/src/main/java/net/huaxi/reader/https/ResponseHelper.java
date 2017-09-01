package net.huaxi.reader.https;

import android.util.Log;

import net.huaxi.reader.common.XSErrorEnum;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ZMW on 2015/12/18.
 * 返回json解析帮助类
 */
public class ResponseHelper {
    private static final String GIAE_POINT="give_point";
    private static final String ERRORID = "errorid";
    private static final String VDATA = "vdata";
    private static final String ERRORDESC = "errordesc";
    private static final String LAST_UPDATE_TIME = "last_update_time";

    //验证请求是否成功
    public static boolean isSuccess(JSONObject resp) {

        int errorId = XSErrorEnum.JSON_NULL_POINT_EXCEPTION.getCode();
        if (resp != null) {

            errorId = resp.optInt(ERRORID, XSErrorEnum.JSON_NO_FEILD.getCode());
            Log.i("ttttt", "isSuccess: errorId========="+errorId);
        }
        if (0 == errorId) {
            return true;
        }
        return false;
    }

    //获取vdata
    public static JSONObject getVdata(JSONObject resp) {
        JSONObject jsonObject = resp.optJSONObject(VDATA);
//        LogUtils.debug("jsonobject--->"+jsonObject.toString());
        if (jsonObject == null) {
            jsonObject = new JSONObject();
        }
        return jsonObject;

    }

    public static org.json.JSONArray getVdataList(JSONObject resp) {
        JSONArray array = null;
        if (resp != null) {
            array = resp.optJSONArray(VDATA);
        }
        return array;
    }

    //得到errorid
    public static int getErrorId(JSONObject resp) {
        int errorId = XSErrorEnum.JSON_NULL_POINT_EXCEPTION.getCode();
        if (resp != null) {
            errorId = resp.optInt(ERRORID, XSErrorEnum.JSON_NO_FEILD.getCode());
        }
        return errorId;
    }
    //得到give_point
    public static int getGIVE_POINT(JSONObject resp) {
        int give_point=0;
        if (resp != null) {
            give_point = resp.optInt(GIAE_POINT, XSErrorEnum.JSON_NO_FEILD.getCode());
        }
        return give_point;
    }

    /**
     * 是否已经订阅.
     *
     * @return
     */
    public static boolean hasSubed(JSONObject resp) {
        int errorId = getErrorId(resp);
        if (errorId == XSErrorEnum.SUCCESS.getCode() || errorId == XSErrorEnum.CHAPTER_USER_HAS_SUBSCRIBE.getCode() || errorId ==
                XSErrorEnum.CHAPTER_VIP_READ.getCode() || errorId == XSErrorEnum.CHAPTER_SUBSCRIBE_SUCCESS.getCode()) {
            return true;
        }
        return false;
    }

    /**
     * 返回错误信息
     *
     * @return string
     */
    public static String getErrorDesc(JSONObject resp) {
        String result = "";
        if (resp != null) {
            result = resp.optString(ERRORDESC, "");
        }
        return result;
    }


}
