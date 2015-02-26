package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Kalender");
		initRootLayout();
		showLogin();
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout, 1280, 720);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showLogin()	 {
		try {
			// Load overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("Login.fxml"));
			AnchorPane login = (AnchorPane) loader.load();

			// Set  overview into the center of root layout.
			rootLayout.setCenter(login);
			// Give the controller access to the main app.
			LoginController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showRegister()	 {
		try {
			// Load overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("Register.fxml"));
			AnchorPane register = (AnchorPane) loader.load();

			// Set  overview into the center of root layout.
			rootLayout.setCenter(register);
			// Give the controller access to the main app.
			RegisterController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void login() {
		rootLayout.getChildren().clear();
		showList();
		showToolbar();
		showCalendar();
	}

	public void showCalendar() {
		try {
			// Load overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("Calendar.fxml"));
			AnchorPane calendar = (AnchorPane) loader.load();

			// Set  overview into the center of root layout.
			rootLayout.setCenter(calendar);

			// Give the controller access to the main app.
			CalendarController controller = loader.getController();
			controller.setMainApp(this);
			controller.fillCalendar();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showToolbar() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("Toolbar.fxml"));
			AnchorPane toolbar = (AnchorPane) loader.load();

			rootLayout.setTop(toolbar);

			ToolbarController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showList() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("List.fxml"));
			AnchorPane list = (AnchorPane) loader.load();

			rootLayout.setLeft(list);

			ListController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showAppointmentPopup(CalendarSquarePane csp, AppointmentSquarePane asp) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("AppointmentPopup.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage popupStage = new Stage();
			if (csp != null) {				
				popupStage.setTitle("Lag avtale for " + csp.getDate());
			} else {
				popupStage.setTitle("Lag avtale for " + asp.getAppointment().getDate());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(primaryStage);
			popupStage.setResizable(false);
			Scene scene = new Scene(page);
			popupStage.setScene(scene);
			AppointmentPopupController controller = loader.getController();
			controller.setPopupStage(popupStage);
			controller.fillPopup(csp, asp);

			// Show the dialog and wait until the user closes it
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showGroupPopup(TreeView<Group> treeView, TreeItem<Group> group, boolean createSub) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("GroupPopup.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage popupStage = new Stage();
			if (group == null) {				
				popupStage.setTitle("Lag gruppe");
			} else if (createSub){
				popupStage.setTitle("Lag subgruppe for " + group.getValue().getName());
			} else {
				popupStage.setTitle("Endre på " + group.getValue().getName());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(primaryStage);
			popupStage.setResizable(false);
			Scene scene = new Scene(page);
			popupStage.setScene(scene);
			GroupPopupController controller = loader.getController();
			controller.setPopupStage(popupStage);
			controller.setTreeView(treeView);
			controller.fillPopup(group, createSub);
			// Show the dialog and wait until the user closes it
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}