/**
 * Node for CCP
 */
package entities;

import java.util.ArrayList;

import tools.Cloner;
import tools.Configurator;
import tools.Reportor;

/**
 * @author Liang Wang
 *
 */
public class NodeCCP extends Node implements Runnable {
	private Thread timeout; // Just for simulation and is not a timer.
	private int size_tag;
	private int threshold;
	
	public NodeCCP(int id, long to, int size_tag) {
		super(id);
		this.size_tag = size_tag;
		threshold = 2;
		timeout = new Thread(this);
	}
	
	public void startWork() {
		new Thread(this).start();
	}
	/**
	 * Pack a block.
	 * @param tag The tag object that is used for broadcast. If not the inboxTagChecker call this function, tag should be set to null.
	 */
	private synchronized void packingBlock() {
		try {
			if(Thread.currentThread() == timeout) {
				if(Configurator.poolTxns.size() >= (Integer.valueOf(Configurator.params.get("n")) * threshold)) {
					return;
				}
			}
			else {
				if(Configurator.poolTxns.size() < (Integer.valueOf(Configurator.params.get("n")) * threshold)) {
					return;
				}
			}
			
			int half = Integer.valueOf(Configurator.params.get("n")) * (int)Math.ceil(threshold / 2);
			if(Configurator.poolTxns.size() < half) {
				half = Configurator.poolTxns.size();
			}
			if(half == 0) { //When timeout thread runs, no left transaction will turn off it.
				return;
			}
			if(getId() == 0) { //For simulation, let the leader node always be the node number 0.
//				Get the first half of the transaction pool.
				ArrayList<Transaction> txns = new ArrayList<Transaction>();
				for(int i = 0; i < half; i ++) {
					txns.add(Configurator.poolTxns.take());
				}
//				Generate a block and add it to the blockchain.
//				Note that the argument addressTable of Block() should be null because we do not broadcast it but a Tag.
				Block block = new Block(txns, null, this);
				getBlockchain().put(block);
//				Generate a Tag and put it into outboxTag.
				@SuppressWarnings("unchecked")
				ArrayList<Node> addressTable = (ArrayList<Node>)Cloner.skinClone(Configurator.nodes);
				getOutboxTag().put(new Tag(half, size_tag, addressTable, this, block));
			}
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				if(Thread.currentThread() == timeout) {
//					Thread.sleep(to);
//					When timeout is over, pack a block. 
					if(! Configurator.poolTxns.isEmpty()) {
						if(Configurator.poolTxns.size() < (Integer.valueOf(Configurator.params.get("n")) * threshold)) {
							packingBlock();
						}
					}
				}
				else {
//					Check inboxTxn
					if(! getInboxTxn().isEmpty()) {
						Transaction txn = getInboxTxn().take();
//						If the sent transaction comes back, put it into the transaction pool, otherwise, forward it.
						if(txn.getGetherer().getId() != getId()) {
							getOutboxTxn().put(txn);
						}
						else {
							Configurator.poolTxns.put(txn);
						}
					}
//					Check outboxTxn
					if(! getOutboxTxn().isEmpty()) {
						Transaction txn = getOutboxTxn().take();
						Configurator.threadPool.submit(new Transferrer(txn, null, null, 1));
					}
//					Check inboxTag
					if(! getInboxTag().isEmpty()) {
						Tag tag = getInboxTag().take();
						if(tag.getNode().getId() == getId()) {
//							The tag has got its final destination, which indicates that the transactions it contains has been completed.
							for(int i = 0; i < tag.getBlock().getTxns().size(); i ++) {
								tag.getBlock().getTxns().get(i).setEndTime(System.nanoTime());
								Configurator.scheduleTxns.incrementAndGet();
							}
//							Check progress
							System.out.println(Configurator.scheduleTxns.intValue() + " / " + Configurator.params.get("txns"));
							System.out.println(Configurator.poolTxns.size());
							
//							Set end time for the whole test.
							if(Configurator.scheduleTxns.intValue() == Integer.valueOf(Configurator.params.get("txns"))) {
								Configurator.endTime = System.nanoTime();
								Reportor.report();
							}
						}
						else {
							getBlockchain().put(tag.getBlock());
							getOutboxTag().put(tag);
						}
					}
//					Check outboxTag
					if(! getOutboxTag().isEmpty()) {
						Tag tag = getOutboxTag().take();
						Configurator.threadPool.submit(new Transferrer(null, tag, null, 2));
					}
//					Check poolTxn 
					if(Configurator.poolTxns.size() >= (Integer.valueOf(Configurator.params.get("n")) * threshold)) {
						packingBlock();
					}
//					Start timeout if needed.
					if((Integer.valueOf(Configurator.params.get("txns")) - Configurator.scheduleTxns.intValue()) <  (Integer.valueOf(Configurator.params.get("n")) * threshold)) {
						if(! timeout.isAlive()) {
							timeout.start();
						}
					}
				}
			}
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*v1-start*/
//	private Thread timeout; // Just for simulation and is not a timer.
//	private Thread inboxTxnChecker;
//	private Thread inboxTagChecker;
//	private Thread outboxTxnChecker;
//	private Thread outboxTagChecker;
//	private Thread poolTxnChecker;
//	private long to;
//	private int size_tag;
//	private int threshold;
//	
//	public NodeCCP(int id, long to, int size_tag) {
//		super(id);
//		this.to = to;
//		this.size_tag = size_tag;
//		threshold = 2;
//		timeout = new Thread(this);
//		inboxTxnChecker = new Thread(this);
//		outboxTxnChecker = new Thread(this);
//		inboxTagChecker = new Thread(this);
//		outboxTagChecker = new Thread(this);
//		poolTxnChecker = new Thread(this);
//	}
//	
//	public void startWork() {
////		timeout.start();
//		inboxTxnChecker.start();
//		outboxTxnChecker.start();
//		inboxTagChecker.start();
//		outboxTagChecker.start();
//		poolTxnChecker.start();
//	}
//	/**
//	 * Pack a block.
//	 * @param tag The tag object that is used for broadcast. If not the inboxTagChecker call this function, tag should be set to null.
//	 */
//	private synchronized void packingBlock(Tag tag) {
//		try {
//			if(Thread.currentThread() == poolTxnChecker) {
//				if(Configurator.poolTxns.size() < (Integer.valueOf(Configurator.params.get("n")) * threshold)) {
////					wait(1000);
//					return;
//				}
//			}
//			else if(Thread.currentThread() == inboxTagChecker) {
////				Consider the order and existence of transactions.
////				if(Configurator.poolTxns.containsAll(tag.getBlock().getTxns())) {
////					Configurator.poolTxns.removeAll(tag.getBlock().getTxns());
////				}
////				else {// Makes the tag wait for next checking.
////					getInboxTag().put(tag);
////					return;
////				}
////				Without considering the order and existence of transactions.
////				ArrayList<Transaction> txns = new ArrayList<Transaction>();
////				for(int i = 0; i < tag.getNum(); i ++) {
////					txns.add(getPoolTxn().take());
////				}
////				Generate a block and add it to the blockchain.
////				Note that the argument addressTable of Block() should be null because we do not broadcast it but a Tag.
////				getBlockchain().put(new Block(txns, null, this));
//				getBlockchain().put(tag.getBlock());
////				Generate a Tag and put it into outboxTag.
//				getOutboxTag().put(tag);
//				return;
//			}
//			else if(Thread.currentThread() == timeout) {
//				if(Configurator.poolTxns.size() >= (Integer.valueOf(Configurator.params.get("n")) * threshold)) {
//					return;
//				}
//			}
//			
//			int half = Integer.valueOf(Configurator.params.get("n")) * (int)Math.ceil(threshold / 2); // (int)(Math.ceil(Integer.valueOf(Configurator.params.get("n")) / 2d));
//			if(Configurator.poolTxns.size() < half) {
//				half = Configurator.poolTxns.size();
//			}
//			if(half == 0) { //When timeout thread runs, no left transaction will turn off it.
//				return;
//			}
////			System.out.println(getId() + ":" + Selectors.selectLeaderNode(getPoolTxn(), half));
////			System.out.println(half);
////			if(getId() == Selectors.selectLeaderNode(Configurator.poolTxns, half)) {
//			if(getId() == 0) { //For simulation, let the leader node always be the node number 0.
////				Get the first half of the transaction pool.
//				ArrayList<Transaction> txns = new ArrayList<Transaction>();
//				for(int i = 0; i < half; i ++) {
//					txns.add(Configurator.poolTxns.take());
//				}
////				Generate a block and add it to the blockchain.
////				Note that the argument addressTable of Block() should be null because we do not broadcast it but a Tag.
//				Block block =new Block(txns, null, this);
//				getBlockchain().put(block);
////				Generate a Tag and put it into outboxTag.
//				@SuppressWarnings("unchecked")
//				ArrayList<Node> addressTable = (ArrayList<Node>)Cloner.skinClone(Configurator.nodes);
//				getOutboxTag().put(new Tag(half, size_tag, addressTable, this, block));
//			}
//		}
//		catch(InterruptedException e) {
//			e.printStackTrace();
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	@Override
//	public void run() {
//		try {
//			while(true) {
//				if(Thread.currentThread() == timeout) {
////					Thread.sleep(to);
////					When timeout is over, pack a block. 
//					if(! Configurator.poolTxns.isEmpty()) {
//						if(Configurator.poolTxns.size() < (Integer.valueOf(Configurator.params.get("n")) * threshold)) {
//							packingBlock(null);
//						}
//					}
//				}
//				else if(Thread.currentThread() == inboxTxnChecker){
////					Check inboxTxn
//					if(! getInboxTxn().isEmpty()) {
//						Transaction txn = getInboxTxn().take();
////						If the sent transaction comes back, put it into the transaction pool, otherwise, forward it.
//						if(txn.getGetherer().getId() != getId()) {
////							getPoolTxn().put(txn);
//							getOutboxTxn().put(txn);
//						}
//						else {
//							Configurator.poolTxns.put(txn);
//						}
//					}
//				}
//				else if(Thread.currentThread() == outboxTxnChecker) {
////					Check outboxTxn
//					if(! getOutboxTxn().isEmpty()) {
//						Transaction txn = getOutboxTxn().take();
////						if(txn.getGetherer().getId() == getId()) {
////							getPoolTxn().put(txn);
////						}
//						Configurator.threadPool.submit(new Transferrer(txn, null, null, 1));
//					}
//				}
//				else if(Thread.currentThread() == inboxTagChecker) {
////					Check inboxTag
//					if(! getInboxTag().isEmpty()) {
//						Tag tag = getInboxTag().take();
//						if(tag.getNode().getId() == getId()) {
////							The tag has got its final destination, which indicates that the transactions it contains has been completed.
//							for(int i = 0; i < tag.getBlock().getTxns().size(); i ++) {
//								tag.getBlock().getTxns().get(i).setEndTime(System.nanoTime());
//								Configurator.scheduleTxns.incrementAndGet();
//							}
////							Set end time for the whole test.
//							if(Configurator.scheduleTxns.intValue() == Integer.valueOf(Configurator.params.get("txns"))) {
//								Configurator.endTime = System.nanoTime();
//								Reportor.report();
//							}
//							else if((Integer.valueOf(Configurator.params.get("txns")) - Configurator.scheduleTxns.intValue()) <  (Integer.valueOf(Configurator.params.get("n")) * threshold)) {
//								if(timeout.getState() != Thread.State.RUNNABLE) {
//									timeout.start();
//								}
//							}
//						}
//						else {
//							packingBlock(tag);
//						}
//					}
//				}
//				else if(Thread.currentThread() == outboxTagChecker) {
////					Check outboxTag
//					if(! getOutboxTag().isEmpty()) {
//						Tag tag = getOutboxTag().take();
//						Configurator.threadPool.submit(new Transferrer(null, tag, null, 2));
//					}
//				}
//				else if(Thread.currentThread() == poolTxnChecker) {
////					Check poolTxn 
//					if(Configurator.poolTxns.size() >= (Integer.valueOf(Configurator.params.get("n")) * threshold)) {
//						packingBlock(null);
//					}
//				}
//			}
//		}
//		catch(InterruptedException e) {
//			e.printStackTrace();
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
	/*v1-end*/
}
