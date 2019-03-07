package sample.helpfullClass;

public class HoldPass {
    private static String login;
    private static String password;



    public HoldPass(String login, String password) {
        this.login = login;
        this.password = password;
    }
    public HoldPass(){};



    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

}
