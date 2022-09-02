package knox.sudoku;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * 
 * This is the MODEL class. This class knows all about the
 * underlying state of the Sudoku game. We can VIEW the data
 * stored in this class in a variety of ways, for example,
 * using a simple toString() method, or using a more complex
 * GUI (Graphical User Interface) such as the SudokuGUI 
 * class that is included.
 * 
 * @author jaimespacco
 *
 */
public class Sudoku {
	int[][] playBoard = new int[9][9];
	int[][] baseBoard = new int[9][9];
	
	public int get(int row, int col) {
		// TODO: check for out of bounds
		return playBoard[row][col];
	}
	
	public int getBase(int row, int col) {
		return baseBoard[row][col];
	}
	
	public void set(int row, int col, int val) {
		if(this.isLegal(row, col, val)) {
			playBoard[row][col] = val;
		} else {
			JOptionPane.showMessageDialog(null, "A " + val + " can't go here.", "Illegal Value", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public boolean isLegal(int row, int col, int val) {
		//zeros are always legal to clear squares
		if(val == 0) {
			return true;
		}
		
		for(int i = 0; i < 9; i++) {
			//row check
			if(this.get(row, i) == val) {
				return false;
			}
			//col check
			if(this.get(i, col) == val) {
				return false;
			}
		}
		//get square properly cornered
		int sqRow, sqCol;
		
		if(0 <= row && row <= 2) {
			sqRow = 0;
		} else if (3 <= row && row <= 5) {
			sqRow = 3;
		} else {
			sqRow = 6;
		}
	
		if(0 <= col && col <= 2) {
			sqCol = 0;
		} else if (3 <= col && col <= 5) {
			sqCol = 3;
		} else {
			sqCol = 6;
		}
		//square check
		for(int i = sqRow; i < sqRow + 3; i++) {
			for(int j = sqCol; j < sqCol + 3; j++) {
				if(this.get(i, j) == val) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public Collection<Integer> getLegalValues(int row, int col) {
		// TODO: return only the legal values that can be stored at the given row, col
		Collection<Integer> temp = new LinkedList();
		
		//if not empty, there aren't any legal values!
		if(this.get(row, col) != 0) {
			return temp;
		}
		
		for(int i = 1; i < 10; i++) {
			if(this.isLegal(row, col, i)) {
				temp.add(i);
			}
		}
		return temp;
	}
	
	public String legalValuesToString (int row, int col) {
		Collection<Integer> temp = this.getLegalValues(row, col);
		
		String values = "";
		
		Iterator goGo = temp.iterator();
		
		while(goGo.hasNext()) {
			values = values +  String.valueOf(goGo.next()) + ", ";
		}
		
		return values.substring(0, values.length() - 2);
	}
	
	public Boolean victoryCheck() {
		//if there's still an empty square, not a victory
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(this.get(i,  j) == 0) {
					return false;
				}
			}
		}
		//if all filled, because of the legal checks it must be a win!
		return true;
	}
	
	
/**

_ _ _ 3 _ 4 _ 8 9
1 _ 3 2 _ _ _ _ _
etc


0 0 0 3 0 4 0 8 9

 */
	public void load(String filename) {
		try {
			Scanner scan = new Scanner(new FileInputStream(filename));
			// read the file
			for (int r=0; r<9; r++) {
				for (int c=0; c<9; c++) {
					int val = scan.nextInt();
					baseBoard[r][c] = val;
					playBoard[r][c] = val;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Return which 3x3 grid this row is contained in.
	 * 
	 * @param row
	 * @return
	 */
	public int get3x3row(int row) {
		return row / 3;
	}
	
	/**
	 * Convert this Sudoku board into a String
	 */
	public String toString() {
		String result = "";
		for (int r=0; r<9; r++) {
			for (int c=0; c<9; c++) {
				int val = get(r, c);
				if (val == 0) {
					result += "_ ";
				} else {
					result += val + " ";
				}
			}
			result += "\n";
		}
		return result;
	}
	
	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku();
		sudoku.load("easy1.txt");
		System.out.println(sudoku);
		
		Scanner scan = new Scanner(System.in);
		while (!sudoku.gameOver()) {
			System.out.println("enter value r, c, v :");
			int r = scan.nextInt();
			int c = scan.nextInt();
			int v = scan.nextInt();
			sudoku.set(r, c, v);

			System.out.println(sudoku);
		}
	}

	public boolean gameOver() {
		// TODO check that there are still open spots
		return false;
	}

	public boolean isBlank(int row, int col) {
		return baseBoard[row][col] == 0;
	}
	
	public String boardState() {
		String theBOARD = "";
		
		for (int r=0; r<9; r++) {
			for (int c=0; c<9; c++) {
				theBOARD = theBOARD + this.get(r, c) + " ";
			}
			theBOARD = theBOARD + "\n";
		}
		
		return theBOARD;
	}

}
