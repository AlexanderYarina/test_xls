package home.service;

import home.dao.AppDb;
import home.model.ColumnsModel;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Alex on 08.12.2016.
 */
public class ValidatorImpl implements Validator{
    @Override
    public boolean validateTableName(String value) {
        return value.matches("^[a-zA-Z]+\\w+");
    }

    @Override
    public boolean validateColumnName(String value) {
        return value.matches("^[a-zA-Z]+\\w+") &&
                !AppDb.columnsModelTable
                        .stream()
                        .map(e -> e.getColumnInDataTable())
                        .collect(Collectors.toList())
                        .contains(value);
    }

    @Override
    public boolean validateColumnsNamesInTable(ObservableList<ColumnsModel> correspondencesTable) {
        Set<ColumnsModel> set = new HashSet<>();
        set.addAll((Collection) correspondencesTable);
        return set.size() == AppDb.columnsModelTable.size() && AppDb.columnsModelTable
                .stream()
                .map(e -> e.getColumnInDataTable().get())
                .collect(Collectors.toList())
                .stream()
                .allMatch(e -> e.matches("^[a-zA-Z]+\\w+"));
    }
}
