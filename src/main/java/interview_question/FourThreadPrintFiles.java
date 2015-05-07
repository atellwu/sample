package interview_question;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

//http://www.oschina.net/question/576223_71611
public class FourThreadPrintFiles implements Runnable{
	private Integer index;
	private List<BlockingQueue<PrintStream>> list;
	static CountDownLatch latch = new CountDownLatch(4);
	public FourThreadPrintFiles(Integer index,List<BlockingQueue<PrintStream>> list){
		this.index = index;
		this.list  = list;
	}
	
	public static void main(String[] args) throws FileNotFoundException, InterruptedException {
		PrintStream	   pa = new PrintStream(new FileOutputStream( new File("/tmp/a.txt")));
		PrintStream    pb = new PrintStream(new FileOutputStream( new File("/tmp/b.txt")));
		PrintStream    pc = new PrintStream(new FileOutputStream( new File("/tmp/c.txt")));
		PrintStream    pd = new PrintStream(new FileOutputStream( new File("/tmp/d.txt")));
		List<BlockingQueue<PrintStream>> list = new ArrayList<BlockingQueue<PrintStream>>();
		for (int i = 0; i < 4; i++) {
	        list.add(new LinkedBlockingQueue<PrintStream>());
        }
		list.get(0).add(pa);
		list.get(1).add(pb);
		list.get(2).add(pc);
		list.get(3).add(pd);
		ExecutorService executor  = Executors.newFixedThreadPool(4);
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < 4; i++) {
			executor.submit(new FourThreadPrintFiles(i,list));
        }
		latch.await();
		long end = System.currentTimeMillis();
		System.out.println("cousuming="+(end-start));
		executor.shutdown();
    }

	@Override
    public void run() {
		Integer count = 1;
		while (true) {
			BlockingQueue<PrintStream> queue = list.get(index);
			BlockingQueue<PrintStream> next = list.get((index+1)%4);
			PrintStream tmp = null;
			try {
		        tmp = queue.take();
		        tmp.println(index+1);
		        next.put(tmp);
		        count++;
	        } catch (Exception e) {
		        e.printStackTrace();
	        }
			if (count>1000000) {
	            latch.countDown();
	            break;
            }
        }
		
	    
    }
	
	

}
