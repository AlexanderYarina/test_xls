package home.controller;

import home.dao.AppDb;
import home.service.Validator;
import home.service.ValidatorImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class ChangeFieldNameController {
    @FXML
    private TextField editField;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label messageLabel;

    private Stage stage;

    private Validator validator = new ValidatorImpl();

    public ChangeFieldNameController() {
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void buttonOk() {
        String newValue = editField.textProperty().get();
        if (!validator.validateColumnName(newValue)) {
            messageLabel.textProperty().set("Неверно введено имя");
            return;
        }
        AppDb.correspondence.setColumnInDataTable(newValue);
        stage.close();
    }

    @FXML
    private void buttonCancel() {
        AppDb.correspondence.setColumnInDataTable("");
        stage.close();
    }

    public void onStageShown() {
        if (messageLabel != null) messageLabel.textProperty().set("");
        if (editField != null) editField.textProperty().set(AppDb.correspondence.getColumnInDataTable().get());
    }
}
