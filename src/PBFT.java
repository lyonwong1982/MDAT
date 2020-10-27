import entities.Coordinator;
import entities.NodePBFT;
import entities.Trader;
import tools.Configurator;

/**
 * Practical Byzantine Fault Tolerance
 */

/**
 * @author Liang Wang
 *
 */
public class PBFT {
	/**
	 * Initialization
	 */
	public PBFT() {
		Configurator.protocolName = "PBFT";
		Configurator.params = Configurator.readParameters();
//		Convert tr from Mbps to b/s
		double tr = Double.valueOf(Configurator.params.get("tr")) * 1024d * 1024d / 8d;
		Configurator.params.put("tr", String.valueOf(tr));
		
//		Create nodes
		for(int i = 0; i < Integer.valueOf(Configurator.params.get("n")); i ++) {
			Configurator.nodes.add(new NodePBFT(i));
		}
	}
	
	public static void main(String[] args) {
//		Initialization
		new PBFT();
//		Print parameters
		for(String key : Configurator.params.keySet()) {
			System.out.println(key + "=" + Configurator.params.get(key));
		}
		System.out.println("-----PBFT-----");
//		Get all nodes to work.
		for(int i = 0; i <Configurator.nodes.size(); i ++) {
			((NodePBFT)Configurator.nodes.get(i)).startWork();
		}
//		Get the Trader to work, which means that start to generate transactions.
		new Trader(Configurator.nodes, 
				Long.valueOf(Configurator.params.get("size_txn")),
				Integer.valueOf(Configurator.params.get("txns")),
				Configurator.params.get("rd"));
//		Get the Coordinator to work, which means that start to collect transaction for packing blocks.
		new Coordinator();
//		Timer start
		Configurator.startTime = System.nanoTime();
//		See progress
//		Runnable r1 = ()->{
//			while(true) {
//				try {
//					Thread.sleep(2000);
//				}catch(InterruptedException e) {
//					e.printStackTrace();
//				}
//				System.out.println(Configurator.scheduleTxns.intValue() + " / " + Configurator.params.get("txns"));
//				System.out.println(
//						Configurator.bufferPoolTxns.size() + "->" +
//						Configurator.poolTxns.size() + "->" + 
//						Configurator.bufferBlockchain.size() + "->" +
////						Configurator.bufferBlockchain.get(Configurator.bufferBlockchain.keys().nextElement()) + "->" +
//						Configurator.blockchain.size());
//			}
//		};
//		new Thread(r1).start();
	}
}
