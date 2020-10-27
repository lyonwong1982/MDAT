/**
 * Trader is responsible for generating transactions.
 */
package entities;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import functions.Selectors;
import tools.Cloner;
import tools.Configurator;
import tools.RandTools;

/**
 * @author Liang Wang
 *
 */
public class Trader implements Runnable {
	private ArrayList<Node> nodes;
	private long size_txn;
	private long rd;
	private int txns;
	/**
	 * Initiate and start a Trader.
	 * @param nodes The target list of nodes.
	 * @param size_txn The average size of each new transaction.
	 * @param txns The maximum number of transaction to be created.
	 * @param rd The random delay of creating a new transaction.
	 */
	public Trader(ArrayList<Node> nodes, long size_txn, int txns, String rd) {
		this.nodes = nodes;
		this.size_txn = size_txn;
		this.txns = txns;
		int rd_start = Integer.valueOf(rd.substring(0, rd.indexOf("-")));
		int rd_end = Integer.valueOf(rd.substring(rd.indexOf("-") + 1));
		this.rd = RandTools.randomInt(rd_start, rd_end);
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			for(int i = 0; i < txns; i ++) {
//				Pick a node randomly.
				Node getherer = Selectors.selectOneNode(nodes);
//				Get an address table.
				@SuppressWarnings("unchecked")
				ArrayList<Node> addressTable = (ArrayList<Node>)Cloner.skinClone(nodes);
//				Make a transaction.
				Transaction txn = new Transaction(getherer, addressTable, size_txn);
//				Simulate the delay.
				Thread.sleep(rd);
//				For some protocols such as PBFT
				Configurator.bufferPoolTxns.put(txn, new AtomicInteger());
//				Set the start time to the new transaction.
				txn.setStartTime(System.nanoTime());
//				Put the new transaction into the getherer's outbox.
				getherer.getOutboxTxn().put(txn);
				Configurator.statTxns.put(txn);
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
