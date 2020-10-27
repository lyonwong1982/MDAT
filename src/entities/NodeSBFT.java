/**
 * Node for SBFT
 */
package entities;

import java.util.concurrent.ConcurrentHashMap;

import tools.Configurator;
import tools.Reportor;

/**
 * @author Liang Wang
 *
 */
public class NodeSBFT extends Node implements Runnable {
	private ConcurrentHashMap<Transaction, Integer> bufferInboxTxn;
	private ConcurrentHashMap<Block, Integer> bufferInboxBlock;
	
	public NodeSBFT(int id) {
		super(id);
		bufferInboxTxn = new ConcurrentHashMap<Transaction, Integer>();
		bufferInboxBlock = new ConcurrentHashMap<Block, Integer>();
	}
	
	public void startWork() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			while(true) {
//				Check inboxTxn
				if(! getInboxTxn().isEmpty()) {
					Transaction txn = getInboxTxn().take();
					if(bufferInboxTxn.containsKey(txn)) {
						bufferInboxTxn.put(txn, bufferInboxTxn.get(txn) + 1);
					}
					else {
						bufferInboxTxn.put(txn, 1);
					}
//					System.out.println(bufferInboxTxn.get(txn));
					if(bufferInboxTxn.get(txn) == 1) {
//						Send to C_Collector
						Configurator.threadPool.submit(new Forwarder(txn, ((TransactionSBFT)txn).getC_Collector()));
					}
					else if(bufferInboxTxn.get(txn) == 2) {
						if(((TransactionSBFT)txn).getC_Collector() != this) {
//							Send to E_Collector
							Configurator.threadPool.submit(new Forwarder(txn, ((TransactionSBFT)txn).getE_Collector()));
						}
					}
					else if(bufferInboxTxn.get(txn) == (Integer.valueOf(Configurator.params.get("n")) + 1)) {
						if(((TransactionSBFT)txn).getC_Collector() == this) {
//							Put to outboxTxn, and send to others afterward
							getOutboxTxn().put(txn);
						}
					}
					else if(bufferInboxTxn.get(txn) == (Integer.valueOf(Configurator.params.get("n")) + 2)) {
						if(((TransactionSBFT)txn).getC_Collector() == this) {
//							C_Collector sends the transaction to E_Collector
							Configurator.threadPool.submit(new Forwarder(txn, ((TransactionSBFT)txn).getE_Collector()));
						}
						else if(((TransactionSBFT)txn).getE_Collector() == this) {
//							Submit transaction to public poolTxn
							Configurator.poolTxns.put(txn);
						}
					}
				}
//				Check outboxTxn
				if(! getOutboxTxn().isEmpty()) {
					Transaction txn = getOutboxTxn().take();
					for(int i = 0; i < Configurator.nodes.size(); i ++) {
						Configurator.threadPool.submit(new Forwarder(txn, Configurator.nodes.get(i)));
//						System.out.println("here");
					}
				}
//				Check inboxBlock
				if(! getInboxBlock().isEmpty()) {
					Block b = getInboxBlock().take();
					if(bufferInboxBlock.containsKey(b)) {
						bufferInboxBlock.put(b, bufferInboxBlock.get(b) + 1);
					}
					else {
						bufferInboxBlock.put(b, 1);
					}
					if(bufferInboxBlock.get(b) == 1) {
//						Send to C_Collector
						Configurator.threadPool.submit(new Forwarder(b, ((BlockSBFT)b).getC_Collector()));
					}
					else if(bufferInboxBlock.get(b) == 2) {
						if(((BlockSBFT)b).getC_Collector() != this) {
//							Send to E_Collector
							Configurator.threadPool.submit(new Forwarder(b, ((BlockSBFT)b).getE_Collector()));
						}
					}
					else if(bufferInboxBlock.get(b) == (Integer.valueOf(Configurator.params.get("n")) + 1)) {
						if(((BlockSBFT)b).getC_Collector() == this) {
//							Put to outboxTxn, and send to others afterward
							getOutboxBlock().put(b);
						}
					}
					else if(bufferInboxBlock.get(b) == (Integer.valueOf(Configurator.params.get("n")) + 2)) {
						if(((BlockSBFT)b).getC_Collector() == this) {
//							C_Collector sends the transaction to E_Collector
							Configurator.threadPool.submit(new Forwarder(b, ((BlockSBFT)b).getE_Collector()));
						}
						else if(((BlockSBFT)b).getE_Collector() == this) {
							Configurator.blockchain.put(b);
							for(int i = 0; i < b.getTxns().size(); i ++) {
								b.getTxns().get(i).setEndTime(System.nanoTime());
								Configurator.scheduleTxns.incrementAndGet();
							}
//							Check progress
							System.out.println(Configurator.scheduleTxns.intValue() + " / " + Configurator.params.get("txns"));
							System.out.println(Configurator.blockchain.size());
									
							if(Configurator.scheduleTxns.intValue() == Integer.valueOf(Configurator.params.get("txns"))) {
								Configurator.endTime = System.nanoTime();
								Reportor.report();
							}
						}
					}
				}
//				Check outboxBlock
				if(! getOutboxBlock().isEmpty()) {
					Block b = getOutboxBlock().take();
					for(int i = 0; i < Configurator.nodes.size(); i ++) {
						Configurator.threadPool.submit(new Forwarder(b, Configurator.nodes.get(i)));
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
}
