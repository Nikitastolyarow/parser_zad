import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        String fileName = "data.csv";
        String jsonName = "data.json";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json,jsonName);

        String xmlName = "data.xml";
        String jsonName2 = "data2.json";
        List<Employee> list2 = parseXML(xmlName);
        String json2 = listToJson(list2);
        writeString(json2,jsonName2);

        String jsonName3 = "new_data.json";
        String json3 = readString(jsonName3);
        List<Employee> list3 = jsonToList(json3);
        list3.forEach(System.out::println);

    }

    private static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();
        try {
            Object obj = new JSONParser().parse(json);
            JSONArray array = (JSONArray) obj;
            for (Object spisc : array) {
                list.add(gson.fromJson(spisc.toString(), Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }


    private static String readString(String fileName) {
        StringBuilder sb =  new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
               sb.append(line);
            }
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return sb.toString();
    }

    private static List<Employee> parseXML(String xmlFileName) throws ParserConfigurationException, IOException, SAXException {

        List<String> elements = new ArrayList<>();
        List<Employee> list = new ArrayList<>();

           DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
           DocumentBuilder builder = factory.newDocumentBuilder();
           Document doc = builder.parse( new File(xmlFileName));
           Node root = doc.getDocumentElement();
           NodeList nodeList = root.getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
             Node node = nodeList.item(i);
                if(node.getNodeName().equals("employee")){
                    NodeList nodeListNew = node.getChildNodes();
                for (int j=0 ; j <nodeListNew.getLength(); j++ ){
                    Node newList = nodeListNew.item(j);
                    if (Node.ELEMENT_NODE == newList.getNodeType()) {
                        elements.add(newList.getTextContent());
                    }
                }
                list.add(new Employee(
                        Long.parseLong(elements.get(0)),
                        elements.get(1),
                        elements.get(2),
                        elements.get(3),
                        Integer.parseInt(elements.get(4))));
                elements.clear();
                }
            }
            return list;

    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static void  writeString(String json , String jsonName) {
        try (FileWriter file = new FileWriter(jsonName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
