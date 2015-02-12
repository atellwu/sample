package leetcode;

import java.util.ArrayList;
import java.util.List;

//https://oj.leetcode.com/problems/unique-binary-search-trees-ii/
public class UniqueBinarySearchTrees2 {

	public static List<TreeNode> generateTrees(int n) {
		int[] numbers = new int[n];
		for (int i = 0; i < n; i++) {
			numbers[i] = i + 1;
		}

		return loop(numbers, 0, numbers.length);
	}

	private static List<TreeNode> loop(int[] numbers, int left, int right) {
		List<TreeNode> list = new ArrayList<TreeNode>();

		if (left == right) {
			list.add(null);
			return list;
		}

		for (int i = left; i < right; i++) {
			// 左边
			List<TreeNode> leftTrees = loop(numbers, left, i);
			// 右边
			List<TreeNode> rightTrees = loop(numbers, i + 1, right);

			for (TreeNode leftNode : leftTrees) {
				for (TreeNode rightNode : rightTrees) {
					TreeNode root = new TreeNode(numbers[i]);
					root.left = leftNode;
					root.right = rightNode;

					list.add(root);
				}
			}
		}
		return list;
	}

	static class TreeNode {
		int val;
		TreeNode left;
		TreeNode right;

		TreeNode(int x) {
			val = x;
			left = null;
			right = null;
		}

		@Override
		public String toString() {
			return "TreeNode [val=" + val + ", left=" + left + ", right="
					+ right + "]";
		}

	}

	public static void main(String[] args) {
		System.out.println(generateTrees(3));
	}
}
