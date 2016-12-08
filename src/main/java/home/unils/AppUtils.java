package home.unils;

import com.google.gson.Gson;
import home.dao.AppDb;
import home.model.ColumnsModel;
import javafx.collections.ObservableList;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Alex on 07.12.2016.
 */
public class AppUtils {

    public static Map<Integer, String> getColumnNames(File xlsFile){
        Map<Integer, String> result = new HashMap<>();
        try (HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(xlsFile))) {
            Row row = detectFirstRowWithData(wb);

            for (Cell cell : row) {
                int cellType = cell.getCellType();
                if (cell.getCellType() != Cell.CELL_TYPE_BLANK && cellType == Cell.CELL_TYPE_STRING && !cell.getStringCellValue().equals("")) {
                    result.put(cell.getColumnIndex(), cell.getStringCellValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean createDbTable(File xlsFile, String tableName, Map<Integer, String> columnMap) {
        File csvFile = createCsv(xlsFile, columnMap);
        return csvFile != null && saveXlsData(tableName, csvFile);
    }


    public static String saveGson(String jsonFileName, String tableName, ObservableList<ColumnsModel> correspondencesTable) {
        Gson gson = new Gson();
        String result = "";
        String json = gson.toJson(correspondencesTable);

        try (StringWriter sw = new StringWriter()) {
            Path path = Paths.get(jsonFileName);
            Files.write(path, json.getBytes());
            result = path.toFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static boolean saveXlsData(String tableName, File csvFile) {
        boolean result = false;
        Session session = null;
        tableName = tableName.toUpperCase();
        try {
            {
                session = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = session.beginTransaction();
                String sql = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME LIKE '" + tableName + "' ";
                Query query = session.createSQLQuery(sql);
                if (query.list().size() != 0) {
                    sql = "DROP TABLE " + tableName;
                    query = session.createSQLQuery(sql);
                    query.executeUpdate();
                }
                sql = "CREATE TABLE " + tableName + " AS SELECT * FROM CSVREAD ('" + csvFile.getAbsolutePath() + "')";
                query = session.createSQLQuery(sql);
                query.executeUpdate();
                transaction.commit();
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.flush();
                session.close();
            }
        }
        return result;
    }

    private static File createCsv(File xlsFile, Map<Integer, String> columnMap) {
        File result;
        try {
            result = File.createTempFile("temp", ".csv");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try (InputStream in = new FileInputStream(xlsFile);
             HSSFWorkbook wb = new HSSFWorkbook(in);
             BufferedWriter out = new BufferedWriter(new FileWriter(result))) {

            Row row = detectFirstRowWithData(wb);
            StringBuilder rowForWrite = new StringBuilder();

            for (Map.Entry<Integer, String> entry : columnMap.entrySet()) {
                if (!rowForWrite.toString().equals("")) {
                    rowForWrite.append(',');
                }
                String origFldName = entry.getValue();
                String newFldName = "";
                for (ColumnsModel correspondence : AppDb.columnsModelTable) {
                    if (correspondence.getColumnInFile().equals(origFldName)) {
                        newFldName = correspondence.getColumnInDataTable().get();
                        break;
                    }
                }
                rowForWrite.append('"').append(newFldName).append('"');
            }
            out.write(rowForWrite.toString());
            out.newLine();

            Sheet sheet = row.getSheet();
            Iterator<Row> iterator = sheet.iterator();
            for (iterator.next(); iterator.hasNext(); ) {
                rowForWrite = new StringBuilder();
                Row currentRow = iterator.next();
                boolean first = true;
                for (Map.Entry<Integer, String> entry : columnMap.entrySet()) {
                    if (!first) {
                        rowForWrite.append(',');
                    }
                    first = false;
                    Cell cell = currentRow.getCell(entry.getKey());
                    if (cell == null) {
                        rowForWrite.append("");
                        continue;
                    }
                    rowForWrite.append('"').append(cell.getStringCellValue()).append('"');
                }
                out.write(rowForWrite.toString());
                out.newLine();
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static Row detectFirstRowWithData(HSSFWorkbook wb) {
        Row row = null;
        Iterator<Sheet> iterator = wb.iterator();
        while (iterator.hasNext() && row == null) {
            Sheet sheet = iterator.next();
            for (Row r : sheet) {
                if (r.getFirstCellNum() != -1) {
                    row = r;
                    break;
                }
            }
        }
        return row;
    }
}
