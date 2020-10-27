/**
 * Node
 */
package entities;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Liang Wang
 *
 */
public class Node implements Runnable {
	
	private int id;
	private PriorityBlockingQueue<Transaction> inboxTxn;
	private PriorityBlockingQueue<Block> inboxBlock;
	private LinkedBlockingQueue<Tag> inboxTag;
	private PriorityBlockingQueue<Transaction> outboxTxn;
	private PriorityBlockingQueue<Block> outboxBlock;
	private PriorityBlockingQueue<Tag> outboxTag;
	private PriorityBlockingQueue<Transaction> poolTxn;
	private PriorityBlockingQueue<Block> blockchain;
	
	
	public Node(int id) {
		this.id = id;
		inboxTxn = new PriorityBlockingQueue<Transaction>();
		inboxBlock = new PriorityBlockingQueue<Block>();
		inboxTag = new LinkedBlockingQueue<Tag>();
		outboxTxn = new PriorityBlockingQueue<Transaction>();
		outboxBlock = new PriorityBlockingQueue<Block>();
		outboxTag = new PriorityBlockingQueue<Tag>();
		poolTxn = new PriorityBlockingQueue<Transaction>();
		blockchain = new PriorityBlockingQueue<Block>();
	}
	
	public int getId() {return id;}
	public PriorityBlockingQueue<Transaction> getInboxTxn() {return inboxTxn;}
	public PriorityBlockingQueue<Block> getInboxBlock() {return inboxBlock;}
	public LinkedBlockingQueue<Tag> getInboxTag() {return inboxTag;}
	public PriorityBlockingQueue<Transaction> getOutboxTxn() {return outboxTxn;}
	public PriorityBlockingQueue<Block> getOutboxBlock() {return outboxBlock;}
	public PriorityBlockingQueue<Tag> getOutboxTag() {return outboxTag;}
	public PriorityBlockingQueue<Transaction> getPoolTxn() {return poolTxn;}
	public PriorityBlockingQueue<Block> getBlockchain() {return blockchain;}
	
	@Override
	public void run() {}

}
