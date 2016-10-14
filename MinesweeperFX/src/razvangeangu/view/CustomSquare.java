package razvangeangu.view;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class CustomSquare extends Pane {
	
	private int type;
	private ImageView icon;

	public CustomSquare() {
		maxWidth(20);
		maxHeight(20);
		
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
}
