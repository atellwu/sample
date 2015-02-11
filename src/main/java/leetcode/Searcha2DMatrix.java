package leetcode;

//https://oj.leetcode.com/problems/search-a-2d-matrix/
public class Searcha2DMatrix {

	// 复杂度 log(m)+log(n) 即 log(mn)
	public static boolean searchMatrix(int[][] matrix, int target) {
		// 先找出再哪一行
		int lineNum = binarySearch2D(matrix, 0, matrix.length - 1, target);
		// 行内查找
		int found = -1;
		if (lineNum != -1) {
			found = binarySearch(matrix[lineNum], 0,
					matrix[lineNum].length - 1, target);
		}

		return found != -1;

	}

	static int binarySearch2D(int[][] array, int m, int n, int target) {
		if (m > n) {
			return -1;
		}
		int i = (m + n) / 2;

		int re = contain(array[i], target);
		if (re == 0) {
			return i;
		}
		if (re > 0) {
			return binarySearch2D(array, m, i - 1, target);
		} else {
			return binarySearch2D(array, i + 1, n, target);
		}
	}

	private static int contain(int[] is, int target) {
		if (target < is[0]) {
			return 1;
		} else if (target > is[is.length - 1]) {
			return -1;
		} else {
			return 0;
		}
	}

	// [m,n]
	static int binarySearch(int[] array, int m, int n, int target) {
		if (m > n) {
			return -1;
		}
		int i = (m + n) / 2;
		if (array[i] == target) {
			return i;
		}
		if (array[i] > target) {
			return binarySearch(array, m, i - 1, target);
		} else {
			return binarySearch(array, i + 1, n, target);
		}
	}

	public static void main(String[] args) {
		{
			int[][] array = new int[][] { { 1, 2, 3 }, { 5, 7, 9 },
					{ 10, 17, 19 } };
			System.out.println(searchMatrix(array, 17));
		}

		{
			int[][] array = new int[][] { { 1, 2, 3 } };
			System.out.println(searchMatrix(array, 17));
		}
	}
}
