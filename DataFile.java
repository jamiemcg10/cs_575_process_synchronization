import java.util.concurrent.TimeUnit;

public class DataFile {


	private String data;
	private boolean writeFlag;
	private boolean readFlag;
	
	public DataFile() {
		data = "Time\tData\n";
		writeFlag = false;
		
	}  // end DataFile() constructor
	
	public void writeData(String toAdd) {
		
		data = data + toAdd;
		
	}
	
	public void requestFile(String pName, boolean annotate) {
		requestFile(pName, 1, annotate);
		
	}
	
	private boolean requestFile(String pName, int attemptNum, boolean annotate) {
		if (attemptNum == 1 & annotate) {
			System.out.println(pName + " is requesting permission to write to data file");
		}

		
		while (writeFlag == true | readFlag == true) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			requestFile(pName, attemptNum+1, annotate);
		}
		
		if (annotate) {
			System.out.println(pName + " has acquired file");
		}
		
		writeFlag = true;
		return true;
	}
	
	public void requestRead(String pName) {
		requestRead(pName, 1);
	}
	
	private boolean requestRead(String pName, int attemptNum) {
		if (attemptNum == 1 & Simulator.annotate) {
			System.out.println(pName + " is attempting to read data file");
		}
		
		while (writeFlag == true) {
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			requestRead(pName, attemptNum+1);
		}
		
		if (Simulator.annotate) {
			System.out.println(pName + " has acquired file");
		}
		readFlag = true;
		return true;
	}
	
	public void releaseFile(String pName) {
		if (Simulator.annotate) {
			System.out.println(pName + " is no longer using data file");
		}
		writeFlag = false;
	} // end releaseLog()
	
	public void doneReading(String pName) {
		if (Simulator.annotate) {
			System.out.println(pName + " is no longer reading data file");
		}
		readFlag = false;
	}
	
	
	public String getFileData() {
		return this.data;
	}
	
	public void getReadFlag() {
		if (readFlag) {
			System.out.println("DATA FILE IS BEING READ");
		} else {
			System.out.println("DATA FILE IS NOT BEING READ");
		}
	}
	
	
	public void getWriteFlag() {
		if (writeFlag) {
			System.out.println("DATA FILE IS BEING WRITTEN TO");
		} else {
			System.out.println("DATA FILE IS NOT BEING WRITTEN TO");
		}
	}
	
	
	public String toString() {
		
		return data;
		
	} // end toString()
	

}
