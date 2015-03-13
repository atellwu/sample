package socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketReuse {


	public static void main(String[] args) throws Exception {
		{
			Socket socket = new Socket();
			socket.setReuseAddress(true);
			SocketAddress address = new InetSocketAddress(
					6666);
			socket.bind(address);
			SocketAddress endpoint= new InetSocketAddress("172.20.0.135",
					8080);
			socket.connect(endpoint);
		}
		{
			Socket socket = new Socket();
			socket.setReuseAddress(true);
			SocketAddress address = new InetSocketAddress(
					6666);
			socket.bind(address);//四元组决定唯一性，但前提是setReuseAddress，否则bind一样的本地，会异常
			SocketAddress endpoint= new InetSocketAddress("172.20.0.135",
					80);//四元组决定唯一性，目标需要不一样
			socket.connect(endpoint);
			
			
		}
	}
}
