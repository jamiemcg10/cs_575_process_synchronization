import java.util.concurrent.TimeUnit;

public class Printer {
	
	private boolean inUse = false;
	
	public boolean requestPrinter(String pName) {
		return requestPrinter(pName, 1);
	}
	
	private boolean requestPrinter(String pName, int attemptNum) {
		if (attemptNum == 1 & Simulator.annotate) {
			System.out.println(pName + " is requesting printer");
		}
		
		if (attemptNum > 5) {
			return false;
		}
		
		while (inUse == true & attemptNum <= 5) {
			try {
				TimeUnit.SECONDS.sleep(1);
				requestPrinter(pName, attemptNum+1);
			} catch (InterruptedException e) {
				return false;
			}
			requestPrinter(pName, attemptNum+1);
		}

		if (Simulator.annotate) {
			System.out.println("Printer granted to " + pName);
		}
		
		inUse = true; 
		
		return true;
	}

	public void print(String printStr) {
		System.out.println(printStr);
	}
	
	public void releasePrinter(String pName) {
		if (Simulator.annotate) {
			System.out.println(pName + " has released the printer");
		}
		inUse = false;
	}
	
	public void getUse() {
		if (inUse) {
			System.out.println("PRINTER IS IN USE");
		} else {
			System.out.println("PRINTER IS FREE");
		}
	}

}
