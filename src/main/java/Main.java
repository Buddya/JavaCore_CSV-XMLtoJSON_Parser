import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        //for task 1:
        String fileName = "data.csv";
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> employeeList = parseCSV(columnMapping, fileName);
        String json = listToJson(employeeList);
        writeStringToJson(json, "data_from_csv.json");


        //for task 2:
        String xmlFileName = "data.xml";
        List<Employee> employeeList1 = parseXML(xmlFileName);
        String jsonFromXML = listToJson(employeeList1);
        writeStringToJson(jsonFromXML,"data_from_XML.json");
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employeeList = new ArrayList<>();

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(fileName);

        //xml root
        Node root = document.getDocumentElement();
        NodeList firstLevel = root.getChildNodes();
        for (int i = 0; i < firstLevel.getLength(); i++) {
            Node node_ = firstLevel.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                //need variables for Employee constructor (or just cr8 setters?)
                int id = 0, age = 0;
                String firstName = null, lastName = null, country = null;

                NodeList secondLevel = node_.getChildNodes();
                for (int j = 0; j < secondLevel.getLength(); j++) {
                    Node secondLvlNode = secondLevel.item(j);
                    if (Node.ELEMENT_NODE == secondLvlNode.getNodeType()) {

                        switch (secondLvlNode.getNodeName()) {
                            case "id" -> id = Integer.parseInt(secondLvlNode.getTextContent());
                            case "firstName" -> firstName = secondLvlNode.getTextContent();
                            case "lastName" -> lastName = secondLvlNode.getTextContent();
                            case "country" -> country = secondLvlNode.getTextContent();
                            case "age" -> age = Integer.parseInt(secondLvlNode.getTextContent());
                        }
                    }
                }
                employeeList.add(new Employee(id, firstName, lastName, country, age));
            }
        }
        return employeeList;
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

    private static void writeStringToJson(String json, String parsingFileName) {

        if (!json.isEmpty()) {
            try (FileWriter fileWriter = new FileWriter(parsingFileName, false)) {
                fileWriter.write(json);
                fileWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
