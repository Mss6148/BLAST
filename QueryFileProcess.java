
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

//Class QueryFileProcess: Asks user for an input query file containing a FastA sequence.
//The query file is then parsed into a sequence title (queryTitle) and genetic sequence (querySequence).
//The query file then is processed based on the user input kmersize and stored in a Hashtable
public class QueryFileProcess {
	
	int kmerSize;	//Holds the user-input desired kmer size
	String tempKmerSequence = new String();			//Used to read in FastA file sequences
	String filename = new String();					//Stores the user-input filename of their query sequence
	static String queryTitle = new String();		//Stores the title of the query sequence
	static String querySequence = new String();		//stores the query sequence
	
	//Holds the processed query kmers and their index locations.
	static Hashtable<String,ArrayList<Integer>> queryKmers = new Hashtable<String,ArrayList<Integer>>();

	//QueryFileProcess: Method prompts the user for an input file, then calls the parseAueryFile method
	public QueryFileProcess(int kSize) throws FileNotFoundException{
		
		kmerSize = kSize;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the filename containing your query sequence: ");
		filename = scanner.next();
		parseQueryFile(filename);
	}	//End QueryFileProcess method
	
	//parseQueryFile: Method to parse the user-input query FastA file
	public void parseQueryFile(String file) throws FileNotFoundException{
		
		File in = new File(file);				//Create a new file object containing the user-input filename	
		Scanner scanner = new Scanner(in);		//Scanner to parse the user's query FastA file
		
		//Try reading in the FastA file.  Continue while more lines exist in the file.
		//Then store the header in the queryTitle STring, and store the sequence in the querySequence String.
		try {
			while(scanner.hasNextLine()){
				String line = scanner.nextLine().trim();
				if(line.charAt(0) == '>'){
					queryTitle = line.substring(1);
				}	//End if
				else{
					querySequence = querySequence + line.trim().toUpperCase();
				}
			}	//End while-loop
		}	//End try-block
		
		//Finall, call the processQueryFile method
		finally{
			processQueryFile(querySequence);
		}	//End finally
		
	}	//End parseQueryFile
	
	//Method: processQueryFile processes the query FastA sequence for highscoring kmers and stores them in the
	//		  queryKmers Hashtable object
	//Accepts: String querySequence - the user input FastA genetic sequence
	public void processQueryFile(String querySequence){
		
		long processStartTime = System.nanoTime();
		
		for(int i = 0; i + kmerSize <= querySequence.length()-1; i++){
			tempKmerSequence = querySequence.substring(i,i + kmerSize);
			if(queryKmers.containsKey(tempKmerSequence)){
				queryKmers.get(tempKmerSequence).add(i);
			}	//End if statement
			else{
				ArrayList<Integer> kmerLocation = new ArrayList<Integer>();
				kmerLocation.add(i);
				queryKmers.put(tempKmerSequence, kmerLocation);
			}	//End else statement
		}	//End for-loop
		
		System.out.println("Query file read and processed");
		
		long endTime = System.nanoTime();
		long duration = (endTime - processStartTime);
		long elapsedTime = (duration/1000000);
		
		System.out.println("Process Query took: " + elapsedTime + " milliseconds");
	}	//End processQueryFile method
	
	//getQueryKmers: Method returns the database of processed query kmers
	public static Hashtable<String,ArrayList<Integer>> getQueryKmers(){
		return queryKmers;
	}	//End getQueryKmers Method
	
	//getQuerySequence:  Method returns the query sequence
	public static String getQuerySequence(){
		return querySequence;
	}	//end getQuerySequence method
	
	//getQueryDescription:  Method returns the query sequence header
	public static String getQueryDescription(){
		return queryTitle;
	}	//end getQueryDescription method
}	//End QueryFileProcess Class
