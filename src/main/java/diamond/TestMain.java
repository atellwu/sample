//package diamond;
//
//import java.util.concurrent.Executor;
//
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import com.taobao.diamond.client.Diamond;
//import com.taobao.diamond.manager.DiamondManager;
//import com.taobao.diamond.manager.ManagerListener;
//import com.taobao.diamond.manager.impl.DefaultDiamondManager;
//
//@SuppressWarnings("deprecation")
//public class TestMain {
//
//    private static String  group  = "localhost";
//    private static String  dataId = "abc";
//
//
//    public static void main(String[] args) throws Exception {
//
////        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application-service.xml");
////        diamond = context.getBean(Diamond.class);
//
//        Diamond.addListener(dataId, group, new ManagerListener() {
//
//            public Executor getExecutor() {
//                return null;
//            }
//
//            public void receiveConfigInfo(String configInfo) {
//                // �ͻ��˴�����ݵ��߼�
//                System.out.println(configInfo);
//            }
//
//        });
//        
//        Thread.sleep(50000000);
//
//    }
//
//}
