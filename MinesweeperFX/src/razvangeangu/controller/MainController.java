package razvangeangu.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import razvangeangu.model.Board;
import razvangeangu.model.GlobalModel;
import razvangeangu.view.CustomSquare;
import razvangeangu.view.GlobalView;

/**
 * The main controller of the application.
 * @author Razvan-Gabriel Geangu
 *
 */
public class MainController implements Initializable {

	@FXML private Button restartButton;
	@FXML private GridPane boardView;
	@FXML private Label bombsLabel;
	@FXML private Label timeLabel;
	
	@FXML private MenuItem newGameMenuItem;
	@FXML private MenuItem beginnerMenuItem;
	@FXML private MenuItem intermediateMenuItem;
	@FXML private MenuItem expertMenuItem;
	@FXML private MenuItem customGameMenuItem;
	@FXML private MenuItem personalBestMenuItem;
	@FXML private MenuItem championshipMenuItem;
	@FXML private MenuItem exitMenuItem;
	@FXML private MenuItem instructionsMenuItem;
	@FXML private MenuItem aboutMenuItem;
	@FXML private MenuItem undoMenuItem;
	@FXML private MenuItem redoMenuItem;
	
	private Board board;
	private Double timeSeconds;
	private Timeline timeline;
	private static boolean firstClick = true;
	private boolean shouldClearUndo = false;
	
	private Stage stage;
	private GlobalView global;
	
	private ObservableList<String> squaresStates;
	private LinkedList<ObservableList<String>> undoSteps;
	private LinkedList<ObservableList<String>> redoSteps;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Create a new board (model)
		board = new Board();
		
		// Add global view methods
		global = new GlobalView();
		
		// Instantiate the lists
		squaresStates = FXCollections.observableArrayList();
		undoSteps = new LinkedList<ObservableList<String>>();
		redoSteps = new LinkedList<ObservableList<String>>();
		
		// Setup the menu items and buttons onAction values
		setUpMenuItemsAndButtons();
 
        // Update timerLabel
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new EventHandler() {
			@Override
			public void handle(Event event) {
                timeSeconds++;
                timeLabel.setText(convertSecToTime(timeSeconds));
			}
        }));
        
        // Start a easy session
        startGame(8, 8, 10, false, GlobalModel.BESTSCORE_EASY);
    	}

	/**
	 * A method used to start the game.
	 * @param rows The number of rows for the game (within 8-100);
	 * @param columns The number of columns for the game (within 8-100)
	 * @param bombs The number of bombs for the game (maximum 1/3 of rows*columns)
	 * @param handler A handler that tells the method if it should resize or not the window accordingly to the dimensions.
	 * @param difficulty The difficulty of the game.
	 */
	private void startGame(int rows, int columns, int bombs, boolean handler, String difficulty) {
		// Clear redo and undo
		redoSteps.clear();
		undoSteps.clear();
		
		// Clear the board
		boardView.getChildren().clear();
		
		// Stop the timer
		timeline.stop();
		
		// If the dimensions have been changed, adjust the window size
		if (handler) {
			setBoardViewSize(rows, columns);
		}
		
		// Set the view of the board
		setBoard(rows, columns, bombs);
		
		bombsLabel.setText(Integer.toString(board.getBombs()));
        timeSeconds = 0.0;
        firstClick = true;
		timeLabel.setText(convertSecToTime(0.0));
		
		// Set the default difficulty to the game
		board.setDifficulty(difficulty);
	}
	
	/**
	 * A method to resize the window accordingly to the rows and columns of the board.
	 * The method also adds the sizes of other components in order to show a correct board.
	 * @param rows The number of the rows in the board.
	 * @param columns The number of columns in the board.
	 */
	private void setBoardViewSize(int rows, int columns) {
		stage.setWidth(columns * 20 + 10);
		stage.setHeight(rows * 20 + 92);
	}

	private void setUpMenuItemsAndButtons() {
		newGameMenuItem.setOnAction(handler -> {
			startGame(board.getRows(), board.getColumns(), board.getBombs(), true, board.getDifficulty());
		});
		restartButton.setOnAction(handler -> {
			startGame(board.getRows(), board.getColumns(), board.getBombs(), true, board.getDifficulty());
		});
		beginnerMenuItem.setOnAction(handler -> {
			startGame(8, 8, 10, true, board.setDifficulty(GlobalModel.BESTSCORE_EASY));
		});
		intermediateMenuItem.setOnAction(handler -> {
			startGame(16, 16, 40, true, board.setDifficulty(GlobalModel.BESTSCORE_MEDIUM));
		});
		expertMenuItem.setOnAction(handler -> {
			startGame(16, 31, 99, true, board.setDifficulty(GlobalModel.BESTSCORE_HARD));
		});
		customGameMenuItem.setOnAction(handler -> {
			if (timeline.getStatus() != Animation.Status.STOPPED) {
				timeline.pause();
			}
			
			ArrayList<String> fields = new ArrayList<String>();
			fields.add("Rows"); fields.add("Columns"); fields.add("Bombs");
			ArrayList<String> results = global.showTextInputDialog("Custom setup", "Create your custom game", fields);
			
			if (results != null) {
				startGame(Integer.parseInt(results.get(0)), Integer.parseInt(results.get(1)), Integer.parseInt(results.get(2)), true, board.setDifficulty(GlobalModel.BESTSCORE_CUSTOM));
			}
			
			if (timeline.getStatus() == Animation.Status.PAUSED) {
				timeline.play();
			}
		});
		personalBestMenuItem.setOnAction(handler -> {
			if (timeline.getStatus() != Animation.Status.STOPPED) {
				timeline.pause();
			}
			
			ArrayList<String> scores = new ArrayList<String>();
			Collections.addAll(scores, board.readPreference(GlobalModel.BESTSCORE_EASY).toString(), board.readPreference(GlobalModel.BESTSCORE_MEDIUM).toString(), board.readPreference(GlobalModel.BESTSCORE_HARD).toString(), board.readPreference(GlobalModel.BESTSCORE_CUSTOM).toString());
			global.showChoiceDialog("Personal Best", "Your personal best score is the following:", board.readPreference(GlobalModel.BESTSCORE_EASY).toString(), scores);
			
			if (timeline.getStatus() == Animation.Status.PAUSED) {
				timeline.play();
			}
		});	
		championshipMenuItem.setOnAction(handler -> {
			try {
				Desktop.getDesktop().browse(new URL("http://www.minesweeper.info/worldranking.html").toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		exitMenuItem.setOnAction(handler -> {
			Platform.exit();
		});
		instructionsMenuItem.setOnAction(handler -> {
			if (timeline.getStatus() != Animation.Status.STOPPED) {
				timeline.pause();
			}
			
			global.showDialog("Instructions", 
					"Instructions for Minesweeper", 
					"Quick Start:\n\nYou are presented with a board of squares. Some squares contain mines (bombs), others don\'t. If you click on a square containing a bomb, you lose. If you manage to click all the squares (without clicking on any bombs) you win.\nClicking a square which doesn\'t have a bomb reveals the number of neighbouring squares containing bombs. Use this information plus some guess work to avoid the bombs.\nTo open a square, point at the square and click on it. To mark a square you think is a bomb, point and right-click (or hover with the mouse and press Space).", "Detailed Instructions:\n\nA squares \"neighbours\" are the squares adjacent above, below, left, right, and all 4 diagonals. Squares on the sides of the board or in a corner have fewer neighbors. The board does not wrap around the edges.\nIf you open a square with 0 neighboring bombs, all its neighbors will automatically open. This can cause a large area to automatically open.\nTo remove a bomb marker from a square, point at it and right-click again.\nThe first square you open is never a bomb.\nIf you mark a bomb incorrectly, you will have to correct the mistake before you can win. Incorrect bomb marking doesn\'t kill you, but it can lead to mistakes which do.\nYou don\'t have to mark all the bombs to win; you just need to open all non-bomb squares.\nRight-clicking twice will give you a question mark symbol which can be useful if you are unsure about a square\nClick the yellow happy face to start a new game.\nStatus Information:\n\nThe upper left corner contains the number of bombs left to find. The number will update as you mark and unmark squares.\nThe upper right corner contains a time counter. The timer will max out at 999 (16 minutes 39 seconds).\nClick on the time to switch to the number of moves counter. Click again to switch back to the time.\nOptions and Enhancements:\n\nLearning Mode - Show the contents of all unopened cells. Scores do not count towards high scores, and the Opening Move option does not apply.\nOpening Move - Not only will the first square never be a bomb, but neither will any of the neighbors.\nMarks (?) - Right clicking on a marked bomb will change the flag into a question mark. Right clicking again will change it back into an unmarked square.\nArea Open - If an open square has the correct number of marked neighboring bombs, click on the open square to open all remaining unopened neighbor squares all at once. If an incorrect number of neighbors are marked, or all neighbors are marked or open, clicking the square has no effect. If an incorrect neighbor is marked, this will cause instant death.\nOpen Remaining - Once the correct number of bombs have been marked, the bomb counter will turn blue. Click on the blue bomb counter to open all remaining cells. If any bombs are incorrectly marked, this will cause instant death.", 
					AlertType.INFORMATION);
			
			if (timeline.getStatus() == Animation.Status.PAUSED) {
				timeline.play();
			}
		});
		aboutMenuItem.setOnAction(handler -> {
			if (timeline.getStatus() != Animation.Status.STOPPED) {
				timeline.pause();
			}
			
			global.showDialog("About Minesweeper", 
					"History", 
					"The history of Minesweeper goes back to the early 80\'s with many mine-type games being released in that period. The earliest proven ancestor of Minesweeper is Mined-Out, which was released for the Sinclair Spectrum in 1983.\n\nThe game evolved over the 1980\'s into the game we love today. Most of the key features like right-click (to mark a mine) and numbers (showing you adjacent bombs) evolved in the last 2-3 years of the 80\'s.", 
					null, 
					AlertType.INFORMATION);
			
			if (timeline.getStatus() == Animation.Status.PAUSED) {
				timeline.play();
			}
		});
		undoMenuItem.setOnAction(handler -> {
			undo();
		});
		redoMenuItem.setOnAction(handler -> {
			redo();
		});
		
		undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.U, KeyCombination.SHORTCUT_DOWN));
		redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
	}
	
	/**
	 * A method to reset the view of the board.
	 * @param rows The number of rows in the board.
	 * @param columns The number of columns in the board.
	 * @param bombs The number of bombs in the board.
	 */
	private void setBoard(int rows, int columns, int bombs) {
		board.resetBoard(rows, columns, bombs);
		
		for (int i = 0; i < board.getRows(); i++) {
			addNewRow(i, board.getColumns());
		}
	}

	/**
	 * Add a new row to the gridPane at the index provided.
	 * @param rowIndex The index where the row is to be added.
	 */
	private void addNewRow(int rowIndex, int numColumns) {
		int numRows = 1;
		for (Node node : boardView.getChildren()) {
			int currentRow = GridPane.getRowIndex(node);
			if (currentRow >= rowIndex) {
				GridPane.setRowIndex(node, currentRow + 1);
				if (currentRow + 1 > numRows) {
					numRows = currentRow + 1;
				}
			}
		}

		for (int i = 0; i < numColumns; i++) {
			CustomSquare square = new CustomSquare();
			square.setType(board.getBoard()[rowIndex][i]);
			square.getIcon().setOnMouseClicked(new ImageHandler(square));
			square.setRow(rowIndex);
			square.setColumn(i);
			squaresStates.add(square.getTypeDescription());
			boardView.add(square, i, rowIndex);
		}
	}

	/**
	 * A method that is used to show every bomb on the boardView when one was found.
	 */
	private void endGameAndShowBombs(int iconType) {
		timeline.stop();
		for (Node square : boardView.getChildren()) {
			if (square instanceof CustomSquare) {
				((CustomSquare) square).getIcon().setOnMouseClicked(null);
				if (((CustomSquare) square).getType() == -1) {
					((CustomSquare) square).setIcon(iconType);
				}
			}
		}
		board.setCurrentTime(timeSeconds);
	}
	
	/**
	 * A method to resume the game when undo from a last checkpoint.
	 */
	private void resumeGame() {
		timeline.play();
		for (Node square : boardView.getChildren()) {
			if (square instanceof CustomSquare) {
				((CustomSquare) square).getIcon().setOnMouseClicked(new ImageHandler((CustomSquare)square));
			}
		}
		board.setCurrentTime(timeSeconds);
	}
	
	/**
	 * Lee algorithm implemented to disclose any neighbour which is a free space
	 * or it is the first number found in the search. 
	 * @param row The initial row of the free space.
	 * @param column The initial column of the free space.
	 * @author Razvan-Gabriel Geangu, Tania-Laura Copocean
	 */
	private void showNeighbours(int row, int column) {
	     // First check for boundaries...
	     if (row + 1 < board.getRows()) {
	    	 // Go down...
	    	 CustomSquare square = ((CustomSquare)boardView.getChildren().get((row + 1) * board.getColumns() + column));
	    	 if (square.getType() > -1 && !square.getTypeDescription().equals("flag")) {
	    		 if (square.getType() == 0) {
	    			 if (!square.getTypeDescription().equals("free")) {
	    				 square.setIcon(-2);
	    			 	showNeighbours(row + 1, column);
	    			 }
	    		 } else {
	    			 square.setIcon(square.getType());
	    		 }
	    	 }
	     }
	     
	     // First check for boundaries...
	     if (row - 1 >= 0) {
	    	 // Go up...
	    	 CustomSquare square = ((CustomSquare)boardView.getChildren().get((row - 1) * board.getColumns() + column));
	    	 if (square.getType() > -1 && !square.getTypeDescription().equals("flag")) {
	    		 if (square.getType() == 0) {
	    			 if (!square.getTypeDescription().equals("free")) {
	    				 square.setIcon(-2);
		    			 showNeighbours(row - 1, column);
	    			 }
	    		 } else {
	    			 square.setIcon(square.getType());
	    		 }
	    	 }
	     }
	     
	     // First check for boundaries...
	     if (column + 1 < board.getColumns()) {
	    	 // Go right...
	    	 CustomSquare square = ((CustomSquare)boardView.getChildren().get(row * board.getColumns() + column + 1));
	    	 if (square.getType() > -1 && !square.getTypeDescription().equals("flag")) {
	    		 if (square.getType() == 0) {
	    			 if (!square.getTypeDescription().equals("free")) {
	    			 	square.setIcon(-2);
	    			 	showNeighbours(row, column + 1);
	    			 }
	    		 } else {
	    			 square.setIcon(square.getType());
	    		 }
	    	 }
	     }
	     
	     // First check for boundaries...
	     if (column - 1 >= 0) {
	    	 // Go left...
	    	 CustomSquare square = ((CustomSquare)boardView.getChildren().get(row * board.getColumns() + column - 1));
	    	 if (square.getType() > -1 && !square.getTypeDescription().equals("flag")) {
	    		 if (square.getType() == 0) {
	    			 if (!square.getTypeDescription().equals("free")) {
		    			 square.setIcon(-2);
		    			 showNeighbours(row, column -1);
	    			 }
	    		 } else {
	    			 square.setIcon(square.getType());
	    		 }
	    	 }
	     }
	}
	
	/**
	 * A method to convert the time from seconds to a String time stamp in the format mm:ss.
	 * @param sec The amount of seconds to be converted to a String time stamp.
	 * @return A String that represents the current time in the format mm:ss.
	 */
	public String convertSecToTime(Double sec) {
		String time = "";
		
		int minutes = (int) ((sec % 3600) / 60);
		int seconds = (int) (sec % 60);

		time = String.format("%02d:%02d", minutes, seconds);
		
		return time;
	}
	
	/**
	 * A method used in Main of the application to set the stage for this controller.
	 * The role of the stage in this controller is to be able to resize it accordingly 
	 * to the dimensions of the board.
	 * @param stage The stage of this controller.
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	/**
	 * A method that checks if the game should end. 
	 * @return True if the board has only undisclosed square of which none of them is a bomb. 
	 * False otherwise.
	 */
	public boolean shouldEndGame() {
		for (Node square : boardView.getChildren()) {
			if (square instanceof CustomSquare) {
				if (((CustomSquare)square).getTypeDescription().equals("undisclosed")) {
					if (((CustomSquare)square).getType() != -1) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * A method that uses a list of containing all the states of the game 
	 * to be able to undo one or more moves.
	 */
	private void undo() {
		if (!undoSteps.isEmpty()) {
			redoSteps.addFirst(getCurrentStates());
			updateSquares(undoSteps.removeFirst());
			shouldClearUndo = true;
			
			if (undoSteps.isEmpty()) {
				undoMenuItem.setDisable(true);
			}
			
			if (!redoSteps.isEmpty()) {
				redoMenuItem.setDisable(false);
			}
		}
		
		if (timeline.getStatus() == Animation.Status.STOPPED) {
			resumeGame();
		}
		
		redoMenuItem.setDisable(false);
	}
	
	/**
	 * A method that uses a list of containing all the states of the game that
	 * have been undone and the current state to be able to redo one or more moves.
	 */
	private void redo() {
		if (!redoSteps.isEmpty()) {
			undoSteps.addFirst(getCurrentStates());
			updateSquares(redoSteps.removeFirst());
			
			// 1 from currentState which is always in.
			if (redoSteps.isEmpty() || redoSteps.size() == 1) {
				redoMenuItem.setDisable(true);
			}
			
			if (!undoSteps.isEmpty()) {
				undoMenuItem.setDisable(false);
			}
		}
	}
	
	/**
	 * A method that tries to get the current states of each square from the board.
	 * @return An ObservableList that contains the states of each square.
	 */
	private ObservableList<String> getCurrentStates() {
		ObservableList<String> states = FXCollections.observableArrayList();
		
		for (Node square: boardView.getChildren()) {
			if (square instanceof CustomSquare) {
				states.add(((CustomSquare)square).getTypeDescription());
			}
		}
		
		return states;
	}
	
	/**
	 * A method that updates each square on the board to the new state.
	 * @param states An ObservableList that contains each state for every square on the board.
	 */
	private void updateSquares(ObservableList<String> states) {
		int i = 0;
		for (Node square: boardView.getChildren()) {
			if (square instanceof CustomSquare) {
				((CustomSquare) square).setIcon(global.getIconType(states.get(i++)));
			}
		}
	}
	
	/**
	 * A custom implementation of the ImageHandler the has a CustomSquare and
	 * a custom handler used in order to give feedback to the user accordingly to the 
	 * logic of the game.
	 * @author Razvan-Gabriel Geangu
	 */
	private class ImageHandler implements EventHandler<MouseEvent> {
		
		private CustomSquare square;
		
		public ImageHandler(CustomSquare square) {
			this.square = square;
		}

		@Override
		public void handle(MouseEvent event) {
			// If this is the first click
			if (firstClick) {
				
				// Start the timer
		        timeline.playFromStart();
		        
		        // Set boolean variable to false
		        firstClick = false;
		        
		        // Add the first state of the game
		        undoSteps.addFirst(getCurrentStates());
			} else {
				
				// If should clear the list of the undo (when an action has been done after an undo)
				if (shouldClearUndo) {
					
					// Remove the first element in the undo list
					undoSteps.removeFirst();
					
					// Set the square states to the current states 
					squaresStates = getCurrentStates();
					
					// Set boolean variable to false
					shouldClearUndo = false;
				}
				
				// Add at the beginning of the linked list, the current state of the game
				undoSteps.addFirst(squaresStates);
			}
			
			// If the event is a primary click and the clicked object is not flagged
			if (event.getButton() == MouseButton.PRIMARY && !square.hasFlag()) {
				// If the square is a bomb
				if (square.getType() == -1) {
					
					// Set the icon to a bomb
					square.setIcon(square.getType());
					
					// End the game by showing all the bombs and stop the timer
					endGameAndShowBombs(-1);
					
				// If it is a free space
				} else if (square.getType() == 0) {
					
					// Set the icon to a free space
					square.setIcon(-2);
					
					// If it has neighbours show them
					showNeighbours(square.getRow(), square.getColumn());
					
					// If the game should end, show the bombs using flags
					if (shouldEndGame()) {
						
						// Show the bombs as flags '9'
						endGameAndShowBombs(9);
						
						// Check if beats the best score.
						board.checkBestScoreAndChangeIt();
					}
					
				// If it is something else than a bomb or a free space
				} else {
					
					// Set the icon accordingly
					square.setIcon(square.getType());
					
					// Check if the game should end
					if (shouldEndGame()) {
						
						// Show the bombs as flags '9'
						endGameAndShowBombs(9);
						
						// Check if beats the best score
						board.checkBestScoreAndChangeIt();
						
						global.showDialog("End game", "You just won the game", "Your score: " + board.getCurrentTime(), null, AlertType.CONFIRMATION);
					}
				}
			
			// If the event is a secondary click
			} else if (event.getButton() == MouseButton.SECONDARY) {
				// If the square is undisclosed or it is flagged
				if (square.getTypeDescription().equals("undisclosed") || square.getTypeDescription().equals("flag")) {
					
					// If the square is not flagged
					if (!square.hasFlag()) {
						
						// Set the icon to a flag
						square.setIcon(9);
						
						// Set a boolean that says it is flagged
						square.setHasFlag(true);
						
						// Set the typeDescription to a flag
						square.setTypeDescription("flag");
						
					} else {
						
						// Set the icon of the square to a undisclosed icon 
						square.setIcon(0);
						
						// Set the boolean that says it is not flagged
						square.setHasFlag(false);
						
						// Set the type description to undisclosed
						square.setTypeDescription("undisclosed");
					}
				}
			}
			
			squaresStates = getCurrentStates();
			redoSteps.clear();
			redoSteps.addFirst(squaresStates);
			undoMenuItem.setDisable(false);
		}
	}
}
