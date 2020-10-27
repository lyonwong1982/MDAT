import entities.NodeCCP;
import entities.Trader;
import tools.Configurator;

/**
 * Cascading Consensus Protocol
 */

/**
 * @author Liang Wang
 *
 */
public class CCP {
	/**
	 * Initialization
	 */
	public CCP() {
		Configurator.protocolName = "CCP";
		Configurator.params = Configurator.readParameters();
//		Convert tr from Mbps to b/s
		double tr = Double.valueOf(Configurator.params.get("tr")) * 1024d * 1024d / 8d;
		Configurator.params.put("tr", String.valueOf(tr));
		
//		Create nodes
		for(int i = 0; i < Integer.valueOf(Configurator.params.get("n")); i ++) {
			Configurator.nodes.add(
					new NodeCCP(i, 
					Long.valueOf(Configurator.params.get("to")),
					Integer.valueOf(Configurator.params.get("size_tag")))
					);
		}
	}
	
	public static void main(String[] args) {
//		Initialization
		new CCP();
//		Print parameters
		for(String key : Configurator.params.keySet()) {
			System.out.println(key + "=" + Configurator.params.get(key));
		}
		System.out.println("-----CCP-----");
//		Get all nodes to work.
		for(int i = 0; i <Configurator.nodes.size(); i ++) {
			((NodeCCP)Configurator.nodes.get(i)).startWork();
		}
//		Get the Trader to work, which means that start to generate transactions.
		new Trader(Configurator.nodes, 
				Long.valueOf(Configurator.params.get("size_txn")),
				Integer.valueOf(Configurator.params.get("txns")),
				Configurator.params.get("rd"));
//		Timer start
		Configurator.startTime = System.nanoTime();
//		See progress
//		Runnable r1 = ()->{
//			while(true) {
//				try {
//					Thread.sleep(3000);
//				}catch(InterruptedException e) {
//					e.printStackTrace();
//				}
//				System.out.println(Configurator.scheduleTxns.intValue() + " / " + Configurator.params.get("txns"));
//				System.out.println(Configurator.poolTxns.size());
////				Reportor.report();
//			}
//		};
//		new Thread(r1).start();
	}
}
