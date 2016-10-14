package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/razvangeangu/view/Main.fxml"));
			Parent root;
			root = (Parent) fxmlLoader.load();
			Scene scene = new Scene(root);
			primaryStage.setTitle("Minesweeper 2.0");
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setMinWidth(170);
			primaryStage.setMinHeight(250);
			primaryStage.setResizable(false);
			
	//		scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
	//	        public void handle(WindowEvent ev) {
	//	            if (!((MainViewController) fxmlLoader.getController()).shutdown()) {
	//	                ev.consume();
	//	            }
	//	        }
	//	    });
			
	//		((MainViewController) fxmlLoader.getController()).setHostServices(getHostServices());
			
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}