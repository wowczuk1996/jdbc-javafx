package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.ExceptionClass.ExceptionEmptyField;
import sample.helpfullClass.CreateURL;
import sample.helpfullClass.HoldPass;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class SendEmail extends HoldPass implements Initializable {
    @FXML
    private Label exSend;
    @FXML
    private Label nameUser;
    @FXML
    private TextField textFieldTitle;
    @FXML
    private TextArea textAreaEmail;
    @FXML
    private TextField recipient;
    @FXML
    private Button downoladEmails;
    @FXML
    private Button sendEmail;

    private String username;
    private String password;
    private boolean bool = false;
    List<String> listEmailFromDatabase=new ArrayList<>();




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.password = new SendEmail().getPassword();
        this.username = new SendEmail().getLogin();
        nameUser.setText("Witaj, " + username);
        connectionEmail();

    }
    CreateURL createURL = new CreateURL();

    public List<String> selectAllEmail() {
      List<String> listEmail = new ArrayList<String>();

        try {
            Class.forName("org.postgresql.Driver");
            Connection con= DriverManager.getConnection(createURL.getUrl(),createURL.getUserName(),createURL.getPassword());
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("Select email FROM dziekanat.osobiste");

                    while (rs.next()){

                        if(null!=rs.getString("email"))
                        listEmail.add(rs.getString("email"));
                    }

                st.close();
            con.close();


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listEmail;

    }

    public boolean addRecipientEmail() {
        if(recipient.getText().equals("") && (listEmailFromDatabase !=null && !listEmailFromDatabase.isEmpty())){
            recipient.setStyle("-fx-border-color: green");
            return true;
        }

        List<String> addToList = new ArrayList<>();
        String[] downoladTextField = recipient.getText().split(",");


        for(String recipientEmail:downoladTextField){

            if (isCorect(recipientEmail)) {
                recipient.setStyle("-fx-border-color:green");
                addToList.add(recipientEmail);
            } else {
                System.out.println("false");
                recipient.setStyle("-fx-border-color: red");
                exSend.setText("Nie podano prawidłowego emaila");
                return false;
            }

        }

        if(listEmailFromDatabase!=null) {
            listEmailFromDatabase.addAll(addToList);
        }
        addToList.clear();
        return true;

    }


    public boolean isCorect(String recipientEmail){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
        "[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
        "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(recipientEmail).matches();

    }

    public boolean connectionEmail(){
            downoladEmails.setStyle("-fx-background-color: red");
            downoladEmails.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (bool == false) {
                        listEmailFromDatabase.addAll(selectAllEmail());
                        downoladEmails.setStyle("-fx-background-color: green");
                        recipient.setStyle("-fx-border-color: green");
                        bool = true;
                    } else {
                        downoladEmails.setStyle("-fx-background-color: red");
                        recipient.setStyle("-fx-border-color: none");
                        listEmailFromDatabase.removeAll(selectAllEmail());
                        bool = false;
                    }
                }
            });
            return bool;
    }
    public void prepareEmailToSend(ActionEvent actionEvent) {
         if(addRecipientEmail()==true &&listEmailFromDatabase!=null) {
                Properties props = new Properties();

                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");


                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));


                    if (listEmailFromDatabase != null && (listEmailFromDatabase.size() > 0)) {
                        InternetAddress[] address = new InternetAddress[listEmailFromDatabase.size()];

                        for (int i = 0; i < listEmailFromDatabase.size(); i++) {
                            if (null != listEmailFromDatabase.get(i)) {
                                address[i] = new InternetAddress(listEmailFromDatabase.get(i));
                            }
                        }
                        message.addRecipients(Message.RecipientType.TO, address);
                    }



                    if(textFieldTitle.getPromptText() ==null || textFieldTitle.getText().equals(""))
                        throw new ExceptionEmptyField("TO POLE MUSI BYC UZUPELNIONE ");


                         message.setSubject(textFieldTitle.getText());
                         message.setText(textAreaEmail.getText());

                         Transport.send(message);


                          listEmailFromDatabase.clear();

                          downoladEmails.setStyle("-fx-background-color: red");
                          recipient.setStyle("-fx-border-color: none");
                          textFieldTitle.setStyle("-fx-border-color: none");

                          recipient.setText("");
                          textAreaEmail.setText("");
                          textFieldTitle.setText("");

                          exSend.setText("Wysłano");

                } catch (MessagingException e) {
                    exSend.setText("Nie prawidłowy email");
                    throw new RuntimeException(e);
                } catch (ExceptionEmptyField exceptionEmptyField) {
                    textFieldTitle.setStyle("-fx-border-color: red");
                    textFieldTitle.setPromptText(exceptionEmptyField.getMessage());

                }
         }
    }

}




