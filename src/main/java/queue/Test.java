package queue;

public class Test {
	public static void main(String[] args) throws InterruptedException {
		CircularFifoQueue<String> queue = new CircularFifoQueue<String>(1);

//		PutThread put1 = new PutThread(queue, 1);
//		PutThread put2 = new PutThread(queue, 2);
//		PutThread put3 = new PutThread(queue, 3);
		
		queue.put("1");
		queue.put("2");
		queue.put("3");
		
		while(!queue.isEmpty()){
			System.out.println(queue.take());
		}
	}
}

class PutThread extends Thread {
	private CircularFifoQueue queue;
	private int sign;

	public PutThread(CircularFifoQueue queue, int sign) {
		super();
		this.queue = queue;
		this.sign = sign;
	}

	public void run() {
		try {
			queue.put("" + sign);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
class TakeThread extends Thread {
	private CircularFifoQueue queue;
	
	public TakeThread(CircularFifoQueue queue) {
		super();
		this.queue = queue;
	}
	
	public void run() {
		try {
			System.out.println(queue.take());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
