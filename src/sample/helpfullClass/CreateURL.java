package sample.helpfullClass;

public class CreateURL {

    private static  String url ;
    private static String  user;
    private static String pass;

    private String userName;
    private String ip;
    private String databaseName;
    private String port;
    private String password;

    public CreateURL( String ip, String userName, String password, String port, String databaseName) {

        this.ip=ip;
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.databaseName = databaseName;
        createSQL();
    }
    public CreateURL(){};

    public void createSQL() {
        url =  "jdbc:postgresql://" + ip + ":" + port + "/" + databaseName;
        user = userName;
        pass = password;

    }


    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return user;
    }

    public String getPassword() {
        return pass;
    }
}
