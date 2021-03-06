package gui;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import core.Group;
import core.User;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private BorderPane loginLayout;
	private User user;
	private ListController listController;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Kalender");
		primaryStage.getIcons().add(new Image("/Images/icon.png"));
		initRootLayout();
		initLoginLayout();
		showLogin();
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public User getUser() {
		return user;
	}

	public void initRootLayout() {		
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout, 1280, 720);

			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initLoginLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/LoginLayout.fxml"));
			loginLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(loginLayout, 1280, 720);
			primaryStage.setMaximized(true);;
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
			loader.setLocation(MainApp.class.getResource("/views/Login.fxml"));
			AnchorPane login = (AnchorPane) loader.load();

			// Set  overview into the center of root layout.
			loginLayout.setCenter(login);
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
			loader.setLocation(MainApp.class.getResource("/views/Register.fxml"));
			AnchorPane register = (AnchorPane) loader.load();

			// Set  overview into the center of root layout.
			loginLayout.setCenter(register);
			// Give the controller access to the main app.
			RegisterController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void login(String username, String name) throws SQLException {
		user = new User(username, name);
		loginLayout.setVisible(false);
		initRootLayout();
		showList();
		showToolbar();
	}

	public void showCalendar(Group group) throws SQLException {
		try {
			// Load overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/Calendar.fxml"));
			AnchorPane calendar = (AnchorPane) loader.load();

			// Set  overview into the center of root layout.
			rootLayout.setCenter(calendar);

			// Give the controller access to the main app.
			CalendarController controller = loader.getController();
			controller.setMainApp(this);
			controller.fillCalendar(group);
			controller.setKeyEventHandler(primaryStage.getScene());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showWelcome() {
		listController.selectPrivateGroup();
	}
	
	public void deselectGroup() {
		listController.deselectGroup();
	}

	public void showToolbar() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/Toolbar.fxml"));
		
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
			loader.setLocation(MainApp.class.getResource("/views/List.fxml"));
			AnchorPane list = (AnchorPane) loader.load();

			rootLayout.setLeft(list);

			ListController controller = loader.getController();
			this.listController = controller;
			controller.setMainApp(this);
			controller.init2();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showNotification(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/Notification.fxml"));
			AnchorPane notification = (AnchorPane) loader.load();

			rootLayout.setCenter(notification);

			NotificationController controller = loader.getController();
			controller.setMainApp(this);
			controller.init2();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showSettings(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/Settings.fxml"));
			AnchorPane settings = (AnchorPane) loader.load();

			rootLayout.setCenter(settings);
			SettingsController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void logOut(){
		this.primaryStage.setTitle("Kalender");
		initRootLayout();
		initLoginLayout();
		showLogin();
	}

	private void setEscapeKeyEventHandler(Stage stage) {
		EventHandler<KeyEvent> keyHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent keyCode) {
				if (keyCode.getCode() == KeyCode.ESCAPE) {
					stage.close();
				}
			}
		};
		stage.getScene().setOnKeyPressed(keyHandler);
	}

	public void showAppointmentPopup(CalendarSquarePane csp, AppointmentSquarePane asp, Group group) throws SQLException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/AppointmentPopup.fxml"));
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
			popupStage.getIcons().add(new Image("/Images/icon.png"));
			setEscapeKeyEventHandler(popupStage);
			AppointmentPopupController controller = loader.getController();
			controller.setPopupStage(popupStage);
			controller.setMainApp(this);
			controller.fillPopup(csp, asp, group, getUser().getUsername());

			// Show the dialog and wait until the user closes it
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showEditAppointmentPopup(CalendarSquarePane csp, AppointmentSquarePane asp, Group group) throws SQLException {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/EditAppointmentPopup.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage popupStage = new Stage();
			if (csp != null) {				
				popupStage.setTitle("Edit avtale for " + csp.getDate());
			} else {
				popupStage.setTitle("Edit avtale for " + asp.getAppointment().getDate());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(primaryStage);
			popupStage.setResizable(false);
			Scene scene = new Scene(page);
			popupStage.setScene(scene);
			popupStage.getIcons().add(new Image("/Images/icon.png"));
			setEscapeKeyEventHandler(popupStage);
			EditAppointmentPopupController controller = loader.getController();
			controller.setPopupStage(popupStage);
			controller.setMainApp(this);
			controller.fillPopup(csp, asp, group);

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
			loader.setLocation(MainApp.class.getResource("/views/GroupPopup.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage popupStage = new Stage();
			if (group == null) {				
				popupStage.setTitle("Lag gruppe");
			} else if (createSub){
				popupStage.setTitle("Lag subgruppe for " + group.getValue().getName());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(primaryStage);
			popupStage.setResizable(false);
			Scene scene = new Scene(page);
			popupStage.setScene(scene);
			popupStage.getIcons().add(new Image("/Images/icon.png"));
			setEscapeKeyEventHandler(popupStage);
			GroupPopupController controller = loader.getController();
			controller.setPopupStage(popupStage);
			controller.setTreeView(treeView);
			controller.fillPopup(group, createSub, this);
			// Show the dialog and wait until the user closes it
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showEditGroupPopup(TreeItem<Group> group) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/views/EditGroupPopup.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage popupStage = new Stage();
			popupStage.setTitle("Endre paa: " + group.getValue().getName());
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(primaryStage);
			popupStage.setResizable(false);
			Scene scene = new Scene(page);
			popupStage.setScene(scene);
			popupStage.getIcons().add(new Image("/Images/icon.png"));
			setEscapeKeyEventHandler(popupStage);
			EditGroupPopupController controller = loader.getController();

			controller.setPopupStage(popupStage);

			controller.fillPopup(group, user.getUsername());
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