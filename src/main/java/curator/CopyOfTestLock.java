package curator;

import java.util.concurrent.TimeUnit;

import com.netflix.curator.RetryPolicy;
import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.framework.api.CuratorEvent;
import com.netflix.curator.framework.api.CuratorListener;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import com.netflix.curator.framework.state.ConnectionState;
import com.netflix.curator.framework.state.ConnectionStateListener;
import com.netflix.curator.retry.BoundedExponentialBackoffRetry;

public class CopyOfTestLock {

    public static final String      zkConnectString = "localhost";
    public static final RetryPolicy zkRetryPolicy   = new BoundedExponentialBackoffRetry(1000, 5000, 3);
    public static final String      zkLockPath      = "/lock";
    public static final String      zkCachePath     = "/cache";

    public static void main(String[] args) throws Exception {
        CuratorFramework mClient = CuratorFrameworkFactory.newClient(zkConnectString, zkRetryPolicy);
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
                System.out.println(event);
            }
        });
        mClient.start();

        try {
            mLock.acquire(5000,TimeUnit.SECONDS);
            System.out.println("get the lock.....");
            TimeUnit.SECONDS.sleep(60000);
        } finally {
            mLock.release();
        }
    }

}
