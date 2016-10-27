package razvangeangu.view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import razvangeangu.model.GlobalModel;

/**
 * 
 * @author Razvan-Gabriel Geangu
 *
 */
public class GlobalView {
	
	public GlobalView() {
		
	}
	
	public ArrayList<String> showTextInputDialog(String title, String header, ArrayList<String> fields) throws NullPointerException {
		// Create the custom dialog.
		Dialog<ArrayList<String>> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setResizable(false);

		// Set the button types.
		ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType);

		// Create the labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
		okButton.setDisable(true);
		
		ArrayList<TextField> textFields = new ArrayList<TextField>();
		for (int i = 0; i < fields.size(); ++i) {
			TextField aField = new TextField();
			aField.setPromptText(fields.get(i));
			aField.setId(fields.get(i));
			grid.add(new Label(fields.get(i)), 0, i);
			grid.add(aField, 1, i);
			
			aField.textProperty().addListener((observable, oldValue, newValue) -> {
				if (!newValue.matches("\\d*")) {
					aField.setText(oldValue);
				}
				
				if (!aField.getText().isEmpty() && !textFields.get(1).getText().isEmpty() && !textFields.get(2).getText().isEmpty()) {
					okButton.setDisable(false);
				} else {
					okButton.setDisable(true);
				}
			});
			
			aField.focusedProperty().addListener((observable, oldValue, newValue) -> {
				if (!newValue && !aField.getText().isEmpty()) {
					if (aField.getId().equals("Rows") || aField.getId().equals("Columns")) {
						if (Integer.parseInt(aField.getText()) < 8) {
							aField.setText("8");
						}
						
						if (Integer.parseInt(aField.getText()) > 100) {
							aField.setText("100");
						}
					} else if(aField.getId().equals("Bombs")) {
						TextField rowField = textFields.get(0);
						TextField columnField = textFields.get(1);
						if (!rowField.getText().isEmpty() && !columnField.getText().isEmpty()) {
							if (Integer.parseInt(aField.getText()) > 
									(Integer.parseInt(rowField.getText()) * 
											Integer.parseInt(columnField.getText())) 
									/ 3 || Integer.parseInt(aField.getText()) <= 0) {
								int bombs = (Integer.parseInt(rowField.getText()) * 
										Integer.parseInt(columnField.getText())) 
										/ 3;
								aField.setText(Integer.toString(bombs));
							}
						}
					}
				}
			});
			
			textFields.add(aField);
		}

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			ArrayList<String> textFieldsResults = new ArrayList<String>();
		    for (TextField resultField : textFields) {
		    	if (!resultField.getText().isEmpty()) {
		    		textFieldsResults.add(resultField.getText());
		    	} else {
		    		textFieldsResults = null;
		    		break;
		    	}
		    }
	        return textFieldsResults;
		});

		Optional<ArrayList<String>> result = dialog.showAndWait();

		if (result.isPresent()){
		    return result.get();
		}
		
		return null;
	}
	
	/**
	 * A method to show a dialog.
	 * @param title The title of the dialog.
	 * @param header The header of the dialog
	 * @param content The content of the dialog.
	 * @param details The details of the dialog.
	 */
	public void showDialog(String title, String header, String content, String details, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		if (details != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			pw.print(details);
			String exceptionText = sw.toString();

			Label label = new Label("Details:");

			TextArea textArea = new TextArea(exceptionText);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);
		}
		
		alert.showAndWait();
	}
	
	@SuppressWarnings("unchecked")
	public void showChoiceDialog(String title, String header, String content, ArrayList<String> scores) {
		List<String> choices = new ArrayList<>();
		choices.add(GlobalModel.BESTSCORE_EASY);
		choices.add(GlobalModel.BESTSCORE_MEDIUM);
		choices.add(GlobalModel.BESTSCORE_HARD);
		choices.add(GlobalModel.BESTSCORE_CUSTOM);

		// Create the custom dialog.
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle(title);
		dialog.setHeaderText(header);

		// Set the button types.
		ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		Label scoreLabel = new Label("");
		
		@SuppressWarnings("rawtypes")
		ChoiceBox gameMode = new ChoiceBox(FXCollections.observableArrayList(choices));
		
		gameMode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			int index = 0;
			
			switch ((String)newValue) {
				case GlobalModel.BESTSCORE_EASY: {
					index = 0;
					break;
				}
				
				case GlobalModel.BESTSCORE_MEDIUM: {
					index = 1;
					break;
				}
				
				case GlobalModel.BESTSCORE_HARD: {
					index = 2;
					break;
				}
				
				case GlobalModel.BESTSCORE_CUSTOM: {
					index = 3;
					break;
				}
			}
			
			if (Double.valueOf(scores.get(index)) != Double.MAX_VALUE) {
				scoreLabel.setText(scores.get(index));
			} else {
				scoreLabel.setText("Not yet available");
			}
		});
		
		gameMode.getSelectionModel().select(0);

		grid.add(new Label("Game mode:"), 0, 0);
		grid.add(gameMode, 1, 0);
		grid.add(new Label("Score:"), 0, 1);
		grid.add(scoreLabel, 1, 1);

		dialog.getDialogPane().setContent(grid);
		
		dialog.showAndWait();
	}
	
	public int getIconType(String typeDescription) {
		int type = 0;
		
		switch (typeDescription) {
	        case "free": {
	        	type = -2;
	        	break;
	        }
	        case "bomb": {
	        	type = -1;
	        	break;
	        }
	        case "undisclosed": {
	        	type = 0;
	        	break;
	        }
	        case "flag": {
	        	type = 9;
	        	break;
	        }
	        default: {
	        	type = Integer.valueOf(typeDescription);
	        	break;
	        }
		}
		
		return type;
	}

}
