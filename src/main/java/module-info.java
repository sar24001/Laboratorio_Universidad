module uam.edu.ni.laboratorio_universidad {

    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.sql;

    opens uam.edu.ni.laboratorio_universidad.controller to javafx.fxml;

    opens uam.edu.ni.laboratorio_universidad to javafx.fxml;


    opens uam.edu.ni.laboratorio_universidad.model
            to org.hibernate.orm.core, javafx.base;

    // Exportar paquetes del proyecto
    exports uam.edu.ni.laboratorio_universidad;
    exports uam.edu.ni.laboratorio_universidad.controller;
    exports uam.edu.ni.laboratorio_universidad.model;
}
