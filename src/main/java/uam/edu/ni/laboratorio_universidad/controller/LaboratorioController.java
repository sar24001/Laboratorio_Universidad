package uam.edu.ni.laboratorio_universidad.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import uam.edu.ni.laboratorio_universidad.model.Equipo;
import uam.edu.ni.laboratorio_universidad.model.TipoEquipo;

import uam.edu.ni.laboratorio_universidad.dao.EquipoDAO;
import uam.edu.ni.laboratorio_universidad.dao.TipoEquipoDAO;
import uam.edu.ni.laboratorio_universidad.model.Equipo;
import uam.edu.ni.laboratorio_universidad.model.TipoEquipo;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

    public class LaboratorioController {

        // Componentes del formulario
        @FXML private TextField txtCodigo;
        @FXML private TextField txtNombre;
        @FXML private TextField txtMarca;
        @FXML private TextField txtCantidad;

        @FXML private ComboBox<TipoEquipo> cmbTipoEquipo;
        @FXML private ComboBox<String> cmbEstado;

        // Componentes de búsqueda
        @FXML private TextField txtBuscarNombre;
        @FXML private ComboBox<TipoEquipo> cmbFiltroTipo;

        // Tabla
        @FXML private TableView<Equipo> tblEquipos;

        @FXML private TableColumn<Equipo, String> colCodigo;
        @FXML private TableColumn<Equipo, String> colNombre;
        @FXML private TableColumn<Equipo, String> colMarca;
        @FXML private TableColumn<Equipo, String> colEstado;
        @FXML private TableColumn<Equipo, Integer> colCantidad;
        @FXML private TableColumn<Equipo, String> colTipo;

        // Botones
        @FXML private Button btnActualizar;
        @FXML private Button btnEliminar;

        // Objetos DAO
        private final EquipoDAO equipoDAO = new EquipoDAO();
        private final TipoEquipoDAO tipoEquipoDAO = new TipoEquipoDAO();

        // Identificador del equipo seleccionado
        private Long idSeleccionado = null;

        // Inicializar la interfaz
        @FXML
        public void initialize() {

            colCodigo.setCellValueFactory(
                    new PropertyValueFactory<>("codigo")
            );

            colNombre.setCellValueFactory(
                    new PropertyValueFactory<>("nombre")
            );

            colMarca.setCellValueFactory(
                    new PropertyValueFactory<>("marca")
            );

            colEstado.setCellValueFactory(
                    new PropertyValueFactory<>("estado")
            );

            colCantidad.setCellValueFactory(
                    new PropertyValueFactory<>("cantidad")
            );

            colTipo.setCellValueFactory(datos ->
                    new SimpleStringProperty(
                            datos.getValue().getTipoEquipo().getNombre()
                    )
            );

            // Estados permitidos
            cmbEstado.setItems(FXCollections.observableArrayList(
                    "Disponible",
                    "En mantenimiento",
                    "Fuera de servicio"
            ));

            // Detectar selección de una fila
            tblEquipos.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((obs, anterior, actual) -> {

                        if (actual != null) {
                            seleccionarEquipo(actual);
                        } else {
                            idSeleccionado = null;
                            actualizarBotones();
                        }
                    });

            actualizarBotones();
            cargarTipos();
            listarEquipos();
        }

        // Cargar tipos desde PostgreSQL
        private void cargarTipos() {
            try {
                List<TipoEquipo> tipos = tipoEquipoDAO.listar();

                cmbTipoEquipo.setItems(
                        FXCollections.observableArrayList(tipos)
                );

                cmbFiltroTipo.setItems(
                        FXCollections.observableArrayList(tipos)
                );

            } catch (RuntimeException ex) {
                mostrarError("cargar los tipos", ex);
            }
        }

        // Mostrar todos los equipos
        @FXML
        private void listarEquipos() {

            txtBuscarNombre.clear();
            cmbFiltroTipo.getSelectionModel().clearSelection();

            cargarEquipos(null, null);
        }

        // Buscar por nombre y tipo
        @FXML
        private void buscarEquipos() {

            String nombre = txtBuscarNombre.getText();
            TipoEquipo tipo = cmbFiltroTipo.getValue();

            Long idTipo = null;

            if (tipo != null) {
                idTipo = tipo.getIdTipo();
            }

            cargarEquipos(nombre, idTipo);
        }

        // Actualizar los datos de la tabla
        private void cargarEquipos(String nombre, Long idTipo) {

            try {
                List<Equipo> equipos = equipoDAO.buscar(
                        nombre, idTipo
                );

                tblEquipos.getItems().setAll(equipos);
                tblEquipos.getSelectionModel().clearSelection();

                idSeleccionado = null;
                actualizarBotones();

            } catch (RuntimeException ex) {
                mostrarError("cargar los equipos", ex);
            }
        }

        // Seleccionar un equipo de la tabla
        private void seleccionarEquipo(Equipo equipo) {

            idSeleccionado = equipo.getIdEquipo();

            txtCodigo.setText(equipo.getCodigo());
            txtNombre.setText(equipo.getNombre());
            txtMarca.setText(equipo.getMarca() == null
                    ? "" : equipo.getMarca());

            txtCantidad.setText(
                    String.valueOf(equipo.getCantidad())
            );

            cmbEstado.setValue(equipo.getEstado());

            // Buscar el tipo correspondiente por ID
            cmbTipoEquipo.getSelectionModel().clearSelection();

            for (TipoEquipo tipo : cmbTipoEquipo.getItems()) {

                if (tipo.getIdTipo().equals(
                        equipo.getTipoEquipo().getIdTipo())) {

                    cmbTipoEquipo.setValue(tipo);
                    break;
                }
            }

            actualizarBotones();
        }

        // Habilitar o deshabilitar botones
        private void actualizarBotones() {

            boolean sinSeleccion = idSeleccionado == null;

            btnActualizar.setDisable(sinSeleccion);
            btnEliminar.setDisable(sinSeleccion);
        }

        // Validar y obtener datos del formulario
        private Equipo obtenerEquipoFormulario() {

            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String marca = txtMarca.getText().trim();

            TipoEquipo tipo = cmbTipoEquipo.getValue();
            String estado = cmbEstado.getValue();

            if (codigo.isEmpty() || nombre.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "El código y el nombre son obligatorios."
                );
                return null;
            }

            if (tipo == null) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Debe seleccionar un tipo de equipo."
                );
                return null;
            }

            if (estado == null ||
                    !(estado.equals("Disponible") ||
                            estado.equals("En mantenimiento") ||
                            estado.equals("Fuera de servicio"))) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Debe seleccionar un estado válido."
                );
                return null;
            }

            if (codigo.length() > 30 ||
                    nombre.length() > 150 ||
                    marca.length() > 100) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Uno de los campos supera la longitud permitida."
                );
                return null;
            }

            int cantidad;

            try {
                cantidad = Integer.parseInt(
                        txtCantidad.getText().trim()
                );

            } catch (NumberFormatException ex) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "La cantidad debe ser un número entero."
                );
                return null;
            }

            if (cantidad < 0) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "La cantidad no puede ser negativa."
                );
                return null;
            }

            return new Equipo(
                    codigo,
                    nombre,
                    marca,
                    estado,
                    cantidad,
                    tipo
            );
        }

        // Guardar equipo
        @FXML
        private void guardarEquipo() {

            Equipo equipo = obtenerEquipoFormulario();

            if (equipo == null) {
                return;
            }

            try {
                equipoDAO.guardar(equipo);

                limpiarFormulario();
                listarEquipos();

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Equipo guardado correctamente."
                );

            } catch (RuntimeException ex) {
                mostrarError("guardar el equipo", ex);
            }
        }

        // Actualizar equipo
        @FXML
        private void actualizarEquipo() {

            if (idSeleccionado == null) {
                return;
            }

            Equipo equipo = obtenerEquipoFormulario();

            if (equipo == null) {
                return;
            }

            if (!confirmar(
                    "¿Desea actualizar este equipo?")) {
                return;
            }

            equipo.setIdEquipo(idSeleccionado);

            try {
                boolean actualizado = equipoDAO.actualizar(equipo);

                if (!actualizado) {
                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "El equipo ya no existe."
                    );
                    return;
                }

                limpiarFormulario();
                listarEquipos();

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Equipo actualizado correctamente."
                );

            } catch (RuntimeException ex) {
                mostrarError("actualizar el equipo", ex);
            }
        }

        // Eliminar equipo
        @FXML
        private void eliminarEquipo() {

            if (idSeleccionado == null) {
                return;
            }

            if (!confirmar(
                    "¿Está seguro de eliminar este equipo?")) {
                return;
            }

            try {
                boolean eliminado = equipoDAO.eliminar(
                        idSeleccionado
                );

                if (!eliminado) {
                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "El equipo ya no existe."
                    );
                    return;
                }

                limpiarFormulario();
                listarEquipos();

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Equipo eliminado correctamente."
                );

            } catch (RuntimeException ex) {
                mostrarError("eliminar el equipo", ex);
            }
        }

        // Limpiar formulario
        @FXML
        private void limpiarFormulario() {

            tblEquipos.getSelectionModel().clearSelection();

            idSeleccionado = null;

            txtCodigo.clear();
            txtNombre.clear();
            txtMarca.clear();
            txtCantidad.clear();

            cmbTipoEquipo.getSelectionModel().clearSelection();
            cmbEstado.getSelectionModel().clearSelection();

            actualizarBotones();
        }

        // Mostrar confirmación
        private boolean confirmar(String mensaje) {

            Alert alerta = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    mensaje,
                    ButtonType.OK,
                    ButtonType.CANCEL
            );

            alerta.setHeaderText(null);

            return alerta.showAndWait()
                    .orElse(ButtonType.CANCEL) == ButtonType.OK;
        }

        // Mostrar mensajes
        private void mostrarAlerta(
                Alert.AlertType tipo, String mensaje) {

            Alert alerta = new Alert(tipo, mensaje);
            alerta.setHeaderText(null);
            alerta.showAndWait();
        }

        // Mostrar errores de base de datos
        private void mostrarError(
                String operacion, RuntimeException ex) {

            Throwable causa = ex;

            while (causa != null) {

                if (causa instanceof SQLException sql) {

                    if ("23505".equals(sql.getSQLState())) {
                        mostrarAlerta(
                                Alert.AlertType.ERROR,
                                "El código del equipo ya está registrado."
                        );
                        return;
                    }

                    if ("23514".equals(sql.getSQLState())) {
                        mostrarAlerta(
                                Alert.AlertType.ERROR,
                                "Los datos no cumplen las restricciones."
                        );
                        return;
                    }
                }

                causa = causa.getCause();
            }

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo " + operacion +
                            ". Revise la conexión y los datos."
            );

            ex.printStackTrace();
        }
    }


}
