public class LogEntry{
		
		String processName;
		int time;
		String action;
		
		public LogEntry() {
			
		}
		
		public LogEntry(String processName, int time, String action) {
			this.processName = processName;
			this.time = time;
			this.action = action;
			
		}
		
		public String toString() {
			String rtnString = "Process: " + processName + " Time: " + time + " Action: " + action;
			
			return rtnString;
			
		}  // end LogEntry toString()
		
	}  // end innerclass LogEntry