module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.bytedeco.javacv;
    requires org.bytedeco.opencv;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}