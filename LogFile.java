
public class LogFile {

	private int logSize = 100;
	private int logIndex = 0;
	private LogEntry[] logFile = new LogEntry[logSize];
	private boolean logFree = true;
	
	public LogFile() {
		
		for (int i=0; i< logSize; i++) {
			logFile[i] = new LogEntry();
		}  // end for (int i=0; i<=logSize; i++)
		
	}  // end LogFile() constructor
	
	public void log(String processName, int time, String action) {
		
		LogEntry entry = new LogEntry(processName, time, action);
		logFile[logIndex] = entry;
		logIndex++;
		
		if (logIndex == logSize) {
			// make log bigger
		}
		
	}
	
	public void acquireLog() {
			logFree = false;
		}
	
	public void releaseLog() {
		logFree = true;
	} // end releaseLog()
	
	
	public boolean isFree() {
		// returns whether the log is free or not
		return logFree;
	}
	
	public void addEntry(LogEntry newEntry) {
		
		logFile[logIndex] = newEntry;
		logIndex++;
		
	}
	
	public void getUse() {
		if (!logFree) {
			System.out.println("LOG FILE IS IN USE");
		} else {
			System.out.println("LOG FILE IS FREE");
		}
	}
	
	public String toString() {
		String rtnString="";
		
		for (int i=0; i<logIndex; i++) {
			rtnString = rtnString + logFile[i] + "\n";
		}
		
		return rtnString;
	} // end toString()
	
}
