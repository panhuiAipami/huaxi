/**
 * 
 */
package com.tools.commonlibs.tools;

import com.tools.commonlibs.common.BaseApplication;
import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.common.OperatorType;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @author li.li
 */
public class NetUtils {

	public static NetType checkNet() {
		return checkNet(CommonApp.getInstance());
	}

	/**
	 * 检测wifi是否可用
	 * @return
	 */
	public static boolean isWifiOpen() {
        boolean isWifiConnect = false;
        ConnectivityManager cm = (ConnectivityManager) BaseApplication
                .context().getSystemService(Context.CONNECTIVITY_SERVICE);
        // check the networkInfos numbers
        NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
        for (int i = 0; i < networkInfos.length; i++) {
            if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
                if (networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE) {
                    isWifiConnect = false;
                }
                if (networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI) {
                    isWifiConnect = true;
                }
            }
        }
        return isWifiConnect;
    }
	
	/**
	 * 检测网络是否可用
	 * 
	 * 同步方法，支持多线程
	 */
	private static synchronized NetType checkNet(Context context) {
		NetType netType = NetType.TYPE_NONE;
		try {
			if (context == null) {
				return NetType.TYPE_UNKNOWN;
			}
			// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();

				// 判断当前网络是否已经连接
				if (info != null && (info.isConnected() || info.isAvailable())) {

					// 判断当前的接入点
					if (ConnectivityManager.TYPE_WIFI == info.getType()) // wifi连接
						netType = NetType.TYPE_WIFI;
					else if (ConnectivityManager.TYPE_MOBILE == info.getType()) {// 手机方式连接

						/**
						* 获取网络类型
						* 
						* NETWORK_TYPE_CDMA 网络类型为CDMA
						* NETWORK_TYPE_EDGE 网络类型为EDGE
						* NETWORK_TYPE_EVDO_0 网络类型为EVDO0
						* NETWORK_TYPE_EVDO_A 网络类型为EVDOA
						* NETWORK_TYPE_GPRS 网络类型为GPRS
						* NETWORK_TYPE_HSDPA 网络类型为HSDPA
						* NETWORK_TYPE_HSPA 网络类型为HSPA
						* NETWORK_TYPE_HSUPA 网络类型为HSUPA
						* NETWORK_TYPE_UMTS 网络类型为UMTS
						* 
						* 在中国，联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO
						*/

						if (TelephonyManager.NETWORK_TYPE_GPRS == info.getSubtype() || //
								TelephonyManager.NETWORK_TYPE_EDGE == info.getSubtype() || //
								TelephonyManager.NETWORK_TYPE_CDMA == info.getSubtype()) {// 2G网络

							netType = NetType.TYPE_2G;

						}else {// 3G或其它手机网络
							netType = NetType.TYPE_3G_OR_OTHERS;
						}

					} else {//其它未知连接方式 
						netType = NetType.TYPE_UNKNOWN;
					}

					LogUtils.info("当前网络类型|" + netType.getDesc() + "|" + info.getType() + "|" + info.getSubtype());
				}
			}

		} catch (Throwable e) {
			LogUtils.error(e.getMessage(), e);
		}

		return netType;
	}
	/**
	 * 检测网络是否通畅
	 */
	public static boolean checkNetworkUnobstructed(){
		if(checkNet()==NetType.TYPE_NONE || checkNet()==NetType.TYPE_UNKNOWN){
			return false;
		}
		return true;
	}

	/**
	 * 获取手机卡类型，移动、联通、电信
	 * 
	 */
	public static OperatorType getMobileType(Context context) {
		TelephonyManager iPhoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String iNumeric = iPhoneManager.getSimOperator();
		if (iNumeric != null && iNumeric.length() > 0) {
			if (iNumeric.equals("46000") || iNumeric.equals("46002") || iNumeric.equals("46007")) // 中国移动
				return OperatorType.CMCC;
			else if (iNumeric.equals("46001")) // 中国联通
				return OperatorType.CU;
			else if (iNumeric.equals("46003")) // 中国电信
				return OperatorType.CT;
		}

		return OperatorType.OTHER;

	}
}
