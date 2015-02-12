package leetcode;

//https://oj.leetcode.com/problems/unique-binary-search-trees/
public class UniqueBinarySearchTrees {

	public static int numTrees(int n) {
		int[] numbers = new int[n];
		for (int i = 0; i < n; i++) {
			numbers[i] = i + 1;
		}
		return loop(numbers, 0, numbers.length);
	}

	private static int loop(int[] numbers, int left, int right) {
		if (left == right) {
			return 1;
		}

		int count = 0;
		for (int i = left; i < right; i++) {
			// 左边
			int leftCount = loop(numbers, left, i);
			// 右边
			int rightCount = loop(numbers, i + 1, right);

			count += leftCount * rightCount;
		}
		return count;
	}

	public static void main(String[] args) {
		System.out.println(numTrees(5));
	}
}
