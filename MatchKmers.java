
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Scanner;

//Class MatchKmers:
//Methods: MatchKmers(Constructor), CreateMatches, PrintDatabaseMatch
//Description: Finds high-scoring kmer seed matches between an input query file and a database of sequences.
//Then attempts to extend the seed length until the the score (+1 for Match, -1 for Mismatch) falls below a
//user-selected threshold.
public class MatchKmers {
	
	Scanner scanner = new Scanner(System.in);
	
	static int kmerSize;				//Stores the user-input kmer size
	int selection;				//Used for a switchcase
	static int lowScore;		//Stores the user-input low score for seed extension
	int extensionScore;			//Stores the extension score for seed extension
	int numExtensionLeft;		//Stores the extension score for extending only to the left
	int numExtensionRight;		//Stores the extension score for extending only to the right
	int numExtensionLeftRight;	//Stores the extension score for extending both left and right
	int queryKmerStartPosition;	//Stores the index of the beginning of the query kmer
	int queryKmerEndPosition;	//Stores the index of the end of the query kmer
	int dbKmerStartPosition;	//Stores the index of the start of the database sequence kmer
	int dbKmerEndPosition;		//Stores the index of the end of the database sequence kmer
	int seedBeginningIndex;
	int seedEndIndex;
	
	//Constant values: Extension scores.
	final int MATCH_SCORE = 0;
	final int MISMATCH_SCORE = -1;
	
	char tempDBNuc;			//Temporary character to hold the next database nucleotide
	char tempQueryNuc;		//Temporary character to hold the next query nucleotide
	
	String multiExtensionString = new String();		//String used for storing the kmer extension
	static String querySequence = new String();		//String stores the query sequence
	static String queryDescription = new String();	//String stores the query sequence header
	
	
	//ArrayLists and Hashtable objects:
	//descriptors: Holds the sequence descriptors/titles
	//sequences: Holds the database sequences
	//kmerDatabase: Holds the processed database sequences kmers and locations
	//queryKmers: Holds the processed query sequence kmers and locations
	
	static ArrayList<String> descriptors = new ArrayList<String>();		//Holds sequence titles/descriptions
	static ArrayList<String> sequences = new ArrayList<String>();		//Holds sequences
	static ArrayList<Hashtable<String,ArrayList<Integer>>> kmerDatabase = new ArrayList<Hashtable<String,ArrayList<Integer>>>();
	static Hashtable<String,ArrayList<Integer>> queryKmers = new Hashtable<String,ArrayList<Integer>>();
	
	//Constructor MatchKmers:  instantiates several objects for use in later methods
	public MatchKmers(ArrayList<String> seqs, ArrayList<String> descrip, ArrayList<Hashtable<String,ArrayList<Integer>>> kmerDB, Hashtable<String,ArrayList<Integer>> qKmers, int size, String querySeq, String queryTitle) {
		sequences = seqs;
		descriptors = descrip;
		kmerDatabase = kmerDB;
		queryKmers = qKmers;
		kmerSize = size;
		querySequence = querySeq;
		queryDescription = queryTitle;
		
	}	//End MatchKmers Constructor
	
	//Method: CreateMatches
	//Description: Prompts the user for an extension score threshold and calls the FindDatabaseMatch method
	public void CreateMatches(){
		
		System.out.println("Select a kmer extension scoring paradigm: ");
		System.out.println("1: Lowest score is equal to 1/2 of Kmer size: " + (kmerSize/2) + "\n" +
						   "2: Lowest score is equal to 1/3 of Kmer size: " + (kmerSize/3) + "\n" +
						   "3: Lowest score is equal to 1/4 of Kmer size: " + (kmerSize/4) + "\n" +
						   "4: Enter your own scoring threshhold between 0 and " + kmerSize);
		selection = scanner.nextInt();
		
		switch(selection){
		case 1:
			lowScore = (kmerSize/2);
			break;
		case 2:
			lowScore = (kmerSize/3);
			break;
		case 3:
			lowScore = (kmerSize/4);
			break;
		case 4:
			System.out.println("Enter the lowest score for the kmer extension (integer): ");
			lowScore = scanner.nextInt();
			break;
		}	//End switch-case
		
		System.out.println("Finding Kmer Matches...");
		findDatabaseMatch();
	}	//End MatchKmers Constructor Method
	
	//findDatabaseMatch:  Method finds kmer matches between the query sequence and database sequences.
	//					  If a match is found in a region that has not already been processed, a bi-directional
	//					  extension is attempted until the score falls below the user-input threshold.
	public void findDatabaseMatch() {
		
		//For each item in the database, compare it with the items in the processed query database.
		//If the database contains the high-scoring kmer, attempt an extension until the score falls below the lowScore.
		//Matches are +0, mismatches are -1.
		
		
		long startTime = System.nanoTime();
		
		//Iteration through all elements of the kmerDatabase
		for(int i = 0; i < kmerDatabase.size(); i++){
			
			//seedKmerMap and kmerMap store found and printed matches to reduce replicate prints in the same region
			Hashtable<Integer,Integer> seedKmerMap = new Hashtable<Integer,Integer>();
			Hashtable<Integer,Integer> kmerMap = new Hashtable<Integer,Integer>();
			kmerMap.clear();
			seedKmerMap.clear();

			
			//Iterate over each kmer match from the query sequence
			for(String key : queryKmers.keySet()){
				if(kmerDatabase.get(i).containsKey(key)){
				
					//Try to extend database for each match.  As each kmer may appear multiple times 
					//throughout both the query and database sequence.  
					for( int x = 0; x < kmerDatabase.get(i).get(key).size(); x++){
						for( int y = 0; y < queryKmers.get(key).size(); y++){
							
							
							multiExtensionString = key;
							extensionScore = kmerSize;
							
							//Stores the index of the beginning of the seed for both the query and database sequence.
							queryKmerStartPosition = queryKmers.get(key).get(y);
							queryKmerEndPosition = queryKmers.get(key).get(y) + kmerSize;
							seedBeginningIndex = queryKmerStartPosition;
							seedEndIndex = queryKmerEndPosition;
							int seedMedianIndex = ((seedBeginningIndex + seedEndIndex)/2);
							
							//Stores the index of the end of the seed for both the query and database sequence.
							dbKmerStartPosition = kmerDatabase.get(i).get(key).get(x);
							dbKmerEndPosition = kmerDatabase.get(i).get(key).get(x) + kmerSize;
							int dbKmerMedianIndex = ((dbKmerStartPosition + dbKmerEndPosition)/2);
							
							//Check to see if the found kmer match has already been processed for this given region
							//in both the database AND query sequences.  
							//If the region has been processed in one, and not the other - it implies that the kmer was
							//duplicated in either the database or query sequence and must be accounted for.
							//If the region is found in one or the other (database or query) or neither, an extension
							//is attempted, the regions are added to the proper Hashmaps and the match is printed.
							if(checkInterval(dbKmerMedianIndex,seedMedianIndex,kmerMap,seedKmerMap) == false){
							
							multiExtensionString += querySequence.charAt(queryKmerStartPosition + 1);
							
							//Extend the alignment in both directions
							while(extensionScore >= lowScore){
								//Extend alignment 1 nucleotide to the left unless you're at the beginning of either sequence.
								if(queryKmerStartPosition - 1 >= 0 && dbKmerStartPosition - 1 >= 0){
									
									//Assign the nucleotide one before the current seed position to a temporary character
									tempDBNuc = sequences.get(i).charAt(dbKmerStartPosition-1);
									tempQueryNuc = querySequence.charAt(queryKmerStartPosition-1);
									
									//If the two nucleotides are the same, assign the match score ( +1 )
									if(tempDBNuc == tempQueryNuc){
										extensionScore += MATCH_SCORE;
									}	//End if statement
									//If the two nucleotides are different, assign the mismatch score ( -1 )
									else{
										extensionScore += MISMATCH_SCORE;
									}	//End else statement
									
									//Appened the nucleotide to the extension string and reduce the position index by 1 (shifting left)
									multiExtensionString = tempQueryNuc + multiExtensionString;
									queryKmerStartPosition -= 1;
									dbKmerStartPosition -= 1;
								}	//End nested while
								//Extend alignment 1 nucleotide to the right unless you're at the end of either sequence
								if(queryKmerEndPosition + 1 < querySequence.length() && dbKmerEndPosition + 1 < sequences.get(i).length() ){
									

									//Assign the characters at the index one before the seed to a temporary character
									tempDBNuc = sequences.get(i).charAt((dbKmerEndPosition+1));
									tempQueryNuc = querySequence.charAt((queryKmerEndPosition+1));
									
									//If the two nucleotides match, add the match score ( +1 )
									if(tempDBNuc == tempQueryNuc){
										extensionScore += MATCH_SCORE;
									}	//End if statement
									//If the two nucleotides don't match, add the mismatch score ( -1)
									else{
										extensionScore += MISMATCH_SCORE;
									}	//End else statement
									
									//Append the nucleotide to the extension seed string and shift the position to the right
									multiExtensionString = multiExtensionString + tempQueryNuc;
									queryKmerEndPosition += 1;
									dbKmerEndPosition += 1;
								}	//End if statement
								
								//System.out.println("Score: " + extensionScore + " / " + lowScore);
								//System.out.println("Extended seed: " + multiExtensionString);
								//System.out.println("Start position: " + queryKmerStartPosition);
								//System.out.println("End position: " + queryKmerEndPosition);
								if(queryKmerStartPosition == 0 && queryKmerEndPosition == querySequence.length()-1) {
									break;
								}
							}	//End while-loop
							
							
							//SEND multiExtensionString, start and end positions to printDatabaseMatch Method
							
								printDatabaseMatch(multiExtensionString, sequences.get(i), descriptors.get(i),queryKmerStartPosition, queryKmerEndPosition, dbKmerStartPosition, dbKmerEndPosition,queryDescription, key, seedBeginningIndex, seedEndIndex);
								kmerMap.put(dbKmerStartPosition, dbKmerEndPosition);
								seedKmerMap.put(queryKmerStartPosition,queryKmerEndPosition);
							}	//End if statement
							
						}	//End nested for-loop
					}	//End outer for-loop
					
				}	//End if statement
				
			}	//End nested for-loop
		}	//End outer for-loop
		
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		long elapsedTime = (duration/1000000);
		
		System.out.println("Finding matches took: " + elapsedTime + " milliseconds");
	}	//End findDatabaseMatch method
	
	//printDatabaseMatch: Method determines consensus and creates a consensus string of '*' and '-' characters.
	//					  Then prints the aligned matched regions from the database, query, and consensus 
	public void printDatabaseMatch(String extendedString, String dbSequence, String dbDescription, int queryStartIndex,
			int queryEndIndex, int dbStartIndex, int dbEndIndex, String queryDesc, String kmer, int seedStart, int seedEnd) {
		
		String printDBSequence = new String();
		printDBSequence = dbSequence.substring(dbStartIndex, dbEndIndex+1);
		String consensus = new String("");
		
		//These variables are used in determining how many loop iterations are required to print 60 character strings
		//This prevents improper formatting during the print.
		int iterations = (extendedString.length()/60);
		int remainder = (extendedString.length() % 60);
		int counter = 0;
		
		//Create a consensus string of the aligned kmer regions between the database and query sequence
		for(int i = 0; i < extendedString.length(); i++){
			if(extendedString.charAt(i) == dbSequence.substring(dbStartIndex, dbEndIndex+1).charAt(i)){
				consensus += "*";
			}	//End if
			else{
				consensus += "-";
			}	//End else
			
		}	//End for loop 
		
		//Print the kmer match details
		System.out.printf("%-20s| %-20s %-20s %n", "Extension details", "\t" + "DB sequence: " + dbDescription + "| ", "DB location: " + dbStartIndex + "-" + dbEndIndex + "| ");
		System.out.printf("%-20s| %-20s %-20s %n", "Extension details", "\t" + "Query location: " + queryStartIndex + "-" + queryEndIndex + "| ", "Seed index: " + seedStart + "-" + seedEnd); 
		System.out.printf("%-20s| %-10s %n", "Starting seed", "\t" + kmer );
		System.out.println();
		
		//Prints aligned 60 character (nucleotide) long strings per line to prevent formatting errors
		for(int i = 0; i < iterations; i++){
		System.out.printf("%-20s| %-10s %n", dbDescription,"\t" + printDBSequence.substring(counter, counter+61));
		System.out.printf("%-20s| %-10s %n", queryDesc, "\t" + extendedString.substring(counter,counter+61));
		System.out.printf("%-20s| %-10s %n", "Consensus", "\t" + consensus.substring(counter, counter+61) );
		System.out.println();
		counter += 60;
		}	//End for-loop
		
		//Print the remaining nucleotides on the last string
		System.out.printf("%-20s| %-10s %n", dbDescription,"\t" + printDBSequence.substring(counter, counter+remainder));
		System.out.printf("%-20s| %-10s %n", queryDesc, "\t" + extendedString.substring(counter,counter+remainder));
		System.out.printf("%-20s| %-10s %n", "Consensus", "\t" + consensus.substring(counter, counter+remainder) );
		
		System.out.println();
	}	//End printDatabaseMatch method
	
	//checkInterval: Method is used to determine if the found kmer is in the range of an already printed kmer extension
	//Returns: false if the kmer does not fall in an already existing extension, or true if it does.
	public static boolean checkInterval(int dbIndex, int seedIndex, Hashtable<Integer,Integer> printedKmers, Hashtable<Integer,Integer> seedKmerMatches){
		boolean kmerExists = false;
		
		for(Integer keyInt : printedKmers.keySet()){
			for(Integer seedKeyInt : seedKmerMatches.keySet()){
			if(dbIndex >= keyInt && dbIndex <= printedKmers.get(keyInt) && seedIndex >= seedKeyInt && seedIndex <= seedKmerMatches.get(seedKeyInt)) {
				kmerExists = true;
				break;
			}	//End if statement
			}	//End nested for loop
		}		//End outer for loop
		
		return kmerExists;
	}
}	//End Class MatchKmers
