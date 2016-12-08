package home.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import home.model.ColumnsModel;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Alex on 07.12.2016.
 */
public class AppDb {
    public static final ObservableList<ColumnsModel> columnsModelTable = FXCollections.observableArrayList();
    public static final Map<Integer, String> columnsMap = new TreeMap<>();
    public static final ColumnsModel correspondence = new ColumnsModel("");
}
