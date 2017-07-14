package net.huaxi.reader.book.datasource;

import net.huaxi.reader.book.FileConstant;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 文件读取类
 * Created by taoyingfeng on 2015/12/3.
 */
public class FileReaderUtil {

    public RandomAccessFile mFile;
    public long mCurrPos;

    /**
     * 打开文件
     *
     * @param path 文件路径
     * @throws FileNotFoundException
     */
    public void open(String path) throws FileNotFoundException {
        if (mFile == null) {
            mFile = new RandomAccessFile(path, "r");
        }
    }

    /**
     * 获取MappedByteBuffer
     *
     * @param position 开始位置
     * @param len      读取长度
     * @return MappedByteBuffer
     */
    public MappedByteBuffer getMappedByteBuffer(int position, int len) {
        MappedByteBuffer m_mbBuf = null;
        try {
            m_mbBuf = mFile.getChannel().map(FileChannel.MapMode.READ_ONLY, position, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return m_mbBuf;
    }

    /**
     * 关闭文件
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if (mFile == null) {
            throw new IOException();
        }
        mFile.close();
    }

    /**
     * 读取文件
     *
     * @param buf    数组
     * @param offset 开始读取位置
     * @param len    读取长度
     * @return 返回长度.
     * @throws IOException
     */
    public int read(byte[] buf, int offset, int len) throws IOException {
        if (mFile == null) {
            throw new IOException();
        }

        int rt = mFile.read(buf, offset, len);
        if (rt != -1) {
            mCurrPos += rt;
        }

        return rt;
    }

    /**
     * 设置当前seek的进度位置,并对文件进行seek
     *
     * @param pos position
     * @throws IOException
     */
    public void setPos(long pos) throws IOException {
        if (mFile == null) {
            throw new IOException();
        }
        mCurrPos = pos;
        mFile.seek(mCurrPos);
    }

    public long getPos() {
        return mCurrPos;
    }

    /**
     * 获取文件的长度
     *
     * @return
     * @throws IOException
     */
    public long getTextLength() throws IOException {
        if (mFile == null) {
            throw new IOException();
        }
        return mFile.length();
    }

    /**
     * 读取上一段落
     *
     * @param mByteBuffer  mByteBuffer
     * @param mFromPos     开始的位置
     * @param mCharsetName 编码
     * @return
     */
    public byte[] readParagraphBack(MappedByteBuffer mByteBuffer, int mFromPos, String mCharsetName) {
        int mEnd = mFromPos;
        int i;
        byte b0, b1;
        // 根据编码格式判断换行
        if (mCharsetName.equals(FileConstant.ENCODING_UTF_16LE)) {
            i = mEnd - 2;
            while (i > 0) {
                b0 = mByteBuffer.get(i);
                b1 = mByteBuffer.get(i + 1);
                if (b0 == 0x0a && b1 == 0x00 && i != mEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        } else if (mCharsetName.equals(FileConstant.ENCODING_UTF_16BE)) {
            i = mEnd - 2;
            while (i > 0) {
                b0 = mByteBuffer.get(i);
                b1 = mByteBuffer.get(i + 1);
                if (b0 == 0x00 && b1 == 0x0a && i != mEnd - 2) {
                    i += 2;
                    break;
                }
                i--;
            }
        }
        // GB2312和UTF-8一样
        else {
            i = mEnd - 1;
            while (i > 0) {
                b0 = mByteBuffer.get(i);
                if (b0 == 0x0a && i != mEnd - 1) {
                    i++;
                    break;
                }
                i--;
            }
        }

        if (i < 0) {
            i = 0;
        }
        int mParaSize = mEnd - i;
        byte[] buf = new byte[mParaSize];
        for (int j = 0; j < mParaSize; j++) {
            buf[j] = mByteBuffer.get(i + j);
        }
        return buf;
    }

    /**
     * 读取下一段落
     *
     * @param mByteBuffer
     * @param mByteBufLen  文件长度
     * @param mFromPos     位置
     * @param mCharsetName 编码
     * @return
     */
    public byte[] readParagraphForward(MappedByteBuffer mByteBuffer, long mByteBufLen,
                                       int mFromPos, String mCharsetName) {
        int mStart = mFromPos;
        int i = mStart;
        byte b0, b1;

        if (mCharsetName.equals(FileConstant.ENCODING_UTF_16LE)) {
            while (i < mByteBufLen - 1) {
                b0 = mByteBuffer.get(i++);
                b1 = mByteBuffer.get(i++);
                if (b0 == 0x0a && b1 == 0x00) {
                    break;
                }
            }
        } else if (mCharsetName.equals(FileConstant.ENCODING_UTF_16BE)) {
            while (i < mByteBufLen - 1) {
                b0 = mByteBuffer.get(i++);
                b1 = mByteBuffer.get(i++);
                if (b0 == 0x00 && b1 == 0x0a) {
                    break;
                }
            }
        }
        // GB2312和UTF-8一样
        else {
            while (i < mByteBufLen) {
                b0 = mByteBuffer.get(i++);
                if (b0 == 0x0a) {
                    break;
                }
            }
        }

        int mParaSize = i - mStart;
        byte[] buf = new byte[mParaSize];
        for (i = 0; i < mParaSize; i++) {
            buf[i] = mByteBuffer.get(mFromPos + i);
        }
        return buf;
    }

}
