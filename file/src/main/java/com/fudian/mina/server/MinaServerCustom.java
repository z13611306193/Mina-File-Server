package com.fudian.mina.server;

import com.fudian.mina.common.CustomProtocolCodecFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

/**
 * @author zyg
 * Server main program
 */
public class MinaServerCustom {

    //Server Address
    private static final String MINA_IP = Init.getProperty("IP");

    // Server Port
    private static final int MINA_PORT = Integer.valueOf(Init.getProperty("PORT"));

    //Enable server listening
    public static void main(String[] args) {
        IoAcceptor acceptor;
        try {
            acceptor = new NioSocketAcceptor(Runtime
                    .getRuntime().availableProcessors() + 1);

            acceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, 60);

            acceptor.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, 45);

            acceptor.getSessionConfig().setWriteTimeout(5000);

            acceptor.getFilterChain().addLast("myCoder", new ProtocolCodecFilter(new CustomProtocolCodecFactory(Charset.forName("UTF-8"))));

            acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)));

            acceptor.getFilterChain().addLast("logger",new LoggingFilter());

            acceptor.getSessionConfig().setMinReadBufferSize(1024);

            acceptor.getSessionConfig().setMaxReadBufferSize(4096);

            acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

            acceptor.setHandler(new MyServerHandler());

            acceptor.bind(new InetSocketAddress(MINA_IP,MINA_PORT));

            System.out.println("Mina server created successfully, port:" + MINA_PORT);

        } catch (IOException e) {

            System.err.println("Error creating Mina server:" + e.getMessage());

            System.exit(-1);

        }
    }
}