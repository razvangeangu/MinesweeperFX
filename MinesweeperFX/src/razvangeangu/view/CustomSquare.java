package razvangeangu.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class CustomSquare extends Pane {
	
	private int type, row, column;
	private ImageView icon;
	private boolean hasFlag;

	public CustomSquare() {
		maxWidth(20);
		maxHeight(20);

		hasFlag = false;
		
		icon = new ImageView();
		setIcon(0);
		getChildren().add(icon);
	
	}
	
	public void setIcon(int type) {
		icon.setImage(new Image("razvangeangu/resources/Minesweeper_" + type + ".png"));
		icon.setFitWidth(20);
        icon.setPreserveRatio(true);
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
	
	
}
