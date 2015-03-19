package test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import org.junit.Test;
import org.loadui.testfx.GuiTest;

public class TestLogin1 extends GuiTest{
	
	@Override
	protected Parent getRootNode() {
		try {
            return FXMLLoader.load(this.getClass().getResource("/views/login.fxml"));
        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
	}
	
	@Test
    public void testWrongPswd() {
        TextField usernameField = find("#usernameField");
        String text1 = "trymon";
        clickOn(usernameField).type(text1);
        TextField passwordField = find("#passwordField");
        String text2 = "wrong";
        clickOn(passwordField).type(text2);
        clickOn("#login");
        assertEquals("Error logging in",((Text) find("#errorText")).getText());
    }
	
	@Test
    public void testWrongUsername() {
        TextField usernameField = find("#usernameField");
        String text1 = "wrong";
        clickOn(usernameField).type(text1);
        TextField passwordField = find("#passwordField");
        String text2 = "correct";
        clickOn(passwordField).type(text2);
        clickOn("#login");
        assertEquals("Username not found",((Text) find("#errorText")).getText());
    }
	
	@Test
	public void testCorrectLogin() {
		TextField usernameField = find("#usernameField");
        String text1 = "trymon";
        clickOn(usernameField).type(text1);
        TextField passwordField = find("#passwordField");
        String text2 = "1234";
        clickOn(passwordField).type(text2);
        clickOn("#login");
        assertEquals("Correct",((Text) find("#errorText")).getText());
	}


}
