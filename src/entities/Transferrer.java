/**
 * Transferrer
 */
package entities;


import functions.Selectors;
import tools.Configurator;

/**
 * @author Liang Wang
 *
 */
public class Transferrer implements Runnable{
	private Transaction txn;
	private Tag tag;
	private Block block;
	private int type;
	/**
	 * Initiate a transferrer for a transaction, a tag, or a block.
	 * @param txn Transaction.
	 * @param tag Tag.
	 * @param block Block.
	 * @param type 1: transaction, 2: tag, and 3: block.
	 */
	public Transferrer(Transaction txn, Tag tag, Block block, int type) {
		try {
			if(type != 1 && type != 2 && type != 3) {
				throw new Exception("Wrong argument(s)!");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		this.txn = txn;
		this.tag = tag;
		this.block = block;
		this.type = type;
//		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
//			Compute the delay
			double millis_d;
			long millis;
			int nanos;
			if(type == 1) {
				millis_d = txn.getSize_txn() * 1000d / Double.valueOf(Configurator.params.get("tr"));
			}
			else if(type == 2) {
				millis_d = tag.getSize_tag() * 1000d / Double.valueOf(Configurator.params.get("tr"));
			}
			else {
				millis_d = block.getSize() * 1000d / Double.valueOf(Configurator.params.get("tr"));
			}
			millis = (long)Math.floor(millis_d);
			nanos = (int)Math.round((millis_d - millis) * 1000000d);
			Thread.sleep(millis, nanos);
			
//			Transfer
			if(type == 1) {
				Node node = Selectors.selectOneDistinctNode(txn.getAddressTable(), txn.getGetherer());
				node.getInboxTxn().put(txn);
			}
			else if(type == 2) {
				Node node = Selectors.selectOneDistinctNode(tag.getAddressTable(), tag.getNode());
				node.getInboxTag().put(tag);
			}
			else {
				Node node = Selectors.selectOneDistinctNode(block.getAddressTable(), block.getNode());
				node.getInboxBlock().put(block);
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
