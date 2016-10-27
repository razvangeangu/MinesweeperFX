package razvangeangu.model;

import java.util.prefs.Preferences;

/**
 * The main model of the application described as the 'board'.
 * @author Razvan-Gabriel Geangu
 *
 */
public class Board {
	
	private int rows, columns, bombs;
	private double bestTime, currentTime;
//	private boolean openingMove, marks, areaOpen, openRemaining;
	private int[][] board;
	private String difficulty;
	
	/**
	 * A constructor for a default game of 8x8 squares with 1/3 of squares bombs
	 */
	public Board() {
		board = new int[8][8];
		bombs = 64 / 3;
		difficulty = GlobalModel.BESTSCORE_EASY;
		fillBoard();
		setDefaultScores();
	}
	
	/**
	 * A method to create a new board game.
	 * @param rows The number of rows of the board.
	 * @param columns The number of columns of the board.
	 * @throws Exception If the number of rows/columns is less than 8 or bigger than 100
	 * or the number of bombs is bigger than 1/3 of the squares.
	 */
	public Board(int rows, int columns) throws Exception {
		if (rows >= 8 && columns >= 8) {
			this.rows = rows;
			this.columns = columns;
			board = new int[rows][columns];
			this.bombs = (rows * columns) / 3;
			fillBoard();
		} else {
			throw new Exception("Minesweeper dimensions invalid:\nWidth: from 8 to 100\nHeight: from 8 to 100\nBombs: 1 to 1/3 of squares");
		}
	}
	
	/**
	 * Set the default values for the best scores to maximum of double.
	 */
	private void setDefaultScores() {
		if (readPreference(GlobalModel.BESTSCORE_EASY) == 0.0) {
			savePreference(Double.MAX_VALUE, GlobalModel.BESTSCORE_EASY);
		}
		
		if (readPreference(GlobalModel.BESTSCORE_MEDIUM) == 0.0) {
			savePreference(Double.MAX_VALUE, GlobalModel.BESTSCORE_MEDIUM);
		}
		
		if (readPreference(GlobalModel.BESTSCORE_HARD) == 0.0) {
			savePreference(Double.MAX_VALUE, GlobalModel.BESTSCORE_HARD);
		}
		
		if (readPreference(GlobalModel.BESTSCORE_CUSTOM) == 0.0) {
			savePreference(Double.MAX_VALUE, GlobalModel.BESTSCORE_CUSTOM);
		}
	}
	
	/**
	 * Fill the board with bombs and increase the numbers within the free cells
	 * accordingly to the minesweeper algorithm.
	 */
	private void fillBoard() {
		int randomRow, randomColumn;
		
		for (int i = 0; i < bombs; ++i) {
			randomRow = (int)(Math.random() * rows);
			randomColumn = (int)(Math.random() * columns);
			board[randomRow][randomColumn] = -1;
		}
		
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < columns; ++c) {
				if (board[r][c] == -1) {
					increaseFreeCells(r, c);
				}
			}
		}
	}
	
	/**
	 * Reset the board with another number of rows, columns and bombs.
	 * @param rows The new number of rows.
	 * @param columns The new number of columns.
	 * @param bombs The new number of bombs.
	 */
	public void resetBoard(int rows, int columns, int bombs) {
		board = new int[rows][columns];
		this.rows = rows;
		this.columns = columns;
		this.bombs = bombs;
		
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < columns; ++c) {
				board[r][c] = 0;
			}
		}
		
		fillBoard();
	}
	
	/**
	 * A method to increase the numbers within the free cells wherever a bomb
	 * accordingly to the minesweeper algorithm.
	 * @param r The row of the square.
	 * @param c The column of the square.
	 */
	private void increaseFreeCells(int r, int c) {
		int[] rSet = new int[] {-1, -1, -1, 0, 1, 1, 1, 0};
		int[] cSet = new int[] {-1, 0, 1, 1, 1, 0, -1, -1};
		
		for (int i = 0; i < rSet.length; i++) {
			if (r - rSet[i] >= 0 && c - cSet[i] >= 0 && r - rSet[i] < rows && c - cSet[i] < columns) {
				board[r - rSet[i]][c - cSet[i]] += 1;
			}
		}
	}
	
	/**
	 * A method used to save data in the registry system used for saving
	 * best score.
	 * @param bestScore The value of the information in the system.
	 * @param preference The key of the information in the system.
	 */
    public void savePreference(Double bestScore, String preference) {
        Preferences prefs = Preferences.userNodeForPackage(Board.class);

        prefs.putDouble(preference, bestScore);
    }

    /**
     * A method to read the data saved in the registry system used for 
     * retrieving the best score.
     * @param preference
     * @return
     */
    public Double readPreference(String preference) {
        Preferences prefs = Preferences.userNodeForPackage(Board.class);

        return prefs.getDouble(preference, 0.0);
    }
    
    /**
     * A method to check if the current best score is better than the old one,
     * in which case the score is replaced in the system registry.
     */
    public void checkBestScoreAndChangeIt() {
    	bestTime = readPreference(difficulty);
    	
    	if (currentTime < bestTime) {
    		bestTime = currentTime;
    		savePreference(bestTime, difficulty);
    	}
    }
	
	/**
	 * Get a reference for the board.
	 * @return An object that represents the board as an int[][].
	 */
	public int[][] getBoard() {
		return board;
	}
	
	// Testing main method
	/*
	public static void main(String[] args) {
		try {
			Board board = new Board(8, 8);
			int[][] myBoard = board.getBoard();
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					System.out.print(myBoard[i][j] + " ");
				}
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/

	/**
	 * Get a reference for the number of rows.
	 * @return An object that represents the number of rows as an int.
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * A method to set the number of rows in the Board.
	 * @param rows The number of rows for the Board.
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * Get a reference for the number of columns.
	 * @return An object that represents the number of columns as an int.
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * A method to set the number of columns in the Board.
	 * @param columns The number of columns for the Board.
	 */
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	/**
	 * Get a reference for the number of bombs.
	 * @return An object that represents the number of bombs as an int.
	 */
	public int getBombs() {
		return bombs;
	}

	/**
	 * A method to set the number of bombs in the Board.
	 * @param columns The number of bombs for the Board.
	 */
	public void setBombs(int bombs) {
		this.bombs = bombs;
	}
	
	/**
	 * Get a reference for the current time.
	 * @return An object that represents the current time as an int.
	 */
	public double getCurrentTime() {
		return currentTime;
	}
	
	/**
	 * A method to set the current time of the sessions.
	 * @param currentTime The current time in the board game session.
	 */
	public void setCurrentTime(double currentTime) {
		this.currentTime = currentTime;
	}

	/**
	 * Get a reference for the difficulty.
	 * @return An object that represents the difficulty as a String.
	 */
	public String getDifficulty() {
		return difficulty;
	}

	/**
	 * A method to set difficulty description of the game.
	 * @param difficulty The type of the difficulty as a String
	 * @see GlobalModel for suggestions.
	 */
	public String setDifficulty(String difficulty) {
		this.difficulty = difficulty;
		return this.difficulty;
	}
}
