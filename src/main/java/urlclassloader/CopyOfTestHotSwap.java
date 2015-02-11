package urlclassloader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class CopyOfTestHotSwap {
	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	public static void main(String args[]) throws Exception {
		// A a = new A();
		B b = new B();
		// a.setB(b);

		// System.out.printf("A classLoader is %s \n", a.getClass()
		// .getClassLoader());
		System.out.printf("B classLoader is %s \n", b.getClass()
				.getClassLoader());
		// System.out.printf("A.b classLoader is %s \n", a.getB().getClass()
		// .getClassLoader());

		// HotSwapClassLoader c1 = new HotSwapClassLoader(new URL[] { new File(
		// "/home/atell/") .toURI().toURL()}, b.getClass().getClassLoader());
		URLClassLoader c1 = new HotSwapClassLoader(
				new URL[] { new File("/home/atell/").toURI().toURL() },null);
//		Thread.currentThread().setContextClassLoader(c1);
		
		Class clazz =c1.loadClass("urlclassloader.A");
		System.out.println(clazz.getClassLoader());
	
		clazz.newInstance();

//		Method method2 = clazz.getMethod("getB", null);
//		Object bInstance = method2.invoke(aInstance, null);

//		System.out.printf(" a classLoader is %s \n", aInstance.getClass()
//				.getClassLoader());
//		System.out.printf(" a.b classLoader is %s \n", bInstance.getClass()
//				.getClassLoader());
	}
}








