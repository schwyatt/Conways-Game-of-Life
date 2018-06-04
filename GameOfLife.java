/* 
*	Wyatt Schiefelbein
*	
*		June 1st, 2018
*		a rough version of the game of life before I go to bed on this fine
*			Friday night.
*	
* Note: maybe make the game board, the int[][][], a String[][][] - will be easier to see changes, plus might use less memory?
*/


import java.util.*;
import java.io.*;
import java.lang.*;
public class GameOfLife{

	
	/************************ MAIN **************************/
	/* Initialises Board, Prints Arrays, and calls the Game of Life function.
	*
	* Input: __
	* Output: none
	*
	*/
	public static void main(String[] args)
	{
	
		// initialise board with the beginning states (the 'seed', stored in GOLSeed.txt)
		int[][][] arr = new int[12][12][2];			// define Dimentions in seed X X X X X X X X X X X X X X X X X X X X X X X X X X X X X 
		arr = fileInput(arr);;
		
		int end = 0;
		
		// used to create a delay between iterations
		long startTime = 0;
		long endTime = 0;
		
		/* Prints to a File, but the file does not automatically refresh. Will create the file if Game of Life Output.txt does not already exist.X X X X X X X X X X X X X X X X X X X X X X X X X X X X X 
		try{
			File outPut = new File("Game of Life Output.txt");
			PrintStream outPutFile = new PrintStream(outPut);
			printTDArrayFile(arr, hold, outPutFile);
		}
		catch (FileNotFoundException ex){
			System.out.println("Game of Life Output.txt not Found.");
			System.exit(-1);
		}*/	
		
		// creates the iterations for the Game of Life
		while(end < 20){	// iterations develop at specific intervals, up to 20 times.
			
			startTime = System.currentTimeMillis();
			
			arr = gameOfLife(arr);
			
			printTDArrayTerminal(arr, 0);		// Prints the 0 array, which displays alive/asleep states.
			//printTDArrayTerminal(arr, 1); 	//Prints the array which is the basis for calculating alive/asleep states found in 0.
			
			zeroBoard(arr, 1);			//makes all indices 0 in arr[][][1]. Necessary to acurately calculate alive/sleep states in arr[][][0].
			
			// delays iterations
			for(long i=0; i < 1000; i++){		// Is there a more elegant way to delay the execution of each iteration? X X X X X X X X X X X X X X X X X X X X X X X X X X X X X 
				endTime = System.currentTimeMillis();
				i = endTime - startTime;
			}
			end++;
		}
	}
	
	/************************ GAME OF LIFE **************************/
	
	/* Updates game board per iteration by:
	*	1) adding 1 to all neighbors of an alive index of arr[][][0] (from now on [0] for short), but stores this in arr[][][1] ([1]).
	*	2) determining which of indices will become alive, and which will sleep after [1] has been completed for this iteration.
	*		a) if an index has a value of 3 in [1], it will be set to alive in [0] for the next iteration
	*		b) if an index has a value of 2 in [1], and a value of 1 on the board [0] (which indicats the index is already alive in [0]), this index survives for the next iteration
	*		c) else, the index is set to asleep in [0] for the next iteration. 
	*
	*Input: 3D int array
	*Output: 3D int array
	*/
	public static int[][][] gameOfLife(int[][][] arr)
	{
		// finds the alive indices in [0], and adds 1 to all neighbors of these indices in [1] using the addToNeighbors function.
		for (int i=0; i<arr.length; i++){
			for (int j=0; j<arr[0].length; j++){
				if(arr[i][j][0] > 0){
					addToNeighbors (arr, i, j);
				}
			}
		}
		
		// calculates alive/asleep states in [0].
		fillBoardZ0(arr);
		
		return arr;
	}
	
	/* Calculates alive/asleep states in [0].
	*
	* Input: 3D int array
	* Output: none
	*
	*/
	public static void fillBoardZ0(int[][][] arr)
	{
		//cycles through each index, comparing its number of naighbors (stored in [1]) to its value in [0], and detemrining its alive/asleep
		//	state for the next iteration
		for (int i=0; i<arr.length; i++){
			for (int j=0; j<arr[0].length; j++){
				// The following are the rules for when an index is set to alive ("1"), or asleep ("0").
				if(arr[i][j][1] == 3){
					arr[i][j][0] = 1;
				}else if(arr[i][j][1] == 2 && arr[i][j][0] == 1){
					arr[i][j][0] = 1;
				}else{
					arr[i][j][0] = 0;
				}
			}	
		}
	}
	
	
	/* Adds 1 to each of the indices (in [1]) neighbouring the current index, arr[x][y][0]
	*
	*Input: 3D int array
	*Output: none
	*
	*/
	public static void addToNeighbors (int[][][] arr, int x, int y)
	{
		//will reset arr[x][y][1] to its original value at the start of this process
		int save = arr[x][y][1];
		
		int tempX = 0;
		int tempY = 0;
		
		// used in the creation of a buffer. The actual size of the game board should be 2 less on x and y than 
		//	specified, the buffer is 1 index around on every side. This is then used to wrap the 
		//	game board around, and make any evolutions which run off the board (such as a glider
		//	would) return to the board, but on the other side. 
		int lastIndexX = arr.length;
		int lastIndexY = arr[0].length;
		
		for (int i= -1; i<2; i++){
			for (int j=-1; j<2; j++){
				// if index is alive. . .
				if(arr[x][y][0] == 1){
					//. . .look at the indices surrounding it, one at a time.
					tempX = x+i;
					tempY = y+j;
					
					// buffer in x. If x+i = the last indices in x (the buffer), move value to other side
					//	of the board.
					if(tempX == lastIndexX-1){
						tempX = 1;
					} else if(tempX == 0){
						tempX = lastIndexX - 2;
					}
					
					// buffer in y.
					if(tempY == lastIndexY-1){
						tempY = 1;
					} else if(tempY == 0){
						tempY = lastIndexY - 2;
					}
					
					// add "1" to the naighboring indices of arr[x][y][0], but in [1]
					arr[tempX][tempY][1] += 1;
				}
			}
		}
		
		// return arr[x][y], the index we are adding to its naighbors, back to its original value in [1].
		//	Without this, arr[x][y][1] would 'have' more naighbors than in reality, and would mess up
		//		alive/asleep states in [0].
		arr[x][y][1] = save;
	}
	
	
	
	/************************ ADAPTATIONS, ZERO BOARD **************************/
	
	public static int[][][] zeroBoard(int[][][] arr, int zAxis)
	{
		for (int i=1; i<arr.length-1; i++){
			for (int j=1; j<arr[0].length-1; j++){
				arr[i][j][zAxis] = 0;
			}
		}
		
		return arr;
	}
	
	/******************************* INITIALISE BOARD *******************************/
	
	/* Scans the file GOLSeed.txt for the seed of GOL, which is then input directly into
	*		the seed[][][] array.
	*
	*Input: 3D int array
	*Output: 3D int array
	*
	*Catches FileNotFound and InputMismatchException, notifying user, and exiting the program.
	*
	*/
	public static int[][][] fileInput(int[][][] arr)
	{
	
		//attempts to find, and read GOLSeed.txt, catches if the file is not found
		//	NOTE: There MUST be a buffer of "0"s on all sides. "1"s in the buffer, for now, will throw an
		//		ArrayIndexOutOfBoundsException later on.						X X X X X X X X X X X X X X X X X X X X X X X X X 
		try{
			Scanner fileScan = new Scanner(new File("GOLSeed.txt"));
			for (int i=0; i<arr.length; i++){
				for (int j=0; j<arr[0].length; j++){
					arr[i][j][0] = fileScan.nextInt();
				}
			}
		}
		catch(java.io.FileNotFoundException ex){
			System.out.println("The file GOLSeed.txt was not found.");
			System.exit(-1);
		}
		catch(java.util.InputMismatchException ex){
			System.out.println("Input Mismatch Exception: The file GOLSeed.txt must contain only integer numbers 0 and 1.");
			System.exit(-1);
		}
		catch(java.util.NoSuchElementException ex){		// This problem will need to be better dealt with when dimensions are given in the file itself. How will you account for the buffers? X X X X X X X X X
			System.out.println("Don't forget to add the buffers to the file GOLSeed.txt");
			System.exit(-1);
		}
		
		return arr;
	}
	
	/******************************* PRINT FUNCTIONS *******************************/
	
	/* prints x and y axis of 3D int arrays to the terminal
	*
	*Input: 3D int array, z plain to print (0 or 1)
	*Output: none
	*
	*/
	public static void printTDArrayTerminal(int[][][] arr, int z)
	{
		System.out.println();
		for (int i=0; i<arr.length; i++){
			for (int j=0; j<arr[0].length; j++){
				System.out.print(arr[i][j][z] + " ");
			}
			System.out.print("\n");
		}
	}
	
	/* prints x and y axis of 3D int arrays to an output file, Game Of Life Output.txt
	*
	*Input: 3D int array, z plain to print, PrintStream to print to
	*Output: none
	*
	*
	public static void printTDArrayFile(int[][][] arr, int z, PrintStream outPutFile)
		throws FileNotFoundException
	{	
		for (int i=0; i<arr.length; i++){
			for (int j=0; j<arr[0].length; j++){
				outPutFile.print(arr[i][j][z] + " ");
			}
			outPutFile.println("\n");
		}
	}*/	
}
