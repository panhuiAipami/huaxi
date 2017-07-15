package net.huaxi.reader.bean;

import com.tools.commonlibs.tools.LogUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by ZMW on 2015/12/23.
 */
public class Image implements Serializable{


    private static final long serialVersionUID = 4434874740863005636L;

    private byte[] data; // 上传文件的数据

    private String fileName;// 文件名称

    private String formName;// type="file"表单字段所对应的name属性值

    private String contentType = "image/jpeg"; // 内容类型。不同的图片类型对应不同的值，具体请参考Multimedia MIME Reference - http://www.w3schools.com/media/media_mimeref.asp

    public Image(String fileName, String formName) {
        this(fileName, formName, null);
    }

    public Image(String filePath, String formName, String contentType) {
        int beginIndex = filePath.lastIndexOf(System.getProperty("file.separator"));// The value of file.separator is '\\'
        if (beginIndex < 0) {
            beginIndex = filePath.lastIndexOf("/");
        }

        this.fileName = filePath.substring(beginIndex + 1, filePath.length());
        this.formName = formName;
        LogUtils.debug("Image_fileName"+this.fileName);
        LogUtils.debug("Image_formName!"+this.formName);
        if (contentType != null)
            this.contentType = contentType;
        if (data == null) {
            try {
                data = Image.read(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件内容
     * @param filepath 文件路径
     * @return
     * @throws IOException
     */
    public static byte[] read(String filepath) throws IOException {
        DataInputStream input = new DataInputStream(new FileInputStream(filepath));
        int len = input.available();
        byte[] content = new byte[len];
        int r = input.read(content, 0, content.length);
        if (r != len) {
            content = null;
            throw new IOException("读取文件失败！");
        }
        input.close();

        return content;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
