package leetcode;

public class CountAndSay {
	public static String countAndSay(int n) {
		String str = "1";
		for (int i = 0; i < (n - 1); i++) {
			str = loop(str);
		}
		return str;
	}

	private static String loop(String str) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		char ch = str.charAt(i++);
		int count = 1;
		do {
			if (i >= str.length()) {
				sb.append(count).append(ch);
			} else if (str.charAt(i) != ch) {
				sb.append(count).append(ch);
				ch = str.charAt(i);
				count = 1;
			} else {
				count++;
			}
		} while (i++ < str.length());
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(countAndSay(5));
	}
}
