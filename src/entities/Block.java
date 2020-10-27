/**
 * Block
 */
package entities;

import java.util.ArrayList;

/**
 * @author Liang Wang
 *
 */
public class Block implements Comparable<Block>{

	private String id;
	private long size;
	private ArrayList<Transaction> txns;
	private ArrayList<Node> addressTable;
	private Node node;
	private long timestamp;
	/**
	 * Instantiate a Block.
	 * @param txns The transactions contained in this block.
	 * @param addressTable The address table for transferring this block.
	 * @param node The creator of this block.
	 */
	public Block(ArrayList<Transaction> txns, ArrayList<Node> addressTable, Node node) {
		id = "B" + System.nanoTime();
		this.txns = txns;
		this.addressTable = addressTable;
		this.node = node;
		size = 0;
		for(int i = 0; i < txns.size(); i ++) {
			size += txns.get(i).getSize_txn();
		}
		timestamp = System.nanoTime();
	}
	
	public String getId() {return id;}
	public long getSize() {return size;}
	public ArrayList<Transaction> getTxns() {return txns;}
	public ArrayList<Node> getAddressTable() {return addressTable;}
	public Node getNode() {return node;}
	public void setNode(Node node) {this.node = node;}
	
	/**
	 * Sort by ASC
	 */
	@Override
	public int compareTo(Block block) {
		long gap = this.timestamp - block.timestamp;
		if(gap > 0) return 1;
		else if(gap < 0) return -1;
		else return 0;
	}
}
