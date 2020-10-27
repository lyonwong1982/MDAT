/**
 * Configuration Reader
 */
package tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import entities.Block;
import entities.Node;
import entities.Transaction;

/**
 * @author Liang Wang
 *
 */
public class Configurator {
//	Parameters
	public static HashMap<String, String> params = null;
//	Name of the protocol
	public static String protocolName = "";
//	The number of nodes
	public static ArrayList<Node> nodes = new ArrayList<Node>();
//	Statistic set for transactions
	public static LinkedBlockingQueue<Transaction> statTxns = new LinkedBlockingQueue<Transaction>();
//	public static ArrayList<Transaction> scheduleTxns = new ArrayList<Transaction>();
//	Start time
	public static long startTime = 0;
//	End time
	public static long endTime = 0;
//	Schedule number of transaction set
	public static AtomicInteger scheduleTxns = new AtomicInteger();
//	Public transactions pool
	public static PriorityBlockingQueue<Transaction> poolTxns = new PriorityBlockingQueue<Transaction>();
//	Buffer for public transactions pool
	public static ConcurrentHashMap<Transaction, AtomicInteger> bufferPoolTxns = new ConcurrentHashMap<Transaction, AtomicInteger>();
//	Public blockchain
	public static PriorityBlockingQueue<Block> blockchain = new PriorityBlockingQueue<Block>();
//	Buffer for public blockchain
	public static ConcurrentHashMap<Block, AtomicInteger> bufferBlockchain = new ConcurrentHashMap<Block, AtomicInteger>();
//	Thread pool
	public static ExecutorService threadPool = Executors.newFixedThreadPool(1000);
	
	public static HashMap<String, String> readParameters() {
		BufferedReader reader = null;
		HashMap<String, String> params = new HashMap<String, String>();
		try {
			reader = new BufferedReader(new FileReader("conf.txt"));
			String str = null;
			while((str = reader.readLine()) != null) {
				params.put(str.substring(0, str.indexOf("=")).trim(),
						str.substring(str.indexOf("=") + 1).trim());
			}
			reader.close();
			return params;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if(reader != null) {
				try {
					reader.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		return params;
	}
	
	public static void main (String[] args) {
//		HashMap<String, String> hm = ConfigurationReader.readParameters();
//		for(String key : hm.keySet()) {
//			System.out.println(key + ":" + hm.get(key));
//		}
//		long start = System.nanoTime();
//		try {
//			Thread.sleep(1000);
//		}
//		catch(InterruptedException e) {
//			e.printStackTrace();
//		}
//		start = System.nanoTime() -start;
//		System.out.println(start);
//		start = System.currentTimeMillis();
//		try {
//			Thread.sleep(1000);
//		}
//		catch(InterruptedException e) {
//			e.printStackTrace();
//		}
//		start = System.currentTimeMillis() -start;
//		System.out.println(start);
	}
}
