module org.trojancs.grammer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;

    opens org.trojancs.grammer to javafx.fxml;
    exports org.trojancs.grammer;
}