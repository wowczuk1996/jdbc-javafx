package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import sample.helpfullClass.HoldPass;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginToEmail implements Initializable {
    @FXML
    private Label exceptionConnectionWithDatabase;
    @FXML
    private AnchorPane paneLoginToEmail;
    @FXML
    private TextField loginEmail;
    @FXML
    private PasswordField passwordEmail;

    private String passwordE;
    private String loginE;


    public void loginInEmail(ActionEvent actionEvent) throws IOException {
        if(checkForErrors(loginEmail.getText(), passwordEmail.getText())==true) {
            HoldPass sendEmail = new HoldPass(loginEmail.getText(), passwordEmail.getText());


            AnchorPane nowe = FXMLLoader.load(getClass().getResource("FXML/SendEmail.fxml"));
            paneLoginToEmail.getChildren().setAll(nowe);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public boolean checkForErrors(String mail, String haslo) {
        try {
            Properties props = new Properties();
            // required for gmail
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            // or use getDefaultInstance instance if desired...
            Session session = Session.getInstance(props, null);
            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", 587, mail, haslo);
            transport.close();
            System.out.println("success");
        } catch (AuthenticationFailedException e) {
            System.out.println("AuthenticationFailedException");

            exceptionConnectionWithDatabase.setText("Nie prawidłowy email albo haslo");
           // e.printStackTrace();
            return false;
        } catch (MessagingException e) {

            exceptionConnectionWithDatabase.setText("Nie można się połaczyć z podanym emailem");
            //e.printStackTrace();
            return false;
        }
        return true;
    }




    public String getPasswordE() {
        return passwordE;
    }

    public String getLoginE() {
        return loginE;
    }






}
