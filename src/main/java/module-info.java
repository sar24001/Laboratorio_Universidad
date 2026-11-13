module uam.edu.ni.laboratorio_universidad {
    requires javafx.controls;
    requires javafx.fxml;


    opens uam.edu.ni.laboratorio_universidad to javafx.fxml;
    exports uam.edu.ni.laboratorio_universidad;
    exports uam.edu.ni.laboratorio_universidad.controller;
    opens uam.edu.ni.laboratorio_universidad.controller to javafx.fxml;
}