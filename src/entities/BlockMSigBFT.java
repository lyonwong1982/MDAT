/**
 * Block for MSig-BFT
 */
package entities;

import java.util.ArrayList;

import functions.Selectors;
import tools.Configurator;

/**
 * @author Liang Wang
 *
 */
public class BlockMSigBFT extends Block {
	private NodeMSigBFT leader;
	private NodeMSigBFT witness;
	
	public BlockMSigBFT(ArrayList<Transaction> txns, ArrayList<Node> addressTable) {
		super(txns, addressTable, null);
		leader = (NodeMSigBFT)Selectors.selectOneNode(Configurator.nodes);
		witness = (NodeMSigBFT)Selectors.selectOneDistinctNodeWithoutRm(Configurator.nodes, leader);
		super.getAddressTable().remove(leader);
		super.getAddressTable().remove(witness);
		Node n = Selectors.selectOneNode(super.getAddressTable());
		super.setNode(n);
	}
	
	public Node getLeader() {return leader;}
	public Node getWitness() {return witness;}
	public void setLeader(NodeMSigBFT leader) {this.leader = leader;}
	public void setWitness(NodeMSigBFT witness) {this.witness = witness;}
	
}
