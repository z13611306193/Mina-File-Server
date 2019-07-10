package com.fudian.mina.common;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.Charset;

/**
 *
 * @author zyg
 * Custom encoder
 */
public class CustomProtocolEncoder implements ProtocolEncoder {

    private final Charset charset;

    public CustomProtocolEncoder() {
        this.charset = Charset.defaultCharset();
    }

    // Constructor to inject encoding format
    public CustomProtocolEncoder(Charset charset) {
        this.charset = charset;
    }

    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {

        CustomPack customPack = (CustomPack)message;

        IoBuffer buffer = IoBuffer.allocate(customPack.getLength()).setAutoExpand(true);

        buffer.putInt(customPack.getLength()); //length 1 4

        buffer.put(Serializer.serialize(customPack.getBean()));

        buffer.flip();

        out.write(buffer);

        buffer.free();

        out.flush();
    }

    public void dispose(IoSession session) throws Exception {

    }
}
