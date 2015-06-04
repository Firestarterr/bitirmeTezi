package org.firestarterr.bitirmeTezi.analyzers;

import org.apache.commons.lang.StringUtils;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.CommitWrapper;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.FileWrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public abstract class BaseGitAnalyzer extends BaseAnalyzer {

    public BaseGitAnalyzer(String projectName) {
        super(projectName);
    }

    protected CommitWrapper parseCommitData(String line) throws ParseException {
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

    protected void parseCommitFileData(CommitWrapper parsed, String line) {
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
