/* 
*	Wyatt Schiefelbein
*	
*		June 1st, 2018
*		a rough version of the game of life before I go to bed on this fine
*			Friday night.
*	
* Note: maybe make the game board, the int[][][], and String[][][] - less memory needed?
*/


import java.util.*;
import java.io.*;
import java.lang.*;
public class GameOfLife{

	/* Initialises Board, Prints Arrays, and calls the Game of Life itself.
	*
	* Input: __
	* Output: none
	*
	*/
	public static void main(String[] args)
	{
	
		// initialise board with the beginning states states (the 'seed')
		int[][][] arr = new int[12][12][2];		// note, the order of the axis are: [y][x][z]
		arr = initBoard(arr);
		
		System.out.print("Initialised:");
		int hold = 0;
		printTDArrayTerminal(arr, hold);
		System.out.println();
		
		boolean cont = true;
		
		Scanner user = new Scanner(System.in);
		String usercont = "y";
		
		long startTime = 0;
		long endTime = 0;
		
		printTDArrayTerminal(arr, hold);
		
		/* Prints to a File, only, the file does not automatically refresh					X X X X X X X X X X X X X 
		try{
			File outPut = new File("Game of Life Output.txt");
			PrintStream outPutFile = new PrintStream(outPut);
			printTDArrayFile(arr, hold, outPutFile);
		}
		catch (FileNotFoundException ex){
			System.out.println("Game of Life Output.txt not Found.");
			System.exit(-1);
		}*/	
		
		while(cont){
		
			
			usercont = user.next();
		
			if(usercont.equalsIgnoreCase("n")){
				System.out.println("ekosi");
				cont = false;
			}
			
			startTime = System.currentTimeMillis();
			
			arr = gameOfLife(arr);
			
			printTDArrayTerminal(arr, 0);
			//printTDArrayTerminal(arr, 1);  //Prints the array which is the basis for calculating which indices will live or sleep
			
			zeroBoard(arr, 1);
			/*
			for(long i=0; i < 1000; i++){	// Is there a more elegant way to delay the execution of each iteration? X X X X X X X X X
				endTime = System.currentTimeMillis();
				i = endTime - startTime;
			}
			*/
		}
	}
	
	/************************ GAME OF LIFE **************************/
	
	/* Updates game board per iteration by:
	*1) adding 1 to all neighbors of an alive board.
	*		a) in the board  (which is two boards, 0 and 1), 0 stores which indices are alive
	*			and 1 stores neighboring values
	*2) determining which of these indices will become alive, or will sleep.
	*		a) if an indices has a value of 3 on the board 1, it will be alive next iteration
	*		b) if an indices has a value of 2 on the board 1, and a value of 1 on the board 0
	*			(which indicated it is already alive), this indice survives the next iteration
	*		c) else, the indice is asleep for the next iteration. 
	*
	*Input: 3D array
	*Output: 3D array
	*/
	public static int[][][] gameOfLife(int[][][] arr)
	{
		for (int i=0; i<arr.length; i++){
			for (int j=0; j<arr[0].length; j++){
				if(arr[i][j][0] > 0){
					addToNeighbors (arr, i, j);
				}
			}
		}
		
		fillBoardZ0(arr);
		
		return arr;
	}
	
	public static void fillBoardZ0(int[][][] arr)
	{
		for (int i=0; i<arr.length; i++){
			for (int j=0; j<arr[0].length; j++){
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
	
	
	/* Adds 1 to each of the [z1] indices neighbouring arr[x][y][z0]
	*
	*Input:
	*Output:
	*
	*/
	public static void addToNeighbors (int[][][] arr, int x, int y)
	{
		int save = arr[x][y][1];
		
		int tempX = 0;
		int tempY = 0;
		
		int lastIndexX = arr.length;
		int lastIndexY = arr[0].length;
		
		for (int i= -1; i<2; i++){
			for (int j=-1; j<2; j++){
				if(arr[x][y][0] == 1){
					tempX = x+i;
					tempY = y+j;
			
					if(tempX == lastIndexX-1){
						tempX = 1;
					} else if(tempX == 0){
						tempX = lastIndexX - 2;
					}
				
					if(tempY == lastIndexY-1){
						tempY = 1;
					} else if(tempY == 0){
						tempY = lastIndexY - 2;
					}
				
					arr[tempX][tempY][1] += 1;
				}
			}
		}
		
		arr[x][y][1] = save;
	}
	
	
	
	/************************ BOARD INITIALISATION **************************/
	
	/* Creates initial board: initialises board with 0's at each index
	*
	*Input: int [y][x], where y and x are the axis, and are of any lengths
	*Output: int [y][x], with each indices set to 0
	*
	*
	*Could this whole function be replaced with fileInput, if the file GOLSeed also specifies the dimensions for the board? X X X X X X X X
	*
	*/
	public static int[][][] initBoard(int[][][] arr)
	{
		// zero's the board at z plain hold
		int hold = 0;
		arr = zeroBoard(arr, hold);
		
		// gives life to certain indices
		arr = giveLife(arr);
		
		return arr;	
	}
	
	public static int[][][] zeroBoard(int[][][] arr, int hold)
	{
		for (int i=1; i<arr.length-1; i++){
			for (int j=1; j<arr[0].length-1; j++){
				arr[i][j][hold] = 0;
			}
		}
		
		return arr;
	}
	
	
	/* Sets certain Indices to be alive, ie: set to any number other than 0.
	*
	*Input: int[y][x], where y and x are the axis, and are of any length
	*Output: int[y][x], with certain indices set to 1
	* 
	*
	*/
	public static int[][][] giveLife(int[][][] seed)
	{

		seed = fileInput(seed);
	
		// seed[seed.length/2][seed.length/2] = 1;
	
		return seed;
	}
	
	
	/******************************* SCANNING FILES *******************************/
	
	/* Scans the file GOLSeed.txt for the seed of GOL, which is then input directly into
	*		the seed[][][] array.
	*
	*Input: 2D int array
	*Output: 2D int array
	*
	*Catches FileNotFound and InputMismatchException, notifying user, and exiting the program.
	*
	*/
	public static int[][][] fileInput(int[][][] seed)
	{
	
		//attempts to find, and read GOLSeed.txt, catches if the file is not found
		try{
			Scanner fileScan = new Scanner(new File("GOLSeed.txt"));
			for (int i=0; i<seed.length; i++){
				for (int j=0; j<seed[0].length; j++){
					seed[i][j][0] = fileScan.nextInt();
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
		
		return seed;
	}
	
	/******************************* PRINT FUNCTIONS *******************************/
	
	/* prints x and y axis of 3D int arrays to the terminal
	*
	*Input: 3D int array, z plain to print
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
	
	/* prints x and y axis of 3D int arrays to an output file
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