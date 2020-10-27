/**
 * Trader for MSigBFT. This class is similar to Trader, but has tiny differences.
 */
package entities;

import java.util.ArrayList;

import tools.Cloner;
import tools.Configurator;
import tools.RandTools;

/**
 * @author Liang Wang
 *
 */
public class TraderMSigBFT implements Runnable {
	private ArrayList<Node> nodes;
	private long size_txn;
	private long rd;
	private int txns;
	
	public TraderMSigBFT() {
		this.nodes = Configurator.nodes;
		this.size_txn = Long.valueOf(Configurator.params.get("size_txn"));
		this.txns = Integer.valueOf(Configurator.params.get("txns"));
		String rd = Configurator.params.get("rd");
		int rd_start = Integer.valueOf(rd.substring(0, rd.indexOf("-")));
		int rd_end = Integer.valueOf(rd.substring(rd.indexOf("-") + 1));
		this.rd = RandTools.randomInt(rd_start, rd_end);
		new Thread(this).start();
	}
	@Override
	public void run() {
		try {
			for(int i = 0; i < txns; i ++) {
//				Get an address table.
				@SuppressWarnings("unchecked")
				ArrayList<Node> addressTable = (ArrayList<Node>)Cloner.skinClone(nodes);
//				Make a transaction.
				Transaction txn = new TransactionMSigBFT(addressTable, size_txn);
//				Simulate the delay.
				Thread.sleep(rd);
//				Set the start time to the new transaction.
				txn.setStartTime(System.nanoTime());
//				Put the new transaction into the leader's outbox.
				((TransactionMSigBFT)txn).getLeader().getOutboxTxn().put(txn);
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
