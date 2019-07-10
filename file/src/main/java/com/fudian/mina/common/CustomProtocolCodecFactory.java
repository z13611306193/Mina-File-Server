package com.fudian.mina.common;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

/**
 * @author zyg
 * Custom codec factory class
 */
public class CustomProtocolCodecFactory implements ProtocolCodecFactory {

    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    public CustomProtocolCodecFactory() {
        this(Charset.forName("UTF-8"));
    }

    // Construct a method-injected codec
    public CustomProtocolCodecFactory(Charset charset) {
        this.encoder = new CustomProtocolEncoder(charset);
        this.decoder = new CustomProtocolDecoder(charset);
    }

    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }

    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }
}