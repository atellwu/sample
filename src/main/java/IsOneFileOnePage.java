import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;

public class IsOneFileOnePage {

	public static void main(String[] args) throws Exception {

		FileInputStream[] inputs = new FileInputStream[100 * 1024];

		System.out.println("start after 20s... ");
		Thread.sleep(20 * 1000);
		System.out.println("started");

		int count = 1;
		try {
			for (int i = 0; i < inputs.length; i++) {
				File file = new File("/tmp/tiny/tinyFile-" + (i + 1));
				inputs[i] = new FileInputStream(file);
				inputs[i].read();
				if (count++ % 1024 == 0) {// 每1k次，就停一下（每新增1k*4k个page，内存使用量是否增加4M？）
					System.out.println("times " + (count / 1024));
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread.sleep(88999000);

	}

}
