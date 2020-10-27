/**
 * Selectors
 */
package functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;

import entities.Node;
import entities.Transaction;
import tools.RandTools;

/**
 * @author Liang Wang
 *
 */
public class Selectors {
	/**
	 * Select one node from a list of nodes.
	 * @param nodes A list of nodes.
	 * @return The selected node.
	 */
	public static Node selectOneNode(ArrayList<Node> nodes) {
		if(nodes == null) {return null;}
		int rand = RandTools.randomInt(0, nodes.size());
		return nodes.get(rand);
	}
	/**
	 * Select one node from a list of nodes. 
	 * The selected node is different from the given one and will be removed from this list. 
	 * @param nodes A list of nodes.
	 * @param node The node can not be delete.
	 * @return The selected node.
	 */
	public static Node selectOneDistinctNode(ArrayList<Node> nodes, Node node) {
		if(nodes == null) {return null;}
		if(nodes.size() == 0) {return null;}
		if(nodes.size() != 1 && node == null) {return null;}
		Node n = node;
//		When there is only one node in the list, then directly return it, because it should be equal to the given one.
		if(nodes.size() == 1) {
			n = nodes.get(0);
			nodes.clear();
			return n;
		}
		
		while(n.getId() == node.getId()) {
			int rand = RandTools.randomInt(0, nodes.size());
			n = nodes.get(rand);
		}
		nodes.remove(n);
		return n;
	}
	/**
	 * Select one node from a list of nodes distinctly without remove any node. 
	 * @param nodes The node list.
	 * @param node The selected node must be different from this specified node.
	 * @return A distinct node.
	 */
	public static Node selectOneDistinctNodeWithoutRm(ArrayList<Node> nodes, Node node) {
		if(nodes == null) {return null;}
		if(nodes.size() == 0) {return null;}
		if(nodes.size() != 1 && node == null) {return null;}
		Node n = node;
//		When there is only one node in the list, then directly return it, because it should be equal to the given one.
		if(nodes.size() == 1) {
			n = nodes.get(0);
			nodes.clear();
			return n;
		}
		
		while(n.getId() == node.getId()) {
			int rand = RandTools.randomInt(0, nodes.size());
			n = nodes.get(rand);
		}
		return n;
	}
	
	/**
	 * Select the leader node from the transaction pool. Only for CCP.
	 * @param poolTxn The transaction pool.
	 * @param num Indicate the number of transactions where the counting is on.
	 * @return The Id of the selected node.
	 */
	public static int selectLeaderNode(PriorityBlockingQueue<Transaction> poolTxn, int num) {
		if(poolTxn == null) {return -1;}
		if(num == 0) {return -1;}
		if(num > poolTxn.size()) {return -1;}
		
		HashMap<Integer, Integer> nodeCount = new HashMap<Integer, Integer>();
		Iterator<Transaction> itr = poolTxn.iterator();
		for(int i = 0; i < num; i ++) {
			int nodeId = itr.next().getGetherer().getId();
			if(nodeCount.get(nodeId) != null) {
				nodeCount.put(nodeId, nodeCount.get(nodeId) + 1);
			}
			else {
				nodeCount.put(nodeId, 1);
			}
		}
		int max = 0;
		int nodeId = 0;
		for(int key : nodeCount.keySet()) {
			int times = nodeCount.get(key);
			if(times > max) {
				max = times;
				nodeId = key;
			}
			else if(times == max) {
				if(nodeId > key) {
					nodeId = key;
				}
			}
		}
		return nodeId;
	}
	
	public static void main(String[] args) {
//		HashMap<String, Integer> hm = new HashMap<String, Integer>();
//		hm.put("a", 12);
//		System.out.println(hm.get("c"));
	}
}
