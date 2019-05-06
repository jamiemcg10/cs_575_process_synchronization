import java.util.*;
import java.util.concurrent.*;

public class Simulator {

	static boolean annotate;  // tracks whether console should display detail 
	
	public static void main(String[] args) throws ExecutionException{
		Simulator simulator = new Simulator();
		try {
			annotate = Boolean.parseBoolean(args[0]);
			simulator.simulate(30, annotate);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			annotate = true;
			simulator.simulate(30, annotate);
		}
		
	}

	
	boolean criticalSection = false; // Critical section var for whether a P is in its critical section
	BufferQueue buffer = new BufferQueue(50);  // shared buffer
	LogFile log = new LogFile();  // shared log file
	DataFile dataFile = new DataFile();  // shared "data" file
	Printer printer = new Printer();  // shared printer
	
	 
	 public void simulate(int cycles, boolean annotate) throws ExecutionException  {


		for (int i=0; i<=cycles; i++) {  // SIMULATE CPU CLOCK CYCLE
			
			System.out.println("\n***TIME: " + i);
			
			{
				int t = i;

			   //create a callable for each method
			   Callable<Void> callable1 = new Callable<Void>() {
			      public Void call() throws Exception  {
			         process1(t);
			         return null;
			      }
			   };

			   Callable<Void> callable2 = new Callable<Void>() {
			      public Void call() throws Exception {
			         process2(t);
			         return null;
			      }
			   };

			   Callable<Void> callable3 = new Callable<Void>() {
			      public Void call() throws Exception {
			         process3(t);
			         return null;
			      }
			   };
			   
			   Callable<Void> callable4 = new Callable<Void>() {
			      public Void call() throws Exception  {
			         process4(t);
			         return null;
			      }
			   };
			   

			   //add to a list
			   List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
			   taskList.add(callable1);
			   taskList.add(callable2);
			   taskList.add(callable3);
			   taskList.add(callable4);

			   //create a pool executor with 4 threads
			   ExecutorService executor = Executors.newFixedThreadPool(5);
			   
			   try  {
			      //start threads for all processes and wait for them to finish
			      Future<Void> p1 = executor.submit(callable1);
			      Future<Void> p2 = executor.submit(callable2);
			      Future<Void> p3 = executor.submit(callable3);
			      Future<Void> p4 = executor.submit(callable4);
			      p1.get();
			      p2.get();
			      p3.get();
			      p4.get();
				  executor.shutdown();
				  executor.awaitTermination(10, TimeUnit.SECONDS);
				  executor.shutdownNow();
			      
			   }
			   catch (InterruptedException ie)
			   {
				   //do something if you care about interruption;
			   }

			}
			

			if (annotate & i <= 5) {
				System.out.println("DATA FILE:\n" + dataFile); //print data file
				System.out.println("BUFFER:\n" + buffer); //print buffer
				System.out.println("LOG FILE:\n" + log); //print log file
			}
			
			
			if (!buffer.isEmpty()){
				log.addEntry(buffer.removeFromQueue());
			}

			log.releaseLog();  // simulate only 1 write to log per second
			
		}  // END CPU CLOCK CYCLE [for(int i=0; i<=cycles; i++)]
		
		while (!buffer.isEmpty()) {
			log.addEntry(buffer.removeFromQueue());
		}
		
		if (annotate) {
			System.out.println("FINAL BUFFER:\n" + buffer); //print buffer
			System.out.println("FINAL LOG FILE:\n" + log); //print log file		
			System.out.println("FINAL DATA FILE:\n" + dataFile); //print log file
		}

		System.out.println("SIMULATION COMPLETE");
	}  // end simulate()
	

	
	public void requestCritical(String pName) throws ExecutionException, InterruptedException {
		int sleepTime=0;
		
		switch (pName) {  // offset requests to avoids deadlock
			case "P1": 
				sleepTime=100;
				break;
			case "P2": 
				sleepTime=200;
				break;
			case "P3": 
				sleepTime=300;
				break;
			case "P4": 
				sleepTime=400;
				break;
			case "P5": 
				sleepTime=500;
				break;		
		}
	
			requestCritical (pName, 1, sleepTime);
		
	}
	
	private void requestCritical(String pName, int attemptNum, int sleepTime) throws ExecutionException {
		// method adds to pool of processes waiting to enter critical section and selects
		
		if (attemptNum == 1 & annotate) {
			System.out.println(pName + " is requesting permission to enter its critical section");
		}
	
					
		if (criticalSection == true) {
			try {
				TimeUnit.SECONDS.sleep(1);
				if (attemptNum == 1) {
					TimeUnit.MILLISECONDS.sleep(sleepTime);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			requestCritical(pName, attemptNum+1, sleepTime);
		}
		
		criticalSection = true;
		
		if (attemptNum == 1 & annotate) {
			System.out.println("Permission has been granted to " + pName);
		}
		
	}
	
	public void releaseCritical(String PName) {		
		if (annotate) {
			System.out.println(PName + " is no longer in its critical section");
		}
		criticalSection = false;
			
	}

	public void process1(int time) throws ExecutionException, InterruptedException {
		// PROCESS 1 - writes string to file in critical section
		String WRITE_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String action = "";
		
			if (time % 3 == 0) {   // Checks to see if time is divisible by 3 
				
				// BEGIN CRITICAL SECTION
				requestCritical("P1");
				
				// make sure file isn’t being read (written to?)
				     // Writes time and alphabet to file when time is divisible by 3 and writes 
				     // write time to shared log file
					
					dataFile.requestFile("P1", annotate);  // request file
					dataFile.writeData(time + "\t" + WRITE_STRING + "\n");
					dataFile.releaseFile("P1");  // release file
					action = "WRITE";
					
					// write to log file if free, else add to buffer
					if(log.isFree()) {
						log.acquireLog();
						log.log("P1", time, action);
					} else {
						buffer.addToQueue(new LogEntry("P1", time, action));
					}
					
					releaseCritical("P1");	// END CRITICAL SECTION
				 
			
			} // end if (time % 3 == 0)
		
		
	} // end process1()
	
		
	public void process2(int time) throws ExecutionException, InterruptedException{
		// PROCESS 2 - Shares buffer and data with process 1 and prints
		String WRITE_STRING = "0123456789";
		// Writes timestamp and number sequence to file when time is divisible by 2 or   
		// 5 and writes write time to shared log file
		// Contains critical section where bounded buffer and data file are modified

		
			if (time % 2 == 0 | time % 5 == 0) {   // Checks to see if time is divisible by 2 or 5
				requestCritical("P2");  // BEGIN CRITICAL SECTION
				
				// Writes timestamp and num sequence to file when time is divisible by 2 or 5 
				// and writes write time to shared log file
				dataFile.requestFile("P2", annotate);	// request to write to file
				dataFile.writeData(time + "\t" + WRITE_STRING + "\n");
				dataFile.releaseFile("P2");  // free file
				String action = "WRITE";
					
				// write to log file if free, else add to buffer
				if(log.isFree()) {
					log.acquireLog();
					log.log("P2", time, action);
				} else {
					buffer.addToQueue(new LogEntry("P2", time, action));
				}
			
				releaseCritical("P2");	// END CRITICAL SECTION

			}  // end if (time % 2 == 0 | time % 5 = 0)
			
	}  // end process2(int time)
	

	public void process3(int time) throws InterruptedException, ExecutionException {
		// PROCESS 3 – Open/read file and print
			String action="READ AND PRINT";
			if (time % 10 == 0) {
		     // Prints contents of file every 10 seconds
		     // Can only read file if not being written to and a printer is free
			// contains critical section where file is read and printer is used
				
				requestCritical("P3");  // BEGIN CRITICAL SECTION
				
				if (printer.requestPrinter("P3")) {  // wait for free printer
				dataFile.requestRead("P3");  // request file read
				printer.print("------------------------------------\n" 
						+ dataFile.getFileData() 
						+ "------------------------------------\n");
				dataFile.doneReading("P3");  // done reading file
				printer.releasePrinter("P3");  // free printer
			
				
				} else {
					System.out.println("!!!!!!!!!!!! FAILED !!!!!!!!!!!");
				}
				
				
				// write to log file if free, else add to buffer
				if(log.isFree()) {
					log.acquireLog();
					log.log("P3", time, action);
				} else {
					buffer.addToQueue(new LogEntry("P3", time, action));
				}
				
				releaseCritical("P3");  // P3 leaves critical section
				
			}  // end if (time % 10 == 0)	
	}  // end process3(int time)

	
	public void process4(int time) throws ExecutionException, InterruptedException {
		// PROCESS 4 – Print something every 5 seconds
		// critical section contains printer use
		
		String action="PRINT";
		
		     // Can only print if printer is free – will wait if printer is in use
		if (time % 5 == 0) {
			
			String printString = "";
			printString += " ___________________________________\n";
					
			for (int i=0; i<20; i++) {	
				printString += "|___________________________________|\n";
			}
			
			// BEGIN CRITICAL SECTION
			requestCritical("P4"); // P4 enters critical section
			if (printer.requestPrinter("P4")) {
				printer.print(printString);
				//System.out.println(printString);
				printer.releasePrinter("P4");	
			} else {
				System.out.println("!!!!!!!!!!!! PRINTER ACCESS FAILED !!!!!!!!!!!");
			}
			

			// write to log file if log is free, else add to buffer
			if(log.isFree()) { 
				log.acquireLog();
				log.log("P4", time, action);
			} else {
				buffer.addToQueue(new LogEntry("P4", time, action));
			}
			
			releaseCritical("P4"); // P4 leaves critical section
			
		}  // end if (time % 5 == 0)
			
	} // end process4(int time)
	
	
}  // end Simulator class
