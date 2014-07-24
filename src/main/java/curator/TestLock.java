package curator;

import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper.States;

import com.netflix.curator.RetryPolicy;
import com.netflix.curator.RetrySleeper;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.api.CuratorEvent;
import com.netflix.curator.framework.api.CuratorListener;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import com.netflix.curator.framework.state.ConnectionState;
import com.netflix.curator.framework.state.ConnectionStateListener;
import com.netflix.curator.retry.BoundedExponentialBackoffRetry;

public class TestLock {

    public static final String                         zkConnectString = "10.0.128.129:2181";
    public static final BoundedExponentialBackoffRetry zkRetryPolicy   = new BoundedExponentialBackoffRetry(1000, 5000,
                                                                                                            3);
    public static final String                         zkLockPath      = "/lock_test";
    public static final String                         zkCachePath     = "/cache";

    public static void main(String[] args) throws Exception {
        final CuratorFramework mClient = CuratorFrameworkFactory.newClient(zkConnectString, 1000, 15000, zkRetryPolicy);
        InterProcessMutex mLock = new InterProcessMutex(mClient, zkLockPath);
        mClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                System.out.println(newState);
            }
        });
        mClient.getCuratorListenable().addListener(new CuratorListener() {

            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println(client == mClient);
                System.out.println(event);
                // System.out.println("client state: " + client.getZookeeperClient().getZooKeeper().getState());
                // 一旦发现session过期，就说明锁已经丢失，此时需要根据实际场景，终止操作。
                // 本例，同步代码块会做setData操作，故把client关闭，能终止setData。
                //
                if (event.getWatchedEvent() != null) {
                    if (event.getWatchedEvent().getState() == KeeperState.Expired) {
                        System.out.println("close cleint");
                        client.close();
                    }
                }
            }
        });

        mClient.start();

        // 获取到锁
        mLock.acquire();
        System.out.println("got the lock.");

        try {
            // 模拟耗时操作
            TimeUnit.SECONDS.sleep(1);
            System.out.println("waked up.");

            // 再次执行 mLock.acquire()的话，只是判断内存状态，认为是re-entering，不断如何都会成功
            // mLock.acquire();
            // System.out.println("got the lock again.....");

            // 如果session断开过，lock丢失。再次创建新session后，setData依旧会成功。
            mClient.setData().forPath(zkLockPath, "test".getBytes());
            System.out.println("set data success.");

        } finally {
//            mLock.release();
            System.out.println("release lock.");
        }

        mClient.close();
        TimeUnit.SECONDS.sleep(500);
    }

}
