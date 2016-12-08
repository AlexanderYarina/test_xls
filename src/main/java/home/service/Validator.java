package home.service;

import home.model.ColumnsModel;
import javafx.collections.ObservableList;

/**
 * Created by Alex on 08.12.2016.
 */
public interface Validator {
    boolean validateTableName(String value);
    boolean validateColumnName(String value);
    boolean validateColumnsNamesInTable(ObservableList<ColumnsModel> correspondencesTable);
}
