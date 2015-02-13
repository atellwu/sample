package leetcode;

public class SingleNumber {
	public static int singleNumber(int[] A) {
		int result = 0;
		for(int num:A){
			result ^= num;
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		int[] A = {1,1,2,2,5};
		System.out.println(singleNumber(A ));
	}
}
