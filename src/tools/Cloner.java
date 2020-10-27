/**
 * Tools for clone.
 */
package tools;

import java.util.ArrayList;


/**
 * @author Liang Wang
 *
 */
public class Cloner {
	/**
	 * Skin clone only copy the references of the objects in an ArrayList to a new ArrayList.
	 * @param al The ArrayList to be skin cloned.
	 * @return The new ArrayList having the same objects with al.
	 */
	public static ArrayList<?> skinClone(ArrayList<?> al) {
		if(al == null) {return null;}
		ArrayList<Object> aln = new ArrayList<Object>();
		for(int i = 0; i < al.size(); i ++) {
			aln.add(al.get(i));
		}
		return aln;
	}
	
	public static void main(String[] args) {
//		ArrayList<Node> al = new ArrayList<Node>();
//		al.add(new Node("a"));
//		al.add(new Node("b"));
//		ArrayList<Node> aln = (ArrayList<Node>) Cloner.skinClone(al);
//		System.out.println(aln.get(0).getId());
//		System.out.println(aln.get(1).getId());
		
	}
}
