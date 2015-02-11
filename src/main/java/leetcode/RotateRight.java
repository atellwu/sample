package leetcode;
//https://oj.leetcode.com/problems/rotate-list/
/**
 * Definition for singly-linked list. public class ListNode { int val; ListNode
 * next; ListNode(int x) { val = x; next = null; } }
 */
public class RotateRight {
	public static ListNode rotateRight(ListNode head, int n) {
		if(head==null|| n==0){
			return head;
		}
		// 添加头节点
		ListNode emptyHead = new ListNode(-1);
		emptyHead.next = head;

		// ***** 找到倒数第n个节点 ******

		// first先走n步
		ListNode first = emptyHead;
		int i = 0;
		while (i++ < (n - 1)) {
			first = first.next;
			// first遇到null，说明长度不够，则直接返回原链表
			if (first == null) {
				first =  head;
			}
		}
		// 此时如果first再最后一个，则也不需要转
		if (first.next == null) {
			return head;
		}

		// second
		ListNode second = emptyHead;

		// first和second一起往前走，直到first.next是null. second即指向倒数第n+1个节点
		while (first.next != null) {
			first = first.next;
			second = second.next;
		}

		// ***** 将后n个节点，转到前面 ******
		// 把first指向head，second返回
		first.next = emptyHead.next;
		ListNode newHead = second.next;
		second.next = null;
		return newHead;
	}

	private static class ListNode {
		int val;
		ListNode next;

		ListNode(int x) {
			val = x;
			next = null;
		}

		@Override
		public String toString() {
			return String.format("ListNode [val=%s, next=%s]", val, next);
		}

	}

	public static void main(String[] args) {
		ListNode node5 = new ListNode(5);
		ListNode node4 = new ListNode(4);
		ListNode node3 = new ListNode(3);
		ListNode node2 = new ListNode(2);
		ListNode node1 = new ListNode(1);

		node5.next = node4;
		node4.next = node3;
		node3.next = node2;
		node2.next = node1;
		
		System.out.println(rotateRight(node5,6));
	}
}
