/**
 * Tools about random number generation.
 */
package tools;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Liang Wang
 *
 */
public class RandTools {
	
	public static boolean probabilityTest(double p, int v) {
		try {
			if(p > 1 || p < 0 || v < 0) {
				throw new Exception("Wrong argument(s)!");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		long freq = Math.round(p * Math.pow(10, v));
		int rand = ThreadLocalRandom.current().nextInt((int)Math.pow(10, v));
		if(rand < freq) {return true;}
		else {return false;}
	}
	/**
	 * Get a random integer that begins with start and end with (end - 1).
	 * @param start The beginning of the range.
	 * @param end The ending of the range.
	 * @return
	 */
	public static int randomInt(int start, int end) {
		try {
			if(start >= end) {
				throw new Exception("Wrong argument(s)!");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Random r = new Random();
		int rand = r.nextInt(end - start);
		return rand + start;
	}
	
	public static void main(String[] args) {
//		System.out.println(RandTools.randomInt(-10, -3));
//		System.out.println(RandTools.probabilityTest(0.8, 4));
	}
}
