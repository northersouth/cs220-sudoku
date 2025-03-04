package knox.sudoku;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


/**
 * 
 * This is the GUI (Graphical User Interface) for Sudoku.
 * 
 * It extends JFrame, which means that it is a subclass of JFrame.
 * The JFrame is the main class, and owns the JMenuBar, which is 
 * the menu bar at the top of the screen with the File and Help 
 * and other menus.
 * 
 * 
 * One of the most important instance variables is a JCanvas, which is 
 * kind of like the canvas that we will paint all of the grid squared onto.
 * 
 * @author jaimespacco
 *
 */
public class SudokuGUI extends JFrame {
	
	private JFileChooser getFiles = new JFileChooser();
	
	private Sudoku sudoku;
    
	private static final long serialVersionUID = 1L;
	
	// Sudoku boards have 9 rows and 9 columns
    private int numRows = 9;
    private int numCols = 9;
    
    // the current row and column we are potentially putting values into
    private int currentRow = -1;
    private int currentCol = -1;
    
    // the current guessed number for currentRow and currentCol
    private int guess = -1;
    
    // figuring out how big to make each button
    // honestly not sure how much detail is needed here with margins
	protected final int MARGIN_SIZE = 5;
    protected final int DOUBLE_MARGIN_SIZE = MARGIN_SIZE*2;
    protected int squareSize = 90;
    private int width = DOUBLE_MARGIN_SIZE + squareSize * numCols;    		
    private int height = DOUBLE_MARGIN_SIZE + squareSize * numRows;  
    
    private static Font FONT = new Font("Verdana", Font.BOLD, 40);
    private static Color FONT_COLOR = Color.BLACK;
    private static Color BACKGROUND_COLOR = Color.GRAY;
    
    // the canvas is a panel that gets drawn on
    private JPanel panel;

    // this is the menu bar at the top that owns all of the buttons
    private JMenuBar menuBar;
    
    // 2D array of buttons; each sudoku square is a button
    private JButton[][] buttons = new JButton[numRows][numCols];
    
    private class MyKeyListener extends KeyAdapter {
    	public final int row;
    	public final int col;
    	public final Sudoku sudoku;
    	
    	MyKeyListener(int row, int col, Sudoku sudoku){
    		this.sudoku = sudoku;
    		this.row = row;
    		this.col = col;
    	}
    	
    	public void keyTyped(KeyEvent e) {
			char key = e.getKeyChar();
			//System.out.println(key);
			if (Character.isDigit(key)) {
				// use ascii values to convert chars to ints
				int digit = key - '0';
				System.out.println(key);
				if (currentRow == row && currentCol == col) {
					sudoku.set(row, col, digit);
				}
				update();
			} 
			if (key == '-'){
				if(sudoku.getLegalValues(row, col).size() == 0) {
					JOptionPane.showMessageDialog(null, "There are no numbers that can go here,\nthere must be a mistake somewhere.", "Legal Values", JOptionPane.INFORMATION_MESSAGE);

				} else {
					JOptionPane.showMessageDialog(null, "The numbers that could go\n in this square are: " + sudoku.legalValuesToString(row, col), "Legal Values", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
    }
    
    private class ButtonListener implements ActionListener {
    	public final int row;
    	public final int col;
    	public final Sudoku sudoku;
    	ButtonListener(int row, int col, Sudoku sudoku){
    		this.sudoku = sudoku;
    		this.row = row;
    		this.col = col;
    	}
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.printf("row %d, col %d, %s\n", row, col, e);
			JButton button = (JButton)e.getSource();
			
			if (row == currentRow && col == currentCol) {
				currentRow = -1;
				currentCol = -1;
			} else if (sudoku.isBlank(row, col)) {
				// we can try to enter a value in a 
				currentRow = row;
				currentCol = col;
				
				// TODO: figure out some way that users can enter values
				// A simple way to do this is to take keyboard input
				// or you can cycle through possible legal values with each click
				// or pop up a selector with only the legal values
				
				
			} else {
				// TODO: error dialog letting the user know that they cannot enter values
				// where a value has already been placed
			}
			
			update();
		}
		
    }
    
    public void showHint() {
    	//check for squares where there's only one value that could go there
    	for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(sudoku.getLegalValues(i, j).size() == 1) {
					buttons[i][j].setBackground(Color.PINK);
					repaint();
					//return;
				}
			}
		}
	}
    
    /**
     * Put text into the given JButton
     * 
     * @param row
     * @param col
     * @param text
     */
    private void setText(int row, int col, String text) {
    	buttons[row][col].setText(text);
    }
    
    /**
     * This is a private helper method that updates the GUI/view
     * to match any changes to the model
     */
    private void baseCoat() {
    	//set up like update but different to separate starter numbers and new ones
        for (int row=0; row<numRows; row++) {
    		for (int col=0; col<numCols; col++) {
    			buttons[row][col].setForeground(FONT_COLOR);
    			buttons[row][col].setBackground(Color.GRAY);
	    		int val = sudoku.get(row, col);
	    		if (val == 0) {
	    			setText(row, col, "");
	    		} else {
	    			setText(row, col, val+"");
	    		}
    		}
    	}
    	
        repaint();
    }
    
    private void update() {
    	for (int row=0; row<numRows; row++) {
    		for (int col=0; col<numCols; col++) {
    			if (row == currentRow && col == currentCol && sudoku.isBlank(row, col)) {
    				// draw this grid square special!
    				// this is the grid square we are trying to enter value into
    				buttons[row][col].setForeground(Color.RED);
    				// I can't figure out how to change the background color of a grid square, ugh
    				// Maybe I should have used JLabel instead of JButton?
    				buttons[row][col].setBackground(Color.CYAN);
    				if(sudoku.get(row, col) == 0) {
    					setText(row, col, "_");
    				} else {
    					setText(row, col, String.valueOf(sudoku.get(row, col)));
    				}
    				
    			} else if (sudoku.getBase(row, col) == 0){
    				buttons[row][col].setForeground(Color.BLUE);
    				buttons[row][col].setBackground(Color.GRAY);
	    			int val = sudoku.get(row, col);
	    			if (val == 0) {
	    				setText(row, col, "");
	    			} else {
	    				setText(row, col, val+"");
	    			}
    			}
    		}
    		//victory check!
    		if(sudoku.victoryCheck()) {
    			JOptionPane.showMessageDialog(null, "Board Complete: You Win!");
    			System.exit(0);
    		}
    		
    	}
    	repaint();
    }
    
	
    private void createMenuBar() {
    	menuBar = new JMenuBar();
        
    	//
    	// File menu
    	//
    	JMenu file = new JMenu("File");
        menuBar.add(file);
        
        addToMenu(file, "New Game", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.load("easy1.txt");
                repaint();
            }
        });
        
        addToMenu(file, "Save", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// TODO: save the current game to a file!
            	// HINT: Check the Util.java class for helpful methods
            	// HINT: check out JFileChooser
            	// https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
            	
            	int returnVal = getFiles.showSaveDialog(getOwner());
            	if(returnVal == JFileChooser.APPROVE_OPTION) {

					try {
						PrintStream out = new PrintStream(new File(getFiles.getSelectedFile().getName()));
						out.print(sudoku.boardState());
						out.flush();
						out.close();
					} catch (Exception f) {
						// lazy way to convert all static (checked) exceptions into 
						throw new RuntimeException(f);
					}

            	}
            	
                update();
            }
        });
        
        addToMenu(file, "Load", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	//the returnVal stuff is borrowed from the class document itself
            	int returnVal = getFiles.showOpenDialog(getOwner());
            	if(returnVal == JFileChooser.APPROVE_OPTION) {
            		sudoku.load(getFiles.getSelectedFile().getName());
                    baseCoat();
            	}
            	
                
            }
        });
        
        //
        // Help menu
        //
        JMenu help = new JMenu("Help");
        menuBar.add(help);
        
        addToMenu(help, "Controls", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Click on a square, then type 1-9 to try\n" + 
													"and fill it in. Type 0 to clear the square.");
			}
		});
        
        addToMenu(help, "Hint Spots", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Here's a hint! Only one number\n" + 
													"can go in the pink squares.");
				showHint();
			}
		});
        
        addToMenu(help, "Legal Values", new ActionListener(){
        	@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "To show all the values that could go\n" + 
													"in a square, just press ' - '.");
			}
        });
        
        this.setJMenuBar(menuBar);
    }
    
    
    /**
     * Private helper method to put 
     * 
     * @param menu
     * @param title
     * @param listener
     */
    private void addToMenu(JMenu menu, String title, ActionListener listener) {
    	JMenuItem menuItem = new JMenuItem(title);
    	menu.add(menuItem);
    	menuItem.addActionListener(listener);
    }
    
    private void createMouseHandler() {
    	MouseAdapter a = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.printf("%s\n", e.getButton());
			}
    		
    	};
        this.addMouseMotionListener(a);
        this.addMouseListener(a);
    }
    
    
    private void createKeyboardHandlers() {
    	for (int r=0; r<buttons.length; r++) {
    		for (int c=0; c<buttons[r].length; c++) {
    			buttons[r][c].addKeyListener(new MyKeyListener(r, c, sudoku));
    			/*
    			buttons[r][c].addKeyListener(new KeyAdapter() {
    				@Override
    				public void keyTyped(KeyEvent e) {
    					char key = e.getKeyChar();
    					System.out.println(key);
    					if (Character.isDigit(key)) {
    						System.out.println(key);
    						if (currentRow > -1 && currentCol > -1) {
    							guess = Integer.parseInt(key + "");
    						}
    					}
    					
    				}
    			});
    			*/
    		}
    	}
    	/*
    	this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char key = e.getKeyChar();
				System.out.println(key);
				if (Character.isDigit(key)) {
					System.out.println(key);
					if (currentRow > -1 && currentCol > -1) {
						guess = Integer.parseInt(key + "");
					}
				}
				
			}
		});
		*/
    }
    
    public SudokuGUI() {
        sudoku = new Sudoku();
        // load a puzzle from a text file
        // right now we only have 1 puzzle, but we could always add more!
        sudoku.load("easy1.txt");
        
        setTitle("Sudoku!");

        this.setSize(width, height);
        
        // the JPanel where everything gets painted
        panel = new JPanel();
        // set up a 9x9 grid layout, since sudoku boards are 9x9
        panel.setLayout(new GridLayout(9, 9));
        // set the preferred size
        // If we don't do this, often the window will be minimized
        // This is a weird quirk of Java GUIs
        panel.setPreferredSize(new Dimension(width, height));
        
        // This sets up 81 JButtons (9 rows * 9 columns)
        for (int r=0; r<numRows; r++) {
        	for (int c=0; c<numCols; c++) {
        		JButton b = new JButton();
        		b.setPreferredSize(new Dimension(squareSize, squareSize));
        		
        		b.setFont(FONT);
        		b.setForeground(FONT_COLOR);
        		b.setBackground(BACKGROUND_COLOR);
        		buttons[r][c] = b;
        		// add the button to the canvas
        		// the layout manager (the 9x9 GridLayout from a few lines earlier)
        		// will make sure we get a 9x9 grid of these buttons
        		panel.add(b);

        		// thicker borders in some places
        		// sudoku boards use 3x3 sub-grids
        		int top = 1;
        		int left = 1;
        		int right = 1;
        		int bottom = 1;
        		if (r % 3 == 2) {
        			bottom = 5;
        		}
        		if (c % 3 == 2) {
        			right = 5;
        		}
        		if (r == 0) {
        			top = 5;
        		}
        		if (c == 9) {
        			bottom = 5;
        		}
        		b.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.black));
        		
        		//
        		// button handlers!
        		//
        		// check the ButtonListener class to see what this does
        		//
        		b.addActionListener(new ButtonListener(r, c, sudoku));
        	}
        }
        
        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(width, height));
        this.setResizable(false);
        this.pack();
        this.setLocation(100,100);
        this.setFocusable(true);
        
        createMenuBar();
        createKeyboardHandlers();
        createMouseHandler();
        
        // close the GUI application when people click the X to close the window
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        baseCoat();
        
    }
    
    public static void main(String[] args) {
        SudokuGUI g = new SudokuGUI();
        g.setVisible(true);
    }

}
