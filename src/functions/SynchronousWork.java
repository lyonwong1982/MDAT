/**
 * Synchronous Work
 */
package functions;

import entities.Block;
import entities.Node;
import entities.Transaction;
import tools.Configurator;
import tools.Reportor;

/**
 * @author Liang Wang
 *
 */
public class SynchronousWork {
	public synchronized static void packing(Block b) {
		if(! Configurator.blockchain.contains(b)) {
//			Confirm the transactions in a block.
			Configurator.blockchain.put(b);
//			System.out.println(Configurator.blockchain.size());
			for(int i = 0; i < b.getTxns().size(); i ++) {
				b.getTxns().get(i).setEndTime(System.nanoTime());
				Configurator.scheduleTxns.incrementAndGet();
			}
//			Check progress
			System.out.println(Configurator.scheduleTxns.intValue() + " / " + Configurator.params.get("txns"));
			System.out.println(Configurator.blockchain.size());
//			Finish the program
			if(Configurator.scheduleTxns.intValue() == Integer.valueOf(Configurator.params.get("txns"))) {
				Configurator.endTime = System.nanoTime();
				Reportor.report();
			}
		}
	}
	
	public synchronized static void pooling(Transaction txn, Node n) {
		if(txn.getGetherer() != null) {
			return;
		}
		else {
			txn.setGetherer(n);
		}
		if(! Configurator.poolTxns.contains(txn)) {
			Configurator.poolTxns.put(txn);
//			System.out.println(Configurator.poolTxns.size());
		}
	}
}
