package razvangeangu.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import razvangeangu.model.Board;
import razvangeangu.view.CustomSquare;

public class MainController implements Initializable {

	@FXML private Button restartButton;
	@FXML private GridPane boardView;
	@FXML private Label bombsLabel;
	@FXML private Label timeLabel;
	
	private Board board;
	private Integer timeSeconds;
	private Timeline timeline;
	private static boolean firstClick = true;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			board = new Board();
		} catch (Exception e) {
			System.err.println(e.toString());
		}

		setBoard(8, 8, 10);
		setBoardView();
		bombsLabel.setText("10");
        timeSeconds = 0;
 
        // update timerLabel
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), new EventHandler() {
			@Override
			public void handle(Event event) {
                timeSeconds++;
                timeLabel.setText(convertSecToTime(timeSeconds));
			}
        }));
		
		restartButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				board.resetBoard();
				boardView.getChildren().clear();
				setBoardView();
				timeline.pause();
				firstClick = true;
				timeLabel.setText(convertSecToTime(0));
			}
			
		});
	}

	private void setBoard(int rows, int columns, int bombs) {
		board.setRows(rows);
		board.setColumns(columns);
		board.setBombs(bombs);
		board.resetBoard();
	}

	private void setBoardView() {
		for (int i = 0; i < 8; i++) {
			addNewRow(boardView, i);
		}
	}

	private void addNewRow(GridPane gridPane, int rowIndex) {
		int numRows = 1;
		for (Node node : gridPane.getChildren()) {
			int currentRow = GridPane.getRowIndex(node);
			if (currentRow >= rowIndex) {
				GridPane.setRowIndex(node, currentRow + 1);
				if (currentRow + 1 > numRows) {
					numRows = currentRow + 1;
				}
			}
		}

		for (int i = 0; i < board.getColumns(); i++) {
			CustomSquare square = new CustomSquare();
			square.setType(board.getBoard()[rowIndex][i]);
			square.getIcon().setOnMouseClicked(new ImageHandler(square));
			square.setRow(rowIndex);
			square.setColumn(i);
			gridPane.add(square, i, rowIndex);
		}
	}

	private void endGameAndShowBombs() {
		timeline.stop();
		for (Node square : boardView.getChildren()) {
			if (square instanceof CustomSquare) {
				((CustomSquare) square).getIcon().setOnMouseClicked(null);
				if (((CustomSquare) square).getType() == -1) {
					((CustomSquare) square).setIcon(-1);
				}
			}
		}
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
	    	 if (square.getType() > -1) {
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
	    	 if (square.getType() > -1) {
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
	    	 if (square.getType() > -1) {
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
	    	 if (square.getType() > -1) {
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
	public String convertSecToTime(int sec) {
		String time = "";
		
		int minutes = (sec % 3600) / 60;
		int seconds = sec % 60;

		time = String.format("%02d:%02d", minutes, seconds);
		
		return time;
	}
	
	private class ImageHandler implements EventHandler<MouseEvent> {
		
		private CustomSquare square;
		
		public ImageHandler(CustomSquare square) {
			this.square = square;
		}

		@Override
		public void handle(MouseEvent event) {
			if (firstClick) {
		        timeline.playFromStart();
		        firstClick = false;
			}
			
			if (event.getButton() == MouseButton.PRIMARY && !square.hasFlag()) {
				if (square.getType() == -1) {
					square.setIcon(square.getType());
					endGameAndShowBombs();
				} else if (square.getType() == 0) {
					square.setIcon(-2);
					showNeighbours(square.getRow(), square.getColumn());
				} else {
					square.setIcon(square.getType());
				}
			} else if (event.getButton() == MouseButton.SECONDARY) {
				if (square.getTypeDescription().equals("undisclosed") || square.getTypeDescription().equals("flag")) {
					if (!square.hasFlag()) {
						square.setIcon(9);
						square.setHasFlag(true);
					} else {
						square.setIcon(0);
						square.setHasFlag(false);
					}
				}
			}
		}
	}
}
