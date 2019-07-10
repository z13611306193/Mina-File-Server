package com.fudian.mina.common;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @author zyg
 *         Custom decoder
 */
public class CustomProtocolDecoder extends CumulativeProtocolDecoder {

    private final Charset charset;

    public CustomProtocolDecoder() {
        this.charset = Charset.defaultCharset();
    }

    // Constructor to inject encoding format
    public CustomProtocolDecoder(Charset charset) {
        this.charset = charset;
    }

    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {

        final int PACK_HEAD_LENGTH = 4;

        if (in.remaining() < PACK_HEAD_LENGTH) {
            return false;
        }
        if (in.remaining() > 1) {

            in.mark();

            int length = in.getInt(in.position());

            if (in.remaining() < (length - PACK_HEAD_LENGTH)) {
                in.reset();
                return false;
            } else {
                in.reset();

                byte[] bytes = new byte[length];

                in.get(bytes, 0, length);

                byte[] o = new byte[length-PACK_HEAD_LENGTH];

                System.arraycopy(bytes,PACK_HEAD_LENGTH,o,0,length-PACK_HEAD_LENGTH);

                CustomPack customPack = new CustomPack(Serializer.deserialize(o));

                out.write(customPack);

                return in.remaining() > 0;

            }
        }
        return false;
    }

    private static long byteArrayToLong(byte[] b) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(b, 0, b.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    private static int byteArrayToInt(byte[] b) {
        byte[] a = new byte[4];
        int i = a.length - 1, j = b.length - 1;
        for (; i >= 0; i--, j--) {//Copy data starts at the end of b (the low value of int)
            if (j >= 0) {
                a[i] = b[j];
            } else {
                a[i] = 0;//If b.length is less than 4, then 0 is added to the high order
            }
        }
        int v0 = (a[0] & 0xff) << 24;//&0xffThe byte value is unbiased to an int to avoid Java automatic type promotion, which preserves the high symbolic bits
        int v1 = (a[1] & 0xff) << 16;
        int v2 = (a[2] & 0xff) << 8;
        int v3 = (a[3] & 0xff);
        return v0 + v1 + v2 + v3;
    }
}
