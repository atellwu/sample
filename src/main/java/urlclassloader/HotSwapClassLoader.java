package urlclassloader;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 可以重新载入同名类的类加载器实现
 *
 *
 * 放弃了双亲委派的加载链模式. 需要外部维护重载后的类的成员变量状态.
 *
 * @author ken.wu
 * @mail ken.wug@gmail.com 2007-9-28 下午01:37:43
 */
public class HotSwapClassLoader extends URLClassLoader {
	
	HotSwapClassLoader child;

	public HotSwapClassLoader(URL[] urls) {
		super(urls);
	}

	public HotSwapClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	@Override
	public Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		// 如果需要重新加载
		if (needReload(name)) {
			if(null == this.findLoadedClass0(name)){
				//本loader还没加载过它，则直接加载即可
				return this.loadClass(name, resolve);
			}else{
				//已经加载过，需要给孩子去加载
				return reload(name, resolve);
			}
		}

		// 否则，
		return this.loadClass(name, resolve);
	}
	
	

	private Object findLoadedClass0(String name) {
		return this.findLoadedClass(name);
	}

	private boolean needReload(String name) {
		return "urlclassloader.A".equals(name);
	}

	public Class<?> reload(String name, boolean resolve)
			throws ClassNotFoundException {
		if(child==null){
			child = new HotSwapClassLoader(this.getURLs(),this);
		}
		return super.findClass(name);
		// return new HotSwapClassLoader(super.getURLs(),
		// super.getParent()).load(
		// name, resolve);
	}
}