/**
 * Reporter
 */
package tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import entities.Transaction;

/**
 * @author Liang Wang
 *
 */
public class Reportor {
	public static void report() {
		if(Configurator.scheduleTxns.intValue() == Integer.valueOf(Configurator.params.get("txns"))) {
			long latency = 0;
			Iterator<Transaction> itr = Configurator.statTxns.iterator();
			while(itr.hasNext()) {
				latency += itr.next().latency();
			}
			double averageLatency = latency / Double.valueOf(Configurator.params.get("txns"));
//			Convert averageLatency to millisecond
			averageLatency = Math.round(averageLatency / 1000000d);
			long totalTime = Configurator.endTime - Configurator.startTime;
			long throughput = Long.valueOf(Configurator.params.get("txns")) * 1000000000 / totalTime;
			String text = "";
			for(String key : Configurator.params.keySet()) {
				text = text + key + "=" + Configurator.params.get(key) + "\n";
			}
			text = text + "Total time: " + (totalTime / 1000000) + " ms\n";
			text = text + "Average latency: " + averageLatency + " ms\n";
			text = text + "Throughput: " + throughput + " tps";
			String fileName = Configurator.protocolName + "_n_" + 
					Configurator.params.get("n") + 
					"_txns_" + Configurator.params.get("txns") + 
					"_time_" + System.currentTimeMillis();
			System.out.println(text);
			print(fileName, text);
		}
	}
	
	public static void print(String fileName, String text) {
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			File f = new File(fileName +".txt");
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			fw = new FileWriter(f, true);
			pw = new PrintWriter(fw);
			pw.println(text);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				pw.flush();
				fw.flush();
				pw.close();
				fw.close();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finished!");
		Configurator.threadPool.shutdown();
		System.exit(0);
	}
}
