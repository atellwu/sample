package leetcode;

import java.util.ArrayList;
import java.util.List;

//https://oj.leetcode.com/problems/gray-code/
public class GrayCode {
	public static List<Integer> grayCode(int n) {
		int size = (int) Math.pow(2, n);
		List<Integer> list = new ArrayList<Integer>(size);

		for (int i = 0; i < size; i++) {
			int[] bits = new int[n];
			for (int j = 0; j < n; j++) {
				int groupSize = size / ((int) Math.pow(2, j));
				bits[j] = calValue(groupSize, i, j);
			}
			int val = bitsToInt(bits);
			// System.out.println(Arrays.toString(bits));
			list.add(val);
		}

		return list;
	}

	private static int bitsToInt(int[] bits) {
		int val = 0;
		for (int i = 0; i < bits.length; i++) {
			val |= (bits[i] << (bits.length - 1 - i));
		}
		return val;
	}

	private static int calValue(int groupSize, int row, int col) {
		// shang是偶数，则0...1...
		// shang是奇数，则1...0...
		int shang = row / groupSize;
		boolean startIsZero = (shang % 2 == 0);

		// mod是group内偏移量
		int mod = row % groupSize;
		if (mod >= groupSize / 2 && startIsZero) {
			return 1;
		} else if (mod < groupSize / 2 && !startIsZero) {
			return 1;
		} else {
			return 0;
		}
	}

	public static void main(String[] args) {
		// grayCode(3);
		System.out.println(bitsToInt(new int[] { 0, 1, 0 }));
	}

}
