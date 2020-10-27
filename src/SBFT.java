import entities.NodeSBFT;
import entities.Packer;
import entities.TraderSBFT;
import tools.Configurator;

/**
 * Scalable Byzantine Fault Tolerance
 */

/**
 * @author Liang Wang
 *
 */
public class SBFT {
	
	public SBFT() {
		Configurator.protocolName = "SBFT";
		Configurator.params = Configurator.readParameters();
//		Convert tr from Mbps to b/s
		double tr = Double.valueOf(Configurator.params.get("tr")) * 1024d * 1024d / 8d;
		Configurator.params.put("tr", String.valueOf(tr));
		
//		Create nodes
		for(int i = 0; i < Integer.valueOf(Configurator.params.get("n")); i ++) {
			Configurator.nodes.add(new NodeSBFT(i));
		}
	}
	
	public static void main(String[] args) {
//		Initialization
		new SBFT();
//		Print parameters
		for(String key : Configurator.params.keySet()) {
			System.out.println(key + "=" + Configurator.params.get(key));
		}
		System.out.println("-----SBFT-----");
//		Get all nodes to work.
		for(int i = 0; i <Configurator.nodes.size(); i ++) {
			((NodeSBFT)Configurator.nodes.get(i)).startWork();
		}
//		Get the Trader to work, which means that start to generate transactions.
		new TraderSBFT(Configurator.nodes, 
				Long.valueOf(Configurator.params.get("size_txn")),
				Integer.valueOf(Configurator.params.get("txns")),
				Configurator.params.get("rd"));
//		Get the Packer to work, which means that start to collect transaction for packing blocks.
		new Packer();
//		Timer start
		Configurator.startTime = System.nanoTime();
		
//		Runnable r1 = ()->{
//			while(true) {
//				try {
//					Thread.sleep(2000);
//				}catch(InterruptedException e) {
//					e.printStackTrace();
//				}
//				System.out.println(Configurator.scheduleTxns.intValue() + " / " + Configurator.params.get("txns"));
//				System.out.println(Configurator.poolTxns.size() + "->" + Configurator.blockchain.size());
//			}
//		};
//		new Thread(r1).start();
	}
}
