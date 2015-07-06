package generics_type;

import java.util.ArrayList;
import java.util.List;

public class Generic2 {
	public static void main(String[] args) {
		List<? extends A> la;
		la = new ArrayList<B>();
		la = new ArrayList<C>();
		la = new ArrayList<D>();

		List<? super B> lb;
		lb = new ArrayList<A>(); // fine
		// lb = new ArrayList<C>(); //will not compile
	}

	//其实很简单啊。。。。extends 和 super 
	public void someMethod(List<? extends B> lb) {
		B b = lb.get(0); // is fine
//		lb.add(new C()); // will not compile as we do not know the type of the
//							// list, only that it is bounded above by B
	}

	public void otherMethod(List<? super B> lb) {
		// B b = lb.get(0); // will not compile as we do not know whether the
		// list
		// // is of type B, it may be a List<A> and only
		//		// contain instances of A
		lb.add(new B()); // is fine, as we know that it will be a super type of
							// A
	}
}

class A {
}

class B extends A {
}

class C extends B {
}

class D extends B {
}