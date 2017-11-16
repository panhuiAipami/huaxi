package net.huaxi.reader.utils;


import android.util.Log;

import net.huaxi.reader.http.UrlUtils;


/**
 * @file      MLog.java
 * @brief     about Log utils
 * @author    lgs
 * @version   1.0.0.0
 * @date      2014-08-27
 */
public class MLog {
	/**Log Mode **/
	public static boolean DEBUG = true;
	/**Log priority of none*/
    public final static int PRIORITY_NONE = 0xFFFF;
	/**Log priority of verbose.*/
    public final static int PRIORITY_VERBOSE = 2;
    /**Log priority of debug.*/
    public final static int PRIORITY_DEBUG = 3;
    /**Log priority of info.*/
    public final static int PRIORITY_INFO = 4;
    /**Log priority of warning.*/
    public final static int PRIORITY_WARN = 5;
    /**Log priority of error.*/
    public final static int PRIORITY_ERROR = 6;
    /**Log priority of exception.*/
    public final static int PRIORITY_ASSERT = 7;
    
    //-------------------------------------------------------------------------
    // Private static members
    //-------------------------------------------------------------------------
    /**Log TAG 前綴*/
	private final static String TAG = "LGS: ";
	
	/**打印调试级别*/
	private static int msLogPriority = PRIORITY_VERBOSE;

    
	static {
	    resetLogPriority();
	}
	
	
	/**
	 * 判断某个级别的打印是否打开
	 * @param priority
	 * @return
	 */
	public static boolean isLogEnable(int priority) {
		return (msLogPriority <= priority);
	}
    
    
    
    /**
     * 读取打印调试级别
     * @return 当前打印调试级别
     */
	public static int getLogPriority() {
		return msLogPriority;
	}
	
	/**
	 * 重置打印调试级别
	 */
	public static void resetLogPriority(int pri) {
		msLogPriority = pri;
	}
	
	/**
	 * 重置打印调试级别
	 */
	public static void resetLogPriority() {
		if(DEBUG){
			msLogPriority = PRIORITY_VERBOSE;
		}else{
			msLogPriority = PRIORITY_NONE;
		}
	}
	
    
	//-------------------------------------------------------------------------
    /**
     * 打印DEBUG
     */
    public static void d(String tag, String message) {
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
    	if (msLogPriority <= PRIORITY_DEBUG) {
    	    if(message == null) {
    	        message = "";
    	    }
            Log.d(TAG + tag, message);
    	}
    }
    
    /**
     * 打印DEBUG
     */
    public static void d(String tag, String message, Throwable tr) {
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
    	if (msLogPriority <= PRIORITY_DEBUG) {
    	    if(message == null) {
                message = "";
            }
            Log.d(TAG + tag, message, tr);
    	}
    }

    /**
     * 打印DEBUG
     */
    public static void d(String tag, String format, Object... args){
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
    	if (msLogPriority <= PRIORITY_DEBUG) {
    		String msg = String.format(format, args);
            Log.d(TAG + tag, msg);
    	}
    }
    
    //-------------------------------------------------------------------------
    /**
     * 打印ERROR
     */
    public static void e(String tag, String message) {
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
    	if (msLogPriority <= PRIORITY_ERROR) {
    	    if(message == null) {
                message = "";
            }
            Log.e(TAG + tag, message);
    	}
    }
    
    /**
     * 打印ERROR
     */
    public static void e(String tag, String format, Object... args){
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
        if (msLogPriority <= PRIORITY_ERROR) {
            String msg = String.format(format, args);
            Log.e(TAG + tag, msg);
        }
    }
    
    /**
     * 打印ERROR
     */
    public static void e(String tag, String message, Throwable tr) {
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
    	if (msLogPriority <= PRIORITY_ERROR) {
    	    if(message == null) {
                message = "";
            }
            Log.e(TAG + tag, message, tr);
    	}
    }
    
    //-------------------------------------------------------------------------
    /**
     * 打印INFO
     */
    public static void i(String tag, String message) {
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
    	if (msLogPriority <= PRIORITY_INFO) {
    	    if(message == null) {
                message = "";
            }
            Log.i(TAG + tag, message);
    	}
    }
    
    /**
     * 打印INFO
     */
    public static void i(String tag, String format, Object... args){
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
        if (msLogPriority <= PRIORITY_INFO) {
            String msg = String.format(format, args);
            Log.i(TAG + tag, msg);
        }
    }
    
    /**
     * 打印INFO
     */
    public static void i(String tag, String message, Throwable tr){
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
        if (msLogPriority <= PRIORITY_INFO) {
            if(message == null) {
                message = "";
            }
            Log.i(TAG + tag, message, tr);
        }
    }

    //-------------------------------------------------------------------------
    /**
     * 打印WARN
     */
    public static void w(String tag, String message) {
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
    	if (msLogPriority <= PRIORITY_WARN) {
    	    if(message == null) {
                message = "";
            }
            Log.w(TAG + tag, message);
    	}
    }
    
    /**
     * 打印WARN
     */
    public static void w(String tag, String format, Object... args){
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
        if (msLogPriority <= PRIORITY_WARN) {
            String msg = String.format(format, args);
            Log.w(TAG + tag, msg);
        }
    }
    
    /**
     * 打印WARN
     */
    public static void w(String tag, String message, Throwable tr){
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
    	if (msLogPriority <= PRIORITY_WARN) {
    	    if(message == null) {
                message = "";
            }
            Log.w(TAG + tag, message, tr);
    	}
    }

    //-------------------------------------------------------------------------
    /**
     * 打印VERBOSE
     */
    public static void v(String tag, String message) {
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
    	if (msLogPriority <= PRIORITY_VERBOSE) {
    	    if(message == null) {
                message = "";
            }
            Log.v(TAG + tag, message);
    	}
    }
    
    /**
     * 打印VERBOSE
     */
    public static void v(String tag, String format, Object... args){
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
        if (msLogPriority <= PRIORITY_VERBOSE) {
            String msg = String.format(format, args);
            Log.v(TAG + tag, msg);
        }
    }
    
    /**
     * 打印VERBOSE
     */
    public static void v(String tag, String message, Throwable tr){
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
        if (msLogPriority <= PRIORITY_VERBOSE) {
            if(message == null) {
                message = "";
            }
            Log.v(TAG + tag, message, tr);
        }
    }
    //-------------------------------------------------------------------------
    /**
     * println
     */
    public static void println(int priority, String tag, String message) {
    	if(!UrlUtils.IS_DEV){
    		return;
    	}
        if(message == null) {
            message = "";
        }
        Log.println(priority, TAG + tag, message);
    }
    
    /**
     * 得到当前位置的回调栈
     * @return
     */
    public static String getCurrStackMsg() {
        return getStackMsg(new Throwable());
    }
    
    /**
     * 把回调堆栈转换成字符串
     * @param th
     * @return
     */
    public static String getStackMsg(Throwable th) {
        StringBuffer sb = new StringBuffer();  
        StackTraceElement[] stackArray = th.getStackTrace();  
        for (int i = 0; i < stackArray.length; i++) {  
            StackTraceElement element = stackArray[i];  
            sb.append("    " + element.toString() + "\n");  
        }
        return sb.toString();  
    }
}
