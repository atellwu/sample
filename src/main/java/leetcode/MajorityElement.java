package leetcode;

public class MajorityElement {
	public static int majorityElement(int[] num) {
		int[] disabled = new int[num.length];
		for (int i = 0; i < num.length; i++) {
			disabled[i] = 1;
		}
		// 0表示不可用，非0是可用

		return loop(num, disabled, 0);
	}

	private static int loop(int[] num, int[] disabled, int index) {
		if (index == 32) {
			// num剩下的一定都是N
			for (int i = 0; i < num.length; i++) {
				if((disabled[i] == 0)){
					continue;
				}
				return num[i];
			}
		}
		int count1 = 0, count2 = 0;
		for (int i = 0; i < num.length; i++) {
			if((disabled[i] == 0)){
				continue;
			}
			if ((num[i] & (1 << index)) != 0) {
				disabled[i] = 1;
				count1++;
			} else {
				disabled[i] = 2;
				count2++;
			}
		}
		int dropSign = count1 > count2 ? 2 : 1;
		for (int i = 0; i < num.length; i++) {
			if((disabled[i] == 0)){
				continue;
			}
			disabled[i] -= dropSign;// 需要剔除的就会变成0
		}
		return loop(num, disabled, index + 1);
	}

	public static void main(String[] args) {
		System.out.println(majorityElement(new int[] { 1, 2, 3, 4, 5, 4, 4, 4,
				4 }));
	}
}
