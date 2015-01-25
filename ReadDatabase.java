
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

//Class ReadDatabase parses a database of FastA files and stores sequence descriptors in the
//ArrayList descriptors and stores sequences in the ArrayList sequences.
//------------------------------------------------------------------------------------------
public class ReadDatabase {

	String filename;	//String holds user input filename
	static String tempSequence = new String();	//String holds temporary sequence while parsing files
	
	static ArrayList<String> descriptors = new ArrayList<String>();	//Holds sequence titles/descriptions
	static ArrayList<String> sequences = new ArrayList<String>();		//Holds sequences
	
	Scanner scanner = new Scanner(System.in);	//Scanner to read input database file
	
	//Constructor: Prompts the user for a database FastA file, parses it and stores sequence description
	//and sequences in their respective ArrayLists.  
	//------------------------------------------------------------------------------------------
	public ReadDatabase() throws FileNotFoundException {
		
		//Propmt the user for an input filename
		System.out.println("Please enter the name of your database file: ");
		filename = scanner.next();
		if(filename.contentEquals("exit")){
			System.exit(0);
		}	//End if-statement
		
		long startTime = System.nanoTime();
		
		
		File in = new File(filename);	//Create a new file containing the user input filename
		tempSequence = "";	//Initialize tempSequence to ""
		
		//Try: Read and parse file, add sequence information to ArrayLists
		try {
			//Scanner to parse "in" - user input file.
			Scanner scanner = new Scanner(in);
			
			//While the infile has more lines: If the first character = '>' we know it to be
			//a sequence header (title) and can remove the character at index 0 ('>') and add
			//the remainder to our descriptors ArrayList.  Check if tempSequence is not equal to ""
			//as this infers that an entire sequence has been read and we are now at the next header.
			while(scanner.hasNextLine()){
				String line = scanner.nextLine().trim().replace("\n", "");
				//System.out.println(line);
				
				if(line.charAt(0) == '>'){
					descriptors.add(line.substring(1));
					if(tempSequence != ""){
						//System.out.println("tempSequence: " + tempSequence);
						sequences.add(tempSequence);
						
						tempSequence = "";
					}	//End if-statement
				}	//End if-statement
				//If the character at index 0 doesn't equal '>' we know it's not a header and a 
				//sequence string itself.
				else if(line.charAt(0) == 'A' ||
						line.charAt(0) == 'C' ||
						line.charAt(0) == 'T' ||
						line.charAt(0) == 'G'){
					tempSequence = tempSequence + line;
					//System.out.println("tempSequence: " + tempSequence);

				}	//End if-statement
			}	//End while-statement
			//Finally, we have to add the last tempSequence as it hasn't been added yet.
			sequences.add(tempSequence);
			scanner.close();
		}	//End try-block
		finally {
			System.out.println("Database read...");
			ProcessDatabase PD = new ProcessDatabase(sequences,descriptors);
		}	//End finally
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		long elapsedTime = (duration/1000000);
		
		System.out.println("Read Database took: " + elapsedTime + " milliseconds");
		
	}	//End ReadDatabase method (Constructor)
	
	//getSequencesArray: Method to return the arrayList of database sequences
	public static ArrayList<String> getSequencesArray(){
		return sequences;
	}	//End getSequencesArray method
	
	//getSequenceDescriptors: Method to return the arrayList of sequence headers/titles
	public static ArrayList<String> getSequenceDescriptors(){
		return descriptors;
	}	//End getSequenceDescriptors method
}		//End class ReadDatabase
