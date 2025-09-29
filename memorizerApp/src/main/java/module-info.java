module it.unipi.memorizer.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires java.base;
    requires org.apache.logging.log4j;

    opens it.unipi.memorizer.app to javafx.fxml, com.google.gson;
    
    exports it.unipi.memorizer.app;
}
