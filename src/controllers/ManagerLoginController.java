package controllers;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import application.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.LoginModel;

public class ManagerLoginController {
	@FXML
	private TextField txtUsername;

	@FXML
	private PasswordField txtPassword;

	@FXML
	private Label lblError;
	
	private LoginModel model;
	private Stage stage;
	private Integer role = 1;
	private Thread t1;

	public ManagerLoginController() {
		t1 = new Thread(()-> {
			model = new LoginModel();
		});
		t1.start();
//		model = new LoginModel();
	}
	
	public void createModel() {
		model = new LoginModel();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setRole(Integer role) {
		this.role = role;
	}
	
	@FXML
    private void initialize(){
		txtUsername.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
            	txtPassword.requestFocus();
            }
        });
		
		txtPassword.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
            	try {
					onSubmit();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
	}
	
	public void onSubmit() throws NoSuchAlgorithmException {
		t1.interrupt();
		lblError.setText("");
		String username = this.txtUsername.getText();
		String password = this.txtPassword.getText();
		
		if ((username == null || username.trim().equals("")) && (password == null || password.trim().equals(""))) {
			lblError.setText("Username / Password Cannot be empty or spaces");
			return;
		}
		if (username == null || username.trim().equals("")) {
			lblError.setText("Username Cannot be empty or spaces");
			return;
		}
		if (password == null || password.trim().equals("")) {
			lblError.setText("Password Cannot be empaty or spaces");
			return;
		}
		checkCredentials(username, password);
		
		System.out.println("click submit");
		
	}
	
	@SuppressWarnings("rawtypes")
	public void checkCredentials(String username, String password) throws NoSuchAlgorithmException {
		Map userMap = model.queryUser(username, password, role);
		boolean isValid = (boolean) userMap.get("isValid");
		alertCreate(isValid, username);
		if (!isValid) return;
		try {
			AnchorPane root;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PetListView.fxml"));
			root = (AnchorPane) loader.load();
			stage.setTitle("Pet List View");
			PetListController petListController = loader.getController();
			petListController.setUser(userMap);
			petListController.setStage(stage);
			petListController.setRole(role);
			Scene scene = new Scene(root);
			stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
//		System.out.println(isValid ? "登录成功" : "登录失败");
	}
	
	public void alertCreate(Boolean isValid, String username) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Login Tip");
		alert.setHeaderText(isValid ? "Login Successfully" : "Login Faild");
		alert.setContentText(isValid ? "Welcome, " + username + "!" : "The username or password is incorrect!");
		alert.showAndWait();
	}
	
	public void onGoSignUp() {
		t1.interrupt();
		try {
			AnchorPane root;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/SignUpView.fxml"));
			root = (AnchorPane) loader.load();
			stage.setTitle(role == 1 ? "Customer Sign Up View" : "Admin Sign Up View");
			SignUpController signUpController = loader.getController();
			signUpController.setStage(stage);
			if (role == 2) {
				signUpController.setRole(role);
			}
			Scene scene = new Scene(root);
			stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}
	
	public void onBack() {
		t1.interrupt();
		try {
			
			AnchorPane root;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ApplicationView.fxml"));
			root = (AnchorPane) loader.load();
			stage.setTitle("Pet Shop");
			ApplicationController applicationController = loader.getController();
			applicationController.setStage(stage);
//			applicationController.setUser(userMap);
			Scene scene = new Scene(root);
			stage.setScene(scene);
		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}
	}
	
}