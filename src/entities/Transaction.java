/**
 * Transaction
 */
package entities;

import java.util.ArrayList;

/**
 * @author Liang Wang
 *
 */
public class Transaction implements Comparable<Transaction> {
	
	private long id;
	
	private Node getherer;
	
	private ArrayList<Node> addressTable;
	
	private long size_txn;
	
	private long startTime;
	
	private long endTime;
	
	public Transaction(Node getherer, ArrayList<Node> addressTable, long size_txn) {
		id = System.nanoTime(); 
		this.getherer = getherer;
		this.addressTable = addressTable;
		this.size_txn = size_txn;
		startTime = 0;
		endTime = 0;
	}
	
	public long getId() {return id;}
	
	public Node getGetherer() {return getherer;}
	
	public ArrayList<Node> getAddressTable() {return addressTable;}
	
	public long getSize_txn() {return size_txn;}
	
	public void setStartTime(long startTime) {this.startTime = startTime;}
	
	public void setEndTime(long endTime) {this.endTime = endTime;}
	
	public long getEndTime() {return endTime;}
	
	public void setGetherer(Node getherer) {this.getherer = getherer;}
	/**
	 * Get the latency of this transaction.
	 * @return The latency of this transaction in nanosecond.
	 */
	public long latency() {
		return endTime - startTime;
	}
	
	/**
	 * Sort by ASC
	 */
	@Override
	public int compareTo(Transaction txn) {
		long gap = this.startTime - txn.startTime;
		if(gap > 0) return 1;
		else if(gap < 0) return -1;
		else return 0;
	}
}
