package org.firestarterr.bitirmeTezi.analyzers;

import org.apache.commons.lang.StringUtils;
import org.firestarterr.bitirmeTezi.model.*;
import org.firestarterr.bitirmeTezi.model.Package;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrcaAnalyzer {

//    @Autowired
//    transient CommitService commitService;
//
//    @Autowired
//    transient DeveloperService developerService;
//
//    @Autowired
//    transient FileService fileService;
//
//    @Autowired
//    transient ModuleService moduleService;
//
//    @Autowired
//    transient ProjectService projectService;

    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM D kk:mm:ss yyyy");

    List<Project> projects = new ArrayList<>();
    List<Package> packages = new ArrayList<>();
    List<Issue> issues = new ArrayList<>();
    List<File> files = new ArrayList<>();
    List<Developer> developers = new ArrayList<>();
    List<Commit> commitSuccess = new ArrayList<>();
    List<String> linesFailed = new ArrayList<>();

    @Test
    public void run() throws IOException {
        java.io.File file = new java.io.File("C:\\Users\\murat\\Desktop\\workspace\\orca-report\\log.log");

        Project project = new Project();
        project.setName("orca-report");

        Package core = new Package();
        core.setName("core");
        core.setProject(project);
        packages.add(core);

        projects.add(project);

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        Commit commit = null;
        while ((line = br.readLine()) != null) {
            try {
                if (line.contains("*-*-")) {
                    String[] commitMessage = StringUtils.splitByWholeSeparator(line, "*-*-");
                    commit = createCommitData(commitMessage);

                } else if (line.contains("|")) {
                    String[] commitMessage = StringUtils.splitByWholeSeparator(line, "|");
                    updateCommitFiles(commit, commitMessage);
                    updateDeveloperAndCommitUpdateDates(commit);
                }
            } catch (Exception e) {
                e.printStackTrace();
                linesFailed.add(line);
                continue;
            }
        }
        br.close();
        fr.close();
        analyzeData();
        System.out.println("orca-ended.");
    }

    public Commit createCommitData(String[] commitMessage) throws ParseException {
        Commit commit = new Commit();
        if (commitMessage[0] != null) {
            String devName = commitMessage[0];
            if (devName.equals("rcpayan")) {
                devName = "Recep Ayan";
            }
            if (devName.equals("kamilors")) {
                devName = "Kamil Ors";
            }
            Developer dev = findOrCreateDeveloperList(devName);
            dev.getCommits().add(commit);
            commit.setDeveloper(dev);
        }
        if (commitMessage[1] != null) {
            String dateS = commitMessage[1];
            Date date = sdf.parse(dateS);
            commit.setCommitDate(date);
            if (commit.getDeveloper().getIsOrcaDeveloper()) {
                commit.setRelCreatedDate(date);
            }
        }
        if (commitMessage[2] != null) {
            String message = commitMessage[2].trim();
            if (message.startsWith("#") || Character.isDigit(message.charAt(0))) {
                int firstSpace = message.indexOf(" ");
                if (firstSpace == -1) {
                    firstSpace = message.length();
                }
                String issueName = "";
                String cm;
                issueName = message.substring(0, firstSpace);
                try {
                    cm = message.substring(firstSpace + 1);
                } catch (Exception e) {
                    cm = "";
                }
                commit.setRelatedIssue(findOrCreateIssueList(issueName));
                commit.setName(cm);
            } else if (message.startsWith("Issue")) {
                message = message.replace("Issue", "").trim();
                int firstSpace = message.indexOf(" ");
                String issueName = "Issue " + message.substring(0, firstSpace);
                String cm;
                try {
                    cm = message.substring(firstSpace + 1);
                } catch (Exception e) {
                    cm = "";
                }
                commit.setRelatedIssue(findOrCreateIssueList(issueName));
                commit.setName(cm);
            } else {
                commit.setName(message);
            }
        }

        commitSuccess.add(commit);
        return commit;
    }

    public Developer findOrCreateDeveloperList(String name) {
        for (Developer developer : developers) {
            if (developer.getName().equals(name)) {
                return developer;
            }
        }
        Developer dev = createDeveloperData(name);
        developers.add(dev);
        return dev;
    }

    public Issue findOrCreateIssueList(String name) {
        for (Issue issue : issues) {
            if (issue.getName().equals(name)) {
                return issue;
            }
        }
        Issue issue = new Issue();
        issue.setName(name);
        issues.add(issue);
        return issue;
    }

    public Developer createDeveloperData(String devName) {
        Developer developer = new Developer();
        developer.setName(devName);
        boolean isOrca = isOrcaDeveloper(devName);
        developer.setIsOrcaDeveloper(isOrca);
//        developers = developerService.save(developers);
        return developer;
    }

    public boolean isOrcaDeveloper(String devName) {
        return devName.equals("murats")
                || devName.equals("Recep Ayan")
                || devName.equals("Kamil Ors")
                || devName.equals("elif");
    }

    public void updateCommitFiles(Commit commit, String[] messages) {
        File file = findOrCreateFileData(messages[0]);
        commit.getFiles().add(file);
        if (file.getUpdatedDate() == null) {
            file.setUpdatedDate(commit.getCommitDate());
        }
        file.setCreatedDate(commit.getCommitDate());
        if (commit.getDeveloper().getIsOrcaDeveloper()) {
            if (file.getRelUpdatedDate() == null) {
                file.setRelUpdatedDate(commit.getCommitDate());
            }
            file.setRelCreatedDate(commit.getCommitDate());
        }
        file.getCommits().add(commit);
        Integer locEdited = 1;
        if (!messages[1].contains("Bin")) {
            String locPart = messages[1];
            String[] locSplit = StringUtils.splitByWholeSeparator(locPart.trim(), " ");
            locEdited = Integer.parseInt(locSplit[0]);
        }
        if (commit.getLocEdited() == null) {
            commit.setLocEdited(locEdited);
        } else {
            commit.setLocEdited(commit.getLocEdited() + locEdited);
        }
        if (commit.getDeveloper().getLocEdited() == null) {
            commit.getDeveloper().setLocEdited(locEdited);
        } else {
            commit.getDeveloper().setLocEdited(commit.getDeveloper().getLocEdited() + locEdited);
        }
        if (file.getLocEdited() == null) {
            file.setLocEdited(locEdited);
        } else {
            file.setLocEdited(file.getLocEdited() + locEdited);
        }
        if (file.getModule().getLocEdited() == null) {
            file.getModule().setLocEdited(locEdited);
        } else {
            file.getModule().setLocEdited(file.getModule().getLocEdited() + locEdited);
        }
        commit.getLocEditedPerFile().put(file, locEdited);
    }

    public File findOrCreateFileData(String fileString) {
        int lastDot = fileString.lastIndexOf(".");
        String fileNameWithPath = fileString.substring(0, lastDot).trim();

        int lastIndex = fileNameWithPath.lastIndexOf("/");
        String fileName;
        if (lastIndex == -1) {
            fileName = fileNameWithPath;
        } else {
            fileName = fileNameWithPath.substring(lastIndex + 1);
        }
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        File file = createFileData(fileString);

        files.add(file);
        return file;
    }

    public File createFileData(String fileString) {
        File file = new File();

        int lastDot = fileString.lastIndexOf(".");
        String fileNameWithPath = fileString.substring(0, lastDot).trim();
        String ext = fileString.substring(lastDot + 1).trim();

        int lastIndex = fileNameWithPath.lastIndexOf("/");
        String fileName;
        String fullPath;
        if (lastIndex == -1) {
            fileName = fileNameWithPath;
            fullPath = "";
        } else {
            fileName = fileNameWithPath.substring(lastIndex + 1).trim();
            fullPath = fileNameWithPath.substring(0, lastIndex).trim();
        }

        file.setFullPath(fullPath);
        file.setName(fileName);
        file.setModule(packages.get(0));
        file.setProject(projects.get(0));
        file.setFileExt(ext);
        file.setUpdatedDate(null);
        file.setRelUpdatedDate(null);
        file.setCreatedDate(null);
        file.setRelCreatedDate(null);
//        file = fileService.save(file);
        return file;
    }

    public void updateDeveloperAndCommitUpdateDates(Commit commit) {
        Developer dev = commit.getDeveloper();
        if (dev.getUpdatedDate() == null) {
            dev.setUpdatedDate(commit.getCommitDate());
        }
        dev.setCreatedDate(commit.getCommitDate());

        for (Package pack : commit.getPackages()) {
            if (pack.getUpdatedDate() == null) {
                pack.setUpdatedDate(commit.getCommitDate());
            }
            pack.setCreatedDate(commit.getCommitDate());

            if (commit.getDeveloper().getIsOrcaDeveloper()) {
                if (pack.getRelUpdatedDate() == null) {
                    pack.setRelUpdatedDate(commit.getCommitDate());
                }
                pack.setRelCreatedDate(commit.getCommitDate());
            }
        }
    }

    public void analyzeData() {
        for (Commit commit : commitSuccess) {
            Developer dev = commit.getDeveloper();
            dev.increaseRecordCount();
            if (dev.getIsOrcaDeveloper()) {
                dev.increaseRelRecordCount();
            }

            for (Package pack : commit.getPackages()) {
                pack.increaseRecordCount();
                if (commit.getDeveloper().getIsOrcaDeveloper()) {
                    pack.increaseRelRecordCount();
                }
            }

            //private Map<Developer, Integer> developerChangeCountMap
            for (File file : commit.getFiles()) {
                file.increaseRecordCount();
                if (dev.getIsOrcaDeveloper()) {
                    file.increaseRelRecordCount();
                }
                if (file.getDeveloperChangeCountMap().keySet().contains(dev)) {
                    file.getDeveloperChangeCountMap().put(dev, file.getDeveloperChangeCountMap().get(dev) + 1);
                } else {
                    file.getDeveloperChangeCountMap().put(dev, 1);
                }
            }
        }


        for (File file : files) {
            for (Map.Entry<Developer, Integer> entry : file.getDeveloperChangeCountMap().entrySet()) {
                for (Map.Entry<Developer, Integer> iter : file.getDeveloperChangeCountMap().entrySet()) {
                    if (entry.getKey().getName().equals(iter.getKey().getName())) {
                        continue;
                    }
                    Developer dev1 = findOrCreateDeveloperList(entry.getKey().getName());
                    Developer dev2 = findOrCreateDeveloperList(iter.getKey().getName());
                    Integer minContribution = Math.min(entry.getValue(), iter.getValue());

                    //private Map<Developer, Integer> cooperationCount
                    if (dev1.getCooperationCount().get(dev2) == null) {
                        dev1.getCooperationCount().put(dev2, minContribution);
                    } else {
                        dev1.getCooperationCount().put(dev2, minContribution + dev1.getCooperationCount().get(dev2));
                    }
                    if (dev2.getCooperationCount().get(dev1) == null) {
                        dev2.getCooperationCount().put(dev1, minContribution);
                    } else {
                        dev2.getCooperationCount().put(dev1, minContribution + dev2.getCooperationCount().get(dev1));
                    }

                    //private Map<File, Map<Developer, Integer>> cooperatedOnFiles
                    if (dev1.getCooperatedOnFiles().get(file) == null) {
                        Map<Developer, Integer> coopMap = new HashMap<>();
                        coopMap.put(dev2, minContribution);
                        dev1.getCooperatedOnFiles().put(file, coopMap);
                    } else {
                        Map<Developer, Integer> coopMap = dev1.getCooperatedOnFiles().get(file);
                        if (coopMap.get(dev2) == null) {
                            coopMap.put(dev2, minContribution);
                            dev1.getCooperatedOnFiles().put(file, coopMap);
                        } else {
                            coopMap.put(dev2, minContribution + coopMap.get(dev2));
                            dev1.getCooperatedOnFiles().put(file, coopMap);
                        }
                    }
                    if (dev2.getCooperatedOnFiles().get(file) == null) {
                        Map<Developer, Integer> coopMap = new HashMap<>();
                        coopMap.put(dev1, minContribution);
                        dev2.getCooperatedOnFiles().put(file, coopMap);
                    } else {
                        Map<Developer, Integer> coopMap = dev2.getCooperatedOnFiles().get(file);
                        if (coopMap.get(dev1) == null) {
                            coopMap.put(dev1, minContribution);
                            dev2.getCooperatedOnFiles().put(file, coopMap);
                        } else {
                            coopMap.put(dev1, minContribution + coopMap.get(dev1));
                            dev2.getCooperatedOnFiles().put(file, coopMap);
                        }
                    }

                    //private Map<Module, Map<Developer, Integer>> cooperatedOnModules
                    if (dev1.getCooperatedOnModules().get(file.getModule()) == null) {
                        Map<Developer, Integer> coopMap = new HashMap<>();
                        coopMap.put(dev2, minContribution);
                        dev1.getCooperatedOnModules().put(file.getModule(), coopMap);
                    } else {
                        Map<Developer, Integer> coopMap = dev1.getCooperatedOnModules().get(file.getModule());
                        if (coopMap.get(dev2) == null) {
                            coopMap.put(dev2, minContribution);
                            dev1.getCooperatedOnModules().put(file.getModule(), coopMap);
                        } else {
                            coopMap.put(dev2, minContribution + coopMap.get(dev2));
                            dev1.getCooperatedOnModules().put(file.getModule(), coopMap);
                        }
                    }
                    if (dev2.getCooperatedOnModules().get(file.getModule()) == null) {
                        Map<Developer, Integer> coopMap = new HashMap<>();
                        coopMap.put(dev1, minContribution);
                        dev2.getCooperatedOnModules().put(file.getModule(), coopMap);
                    } else {
                        Map<Developer, Integer> coopMap = dev2.getCooperatedOnModules().get(file.getModule());
                        if (coopMap.get(dev1) == null) {
                            coopMap.put(dev1, minContribution);
                            dev2.getCooperatedOnModules().put(file.getModule(), coopMap);
                        } else {
                            coopMap.put(dev1, minContribution + coopMap.get(dev1));
                            dev2.getCooperatedOnModules().put(file.getModule(), coopMap);
                        }
                    }

                }
            }

            //change frequency calculations
            Double age = Double.valueOf(file.getUpdatedDate().getTime() - file.getCreatedDate().getTime());
            age = age / file.getRecordCount();
            file.setChangeFrequencyPerDay(age);

            if (file.getRelCreatedDate() != null) {
                Double relAge = Double.valueOf(file.getRelUpdatedDate().getTime() - file.getRelCreatedDate().getTime());
                relAge = relAge / file.getRelRecordCount();
                file.setRelChangeFrequencyPerDay(relAge);
            }
        }

        for (Developer developer : developers) {
            for (Map.Entry<Developer, Integer> entry : developer.getCooperationCount().entrySet()) {
                developer.getCooperationCount().put(entry.getKey(), entry.getValue() / 2);
            }

            for (Map.Entry<File, Map<Developer, Integer>> coopMapEntry : developer.getCooperatedOnFiles().entrySet()) {
                for (Map.Entry<Developer, Integer> entry : coopMapEntry.getValue().entrySet()) {
                    coopMapEntry.getValue().put(entry.getKey(), entry.getValue() / 2);
                }
            }

            for (Map.Entry<Package, Map<Developer, Integer>> coopMapEntry : developer.getCooperatedOnModules().entrySet()) {
                for (Map.Entry<Developer, Integer> entry : coopMapEntry.getValue().entrySet()) {
                    coopMapEntry.getValue().put(entry.getKey(), entry.getValue() / 2);
                }
            }

            //change frequency calculations
            Double age = Double.valueOf(developer.getUpdatedDate().getTime() - developer.getCreatedDate().getTime());
            age = age / developer.getRecordCount();
            developer.setChangeFrequencyPerDay(age);

            if (developer.getRelCreatedDate() != null) {
                Double relAge = Double.valueOf(developer.getRelUpdatedDate().getTime() - developer.getRelCreatedDate().getTime());
                relAge = relAge / developer.getRelRecordCount();
                developer.setRelChangeFrequencyPerDay(relAge);
            }

        }

        for (Package pack : packages) {

            //change frequency calculations
            Double age = Double.valueOf(pack.getUpdatedDate().getTime() - pack.getCreatedDate().getTime());
            age = age / pack.getRecordCount();
            pack.setChangeFrequencyPerDay(age);

            if (pack.getRelCreatedDate() != null) {
                Double relAge = Double.valueOf(pack.getRelUpdatedDate().getTime() - pack.getRelCreatedDate().getTime());
                relAge = relAge / pack.getRelRecordCount();
                pack.setRelChangeFrequencyPerDay(relAge);
            }
        }

    }

}
