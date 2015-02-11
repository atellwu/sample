package leetcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Definition for binary tree public class TreeNode { int val; TreeNode left;
 * TreeNode right; TreeNode(int x) { val = x; } }
 */
public class Solution {
	public static List<Integer> inorderTraversal(TreeNode root) {
		List<Integer> list = new ArrayList<Integer>();
		if (root == null) {
			return list;
		}

		Stack<TreeNode> stack = new Stack<TreeNode>();
		stack.push(root);

		TreeNode node = null;

		while (!stack.isEmpty()) {
			node = stack.peek();

			if (node.left != null) {
				// 还有左边孩子，则放进去
				stack.push(node.left);
			} else {
				// 没有left，则输出该node，并把右边孩子放进去
				list.add(node.val);

				stack.pop();
				
				if(!stack.isEmpty()){
					stack.peek().left=null;
				}

				if (node.right != null) {
					stack.push(node.right);
				}
			}
		}

		return list;
	}

	public static void main(String[] args) {
		TreeNode node1 = new TreeNode(1);
		TreeNode node2 = new TreeNode(2);
		TreeNode node3 = new TreeNode(3);
		TreeNode node4 = new TreeNode(4);
		TreeNode node5 = new TreeNode(5);
		TreeNode node6 = new TreeNode(6);
		TreeNode node7 = new TreeNode(7);

		node1.left = node2;
		node1.right = node3;

		node2.left = node4;
		node2.right = node5;

		node3.left = node6;
		node3.right = node7;

		System.out.println(inorderTraversal(node1));

	}
}

class TreeNode {
	int val;
	TreeNode left;
	TreeNode right;

	TreeNode(int x) {
		val = x;
	}

	@Override
	public String toString() {
		return String.format("TreeNode [val=%s]", val);
	}
	
}