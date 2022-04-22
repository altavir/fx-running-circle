module ru.mipt.example.fxrunningcircle {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core.jvm;


    opens ru.mipt.example.fxrunningcircle to javafx.fxml;
    exports ru.mipt.example.fxrunningcircle;
}