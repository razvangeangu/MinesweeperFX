package razvangeangu.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import razvangeangu.model.Board;
import razvangeangu.view.CustomSquare;

public class MainController implements Initializable {

	@FXML
	private Button restartButton;
	@FXML
	private GridPane boardView;
	private Board board;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			board = new Board();
		} catch (Exception e) {
			System.err.println(e.toString());
		}

		setBoard(8, 8, 64 / 3);
		setBoardView();
		
		restartButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				board.resetBoard();
				boardView.getChildren().clear();
				setBoardView();
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
			gridPane.add(square, i, rowIndex);
		}
	}

	private void endGameAndShowBombs() {
		for (Node square : boardView.getChildren()) {
			if (square instanceof CustomSquare) {
				((CustomSquare) square).getIcon().setOnMouseClicked(null);
				if (((CustomSquare) square).getType() == -1) {
					((CustomSquare) square).setIcon(-1);
				}
			}
		}
	}
	
	private void showNeighbours() {
		// TODO: mai tarziu..
	}
	
	private class ImageHandler implements EventHandler<MouseEvent> {
		
		CustomSquare square;
		
		public ImageHandler(CustomSquare square) {
			this.square = square;
		}

		@Override
		public void handle(MouseEvent event) {
			if (square.getType() == -1) {
				square.setIcon(square.getType());
				endGameAndShowBombs();
			} else if (square.getType() == 0) {
				square.setIcon(-2);
				showNeighbours();
			} else {
				square.setIcon(square.getType());
			}
		}
	}
}
