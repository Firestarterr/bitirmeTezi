package org.firestarterr.bitirmeTezi.analyzers;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.CommitWrapper;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.FileWrapper;
import org.firestarterr.bitirmeTezi.analyzers.xmlmodals.Logentry;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HysAnalyzer extends BaseAnalyzer {

    public HysAnalyzer() {
        super("HYS");
    }

    @Override
    public void run() throws IOException, MappingException, MarshalException, ValidationException, ParseException {

        FileReader reader = new FileReader("C:\\Users\\murat\\Desktop\\workspace\\hysbitirme\\log.txt");

        // Define mapping.xml file address
        final String MAPPING_FILE = "C:\\Users\\murat\\Desktop\\workspace\\bitirmeTezi\\src\\main\\java\\org\\firestarterr\\bitirmeTezi\\analyzers\\xmlmodals\\mapping.xml";
        // Load Mapping
        Mapping mapping = new Mapping();
        mapping.loadMapping(MAPPING_FILE);

        List<Logentry> log = new ArrayList<>();
        // create a new Marshaller
        Unmarshaller unmarshaller = new Unmarshaller(log);
        unmarshaller.setMapping(mapping);

        log = (List<Logentry>) unmarshaller.unmarshal(reader);

        for (Logentry logentry : log) {
            createCommit(parseCommitData(logentry));
        }

        analyzeData();
        System.out.println("hys-ended.");
    }

    private CommitWrapper parseCommitData(Logentry logentry) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        CommitWrapper wrapper = new CommitWrapper();
        wrapper.commitName = logentry.getMsg();
        wrapper.date = sdf.parse(logentry.getDate());
        wrapper.dev = logentry.getAuthor();
        wrapper.issueName = getIssueName(logentry.getMsg());
        wrapper.fileStrings = getFileMap(logentry);
        return wrapper;
    }

    private String getIssueName(String message) {
        String issueName = null;
        if (message.startsWith("HYS-")) {
            int firstSpace = message.indexOf(" ");
            if (firstSpace == -1) {
                issueName = message;
            } else {
                issueName = message.substring(0, firstSpace);
            }
        }
        return issueName;
    }

    private Map<String, Integer> getFileMap(Logentry logentry) {
        Map<String, Integer> fileMap = new HashMap<>();
        for (String path : logentry.getPaths().getPath()) {
            fileMap.put(path, 1);
        }
        return fileMap;
    }

    @Override
    protected String getPackageName(String path) {
        String packageName;
        path = path.substring(11);
        int packageIndex = path.indexOf("/");

        if (packageIndex == -1) {
            packageName = "root";
        } else {
            packageName = path.substring(0, packageIndex);
        }
        return packageName;
    }

    @Override
    protected FileWrapper parseFileString(String fileString) {
        FileWrapper wrapper = new FileWrapper();
        int lastDot = fileString.lastIndexOf(".");
        if (lastDot == -1) {
            return null;
        }
        String fileNameWithPath = fileString.substring(0, lastDot).trim();
        wrapper.ext = fileString.substring(lastDot + 1).trim();

        int lastIndex = fileNameWithPath.lastIndexOf("/");
        if (lastIndex == -1) {
            wrapper.name = fileNameWithPath;
            wrapper.path = null;
        } else {
            wrapper.path = fileNameWithPath.substring(0, lastIndex + 1);
            wrapper.name = fileNameWithPath.substring(lastIndex + 1);
        }
        return wrapper;
    }
}
