package processbuilder;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

public class Test {

	//网上说(以及按照之前的经验)，jvm挂了，Process开启的子进程是不会挂的，可是以下测试(起因是再本地运行azkaban遇到)，jvm关了，子进程也不负存在。
	//网上也有说，just work，有时会，有时不会，我猜测是看本机环境。
	public static void main(String[] args) throws IOException, InterruptedException {
//		ProcessBuilder processBuilder = new ProcessBuilder("bash","/home/atell/test.sh","100");
//		Process process = processBuilder.start();
//		process.waitFor();
		
		
//		String[] cmdarray = {"bash","/home/atell/test.sh","100"};
//		Process process2 = Runtime.getRuntime().exec(cmdarray);
//		
//		process2.waitFor();

		
		CommandLine cmdLine = new CommandLine("bash");
        cmdLine.addArgument("/home/atell/test.sh");
        cmdLine.addArgument("100");

        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValues(null);
        executor.execute(cmdLine);
		
		System.out.println("done");
	}

}
