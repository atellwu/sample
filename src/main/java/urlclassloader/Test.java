package urlclassloader;

import java.net.URL;
import java.net.URLClassLoader;

public class Test {

	public static void main(String[] args) throws Exception {
		URL urls[] = new URL[] { new URL(
				"http://localhost:8080/tedis-atomic-1.1.8.jar") };
		URLClassLoader loader = new URLClassLoader(urls);
		Class<?> clazz = loader
				.loadClass("com.taobao.common.tedis.atomic.Tedis");
	}

}
