import entities.NodeMSigBFT;
import entities.Packer;
import entities.TraderMSigBFT;
import tools.Configurator;

/**
 * MSig-BFT
 */

/**
 * @author Liang Wang
 *
 */
public class MSigBFT {

	public MSigBFT() {
		Configurator.protocolName = "MSigBFT";
		Configurator.params = Configurator.readParameters();
//		Convert tr from Mbps to b/s
		double tr = Double.valueOf(Configurator.params.get("tr")) * 1024d * 1024d / 8d;
		Configurator.params.put("tr", String.valueOf(tr));
		
//		Create nodes
		for(int i = 0; i < Integer.valueOf(Configurator.params.get("n")); i ++) {
			Configurator.nodes.add(new NodeMSigBFT(i));
		}
	}
	
	public static void main(String[] args) {
//		Initialization
		new MSigBFT();
//		Print parameters
		for(String key : Configurator.params.keySet()) {
			System.out.println(key + "=" + Configurator.params.get(key));
		}
		System.out.println("-----MSigBFT-----");
//		Get all nodes to work.
		for(int i = 0; i <Configurator.nodes.size(); i ++) {
			((NodeMSigBFT)Configurator.nodes.get(i)).startWork();
		}
//		Get the Trader to work, which means that start to generate transactions.
		new TraderMSigBFT();
//		Get the Packer to work, which means that start to collect transaction for packing blocks.
		new Packer();
//		Timer start
		Configurator.startTime = System.nanoTime();
	}
}
