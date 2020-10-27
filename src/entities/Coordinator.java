/**
 * Coordinator is used for packing transactions into blocks in some protocols such as PBFT
 */
package entities;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import functions.Selectors;
import tools.Configurator;
import tools.Reportor;


/**
 * @author Liang Wang
 *
 */
public class Coordinator implements Runnable {
	private ArrayList<Node> nodes;
	private long to;
	private Thread timeout;

	public Coordinator() {
		nodes = Configurator.nodes;
		to = Long.valueOf(Configurator.params.get("to"));
		timeout = new Thread(this);
		timeout.start();
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				if(Thread.currentThread() == timeout) {
					Thread.sleep(to);
//					Packing block
					if(! Configurator.poolTxns.isEmpty()) {
						ArrayList<Transaction> txns = new ArrayList<Transaction>();
						while(! Configurator.poolTxns.isEmpty()) {
							txns.add(Configurator.poolTxns.take());
						}
						Node dest = Selectors.selectOneNode(nodes);
						Block block = new Block(txns, null, dest);
						Configurator.bufferBlockchain.put(block, new AtomicInteger());
						dest.getOutboxBlock().put(block);
					}
				}
				else {
//					Confirm transactions
					for(Entry<Transaction, AtomicInteger> entry : Configurator.bufferPoolTxns.entrySet()) {
						if(entry.getValue().intValue() >= Integer.valueOf(Configurator.params.get("n"))) {
							Configurator.poolTxns.put(entry.getKey());
//							entry.setValue(new AtomicInteger());
							entry.getValue().set(0);
//							Configurator.bufferPoolTxns.put(entry.getKey(), 0);
						}
					}
//					Confirm blocks
					for(Entry<Block, AtomicInteger> entry : Configurator.bufferBlockchain.entrySet()) {
						if(entry.getValue().intValue() >= Integer.valueOf(Configurator.params.get("n"))) {
							Configurator.blockchain.put(entry.getKey());
							entry.getValue().set(0);
//							Configurator.bufferBlockchain.put(entry.getKey(), 0);
							for(int i = 0; i < entry.getKey().getTxns().size(); i ++) {
								entry.getKey().getTxns().get(i).setEndTime(System.nanoTime());
//								System.out.println(entry.getKey().getTxns().get(i).latency());
								Configurator.scheduleTxns.incrementAndGet();
							}
//							Check progress
							System.out.println(Configurator.scheduleTxns.intValue() + " / " + Configurator.params.get("txns"));
							System.out.println(
									Configurator.bufferPoolTxns.size() + "->" +
									Configurator.poolTxns.size() + "->" + 
									Configurator.bufferBlockchain.size() + "->" +
									Configurator.blockchain.size());
									
							if(Configurator.scheduleTxns.intValue() == Integer.valueOf(Configurator.params.get("txns"))) {
								Configurator.endTime = System.nanoTime();
								Reportor.report();
							}
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

}
