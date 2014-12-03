package tedis;

import com.taobao.common.tedis.Group;
import com.taobao.common.tedis.binary.RedisCommands;
import com.taobao.common.tedis.group.TedisGroup;
import com.taobao.common.tedis.group.ZookeeperConfigManager;

public class TestGroup {

	private static String zkAddress = "172.20.0.100:2181";
	private static int zkTimeout = 500000;

	private static final String appName = "test";
	private static final String appVersion = "1.0";

	public static void main(String[] args) throws Exception {
		ZookeeperConfigManager zcm = new ZookeeperConfigManager(appName,
				appVersion);
		zcm.setZkAddress(zkAddress);
		zcm.setZkTimeout(zkTimeout);
		zcm.init();
		
		Group group = new TedisGroup(appName, appVersion);
		group.setConfigManager(zcm);
		group.init();

		RedisCommands tedis = group.getTedis();
		byte[] key = "testGet128b".getBytes();
		tedis.set(key, "test value".getBytes());// //准备或清理数据
		// tedis.del(key);// 清理数据
		
		System.out.println(tedis.get(key));
	}

}
