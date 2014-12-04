package reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		String a  =new String("aaa");
		ReferenceQueue<? super String> queue  =new ReferenceQueue<String>();
		WeakReference<String> wr = new WeakReference<String>(a,queue  );
		
		a = null;
		System.out.println(queue.poll());//null
		System.out.println(wr.get());//aaa

		//after gc
		System.gc();
		Thread.sleep(10);
		
		System.out.println(queue.poll());//java.lang.ref.WeakReference@65cc892e
		System.out.println(wr.get());//null
		
		//结论：queue是被GC用来存放被回收的对象的WeakRef的，不过拿到WeakRef也是拿不回对象了。
		
	}

}
