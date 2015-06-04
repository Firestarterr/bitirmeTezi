package org.firestarterr.bitirmeTezi.analyzers;

import org.apache.commons.lang.StringUtils;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.CommitWrapper;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.FileWrapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class OrcaAnalyzer extends BaseAnalyzer {

    public OrcaAnalyzer() {
        super("ORCA-REPORT");
    }

    @Override
    public void run() throws IOException, MappingException, MarshalException, ValidationException, ParseException {
        java.io.File file = new java.io.File("C:\\Users\\murat\\Desktop\\workspace\\orca-report\\log.log");

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        CommitWrapper parsed = null;
        while ((line = br.readLine()) != null) {
            if (line.contains("*-*-")) {
                if (parsed != null) {
                    createCommit(parsed);
                }
                parsed = parseCommitData(line);
            } else if (line.contains("|")) {
                parseCommitFileData(parsed, line);
            }
        }
        br.close();
        fr.close();
        analyzeData();
        System.out.println("orca-ended.");
    }

    private CommitWrapper parseCommitData(String line) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM D kk:mm:ss yyyy");

        CommitWrapper wrapper = new CommitWrapper();
        String[] commitSplit = StringUtils.splitByWholeSeparator(line, "*-*-");
        wrapper.commitName = commitSplit[2];
        wrapper.date = sdf.parse(commitSplit[1]);
        wrapper.dev = commitSplit[0];
        wrapper.issueName = getIssueName(commitSplit[2]);
        return wrapper;
    }

    private String getIssueName(String message) {
        String issueName = null;
        message = message.trim();
        if (message.startsWith("#") || Character.isDigit(message.charAt(0)) || message.startsWith("Issue")) {
            int firstSpace = message.indexOf(" ");
            if (firstSpace == -1) {
                issueName = message;
            } else {
                issueName = message.substring(0, firstSpace);
            }
        }
        return issueName;
    }

    private void parseCommitFileData(CommitWrapper parsed, String line) {
        String[] lineSplit = StringUtils.splitByWholeSeparator(line, "|");
        String filePart = lineSplit[0].trim();
        String locPart = StringUtils.splitByWholeSeparator(lineSplit[1], " ")[0].trim();

        if (parsed.fileStrings == null) {
            parsed.fileStrings = new HashMap<>();
        }
        if (locPart.contains("Bin")) {
            parsed.fileStrings.put(filePart, 1);
        } else {
            parsed.fileStrings.put(filePart, Integer.parseInt(locPart));
        }
    }

    @Override
    protected String getPackageName(String path) {
        String packageName;
        packageName = "root";
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
            wrapper.path = "";
        } else {
            wrapper.path = fileNameWithPath.substring(0, lastIndex + 1);
            wrapper.name = fileNameWithPath.substring(lastIndex + 1);
        }
        return wrapper;
    }

}
