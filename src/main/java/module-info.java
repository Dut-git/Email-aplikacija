module com.example.projektnizadatakfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;
    requires tornadofx.controls;
    requires tornadofx;
    requires commons.lang3;


    opens com.example.projektnizadatakfx to javafx.fxml;
    exports com.example.projektnizadatakfx;
}