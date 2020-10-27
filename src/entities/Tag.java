/**
 * Tag for Block
 */
package entities;

import java.util.ArrayList;

/**
 * @author Liang Wang
 *
 */
public class Tag implements Comparable<Tag>{
//	The number of transaction to be packed.
	private int num;
//	The size of the tag.
	private long size_tag;
//	The addressTable for broadcasting this tag.
	private ArrayList<Node> addressTable;
//	The node who creates this Tag.
	private Node node;
//	The block that the tag announcing for
	private Block block;
//	The timestamp
	private long timestamp;
	
	public Tag(int num, long size_tag, ArrayList<Node> addressTable, Node node, Block block) {
		this.num = num;
		this.size_tag = size_tag;
		this.addressTable = addressTable;
		this.node = node;
		this.block = block;
		timestamp = System.nanoTime();
	}
	
	public int getNum() {return num;}
	public long getSize_tag() {return size_tag;}
	public ArrayList<Node> getAddressTable() {return addressTable;}
	public Node getNode() {return node;}
	public Block getBlock() {return block;}
	
	/**
	 * Sort by ASC
	 */
	@Override
	public int compareTo(Tag tag) {
		long gap = this.timestamp - tag.timestamp;
		if(gap > 0) return 1;
		else if(gap < 0) return -1;
		else return 0;
	}
}
