package razvangeangu.model;

public class Board {
	
	private int rows, columns, difficulty, currentTime, bombs;
	private double bestScore, bestTime, currentScore;
	private boolean openingMove, marks, areaOpen, openRemaining;
	private int[][] board;
	
	public Board() {
		board = new int[8][8];
		bombs = 64 / 3;
		fillBoard();
	}
	
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
	
	public void resetBoard() {
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < columns; ++c) {
				board[r][c] = 0;
			}
		}
		
		fillBoard();
	}
	
	private void increaseFreeCells(int r, int c) {
		int[] rSet = new int[] {-1, -1, -1, 0, 1, 1, 1, 0};
		int[] cSet = new int[] {-1, 0, 1, 1, 1, 0, -1, -1};
		
		for (int i = 0; i < rSet.length; i++) {
			if (r - rSet[i] >= 0 && c - cSet[i] >= 0 && r - rSet[i] < rows && c - cSet[i] < columns) {
				board[r - rSet[i]][c - cSet[i]] += 1;
			}
		}
	}
	
	public int[][] getBoard() {
		return board;
	}
	
//	public static void main(String[] args) {
//		try {
//			Board board = new Board(8, 8);
//			int[][] myBoard = board.getBoard();
//			for (int i = 0; i < 8; i++) {
//				for (int j = 0; j < 8; j++) {
//					System.out.print(myBoard[i][j] + " ");
//				}
//				System.out.println();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public int getBombs() {
		return bombs;
	}

	public void setBombs(int bombs) {
		this.bombs = bombs;
	}
	
	
}
