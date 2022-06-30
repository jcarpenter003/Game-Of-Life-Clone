module com.jonathan.gameoflife {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jonathan.gameoflife.gamelogic to javafx.fxml;
    exports com.jonathan.gameoflife.gamelogic;
}