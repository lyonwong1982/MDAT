

public class Test {

	public static void main(String[] args) {
//		new Thrd();
//		thrd.getState();
//		int a = 12;
//		if(a == Integer.valueOf("12")) {
//			System.out.println("true");
//		}
//		System.out.println("sdflksdf" + "\t\t" + "sd");
//		System.out.println("sdflksdfsdfdfefef" + "\t" + "sd");
		A a = new AA();
		System.out.println(a.getClass() == A.class);
	}
}

class A {
	public String name = "A";
}
class AA extends A{
	public String name = "AA";
}

class Thrd implements Runnable{
	private Thread thrd1;
	private Thread thrd2;
	private Thread thrd3;
	private int c;
	public Thrd() {
		thrd1 = new Thread(this);
		thrd1.setName("thrd1");
		thrd2 = new Thread(this);
		thrd2.setName("thrd2");
		thrd3 = new Thread(this);
		thrd3.setName("thrd3");
		c = 0;
		thrd1.start();
		thrd2.start();
		thrd3.start();
	}
	public void getState() {
		System.out.println(Thread.currentThread().getState());
		System.out.println(thrd1.getState());
	}
	public synchronized void iDo() {
		try {
			if(Thread.currentThread() == thrd2) {
				if(c < 5) {
					return;
				}
			}
			else {
				if(c < 2) {
					return;
				}
			}
			System.out.println(Thread.currentThread().getName());
			c --;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
		}
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				if(Thread.currentThread() == thrd1) {
					Thread.sleep(1000);
					iDo();
				}
				else if(Thread.currentThread() == thrd2) {
					Thread.sleep(2000);
					c = c +3;
					if(c >= 5) {
						iDo();
					}
				}
				else {
//					Thread.sleep(2000);
//					c ++;
//					if(c >= 5) {
//						iDo();
//					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
