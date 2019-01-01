package main;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		Main m = new Main();
		m.startAllServers();
//		Thread.sleep(1000);
		m.closeAllServers();
		
		Thread.sleep(10000);
	}
}
