/**
 * Transaction for SBFT
 */
package entities;

import java.util.ArrayList;

import functions.Selectors;
import tools.Configurator;

/**
 * @author Liang Wang
 *
 */
public class TransactionSBFT extends Transaction {
	private Node c_Collector;
	private Node e_Collector;
	
	public TransactionSBFT(Node getherer, ArrayList<Node> addressTable, long size_txn) {
		super(getherer, addressTable, size_txn);
		c_Collector = Selectors.selectOneNode(Configurator.nodes);
		e_Collector = Selectors.selectOneDistinctNodeWithoutRm(Configurator.nodes, c_Collector);
	}
	
	public Node getC_Collector() {return c_Collector;}
	public void setC_Collector(Node c_Collector) {this.c_Collector = c_Collector;}
	public Node getE_Collector() {return e_Collector;}

}
