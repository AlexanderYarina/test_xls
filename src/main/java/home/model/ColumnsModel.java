package home.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Alex on 05.12.2016.
 */
public class ColumnsModel {
    private StringProperty columnInFile;
    private StringProperty columnInDataTable;

    public ColumnsModel(String columnInFile) {
        this.columnInFile = new SimpleStringProperty(columnInFile);
        this.columnInDataTable = new SimpleStringProperty(columnInFile);
    }

    public StringProperty getColumnInFile() {
        return columnInFile;
    }


    public void setColumnInFile(String columnInFile) {
        this.columnInFile.set(columnInFile);
    }

    public StringProperty getColumnInDataTable() {
        return columnInDataTable;
    }

    public void setColumnInDataTable(String columnInDataTable) {
        this.columnInDataTable.set(columnInDataTable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnsModel that = (ColumnsModel) o;

        return !(columnInDataTable != null ? !columnInDataTable.get().equals(that.columnInDataTable.get()) : that.columnInDataTable != null);

    }

    @Override
    public int hashCode() {
        return columnInDataTable != null ? columnInDataTable.get().hashCode() : 0;
    }
}
