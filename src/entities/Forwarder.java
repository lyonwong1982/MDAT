/**
 * Forwarder has the same functionality with Transferrer but is more flexible.
 */
package entities;

import tools.Configurator;

/**
 * @author Liang Wang
 *
 */
public class Forwarder implements Runnable {
	private Object o;
	private Node dest;
	public Forwarder(Object o, Node dest) {
		try {
			if(o == null || dest == null) {
				throw new Exception("Wrong argument(s)!");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		this.o = o;
		this.dest = dest;
	}

	@Override
	public void run() {
		try {
//			Compute the delay
			double millis_d;
			long millis;
			int nanos;
			if(o.getClass() == Transaction.class || o.getClass() == TransactionSBFT.class || o.getClass() == TransactionMSigBFT.class) {
				millis_d = ((Transaction)o).getSize_txn() * 1000d / Double.valueOf(Configurator.params.get("tr"));
			}
			else if(o.getClass() == Tag.class) {
				millis_d = ((Tag)o).getSize_tag() * 1000d / Double.valueOf(Configurator.params.get("tr"));
			}
			else {
				millis_d = ((Block)o).getSize() * 1000d / Double.valueOf(Configurator.params.get("tr"));
			}
			millis = (long)Math.floor(millis_d);
			nanos = (int)Math.round((millis_d - millis) * 1000000d);
			Thread.sleep(millis, nanos);

//			Forward
			if(o.getClass() == Transaction.class || o.getClass() == TransactionSBFT.class || o.getClass() == TransactionMSigBFT.class) {
				dest.getInboxTxn().put((Transaction)o);
			}
			else if(o.getClass() == Tag.class) {
				dest.getInboxTag().put((Tag)o);
			}
			else {
				dest.getInboxBlock().put((Block)o);
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
