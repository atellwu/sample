package leetcode;

import java.util.ArrayList;
import java.util.List;

//https://oj.leetcode.com/problems/word-search/
public class WordSearch {

	public static boolean exist(char[][] board, String word) {
		int h = board.length;
		int w = board[0].length;
		int[][] sign = new int[h][w];

		char ch = word.charAt(0);
		List<Pos> posList = find(board, ch);

		return loop(board, posList, word, 0, sign);

	}

	// 寻找pos周边是否，找 word[charAt]
	private static boolean loop(char[][] board, List<Pos> posList, String word,
			int charAt, int[][] sign) {
		if (word.length() <= charAt) {
			return true;
		}
		// 遍历这些可用点
		for (Pos pos : posList) {
			if (board[pos.i][pos.j] == word.charAt(charAt)) {
				sign[pos.i][pos.j] = -1;
				// 寻找周边可用点
				boolean exist = loop(board, getRoundPosList(pos, sign), word,
						charAt + 1, sign);
				if (exist) {
					return true;
				}
				sign[pos.i][pos.j] = 0;
			}
		}

		return false;
	}

	private static List<Pos> getRoundPosList(Pos pos, int[][] sign) {
		List<Pos> list = new ArrayList<WordSearch.Pos>();

		int[][] direction = new int[][] { { 0, 1 }, { 1, 0 }, { 0, -1 },
				{ -1, 0 } };

		for (int i = 0; i < direction.length; i++) {
			if ((pos.i + direction[i][0]) >= 0
					&& (pos.i + direction[i][0]) < sign.length
					&& (pos.j + direction[i][1]) >= 0
					&& (pos.j + direction[i][1]) < sign[0].length
					&& sign[pos.i + direction[i][0]][pos.j + direction[i][1]] == 0) {
				list.add(new Pos(pos.i + direction[i][0], pos.j
						+ direction[i][1]));
			}
		}

		return list;
	}

	private static List<Pos> find(char[][] board, char ch) {
		List<Pos> list = new ArrayList<WordSearch.Pos>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == ch) {
					Pos pos = new Pos(i, j);
					list.add(pos);
				}
			}
		}
		return list;
	}

	static class Pos {

		public Pos(int i, int j) {
			this.i = i;
			this.j = j;
		}

		@Override
		public String toString() {
			return String.format("Pos [i=%s, j=%s]", i, j);
		}

		int i;
		int j;
	}

	public static void main(String[] args) {
		{
			char[][] board = new char[][] { { 'A', 'B', 'C', 'E' },
					{ 'S', 'F', 'C', 'S' }, { 'A', 'D', 'E', 'E' } };
			//
			System.out.println(exist(board, "ABCCED"));
			System.out.println(exist(board, "SEE"));
			System.out.println(exist(board, "ABCB"));
		}
		{
			char[][] board = new char[3][1];
			board[0] = new char[] { 'b' };
			board[1] = new char[] { 'a' };
			board[2] = new char[] { 'b' };

			System.out.println(exist(board, "bbabab"));
		}
		{
			char[][] board = transform(new String[] {
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaab" });
			String search = "baaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
			System.out.println(exist(board, search));
		}
	}

	static char[][] transform(String[] str) {
		char[][] board = new char[str.length][str[0].length()];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				board[i][j] = str[i].charAt(j);
			}
		}
		return board;
	}
}
