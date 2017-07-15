package net.huaxi.reader.bean;

import java.io.Serializable;

/**
 * Created by ZMW on 2015/12/21.
 */
public class AlipayResult implements Serializable {

    private static final long serialVersionUID = 8889273224063768452L;

    String resultStatus;

    public String getResult() {
        return result;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public String getMemo() {
        return memo;
    }

    String result;
    String memo;

    public AlipayResult(String rawResult) {
        try {
            String[] resultParams = rawResult.split(";");
            for (String resultParam : resultParams) {
                if (resultParam.startsWith("resultStatus")) {
                    resultStatus = gatValue(resultParam, "resultStatus");
                }
                if (resultParam.startsWith("result")) {
                    result = gatValue(resultParam, "result");
                }

                if (resultParam.startsWith("memo")) {
                    memo = gatValue(resultParam, "memo");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "resultStatus : " + resultStatus + ", result = " + result + ", memo = " + memo;
    }

    public String gatValue(String content, String key) {
        String prefix = key + "={";
        return content.substring(content.indexOf(prefix) + prefix.length(), content.indexOf("}"));
    }

}
