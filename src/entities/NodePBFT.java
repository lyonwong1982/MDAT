/**
 * Node for PBFT
 */
package entities;

import java.util.concurrent.ConcurrentHashMap;

import tools.Configurator;

/**
 * @author Liang Wang
 *
 */
public class NodePBFT extends Node implements Runnable {
	private ConcurrentHashMap<Transaction, Integer> bufferInboxTxn;
	private ConcurrentHashMap<Block, Integer> bufferInboxBlock;
	public NodePBFT(int id) {
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
//					System.out.println(getId()+ ":::" + txn.getId());
					if(bufferInboxTxn.get(txn) == 1) {
//						Forward for pre-prepare phase
						if(txn.getGetherer() != this) {
							getOutboxTxn().put(txn);
						}
					}
					else if(bufferInboxTxn.get(txn) == (Integer.valueOf(Configurator.params.get("n")) - 1)) {
//						Forward for prepare phase
						getOutboxTxn().put(txn);
					}
					else if(bufferInboxTxn.get(txn) >= ((Integer.valueOf(Configurator.params.get("n")) - 1) * 2)) {
//						Reply transaction
						bufferInboxTxn.remove(txn);
						Configurator.bufferPoolTxns.get(txn).incrementAndGet();
//						System.out.println(getId()+ ":::" + txn.getId());
//						System.out.println(Configurator.bufferPoolTxns.get(txn).intValue());
					}
				}
				
//				Check outboxTxn
				if(! getOutboxTxn().isEmpty()) {
					Transaction txn = getOutboxTxn().take();
//					if(! bufferInboxTxn.contains(txn)) {
//						bufferInboxTxn.put(txn, 0);
//					}
					for(int i = 0; i < Configurator.nodes.size(); i ++) {
						if(Configurator.nodes.get(i) == this) {
							continue;
						}
						Configurator.threadPool.submit(new Forwarder(txn, Configurator.nodes.get(i)));
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
					
//					if(bufferInboxBlock.get(b) == 18) {
//						System.out.println(b.getId()+ ":::" + bufferInboxBlock.get(b) + ":::" + this.getId());
//					}
//					System.out.println(getId()+ ":::" + b.getId());
					
					if(bufferInboxBlock.get(b) == 1) {
//						Forward for pre-prepare phase
						if(b.getNode() != this) {
							getOutboxBlock().put(b);
						}
					}
					else if(bufferInboxBlock.get(b) == (Integer.valueOf(Configurator.params.get("n")) - 1)) {
//						Forward for prepare phase
						getOutboxBlock().put(b);
					}
					else if(bufferInboxBlock.get(b) >= ((Integer.valueOf(Configurator.params.get("n")) - 1) * 2)) {
//						Reply transaction
						bufferInboxBlock.remove(b);
						Configurator.bufferBlockchain.get(b).incrementAndGet();
					}
				}

//				Check outboxBlock
				if(! getOutboxBlock().isEmpty()) {
					Block b = getOutboxBlock().take();
//					if(! bufferInboxBlock.contains(b)) {
//						bufferInboxBlock.put(b, 0);
//					}
					for(int i = 0; i < Configurator.nodes.size(); i ++) {
						if(Configurator.nodes.get(i) == this) {
							continue;
						}
						Configurator.threadPool.submit(new Forwarder(b, Configurator.nodes.get(i)));
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
