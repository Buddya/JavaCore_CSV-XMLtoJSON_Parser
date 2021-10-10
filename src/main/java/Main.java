import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileName = "data.csv";
        //set column
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        //get list from CSV
        List<Employee> employeeList = parseCSV(columnMapping, fileName);
        //create json from list
        String json = listToJson(employeeList);
        //create json file
        writeStringToJson(json);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            //mapping strategy
            ColumnPositionMappingStrategy<Employee> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(Employee.class);
            mappingStrategy.setColumnMapping(columnMapping);

            //bean builder
            CsvToBeanBuilder<Employee> csvToBeanBuilder = new CsvToBeanBuilder<Employee>(csvReader);
            csvToBeanBuilder = csvToBeanBuilder.withMappingStrategy(mappingStrategy);
            CsvToBean<Employee> csvToBean = csvToBeanBuilder.build();

            return csvToBean.parse();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static String listToJson(List<Employee> employeeList) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(employeeList, listType);
    }

    private static void writeStringToJson(String json) {

        if (!json.isEmpty()) {
            try (FileWriter fileWriter = new FileWriter("data.json", false)) {
                fileWriter.write(json);
                fileWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
