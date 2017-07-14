package net.huaxi.reader.book.paging.encode;


public class UTF8Encoding extends IEncoding {
    private static final String LOG_TAG = "UTF8Encoding";

    public static final byte[] FLAG = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final int MAX_BYTE_COUNT = 6;

    private static final char REPLACEMENT_CHAR = ' ';

    // private static final char REPLACEMENT_CHAR = (char) 0xfffd;

    @Override
    public String getEncodingName() {
        return "UTF-8";
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
            byte b0 = data[idx++];
            if ((b0 & 0x80) == 0) {
                // 0xxxxxxx
                // Range: U-00000000 - U-0000007F
                int val = b0 & 0xff;
                v[s++] = (char) val;
            } else if (((b0 & 0xe0) == 0xc0) || ((b0 & 0xf0) == 0xe0) || ((b0 & 0xf8) == 0xf0)
                    || ((b0 & 0xfc) == 0xf8) || ((b0 & 0xfe) == 0xfc)) {
                int utfCount = 1;
                if ((b0 & 0xf0) == 0xe0)
                    utfCount = 2;
                else if ((b0 & 0xf8) == 0xf0)
                    utfCount = 3;
                else if ((b0 & 0xfc) == 0xf8)
                    utfCount = 4;
                else if ((b0 & 0xfe) == 0xfc)
                    utfCount = 5;

                // 110xxxxx (10xxxxxx)+
                // Range: U-00000080 - U-000007FF (count == 1)
                // Range: U-00000800 - U-0000FFFF (count == 2)
                // Range: U-00010000 - U-001FFFFF (count == 3)
                // Range: U-00200000 - U-03FFFFFF (count == 4)
                // Range: U-04000000 - U-7FFFFFFF (count == 5)

                if (idx + utfCount > end) {
                    idx--;
                    break;
                    // v[s++] = REPLACEMENT_CHAR;
                    // continue;
                }

                // Extract usable bits from b0
                int val = b0 & (0x1f >> (utfCount - 1));
                for (int i = 0; i < utfCount; ++i) {
                    byte b = data[idx++];
                    if ((b & 0xc0) != 0x80) {
                        v[s++] = REPLACEMENT_CHAR;
                        idx--; // Put the input char back
                        continue outer;
                    }
                    // Push new bits in from the right side
                    val <<= 6;
                    val |= b & 0x3f;
                }

                // Allow surrogate values (0xD800 - 0xDFFF) to
                // be specified using 3-byte UTF values only
                if ((utfCount != 2) && (val >= 0xD800) && (val <= 0xDFFF)) {
                    v[s++] = REPLACEMENT_CHAR;
                    continue;
                }

                // Reject chars greater than the Unicode maximum of U+10FFFF.
                if (val > 0x10FFFF) {
                    v[s++] = REPLACEMENT_CHAR;
                    continue;
                }

                // Encode chars from U+10000 up as surrogate pairs
                if (val < 0x10000) {
                    v[s++] = (char) val;
                } else {
                    int x = val & 0xffff;
                    int u = (val >> 16) & 0x1f;
                    int w = (u - 1) & 0xffff;
                    int hi = 0xd800 | (w << 6) | (x >> 10);
                    int lo = 0xdc00 | (x & 0x3ff);
                    v[s++] = (char) hi;
                    v[s++] = (char) lo;
                }
            } else {
                // Illegal values 0x8*, 0x9*, 0xa*, 0xb*, 0xfd-0xff
                v[s++] = REPLACEMENT_CHAR;
            }
        }

        if (s == end - offset) {
            // We guessed right, so we can use our temporary array as-is.
            // this.offset = 0;
            this.mValues = v;
        } else {
            // Our temporary array was too big, so reallocate and copy.
            // this.offset = 0;
            this.mValues = new char[s];
            System.arraycopy(v, 0, mValues, 0, s);
        }

        return idx;
    }

    /**
     * 将byte反向转成char
     */
    @Override
    public int toCharsReverse(byte[] data, int start, int end) {
        if (data == null || start >= end) {
            // Log.d(LOG_TAG, "prevChar: from buff is null");
            throw new ArrayIndexOutOfBoundsException();
        }

        int byteCount = end - start;

        char[] v = new char[end - start];
        int s = 0;

        outer:
        while (start < end) {
            byte b1 = data[end - 1];

            if ((b1 & 0x80) == 0) {
                int val = b1 & 0xff;
                v[s++] = (char) val;
                end--;
            } else {
                int rt = 0;
                int cnt = 0;
                for (; cnt < MAX_BYTE_COUNT; cnt++) {
                    if (end - start - 1 < cnt) {
                        // Log.d(LOG_TAG, "preChar: from buff is not enough");
                        break outer;
                    }

                    b1 = data[end - cnt - 1];
                    if ((b1 & 0xC0) == 0x80) {
                        rt += (b1 & 0x7f) << (6 * cnt);
                    } else {
                        if (cnt == 0) {
                            // not a utf8 char
                            v[s++] = REPLACEMENT_CHAR;
                            end--;
                            break;
                        } else if (cnt == 1) {
                            if ((b1 & 0xC0) == 0xC0) {
                                // correct char
                                rt += (b1 & 0x1F) << 6;
                                v[s++] = (char) rt;
                            } else {
                                // not a utf8 char
                                v[s++] = REPLACEMENT_CHAR;
                            }
                            end -= 2;
                            break;
                        } else if (cnt == 2) {
                            if ((b1 & 0xE0) == 0xE0) {
                                rt += (b1 & 0xF) << 12;
                                v[s++] = (char) rt;
                            } else {
                                v[s++] = REPLACEMENT_CHAR;
                            }
                            end -= 3;
                            break;
                        } else if (cnt == 3) {
                            if ((b1 & 0xF0) == 0xF0) {
                                rt += (b1 & 0x7) << 18;
                                v[s++] = (char) rt;
                            } else {
                                v[s++] = REPLACEMENT_CHAR;
                            }
                            end -= 4;
                            break;
                        } else if (cnt == 4) {
                            if ((b1 & 0xF8) == 0xF8) {
                                rt += (b1 & 0x3) << 24;
                                v[s++] = (char) rt;
                            } else {
                                v[s++] = REPLACEMENT_CHAR;
                            }
                            end -= 5;
                            break;
                        } else if (cnt == 5) {
                            if ((b1 & 0xFC) == 0xFC) {
                                rt += (b1 & 0x1) << 30;
                                v[s++] = (char) rt;
                            } else {
                                v[s++] = REPLACEMENT_CHAR;
                            }
                            end -= 6;
                            break;
                        } else {
                            // should not be here
                            v[s++] = REPLACEMENT_CHAR;
                            end -= 6;
                            break;
                        }
                    }
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
