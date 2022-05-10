import classes.Employee;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        //T1
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        //T2
        fileName = "data.xml";
        json = listToJson(parceXML(fileName));
        writeString(json, "data2.json");

        //T3
        fileName = "data.json";
        List<Employee> jsonString = readString(fileName);
        System.out.printf("Классы из файла %s\n", fileName);
        jsonString.forEach(System.out::println);

        fileName = "data2.json";
        System.out.printf("Классы из файла %s\n", fileName);
        jsonString = readString(fileName);
        jsonString.forEach(System.out::println);

    }

    private static List<Employee> readString(String fileName) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        try {
            JSONArray employees = (JSONArray) parser.parse(new FileReader(fileName));
            for (Object employee : employees) {
                list.add(gson.fromJson(employee.toString(), Employee.class));
            }
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    private static List<Employee> parceXML(String fileName) {
        HashMap<String, String> employees = new HashMap<>();
        List<Employee> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(fileName));

            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node_ = nodeList.item(i);
                if (node_.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList employeeNodeList = node_.getChildNodes();
                    for (int j = 0; j < employeeNodeList.getLength(); j++) {
                        if (employeeNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) employeeNodeList.item(j);
                            employees.put(element.getTagName(), element.getTextContent());
                        }
                    }
                    list.add(new Employee(
                            Long.parseLong(employees.get("id")),
                            employees.get("firstName"),
                            employees.get("lastName"),
                            employees.get("country"),
                            Integer.parseInt(employees.get("age"))));
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    private static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, new TypeToken<List<Employee>>() {
        }.getType());
    }

    private static @Nullable List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


}
