package home.controller;

import home.Main;
import home.dao.AppDb;
import home.model.ColumnsModel;
import home.service.Validator;
import home.service.ValidatorImpl;
import home.unils.AppUtils;
import javafx.scene.control.Label;
import java.io.File;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

/**
 * Created by Alex on 07.12.2016.
 */
public class RootController {
    private static final String FILE_NOT_SELECTED = "Файл не выбран";

    @FXML
    private TextField tableNameEdit;

    @FXML
    private Button fileOpenButton;

    @FXML
    private Label selectedFileName;

    @FXML
    private Button saveFileButton;

    @FXML
    private TableColumn<ColumnsModel, String> dataTableColumnName;

    @FXML
    private TableView<ColumnsModel> columnsTable;

    private Main mainApp;

    private Validator validator = new ValidatorImpl();

    File xlsFile;

    public RootController() {
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        selectedFileName.textProperty().set(FILE_NOT_SELECTED);
        saveFileButton.disableProperty().bind(
                selectedFileName.textProperty().isEqualTo(FILE_NOT_SELECTED)
                        .or(selectedFileName.textProperty().isEmpty())
                        .or(tableNameEdit.textProperty().isEmpty()));
        tableNameEdit.disableProperty().bind(selectedFileName.textProperty().isEqualTo(FILE_NOT_SELECTED)
                .or(selectedFileName.textProperty().isEmpty()));
        dataTableColumnName.setCellValueFactory(data -> data.getValue().getColumnInDataTable());
        columnsTable.setItems(AppDb.columnsModelTable);
    }

    @FXML
    private void onFileOpenButtonAction() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XLS files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);
        selectedFileName.textProperty().set("");
        xlsFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (xlsFile != null) {
            selectedFileName.textProperty().set(xlsFile.getAbsolutePath());
            fillColumnsTable();
        }
    }

    @FXML
    private void onSaveFileButtonAction() {
        if (!validator.validateTableName(tableNameEdit.textProperty().get())) {
            mainApp.getMessStage("Не верно введено имя таблицы !").showAndWait();
            return;
        }
        if (!validator.validateColumnsNamesInTable(AppDb.columnsModelTable)) {
            mainApp.getMessStage("Среди столбцов есть столбец с не корректным именем !").showAndWait();
            return;
        }
        boolean scvResult = AppUtils.createDbTable(xlsFile, tableNameEdit.textProperty().get(), AppDb.columnsMap);
        String jsonFileName = xlsFile.getName();
        int index = jsonFileName.lastIndexOf('.');
        if(index > 0) jsonFileName = jsonFileName.substring(0, index);
        jsonFileName =  jsonFileName +"-"+ Math.abs(new Random().nextInt()) + ".json";
        String gsonResult = AppUtils.saveGson(jsonFileName, tableNameEdit.textProperty().get(), AppDb.columnsModelTable);
        if (scvResult){
            mainApp.getMessStage("Операция прошла успешно !").showAndWait();
        } else {
            mainApp.getMessStage("Данные не были сохранены в БД !").showAndWait();
        }
        if (gsonResult.isEmpty()){
            mainApp.getMessStage("JSON данные не были сохранены на диск !").showAndWait();
        } else {
            mainApp.getMessStage("JSON сохранен в " + gsonResult).showAndWait();
        }
    }

    @FXML
    private void onEditTableColumnNameAction(TableColumn.CellEditEvent<ColumnsModel, String> t) {
        AppDb.correspondence.setColumnInDataTable(t.getOldValue());
        mainApp.getFormStage().showAndWait();
        String newValue = AppDb.correspondence.getColumnInDataTable().get();
        if (!"".equals(newValue)) {
            t.getRowValue().setColumnInDataTable(newValue);
        }
        columnsTable.getItems().contains(newValue);
    }

    @FXML
    private void onEditCommitTableColumnNameAction() {
    }

    private void fillColumnsTable() {
        assert (xlsFile != null);
        AppDb.columnsModelTable.clear();
        AppDb.columnsMap.putAll(AppUtils.getColumnNames(xlsFile));
        AppDb.columnsModelTable.addAll(
                AppDb.columnsMap.values()
                        .stream()
                        .map(r -> new ColumnsModel(r))
                        .collect(Collectors.toList()));
    }
}
