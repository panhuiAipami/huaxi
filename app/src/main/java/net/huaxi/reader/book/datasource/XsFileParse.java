package net.huaxi.reader.book.datasource;

import android.text.TextUtils;

import com.tools.commonlibs.tools.LogUtils;
import net.huaxi.reader.util.EncodeUtils;

import net.huaxi.reader.book.datasource.model.XsFile;
import net.huaxi.reader.common.XSErrorEnum;

/**
 * 阅读文件解析
 * Created by taoyingfeng
 * 2015/12/17.
 */
public class XsFileParse {

    private static final String CHARSET_NAME = "UTF-8";
    private static final String HEADER_DEFAULT = "xs.cn";
    public static final int HEADER_LENGTH = 64; //文件头长度

    /**
     * 是否为有效的小阅文件
     *
     * @param data
     * @return
     */
    public static XsFile parseXsFile(byte[] data) {
        XsFile header = parseField(data);
        boolean verify = false;
        if (header != null && header.getState() != XSErrorEnum.CHAPTER_FILE_PARSE_FILED.getCode()) {
            if (HEADER_DEFAULT.equals(header.getHeader())) {
                if (!TextUtils.isEmpty(header.getAuthor()) && !TextUtils.isEmpty(header.getVersion())) {
                        //验证文件CRC加密.
                        int crc = EncodeUtils.getCrc(header.getData());
//                        if (header.getCrc() == crc) {
//                            verify = true;
//                        }
                            verify= true;
                }
            }
        }
        if (header == null) {
            header = new XsFile();
            header.setState(XSErrorEnum.CHAPTER_FILE_VERIGY_FILED.getCode());
        }
        if (verify) {
            header.setState(XSErrorEnum.SUCCESS.getCode());
        } else {
            header.setState(XSErrorEnum.CHAPTER_FILE_VERIGY_FILED.getCode());
        }
        return header;
    }

    /**
     * 解析文件字段
     */
    private static XsFile parseField(byte[] data) {
        XsFile xsFile = new XsFile();
        if (data == null || data.length < HEADER_LENGTH) {
            LogUtils.error("文件内容校验失败");
            xsFile.setState(XSErrorEnum.CHAPTER_FILE_VERIGY_FILED.getCode());
            return xsFile;
        }
        try {
            //crc验证 4个字节
            byte[] xsCrc = new byte[4];
            //文件长度  4个字节
            byte[] xsLength = new byte[4];
            //文件最后修改时间  4个字节
            byte[] xsLastUpdateTime = new byte[4];
            //文件偏移量     4个字节
            byte[] xsOffset = new byte[4];
            System.arraycopy(data, 40, xsCrc, 0, xsCrc.length);
            System.arraycopy(data, 44, xsLength, 0, xsLength.length);
            System.arraycopy(data, 48, xsLastUpdateTime, 0, xsLastUpdateTime.length);
            System.arraycopy(data, 52, xsOffset, 0, xsOffset.length);

            xsFile.setHeader(new String(data, 0, 16, CHARSET_NAME).trim());
            xsFile.setVersion(new String(data, 16, 8, CHARSET_NAME).trim());
            xsFile.setAuthor(new String(data, 24, 8, CHARSET_NAME).trim());
            xsFile.setEncoding(new String(data, 32, 8, CHARSET_NAME).trim());
            xsFile.setCrc(toInt(xsCrc));
            xsFile.setLength(toInt(xsLength));
            xsFile.setLastupdatetime(toInt(xsLastUpdateTime));
            xsFile.setOffset(toInt(xsOffset));
            LogUtils.debug("bookcontent:"+xsFile.getHeader());
            LogUtils.debug("bookcontent:"+xsFile.getVersion());
            LogUtils.debug("bookcontent:"+xsFile.getAuthor());
            LogUtils.debug("bookcontent:"+xsFile.getEncoding());
            LogUtils.debug("bookcontent-CRC:"+xsFile.getCrc());
            LogUtils.debug("bookcontent-Length:"+xsFile.getLength());
            LogUtils.debug("bookcontent-Time:"+xsFile.getLastupdatetime());
            LogUtils.debug("bookcontent-offset:"+xsFile.getOffset());

            xsFile.setReverse(new String(data, 56, 8, CHARSET_NAME).trim());
            if (xsFile.getLength() > data.length - HEADER_LENGTH) {       //文件长度不对称.
                xsFile.setState(XSErrorEnum.CHAPTER_FILE_VERIGY_FILED.getCode());
            } else {
                byte[] xsContent = new byte[xsFile.getLength()];
                xsFile.setData(xsContent);
                System.arraycopy(data, xsFile.getOffset(), xsContent, 0, xsContent.length);
                EncodeUtils.decodeXor(xsContent);
                LogUtils.debug("bookcontent-content:"+xsFile.getData());
                String temp = new String(xsContent,"UTF-8");
                LogUtils.debug("ookcontent-content:"+temp);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return xsFile;
    }

    // 将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
    private static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;

        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

}
