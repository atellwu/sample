package final_test;

/**
 * <pre>
 * https://docs.oracle.com/javase/specs/jls/se7/html/jls-17.html#jls-17.5
 * 这里谈的是final变量的重排序和可见性问题，即如果final变量初始化了，那么就要保证其他线程对其的可见性。
 * 
 * 17.5.2. Reading final Fields During Construction
 * A read of a final field of an object within the thread that constructs that object is ordered with 
 * respect to the initialization of that field within the constructor by the usual happens-before rules. 
 * If the read occurs after the field is set in the constructor, it sees the value the final field is assigned, 
 * otherwise it sees the default value.
 * 翻译：构造完再给别的线程读的话，满足happens-before原则，所以可见其值。但如果使用不当，如下方的“提早暴露this”，还是会读到不正确的final值(默认值)。
 * 
 * </pre>
 * 
 * @author atell
 *
 */
public class Final_MemoryModel {
	final int finalVariable;

	public Final_MemoryModel(FinalVariableReader reader) throws Exception {
		reader.finalModel = this;
		Thread.sleep(10000000);
		finalVariable = 9;
	}

	public static void main(String[] args) throws Exception {
		FinalVariableReader thread = new FinalVariableReader();
		thread.start();
		new Final_MemoryModel(thread);
	}
}

class FinalVariableReader extends Thread {
	Final_MemoryModel finalModel;

	@Override
	public void run() {
		while (true) {
			System.out.println(finalModel.finalVariable);// 读到的是0.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
