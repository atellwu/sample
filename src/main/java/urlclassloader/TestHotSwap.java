package urlclassloader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class TestHotSwap {
	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	public static void main(String args[]) throws Exception {
		HotSwapClassLoader c1 = new HotSwapClassLoader(
				new URL[] { new File("/home/atell/").toURI().toURL() });
		
		System.out.println(c1.loadClass("urlclassloader.A"));
		System.out.println(c1.loadClass("urlclassloader.A"));
		
		Class clazz =c1.loadClass("urlclassloader.A");
		System.out.println(clazz.getClassLoader());
	
//		clazz.newInstance();

//		Method method2 = clazz.getMethod("getB", null);
//		Object bInstance = method2.invoke(aInstance, null);

//		System.out.printf(" a classLoader is %s \n", aInstance.getClass()
//				.getClassLoader());
//		System.out.printf(" a.b classLoader is %s \n", bInstance.getClass()
//				.getClassLoader());
	}
}








