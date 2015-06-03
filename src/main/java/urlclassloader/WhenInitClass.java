package urlclassloader;

public class WhenInitClass {
	static{
		System.out.println("this is A");
	}
	
	private B b=null;//只是用到B类符号信息，并不会初始化B类
	
//	private B b=new B();//实例化B时，才会去初始化B类

	public WhenInitClass(){
	}

	public static void main(String[] args) {
		new WhenInitClass();
	}	
}

class B {
	static{
		System.out.println("this is B static init");
	}
}