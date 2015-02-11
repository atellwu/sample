package leetcode;

public class InsertSortList {

	public static ListNode insertionSortList(ListNode head) {
		ListNode newhead = null;

		ListNode loop = head;

		while (loop != null) {
			// 载掉第一个节点
			ListNode node = loop;
			loop = loop.next;
			node.next = null;

			// 节点插入到newhead;
			newhead = insert(newhead, node);
		}

		return newhead;

	}

	private static ListNode insert(ListNode head, ListNode newNode) {
		ListNode node1 = head;

		// 如果应该插入到第一个
		if (node1 == null || node1.val >= newNode.val) {
			newNode.next = node1;
			return newNode;
		}

		// 只有一个node
		ListNode node2 = head.next;
		if (node2 == null) {
			if (node1.val >= newNode.val) {
				newNode.next = node1;
				return newNode;
			} else {
				node1.next = newNode;
				return head;
			}
		}

		// 遍历，直到找到插入点
		while (node2 != null && node2.val < newNode.val) {
			node1 = node2;
			node2 = node2.next;
		}

		// 插到node1后面
		newNode.next = node2;
		node1.next = newNode;

		return head;

	}

	public static void main(String[] args) {
		ListNode node5 = new ListNode(3);
		ListNode node4 = new ListNode(4);
		ListNode node3 = new ListNode(2);
		ListNode node2 = new ListNode(2);
		ListNode node1 = new ListNode(1);

		node5.next = node4;
		node4.next = node3;
		node3.next = node2;
		node2.next = node1;

		ListNode list = insertionSortList(node5);

		while (list != null) {
			System.out.println(list.val);
			list = list.next;
		}

	}
}

class ListNode {
	int val;
	ListNode next;

	ListNode(int x) {
		val = x;
		next = null;
	}

	@Override
	public String toString() {
		return String.format("ListNode [val=%s]", val);
	}

}
