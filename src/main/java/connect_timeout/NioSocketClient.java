package connect_timeout;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 
 * <pre>
 * 结论：用nio非阻塞模式去connect，无法设置连接超时，最多只是select(timeout)或netty更高级的futrue封装而已，这些情况
 * 都不会影响底层connect的情况。
 * 
 * 执行 sudo tcpdump host 172.20.1.188 and 61.135.169.125，再执行代码，可以看到：
 * 
 * 13:47:23.985466 IP yeah.local.53468 > 61.135.169.125.http: Flags [S], seq 160455070, win 14600, options [mss 1460,sackOK,TS val 320414410 ecr 0,nop,wscale 7], length 0
 * 13:47:24.022321 IP 61.135.169.125.http > yeah.local.53468: Flags [S.], seq 4074231202, ack 160455071, win 14600, options [mss 1440,sackOK,nop,nop,nop,nop,nop,nop,nop,nop,nop,nop,nop,wscale 7], length 0
 * 13:47:24.022385 IP yeah.local.53468 > 61.135.169.125.http: Flags [.], ack 1, win 115, length 0
 * </pre>
 * 
 * <pre>
 * PS： 观察一下再eclipse关闭java进程，client是否会发送FIN。
 * 答案是：会
 * 
 * 连续发了很多FIN：
 * 14:20:54.185032 IP yeah.local.53670 > 61.135.169.125.http: Flags [F.], seq 1, ack 1, win 115, length 0
 * 14:22:04.480647 IP yeah.local.53677 > 61.135.169.125.http: Flags [F.], seq 1, ack 1, win 115, length 0
 * 14:22:04.701026 IP yeah.local.53677 > 61.135.169.125.http: Flags [F.], seq 1, ack 1, win 115, length 0
 * 14:22:05.149005 IP yeah.local.53677 > 61.135.169.125.http: Flags [F.], seq 1, ack 1, win 115, length 0
 * 14:22:06.045036 IP yeah.local.53677 > 61.135.169.125.http: Flags [F.], seq 1, ack 1, win 115, length 0
 * 14:22:07.841032 IP yeah.local.53677 > 61.135.169.125.http: Flags [F.], seq 1, ack 1, win 115, length 0
 * 14:22:11.433027 IP yeah.local.53677 > 61.135.169.125.http: Flags [F.], seq 1, ack 1, win 115, length 0
 * 14:22:18.617034 IP yeah.local.53677 > 61.135.169.125.http: Flags [F.], seq 1, ack 1, win 115, length 0
 * 
 * 原因是，发送FIN后进入FIN_WAIT1状态，但此时百度没有发ACK，最后等了很久，百度还是没有回ack，就自动释放掉了（没有再发任何报文）。
 * atell@yeah:~/mywork/netty[（非分支）*]$ sudo netstat -antlp | grep 61.135.169.125
 * tcp6       0      0 172.20.1.188:53677      61.135.169.125:80       ESTABLISHED 7747/java       
 * tcp6       0      1 172.20.1.188:53677      61.135.169.125:80       FIN_WAIT1   -               
 * tcp6       0      1 172.20.1.188:53677      61.135.169.125:80       FIN_WAIT1   -               
 * tcp6       0      1 172.20.1.188:53677      61.135.169.125:80       FIN_WAIT1   -               
 * tcp6       0      1 172.20.1.188:53677      61.135.169.125:80       FIN_WAIT1   -               
 * tcp6       0      1 172.20.1.188:53677      61.135.169.125:80       FIN_WAIT1   -               
 * tcp6       0      1 172.20.1.188:53677      61.135.169.125:80       FIN_WAIT1   -
 * 
 * </pre>
 * 
 * @author atell
 *
 */
public class NioSocketClient {

	static Socket socket;

	public static void main(String[] args) throws Exception {
		try {
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			SocketAddress endpoint = new InetSocketAddress("www.baidu.com", 80);

			// 在nio下也可以使用bio去connect
			// socketChannel.socket().connect(endpoint, 10);

			socketChannel.connect(endpoint);// 前面设置了非阻塞，因此这里非阻塞；前面如果configureBlocking(true)，那么这里就是阻塞的connect，但超时时间不可设置只能依赖系统默认的21秒
			Selector sel = Selector.open();
			socketChannel.register(sel, SelectionKey.OP_CONNECT);

			System.out.println(sel.select(1));// 1ms后，返回0个key，不会抛出连接超时异常

			// socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread.sleep(90000000);
	}
}
