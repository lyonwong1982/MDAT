/**
 * Node for MSig-BFT
 */
package entities;

import java.util.concurrent.ConcurrentHashMap;

import tools.Configurator;
import tools.Reportor;

/**
 * @author Liang Wang
 *
 */
public class NodeMSigBFT extends Node implements Runnable {
	private ConcurrentHashMap<Transaction, Integer> bufferInboxTxn;
	private ConcurrentHashMap<Block, Integer> bufferInboxBlock;
	
	public NodeMSigBFT(int id) {
		super(id);
		bufferInboxTxn = new ConcurrentHashMap<Transaction, Integer>();
		bufferInboxBlock = new ConcurrentHashMap<Block, Integer>();
	}
	
	public void startWork() {
		new Thread(this).start();
	}
	
	@Override
	public void run(){
		try {
			while(true) {
//				Check inboxTxn
				if(! getInboxTxn().isEmpty()) {
					Transaction txn = getInboxTxn().take();
					if(((TransactionMSigBFT)txn).getWitness() == this) {
//						If current node is the witness, then prepare to broadcast the transaction.
						getOutboxTxn().put(txn);
					}
					else {
//						Count the votes.
						if(bufferInboxTxn.containsKey(txn)) {
							bufferInboxTxn.put(txn, bufferInboxTxn.get(txn) + 1);
						}
						else {
							bufferInboxTxn.put(txn, 1);
						}
//						When collect enough votes, submit the transaction to transaction pool.
						if(bufferInboxTxn.get(txn) == 1) {
							getOutboxTxn().put(txn);
						}
						else if(bufferInboxTxn.get(txn) >= (Integer.valueOf(Configurator.params.get("n")) - 2)) {
							if(txn.getGetherer() == this) {
//								System.out.println("***"+bufferInboxTxn.get(txn));
								Configurator.poolTxns.put(txn);
							}
						}
					}
				}
//				Check outboxTxn
				if(! getOutboxTxn().isEmpty()) {
					Transaction txn = getOutboxTxn().take();
					if(((TransactionMSigBFT)txn).getLeader() == this) {
						Configurator.threadPool.submit(new Forwarder(txn, ((TransactionMSigBFT)txn).getWitness()));
					}
					else {
						for(int i = 0; i < txn.getAddressTable().size(); i ++) {
							if(txn.getAddressTable().get(i) != this) {
								Configurator.threadPool.submit(new Forwarder(txn, txn.getAddressTable().get(i)));
							}
						}
					}
				}
//				Check inboxBlock
				if(! getInboxBlock().isEmpty()) {
					Block b = getInboxBlock().take();

					if(((BlockMSigBFT)b).getWitness() == this) {
//						If current node is the witness, then prepare to broadcast the block.
						getOutboxBlock().put(b);
					}
					else {
//						Count the votes.
						if(bufferInboxBlock.containsKey(b)) {
							bufferInboxBlock.put(b, bufferInboxBlock.get(b) + 1);
						}
						else {
							bufferInboxBlock.put(b, 1);
						}
//						When collect enough votes, append the block to the blockchain, and count the number of transactions.
						if(bufferInboxBlock.get(b) == 1) {
							getOutboxBlock().put(b);
						}
						if(bufferInboxBlock.get(b) >= (Integer.valueOf(Configurator.params.get("n")) - 2)) {
							if(b.getNode() == this) {
//								Confirm the transactions in a block.
								Configurator.blockchain.put(b);
//								System.out.println(Configurator.blockchain.size());
								for(int i = 0; i < b.getTxns().size(); i ++) {
									b.getTxns().get(i).setEndTime(System.nanoTime());
									Configurator.scheduleTxns.incrementAndGet();
								}
//								Check progress
								System.out.println(Configurator.scheduleTxns.intValue() + " / " + Configurator.params.get("txns"));
								System.out.println(Configurator.blockchain.size());
//								Finish the program
								if(Configurator.scheduleTxns.intValue() == Integer.valueOf(Configurator.params.get("txns"))) {
									Configurator.endTime = System.nanoTime();
									Reportor.report();
								}
							}
						}
					}
				}
//				Check outboxBlock
				if(! getOutboxBlock().isEmpty()) {
					Block b = getOutboxBlock().take();
					if(((BlockMSigBFT)b).getLeader() == this) {
						Configurator.threadPool.submit(new Forwarder(b, ((BlockMSigBFT)b).getWitness()));
//						System.out.println("OOKK");
					}
					else {
						for(int i = 0; i < b.getAddressTable().size(); i ++) {
							if(b.getAddressTable().get(i) != this) {
								Configurator.threadPool.submit(new Forwarder(b, b.getAddressTable().get(i)));
//								System.out.println("yesyes");
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
