package razvangeangu.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * A class that represent a custom square within the GridPane.
 * @author Razvan-Gabriel Geangu
 *
 */
public class CustomSquare extends Pane {
	
	private int type, row, column;
	private ImageView icon;
	private boolean hasFlag;
	private String typeDescription;

	public CustomSquare() {
		maxWidth(20);
		maxHeight(20);

		hasFlag = false;
		typeDescription = "undisclosed";
		
		icon = new ImageView();
		setIcon(0);
		getChildren().add(icon);
	}
	
	public void setIcon(int type) {
		icon.setImage(new Image("razvangeangu/resources/Minesweeper_" + type + ".png"));
		icon.setFitWidth(20);
        icon.setPreserveRatio(true);
        
        switch (type) {
	        case -2: {
	        	typeDescription = "free";
	        	break;
	        }
	        case -1: {
	        	typeDescription = "bomb";
	        	break;
	        }
	        case 0: {
	        	typeDescription = "undisclosed";
	        	break;
	        }
	        case 9: {
	        	typeDescription = "flag";
	        	break;
	        }
	        default: {
	        	typeDescription = Integer.toString(type);
	        	break;
	        }
        }
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public ImageView getIcon() {
		return icon;
	}
	
	public int getType() {
		return type;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public boolean hasFlag() {
		return hasFlag;
	}

	public void setHasFlag(boolean hasFlag) {
		this.hasFlag = hasFlag;
	}
	
	public String getTypeDescription() {
		return typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}
}
