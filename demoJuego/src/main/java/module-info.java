module com.example.demojuego {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.demojuego.model to javafx.fxml;
    exports com.example.demojuego.model;
    exports com.example.demojuego.demoJuego;
    opens com.example.demojuego.demoJuego to javafx.fxml;


}