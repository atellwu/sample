package urlclassloader;

public class A {
	static{
		System.out.println("this is A");
	}
	
	
	public A(){
		this.b = new B();
		System.out.println("a.b classloader:"+b.getClass().getClassLoader());
	}
	private B b;

	public void setB(B b) {
		this.b = b;
	}

	public B getB() {
		return b;
	}
}