package leetcode;

import java.util.LinkedList;

//https://oj.leetcode.com/problems/populating-next-right-pointers-in-each-node/
public class PopulatingNextRightPointers {

	public static void connect(TreeLinkNode root) {
		if (root == null) {
			return;
		}
		LinkedList<TreeLinkNode> queue = new LinkedList<TreeLinkNode>();
		queue.add(root);
		queue.add(null);// 用null隔行

		TreeLinkNode node;
		while (!queue.isEmpty()) {
			node = queue.pop();
			if (node.left != null) {
				queue.add(node.left);
			}
			if (node.right != null) {
				queue.add(node.right);
			}

			TreeLinkNode nextNode = queue.peek();
			if (nextNode != null) {
				node.next = nextNode;
			} else {
				queue.pop();// 把隔行的null拿出来
				if (!queue.isEmpty()) {
					queue.add(null);// 这一行的最右，那么node.right就是下一行的最右，就可以把null加进去，表示下一行的隔行
				}
			}
		}
	}

	static class TreeLinkNode {
		int val;
		TreeLinkNode left, right, next;

		TreeLinkNode(int x) {
			val = x;
		}

		@Override
		public String toString() {
			return "TreeLinkNode [val=" + val + ", next=" + next + "]";
		}
	}

	public static void main(String[] args) {
		TreeLinkNode node1 = new TreeLinkNode(1);
		TreeLinkNode node2 = new TreeLinkNode(2);
		TreeLinkNode node3 = new TreeLinkNode(3);
		TreeLinkNode node4 = new TreeLinkNode(4);
		TreeLinkNode node5 = new TreeLinkNode(5);
		TreeLinkNode node6 = new TreeLinkNode(6);
		TreeLinkNode node7 = new TreeLinkNode(7);

		node1.left = node2;
		node1.right = node3;
		node2.left = node4;
		node2.right = node5;
		node3.left = node6;
		node3.right = node7;

		connect(node1);
		System.out.println(node1);
	}
}
