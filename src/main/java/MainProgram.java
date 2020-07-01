import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;

public class MainProgram {

    public static void main(String[] args) {
        try {
            MainProgram main = new MainProgram();
            File fXmlFile = main.getFileFromResources(Constant.DATA_SOURCE);
            Map<String,DcTrackingTime> data = parse(fXmlFile);
            Map<String,DcTrackingTime> result = data.entrySet()
                    .stream().collect(Collectors.toMap(e->e.getKey(),e-> calculateNoWorkingTime(e.getValue())));
            Path p = Paths.get(Constant.RESULT_CSV);
            try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(p, CREATE, WRITE))) {
                result.entrySet().stream().filter(e->e.getValue().noWorkingTime != null)
                        .forEach(e->printNonWorkListTime(e.getValue(), pw));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }

    }
    private static Map<String,DcTrackingTime> parse(File fXmlFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(Constant.ROOT_NODE);
        Map<String,DcTrackingTime> data = new HashMap<>();
        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                String dcName = eElement.getAttribute(Constant.DC_NAME);
                String dcCenter = eElement.getAttribute(Constant.DATA_CENTER);
                String sDate = eElement.getAttribute(Constant.START_DATE);
                LocalDateTime startDate = LocalDateTime.parse(sDate, Constant.DATE_TIME_FORMATTER);
                String currentDate = startDate.format(DateTimeFormatter.ofPattern(Constant.DATE_FORMAT));
                String key = dcName + dcCenter + "||" + currentDate;
                LocalDateTime endDate = LocalDateTime.parse(eElement.getAttribute(Constant.END_DATE), Constant.DATE_TIME_FORMATTER);
                if (!data.containsKey(key)) {
                    data.put(key, new DcTrackingTime(dcName, dcCenter, startDate, endDate));
                } else {
                    DcTrackingTime currentDcTT = data.get(key);
                    currentDcTT.addWorkList(startDate, endDate);
                }
            }
        }
        return data;
    }
    private static DcTrackingTime calculateNoWorkingTime(DcTrackingTime x) {
        List<IntervalWT> workTimeList = x.getWorkListTime();
        int i = 0;
        while (i < workTimeList.size() - 1) {
            IntervalWT prevWT = workTimeList.get(i);
            IntervalWT nextWT = workTimeList.get(i+1);
            if (prevWT.endTime.isBefore(nextWT.startTime)) {
                i++;
                continue;
            }
            if (prevWT.endTime.isBefore(nextWT.endTime)) {
                prevWT.setEndTime(nextWT.endTime);
                workTimeList.remove(i+1);
                continue;
            } else {
                workTimeList.remove(i+1);
                continue;
            }
        }
        x.createNonWorkListTime();
        return x;
    }

    public static void printNonWorkListTime(DcTrackingTime x, PrintWriter pw) {
        String dcName = x.getDcName();
        String dataCenter = x.getDataCenter();
        x.noWorkingTime.stream().forEach(timeInterval -> {
            StringBuilder result = new StringBuilder();
            result.append(dcName + Constant.SEPARATE);
            result.append(dataCenter + Constant.SEPARATE);
            result.append(Constant.FROM + " " + timeInterval.getStartTime().format(Constant.DATE_TIME_FORMATTER)
                    + " " + Constant.TO + " " + timeInterval.getEndTime().format(Constant.DATE_TIME_FORMATTER));
            pw.println(result.toString());
        });
    }

}
