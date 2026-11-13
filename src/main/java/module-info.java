module uam.edu.ni.laboratorio_universidad {
    // Módulos necesarios
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;

    opens uam.edu.ni.laboratorio_universidad.controller to javafx.fxml;
    opens uam.edu.ni.laboratorio_universidad to javafx.fxml;

    opens uam.edu.ni.laboratorio_universidad.model to org.hibernate.orm.core, javafx.base;

    exports uam.edu.ni.laboratorio_universidad;
    exports uam.edu.ni.laboratorio_universidad.controller;
    exports uam.edu.ni.laboratorio_universidad.model;
}