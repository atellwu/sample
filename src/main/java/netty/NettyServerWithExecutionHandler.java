/*
 * Copyright 1999-2011 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.FixedLengthFrameDecoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.MemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

import com.alibaba.dubbo.common.utils.NamedThreadFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * <pre>
 * 对于一个连接，再SimpleChannelUpstreamHandler.messageReceived这块是串行的
 * 比如，发了2次消息(8字节，被分成2个event)，只有第一次messageReceived执行完，才会再触发messageReceived
 * 
 * 所以ExecutionHandler才显得很有用。
 * 使用MemoryAwareThreadPoolExecutor，保证同个channel的handler是有序和串行的(upstream和downstream都是，包括了读和写)
 * 使用OrderedMemoryAwareThreadPoolExecutor，同个channel的handler都可能是并行的(upstream和downstream都是，包括了读和写)
 * </pre>
 * 
 * @author qian.lei
 * @author chao.liuc
 */
public class NettyServerWithExecutionHandler {

	private static ServerBootstrap bootstrap;

	public static void main(String[] args) throws InterruptedException {
		ExecutorService boss = Executors
				.newCachedThreadPool(new NamedThreadFactory("NettyServerBoss",
						true));
		ExecutorService worker = Executors
				.newCachedThreadPool(new NamedThreadFactory(
						"NettyServerWorker", true));
		ChannelFactory channelFactory = new NioServerSocketChannelFactory(boss,
				worker, 5);
		bootstrap = new ServerBootstrap(channelFactory);

		ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder();
		threadFactoryBuilder.setNameFormat("executionHandler-%d");

		Executor executor1 = new OrderedMemoryAwareThreadPoolExecutor(5,
				8033322, 8333034, 500, TimeUnit.SECONDS,
				threadFactoryBuilder.build());
		Executor executor2 = new MemoryAwareThreadPoolExecutor(5,
				8033322, 8333034, 500, TimeUnit.SECONDS,
				threadFactoryBuilder.build());
		final ExecutionHandler executionHandler = new ExecutionHandler(executor2);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("frameDecoder", new FixedLengthFrameDecoder(4));
				pipeline.addLast("executor", executionHandler);
				pipeline.addLast("handler", new SimpleChannelUpstreamHandler() {

					@Override
					public void messageReceived(ChannelHandlerContext ctx,
							MessageEvent e) throws Exception {
						String msg = new String(
								((ChannelBuffer) e.getMessage()).array());
						System.out.println(Thread.currentThread().getName()
								+ ": " + e.getRemoteAddress() + ":" + msg);
						Thread.sleep(9000);
						System.out.println("wake up");
					}

				});
				return pipeline;
			}
		});
		// bind
		org.jboss.netty.channel.Channel channel = bootstrap
				.bind(new InetSocketAddress(8080));
		Thread.sleep(Integer.MAX_VALUE);
	}

}