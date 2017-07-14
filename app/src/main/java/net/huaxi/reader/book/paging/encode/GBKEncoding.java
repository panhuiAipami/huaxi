package net.huaxi.reader.book.paging.encode;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

public class GBKEncoding extends IEncoding {
    private static final String LOG_TAG = "GBKEncoding";

    private static final int MAX_BYTE_COUNT = 2;

    private static int MAX_ASII = 0x7F;

    private CharsetDecoder mDecoder;
    private CoderResult mResult;
    private CharBuffer mCharBuf;

    public GBKEncoding() {
        Charset charset = Charset.forName(ENCODING_GBK);
        mDecoder = charset.newDecoder();

        mCharBuf = CharBuffer.allocate(1);
    }

    @Override
    public String getEncodingName() {
        return ENCODING_GBK;
    }

    @Override
    public int maxBytesCount() {
        return MAX_BYTE_COUNT;
    }

    @Override
    public int toChars(byte[] data, int offset, int end) {
        if ((offset | (end - offset)) < 0 || end - offset > data.length - offset) {
            throw new ArrayIndexOutOfBoundsException();
        }

        char[] v = new char[end - offset];

        int idx = offset;
        int s = 0;

        outer:
        while (idx < end) {
            int firstByte = (char) (data[idx] & 0xFF);
            if (firstByte <= MAX_ASII) {
                v[s++] = (char) firstByte;
                idx++;
            } else {
                if (idx + MAX_BYTE_COUNT <= end) {
                    mResult =
                            mDecoder.decode(ByteBuffer.wrap(data, idx, MAX_BYTE_COUNT), mCharBuf,
                                    true);

                    if (mResult.isError()) {
                        idx++;
                    } else if (mResult.isOverflow()) {
                        idx++;
                    } else {
                        mCharBuf.flip();
                        v[s++] = mCharBuf.charAt(0);
                        idx += MAX_BYTE_COUNT;
                    }
                } else {
                    break outer;
                }
            }
        }

        if (s == end - offset) {
            this.mValues = v;
        } else {
            this.mValues = new char[s];
            System.arraycopy(v, 0, mValues, 0, s);
        }

        return idx;
    }

    @Override
    public int toCharsReverse(byte[] data, int start, int end) {
        if (data == null || start >= end) {
            throw new ArrayIndexOutOfBoundsException();
        }

        int byteCount = end - start;
        char[] v = new char[end - start];
        int s = 0;
        outer:
        while (start < end) {
            int firstByte = (char) (data[end - 1] & 0xFF);
            if (firstByte <= MAX_ASII) {
                v[s++] = (char) firstByte;
                end--;
            } else {
                if (start + MAX_BYTE_COUNT <= end) {
                    mResult =
                            mDecoder.decode(
                                    ByteBuffer.wrap(data, end - MAX_BYTE_COUNT, MAX_BYTE_COUNT),
                                    mCharBuf, true);

                    if (mResult.isError()) {
                        end--;
                    } else if (mResult.isOverflow()) {
                        // v[s++] = (char) firstByte;
                        end--;
                    } else {
                        mCharBuf.flip();
                        v[s++] = mCharBuf.charAt(0);
                        end -= MAX_BYTE_COUNT;
                    }
                } else {
                    break outer;
                }
            }
        }

        if (s == byteCount) {
            // We guessed right, so we can use our temporary array as-is.
            this.mValues = v;
        } else {
            // Our temporary array was too big, so reallocate and copy.
            this.mValues = new char[s];
            System.arraycopy(v, 0, mValues, 0, s);
        }
        mValues = reverse(mValues, 0, s);
        return end;
    }
}
