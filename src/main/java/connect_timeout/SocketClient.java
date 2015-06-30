package connect_timeout;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * 
 * <pre>
 * 结论：socket.connect(endpoint, 10);//连接超时异常时，client底层会自动发送reset报文
 * 
 * 执行 sudo tcpdump host 172.20.1.188 and 61.135.169.125，再执行代码，可以看到：
 * 
 * 13:33:54.017774 IP yeah.local.53449 > 61.135.169.125.http: Flags [S], seq 233489149, win 14600, options [mss 1460,sackOK,TS val 320211918 ecr 0,nop,wscale 7], length 0
 * 13:33:54.042431 IP 61.135.169.125.http > yeah.local.53449: Flags [S.], seq 841411306, ack 233489150, win 14600, options [mss 1440,sackOK,nop,nop,nop,nop,nop,nop,nop,nop,nop,nop,nop,wscale 7], length 0
 * 13:33:54.042453 IP yeah.local.53449 > 61.135.169.125.http: Flags [R], seq 233489150, win 0, length 0
 * </pre>
 * 
 * @author atell
 *
 */
public class SocketClient {

	static Socket socket;

	public static void main(String[] args) throws Exception {
		try {
			socket = new Socket();
			SocketAddress endpoint = new InetSocketAddress("www.baidu.com", 80);
			socket.connect(endpoint, 10);// 连接超时异常时，client底层会自动发送reset报文

			// socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Thread.sleep(90000000);
	}
}
