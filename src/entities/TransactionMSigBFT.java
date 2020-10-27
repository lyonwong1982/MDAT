/**
 * Transaction for MSig-BFT
 */
package entities;

import java.util.ArrayList;

import functions.Selectors;
import tools.Configurator;

/**
 * @author Liang Wang
 *
 */
public class TransactionMSigBFT extends Transaction {
	private NodeMSigBFT leader;
	private NodeMSigBFT witness;
	
	public TransactionMSigBFT(ArrayList<Node> addressTable, long size_txn) {
		super(null, addressTable, size_txn);
		leader = (NodeMSigBFT)Selectors.selectOneNode(Configurator.nodes);
		witness = (NodeMSigBFT)Selectors.selectOneDistinctNodeWithoutRm(Configurator.nodes, leader);
		super.getAddressTable().remove(leader);
		super.getAddressTable().remove(witness);
		Node getherer = Selectors.selectOneNode(super.getAddressTable());
		super.setGetherer(getherer);
	}
	
	public Node getLeader() {return leader;}
	public Node getWitness() {return witness;}
	public void setLeader(NodeMSigBFT leader) {this.leader = leader;}
	public void setWitness(NodeMSigBFT witness) {this.witness = witness;}
}
