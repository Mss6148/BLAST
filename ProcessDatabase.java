
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.InputMismatchException;
import java.util.Scanner;

//Class: ProcessDatabase accepts two ArrayLists sequences, and descriptors and finds all kmers of 
//a length determined by the user.

	//-------------------------------------------------------------------------------------------------------------------
public class ProcessDatabase {
	
	static int kmerSize;	//Holds the user input kmersize
	Scanner scanner = new Scanner(System.in);	//Scanner for user input
	String tempKmerSequence = new String();		//temporary sequence used for reading in FastA files
	
	
	ArrayList<String> sequences = new ArrayList<String>();		//ArrayList of database sequences
	ArrayList<String> descriptors = new ArrayList<String>();	//ArrayList of database sequence headers/titles
	
	//ArrayList to hold the database processed kmers and their start index locations
	static ArrayList<Hashtable<String,ArrayList<Integer>>> kmerDatabase = new ArrayList<Hashtable<String,ArrayList<Integer>>>();
	//-------------------------------------------------------------------------------------------------------------------
	
	
	//Constructor: Accepts ArrayList<String> containing sequences
	//			   Accepts ArrayList<String> containing sequence titles/descriptions
	public ProcessDatabase(ArrayList<String> seqArray, ArrayList<String> descArray) {
		this.sequences = seqArray;
		this.descriptors = descArray;
		
	}	//End ProcessDatabase Constructor
	
	//findKmers:  Method asks the user what size kmers they are searching the database for and processes
	//			  the database sequences for these kmer "windows" and stores the kmer sequence and index location
	//			  of 
	public void findKmers() {
		
		//Prompt the user for desired kmer size.  Run until the user enters a valid integer
		System.out.println("Please enter your nucleotide kmer size.  The recommended value is 28: ");
		kmerSize = Math.abs(scanner.nextInt());

		long startTime = System.nanoTime();

		
		//For loop to create hashtable objects and populate the kmerDatabase ArrayList
		for(int i = 0; i < sequences.size(); i++){
			Hashtable<String,ArrayList<Integer>> sequenceKmersHash = new Hashtable<String,ArrayList<Integer>>();
			kmerDatabase.add(sequenceKmersHash);
		}	//End for loop
		
		//For loop to pre-process the database sequences based on user input kmer sizes
		//Outer for-loop: iterate over each hasthtable (sequence) 
		//Inner for-loop: iterate over each sequence creating "windows" the size of the input kmer size
		//tempKmerSequence: store the sequence in the current "window"
		//If the sequence already exists as an element of the hashtable, append the location
		//If the sequence doesn't exist as an element of the hashtable, create a new key/value
		for(int i = 0; i < kmerDatabase.size(); i++){
			for(int j = 0; j + kmerSize < sequences.get(i).length(); j++){
				tempKmerSequence = sequences.get(i).substring(j,kmerSize + j);
				if(kmerDatabase.get(i).containsKey(tempKmerSequence)){
					kmerDatabase.get(i).get(tempKmerSequence).add(j);
				}	//End if-statement
				else{
					ArrayList<Integer> kmerLocations = new ArrayList<Integer>();
					kmerLocations.add(j);
					kmerDatabase.get(i).put(tempKmerSequence, kmerLocations);
				}	//End else-statement
		
			} //End nested for-loop
		}	//End outer for-loop
		
		System.out.println("Kmer Database Processed");
		System.out.println();
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		long elapsedTime = (duration/1000000);
		
		System.out.println("Process Database took: " + elapsedTime + " milliseconds");
	}	//End FindKmers
	
	//getProcessedDatabase: Method to return the processed database kmer ArrayList
	public static ArrayList<Hashtable<String,ArrayList<Integer>>> getProcessedDatabase(){
		return kmerDatabase;
	}	//End getProcessedDatabase method
	
	//getKmerSize: Method returns the user-input desired kmer size
	public static int getKmerSize(){
		return kmerSize;
	}	//End getKmerSize method
	
}		//End ProcessDatabase Class
