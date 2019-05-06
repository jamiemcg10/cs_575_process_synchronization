
public class BufferQueue {
	int head;
	int tail;
	int size;
	int numElements;
	LogEntry[] entries;
	
	public BufferQueue(int initialLength) {
		
		head=tail=0;
		size = initialLength;
		entries = new LogEntry[size];
		
		for (int i=0; i<size; i++) {
			entries[i] = new LogEntry();
		}
		
		numElements=0;
		
	}
	
	public boolean isEmpty() {
		return numElements==0;
	}
	
	
	public void addToQueue(LogEntry newEntry) {
		if (numElements == size) {  // if queue is full, make queue bigger
			int newSize = size * 2;
			LogEntry[] tempList = new LogEntry[newSize];
			
			int j=head;
			for (int i=0; i<=size/2+1; i++) {
				tempList[i] = entries[j++];
				
				if (j>=size) {
					j = 0;
				}
				
			}
			
			head=0;
			tail=size;
			size=newSize;
			entries = tempList;
			
		}
		
		entries[tail] = newEntry;
		tail++;
		numElements++;
		if (tail >= size) {  // THIS
			tail=0;
		}

	}  // end addToQueue(LogEntry newEntry)
	
	
	public LogEntry removeFromQueue() {
		if (numElements==0) {
			return new LogEntry();
		}
		
		LogEntry rtnEntry = entries[head];
		head++;
		numElements--;
		
		if (head >= size) {
			head = 0;
		}
		
		//System.out.println("after remove\nhead: " + head + "\ntail: " + tail + "\n");
		return rtnEntry;

	}
	
	
	public String toString() {
		String rtnString = "";
		
		int start=head;
		
			for (int i=0; i<numElements; i++) {
				rtnString += entries[start] + "\n";
				start++;
				if (start >= tail) {
					start=0;
				}
			}
		
		
		return rtnString;
	}
	

}
