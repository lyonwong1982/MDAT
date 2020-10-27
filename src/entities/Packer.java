/**
 * Packer has similar functions as Coordinator, but is used for SBFT with some special functionalities.
 */
package entities;

import java.util.ArrayList;

import functions.Selectors;
import tools.Cloner;
import tools.Configurator;

/**
 * @author Liang Wang
 *
 */
public class Packer implements Runnable {
	private ArrayList<Node> nodes;
	private long to;

	public Packer() {
		nodes = Configurator.nodes;
		to = Long.valueOf(Configurator.params.get("to"));
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				Thread.sleep(to);
//				packing block
				if(! Configurator.poolTxns.isEmpty()) {
					ArrayList<Transaction> txns = new ArrayList<Transaction>();
					while(! Configurator.poolTxns.isEmpty()) {
						txns.add(Configurator.poolTxns.take());
					}
					Node dest = null;
					Block block = null;
					if(Configurator.protocolName == "SBFT") {
						dest = Selectors.selectOneNode(nodes);
						block = new BlockSBFT(txns, null, dest);
					}
					else if(Configurator.protocolName == "MSigBFT") {
						@SuppressWarnings("unchecked")
						ArrayList<Node> addressTable = (ArrayList<Node>)Cloner.skinClone(nodes);
						block = new BlockMSigBFT(txns, addressTable);
						dest = ((BlockMSigBFT)block).getLeader();
					}
					dest.getOutboxBlock().put(block);
//					System.out.println("www"+dest.getId());
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
