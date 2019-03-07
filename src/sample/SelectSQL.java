package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;

import sample.helpfullClass.ButtonNameColumns;
import sample.helpfullClass.CreateURL;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class SelectSQL implements Initializable {

    @FXML
    private TextArea showColumns;
    @FXML
    private AnchorPane paneSelect;
    @FXML
    private ChoiceBox choiceScheme;
    @FXML
    private ChoiceBox choiceTable;
    @FXML
    private Button showTablee;

    private List<String> nameColumns;
    private  List<String> listTables;
    private  List<String> listColumns;
    private List<sample.helpfullClass.ButtonNameColumns> listButton= null;
    private String nameScheme ;
    private String nameTable;


    ButtonNameColumns ButtonNameColumns = new ButtonNameColumns();
    CreateURL createURL = new CreateURL();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection con= DriverManager.getConnection(createURL.getUrl(),createURL.getUserName(),createURL.getPassword());

                Statement st = con.createStatement();
                ResultSet scheme =con.getMetaData().getSchemas();

                    while (scheme.next()) {
                        choiceScheme.getItems().add(scheme.getString(1));
                    }
                    choiceScheme.getSelectionModel().select(0);


                        nameScheme = choiceScheme.getItems().get(0).toString();
                        getTables(nameScheme);


                    for(String elementTabel:listTables){
                        choiceTable.getItems().add(elementTabel);
                    }
                    choiceTable.getSelectionModel().select(0);

                        nameTable = choiceTable.getItems().get(0).toString();
                        getColumns(nameScheme, nameTable);

                listenerScheme();
                listenerTables();
                selectNameColumn();


            st.close();
            con.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void listenerScheme(){

        ChangeListener<String> getScheme = new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                choiceTable.getItems().removeAll(choiceTable.getItems());

                nameScheme = newValue;
                getTables(newValue);

                for(String elementTabel:listTables){
                    choiceTable.getItems().add(elementTabel);
                }
                choiceTable.getSelectionModel().select(0);

                if(nameTable!=null) {
                   getColumns(nameScheme, nameTable);
                }
                selectNameColumn();
            }
        };
        choiceScheme.getSelectionModel().selectedItemProperty().addListener(getScheme);

    }

    public void listenerTables() {
        ChangeListener<String> getTable = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                nameTable = newValue;

                if(nameTable!=null) {
                    getColumns(nameScheme, nameTable);
                }else{
                    if(listButton != null) {
                        for (ButtonNameColumns button : listButton) {
                            paneSelect.getChildren().remove(button.getButton());

                        }
                        listButton.clear();

                    }
                }
                selectNameColumn();
            }
        };
        choiceTable.getSelectionModel().selectedItemProperty().addListener(getTable);
    }



    public void getTables(String scheme) {
      try {
          Class.forName("org.postgresql.Driver");
          Connection con= DriverManager.getConnection(createURL.getUrl(),createURL.getUserName(),createURL.getPassword());


          String[] names = {"TABLE"};
          ResultSet table = con.getMetaData().getTables(null, scheme, "%", names);


                listTables = new ArrayList<>();

                while (table.next()) {
                    listTables.add(table.getString(3));
                }


      } catch (ClassNotFoundException e) {
          e.printStackTrace();
      } catch (SQLException e) {
          e.printStackTrace();
      }

    }



  public void getColumns(String scheme, String table){
      try {
          Class.forName("org.postgresql.Driver");
          Connection con= DriverManager.getConnection(createURL.getUrl(),createURL.getUserName(),createURL.getPassword());

              Statement st = con.createStatement();

                  ResultSet rs = st.executeQuery("Select * from " + scheme + "." + table);
                  ResultSetMetaData rsmd = rs.getMetaData();


                  if (listButton == null ) {
                      System.out.println("Dlaczego?");
                      listButton = new ArrayList<ButtonNameColumns>();


                          int columnCount = rsmd.getColumnCount();
                          for (int i = 1; i <= columnCount; i++) {
                              listButton.add(new ButtonNameColumns(new Button(rsmd.getColumnName(i)), false, rsmd.getColumnName(i)));
                          }


                          int layout = 0;
                          for (ButtonNameColumns s : listButton) {
                              s.getButton().setLayoutY(layout+=40);
                              s.getButton().setLayoutX(30);
                              paneSelect.getChildren().add(s.getButton());
                          }

                  } else {

                      for (ButtonNameColumns button : listButton) {
                         paneSelect.getChildren().remove(button.getButton());

                      }
                      listButton.clear();
                      listButton = null;
                      getColumns(nameScheme, nameTable);

                  }

              st.close();
          con.close();
      } catch (ClassNotFoundException e) {
          e.printStackTrace();
      } catch (SQLException e) {
          e.printStackTrace();
      }
    }

  public void selectNameColumn(){

        nameColumns = new ArrayList<String>();
        for(ButtonNameColumns listButton : listButton){
            listButton.getButton().setStyle("-fx-background-color: red");

            listButton.getButton().setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    if(listButton.isBool()==false) {
                        listButton.getButton().setStyle("-fx-background-color: green");
                        listButton.setBool(true);
                        nameColumns.add(listButton.getName());
                    }else{
                        listButton.getButton().setStyle("-fx-background-color: red");
                        listButton.setBool(false);
                        nameColumns.remove(listButton.getName());
                    }
                }

            });
        }
}

  public void showChoiseTable(ActionEvent actionEvent) throws IOException {

      if (!nameColumns.isEmpty()) {
          try {

              Class.forName("org.postgresql.Driver");
              Connection con = DriverManager.getConnection(createURL.getUrl(), createURL.getUserName(), createURL.getPassword());

              int sizeColumn = nameColumns.size();

              PreparedStatement ps = null;
              ps = con.prepareStatement("SELECT " + generujILosc(sizeColumn) + " FROM " + nameScheme + "." + nameTable);


              generujILosc(sizeColumn);
              ResultSet rs = ps.executeQuery();


              List<String> holdColumns = new ArrayList<String>();

              while (rs.next()) {
                  //holdColumns.add(rs.getString(pprintColumns.get(i)));
                  String s = rs.getString(nameColumns.get(0));

                  for (int j = 1; j < sizeColumn; j++) {
                      s += " " + rs.getString(nameColumns.get(j));
                  }
                  holdColumns.add(s);
              }

              showColumns.clear();
              if(showColumns!=null) {
                  for (String hold : holdColumns) {
                      showColumns.appendText(hold + "\n");
                  }
              }

              ps.close();
              con.close();

          } catch (SQLException e) {
              e.printStackTrace();
          } catch (ClassNotFoundException e) {
              e.printStackTrace();
          }
      }else{
          showColumns.clear();
      }
  }

  public String generujILosc(int sizeColumn ){

        String howmany = nameColumns.get(0);
        for(int i =1 ;i<sizeColumn;i++){
            howmany +=",";
            howmany += nameColumns.get(i);
        }

        return howmany;
    }



    public void openSendEmail(ActionEvent actionEvent) throws IOException {

       AnchorPane m = FXMLLoader.load(getClass().getResource("FXML/LoginToEmail.fxml"));
       paneSelect.getChildren().setAll(m);

    }
}

