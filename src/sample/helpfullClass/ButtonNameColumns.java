package sample.helpfullClass;


import javafx.scene.control.Button;

public class ButtonNameColumns {
    private Button button;
    private boolean bool =false;
    private String name;

    public String getName() {
        return name;
    }

    public ButtonNameColumns() {
    }

    public ButtonNameColumns(Button button, boolean bool, String name) {
        this.button = button;
        this.bool = bool;
        this.name=name;
    }








    public Button getButton() {
        return button;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }
}
