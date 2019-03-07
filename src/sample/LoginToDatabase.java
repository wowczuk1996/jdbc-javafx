package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import sample.helpfullClass.CreateURL;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginToDatabase implements Initializable {

    @FXML
    private Label sqlException;
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField port;
    @FXML
    private TextField login;
    @FXML
    private TextField ip;
    @FXML
    private PasswordField haslo;
    @FXML
    private Button zaloguj;

@FXML
    public void connecting(ActionEvent actionEvent)  {
    CreateURL createURL = new CreateURL(ip.getText(),login.getText(),haslo.getText(),port.getText(),login.getText());
    try {
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(createURL.getUrl(),createURL.getUserName(),createURL.getPassword());

        if(!connection.isClosed()) {
            AnchorPane nowe = FXMLLoader.load(getClass().getResource("FXML/SelectSQL.fxml"));
            pane.getChildren().setAll(nowe);
            connection.close();
        }




    } catch (ClassNotFoundException e) {
         e.printStackTrace();
    } catch (SQLException e) {
        sqlException.setText("Blad polaczenia z baza");
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }


}


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
