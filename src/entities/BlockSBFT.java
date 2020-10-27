/**
 * 
 */
package entities;

import java.util.ArrayList;

import functions.Selectors;
import tools.Configurator;

/**
 * @author Liang Wang
 *
 */
public class BlockSBFT extends Block {
	private Node c_Collector;
	private Node e_Collector;

	public BlockSBFT(ArrayList<Transaction> txns, ArrayList<Node> addressTable, Node node) {
		super(txns, addressTable, node);
		c_Collector = Selectors.selectOneNode(Configurator.nodes);
		e_Collector = Selectors.selectOneDistinctNodeWithoutRm(Configurator.nodes, c_Collector);
	}

	public Node getC_Collector() {return c_Collector;}
	public Node getE_Collector() {return e_Collector;}
}
