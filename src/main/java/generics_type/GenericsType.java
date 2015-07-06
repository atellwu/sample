package generics_type;

import java.util.ArrayList;
import java.util.List;

/**
 * http://my.oschina.net/jiemachina/blog/201507
 * 在Java集合框架中，对于参数值是未知类型的容器类，只能读取其中元素，不能向其中添加元素，
 * 因为，其类型是未知，所以编译器无法识别添加元素的类型和容器的类型是否兼容，唯一的例外是NULL
 * 
 * @author atell
 *
 */
public class GenericsType {
	class ListGenertics {
		class Fruit {
		}

		class Apple extends Fruit {
		}

		class Jonathan extends Apple {
		}

		class Orange extends Fruit {
		}

		public void maind(String args) {

			List<Apple> list = new ArrayList<Apple>();
			List<? extends Object> fruit = new ArrayList<Apple>();

			// Object apple = new Apple();
			// fruit.add(apple); // error
			// fruit.add(null); // error
			// fruit.add(new Jonathan());// error

		}
	}

}
