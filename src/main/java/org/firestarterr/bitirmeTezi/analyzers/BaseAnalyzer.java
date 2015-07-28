package org.firestarterr.bitirmeTezi.analyzers;

import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.CommitWrapper;
import org.firestarterr.bitirmeTezi.analyzers.wrappers.FileWrapper;
import org.firestarterr.bitirmeTezi.model.*;
import org.firestarterr.bitirmeTezi.model.Package;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public abstract class BaseAnalyzer {

    Integer minCooperationCount = Integer.MAX_VALUE;
    Integer minOrcaCooperationCount = Integer.MAX_VALUE;
    Integer maxCooperationCount = Integer.MIN_VALUE;
    Integer maxOrcaCooperationCount = Integer.MIN_VALUE;

    Integer minLocEdited = Integer.MAX_VALUE;
    Integer minOrcaLocEdited = Integer.MAX_VALUE;
    Integer maxLocEdited = Integer.MIN_VALUE;
    Integer maxOrcaLocEdited = Integer.MIN_VALUE;

    List<Project> projects = new ArrayList<>();
    List<Package> packages = new ArrayList<>();
    List<Issue> issues = new ArrayList<>();
    List<File> files = new ArrayList<>();
    List<Developer> developers = new ArrayList<>();
    List<Commit> commitSuccess = new ArrayList<>();
    List<Commit> commitsWithIssues = new ArrayList<>();
    List<Commit> commitsWithoutIssues = new ArrayList<>();

    Map<String, List<File>> fileExtMap = new HashMap<>();

    public BaseAnalyzer(String projectName) {
        Project project = new Project();
        project.setName(projectName);
        projects.add(project);

        Package aPackage = new Package();
        aPackage.setName("root");
        aPackage.setProject(projects.get(0));
        packages.add(aPackage);
    }

    public abstract void run() throws IOException, MappingException, MarshalException, ValidationException, ParseException;

    //<editor-fold desc="Package Methods">
    protected abstract String getPackageName(String path);

    private Package findOrCreatePackage(String path, Date date, boolean isOrcaDeveloper) {
        int index = path.indexOf("/");
        if (index == -1) {
            Package root = packages.get(0);
            updateStats(root, date, isOrcaDeveloper);
            return root;
        } else {
            String packageName = getPackageName(path);
            for (Package aPackage : packages) {
                if (aPackage.getName().equals(packageName)) {
                    updateStats(aPackage, date, isOrcaDeveloper);
                    return aPackage;
                }
            }
            Package module = createPackage(packageName, date, isOrcaDeveloper);
            packages.add(module);
            return module;
        }
    }

    private Package createPackage(String name, Date date, boolean isOrcaDeveloper) {
        Package aPackage = new Package();
        aPackage.setName(name);
        aPackage.setProject(projects.get(0));
        updateStats(aPackage, date, isOrcaDeveloper);
        return aPackage;
    }
    //</editor-fold>

    //<editor-fold desc="Issue Methods">
    private Issue findOrCreateIssue(String name, Date date, boolean isOrcaDeveloper) {
        for (Issue issue : issues) {
            if (issue.getName().equals(name)) {
                updateStats(issue, date, isOrcaDeveloper);
                return issue;
            }
        }
        Issue issue = createIssue(name, date, isOrcaDeveloper);
        issues.add(issue);
        return issue;
    }

    private Issue createIssue(String name, Date date, boolean isOrcaDeveloper) {
        Issue issue = new Issue();
        issue.setName(name);
        updateStats(issue, date, isOrcaDeveloper);
        return issue;
    }
    //</editor-fold>

    //<editor-fold desc="File Methods">
    protected abstract FileWrapper parseFileString(String fileString);

    private File findOrCreateFile(String fileString, Date date, boolean isOrcaDeveloper) {
        FileWrapper parsed = parseFileString(fileString);
        if (parsed == null) {
            return null;
        }
        if (!isFileExtCovered(parsed.ext)) {
            return null;
        }
        for (File file : files) {
            if (file.getName().equals(parsed.name) && file.getFullPath().equals(parsed.path)) {
                updateStats(file, date, isOrcaDeveloper);
                return file;
            }
        }
        File file = createFile(parsed, date, isOrcaDeveloper);
        files.add(file);
        return file;
    }

    private boolean isFileExtCovered(String ext) {
        boolean check = false;
        if (ext.equals("java")) {
            check = true;
        }
        if (ext.equals("xhtml")) {
            check = true;
        }
        if (ext.equals("css")) {
            check = true;
        }
        if (ext.equals("js")) {
            check = true;
        }
        if (ext.equals("wsdl")) {
            check = true;
        }
        return check;
    }

    private File createFile(FileWrapper parsed, Date date, boolean isOrcaDeveloper) {
        File file = new File();

        file.setFullPath(parsed.path);
        file.setName(parsed.name);

        file.setModule(findOrCreatePackage(parsed.path, date, isOrcaDeveloper));
        file.setProject(projects.get(0));
        file.setFileExt(parsed.ext);
        updateStats(file, date, isOrcaDeveloper);
        return file;
    }
    //</editor-fold>

    //<editor-fold desc="Dev Methods">
    private Developer findOrCreateDeveloper(String name, Date date) {
        name = replaceDevName(name);
        for (Developer developer : developers) {
            if (developer.getName().equals(name)) {
                updateStats(developer, date, developer.getIsOrcaDeveloper());
                return developer;
            }
        }
        Developer dev = createDeveloper(name, date);
        developers.add(dev);
        return dev;
    }

    private Developer createDeveloper(String devName, Date date) {
        Developer developer = new Developer();
        developer.setName(devName);
        boolean isOrca = isOrcaDeveloper(devName);
        developer.setIsOrcaDeveloper(isOrca);
        updateStats(developer, date, developer.getIsOrcaDeveloper());
        return developer;
    }

    private boolean isOrcaDeveloper(String devName) {
        return devName.equals("Murat Can Sayılgan")
                || devName.equals("Recep Ayan")
                || devName.equals("Kamil Örs")
                || devName.equals("Elif Gül");
    }

    private String replaceDevName(String name) {
        String newName = name;
        if (name.equals("murats")) {
            newName = "Murat Can Sayılgan";
        }

        if (name.equals("recepa")) {
            newName = "Recep Ayan";
        }
        if (name.equals("rcpayan")) {
            newName = "Recep Ayan";
        }

        if (name.equals("elifg")) {
            newName = "Elif Gül";
        }
        if (name.equals("elif")) {
            newName = "Elif Gül";
        }

        if (name.equals("kamilo")) {
            newName = "Kamil Örs";
        }
        if (name.equals("kamilors")) {
            newName = "Kamil Örs";
        }
        if (name.equals("Kamil Ors")) {
            newName = "Kamil Örs";
        }

        return newName;
    }

    private boolean isDeveloperValid(Developer developer) {
        return !(developer.getName().equals("sonerk") || developer.getName().equals("kerimc") || developer.getName().equals("zeynepb") || developer.getName().equals("dilekk"));
    }
    //</editor-fold>

    //<editor-fold desc="Util Methods">

    private boolean isOnlyOrcaDeveloper() {
        for (Developer developer : developers) {
            if (!isDeveloperValid(developer) || !developer.getIsOrcaDeveloper()) {
                return false;
            }
        }
        return true;
    }

    private void updateStats(BaseEntity entity, Date date, boolean isOrcaDeveloper) {
        if (entity.getUpdatedDate() == null || entity.getUpdatedDate().before(date)) {
            entity.setUpdatedDate(date);
        }
        entity.increaseRecordCount();
        if (entity.getCreatedDate() == null || entity.getCreatedDate().after(date)) {
            entity.setCreatedDate(date);
        }
        long differenceAsDay = entity.getAge();
        entity.setChangeFrequencyPerDay(((double) entity.getRecordCount()) / differenceAsDay);
        if (isOrcaDeveloper) {
            if (entity.getRelUpdatedDate() == null || entity.getRelUpdatedDate().before(date)) {
                entity.setRelUpdatedDate(date);
            }
            entity.increaseRelRecordCount();
            if (entity.getRelCreatedDate() == null || entity.getRelCreatedDate().after(date)) {
                entity.setRelCreatedDate(date);
            }
            long relDifferenceAsDay = entity.getRelAge();
            entity.setRelChangeFrequencyPerDay(((double) entity.getRelRecordCount()) / relDifferenceAsDay);
        }
    }

    private void sortDevelopers() {
        Collections.sort(developers, new Comparator<Developer>() {
            @Override
            public int compare(Developer o1, Developer o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="Commit Methods">
    protected Commit createCommit(CommitWrapper parsed) throws ParseException {
        if (!isCommitValid(parsed)) {
            return null;
        }
        Commit commit = new Commit();
        Developer dev = findOrCreateDeveloper(parsed.dev, parsed.date);
        dev.getCommits().add(commit);

        commit.setCommitDate(parsed.date);
        commit.setDeveloper(dev);
        commit.setName(parsed.commitName);
        commit.setProject(projects.get(0));

        if (parsed.issueName != null) {
            commit.setRelatedIssue(findOrCreateIssue(parsed.issueName, parsed.date, dev.getIsOrcaDeveloper()));
        }

        if (parsed.fileStrings != null) {
            for (Map.Entry<String, Integer> entry : parsed.fileStrings.entrySet()) {
                updateCommitFiles(commit, entry.getKey(), entry.getValue());
            }
        }

        commitSuccess.add(commit);
        return commit;
    }

    private boolean isCommitValid(CommitWrapper parsed) {
        return !"Style copy problem on iteration for font family and default font size is fixed.".equals(parsed.commitName);
    }

    private void updateCommitFiles(Commit commit, String fileString, Integer locEdited) {
        File file = findOrCreateFile(fileString, commit.getCommitDate(), commit.getDeveloper().getIsOrcaDeveloper());
        if (file == null) {
            return;
        }
        commit.getFiles().add(file);
        file.getCommits().add(commit);
        updateCommitStats(commit, locEdited);
        updateFileStats(file, locEdited);
        commit.getLocEditedPerFile().put(file, locEdited);
    }

    private void updateCommitStats(Commit commit, Integer locEdited) {
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
    }

    private void updateFileStats(File file, Integer locEdited) {
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
    }

    private boolean isCommitContainsCooperation(Commit commit) {
        for (File file : commit.getFiles()) {
            if (commit.getDeveloper().getCooperatedOnFiles().containsKey(file)) {
                return true;
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Analyze Methods">
    public void analyzeData() {
        for (Commit commit : commitSuccess) {
            Developer dev = commit.getDeveloper();

            //private Map<Developer, Integer> developerChangeCountMap
            for (File file : commit.getFiles()) {
                if (file.getDeveloperChangeCountMap().keySet().contains(dev)) {
                    file.getDeveloperChangeCountMap().put(dev, file.getDeveloperChangeCountMap().get(dev) + 1);
                } else {
                    file.getDeveloperChangeCountMap().put(dev, 1);
                }
            }
            updateStats(commit.getProject(), commit.getCommitDate(), commit.getDeveloper().getIsOrcaDeveloper());
            if (commit.getRelatedIssue() != null) {
                commitsWithIssues.add(commit);
            } else {
                commitsWithoutIssues.add(commit);
            }
        }

        for (File file : files) {
            for (Map.Entry<Developer, Integer> entry : file.getDeveloperChangeCountMap().entrySet()) {
                for (Map.Entry<Developer, Integer> iter : file.getDeveloperChangeCountMap().entrySet()) {
                    if (entry.getKey().getName().equals(iter.getKey().getName())) {
                        continue;
                    }
                    Developer dev1 = entry.getKey();
                    Developer dev2 = iter.getKey();
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
                    if (dev1.getCooperatedOnFiles().get(file) == null || dev1.getCooperatedOnFiles().get(file).isEmpty()) {
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
                    if (dev2.getCooperatedOnFiles().get(file) == null || dev2.getCooperatedOnFiles().get(file).isEmpty()) {
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
                    if (dev1.getCooperatedOnModules().get(file.getModule()) == null || dev1.getCooperatedOnModules().get(file.getModule()).isEmpty()) {
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
                    if (dev2.getCooperatedOnModules().get(file.getModule()) == null || dev2.getCooperatedOnModules().get(file.getModule()).isEmpty()) {
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
        }

        for (Developer dev : developers) {
            for (Map.Entry<Developer, Integer> entry : dev.getCooperationCount().entrySet()) {
                if (minCooperationCount > entry.getValue()) {
                    minCooperationCount = entry.getValue();
                }
                if (isBothOrca(dev, entry.getKey())) {
                    if (minOrcaCooperationCount > entry.getValue()) {
                        minOrcaCooperationCount = entry.getValue();
                    }
                }
                if (maxCooperationCount < entry.getValue()) {
                    maxCooperationCount = entry.getValue();
                }
                if (isBothOrca(dev, entry.getKey())) {
                    if (maxOrcaCooperationCount < entry.getValue()) {
                        maxOrcaCooperationCount = entry.getValue();
                    }
                }
            }

            if (minLocEdited > dev.getLocEdited()) {
                minLocEdited = dev.getLocEdited();
            }
            if (dev.getIsOrcaDeveloper()) {
                if (minOrcaLocEdited > dev.getLocEdited()) {
                    minOrcaLocEdited = dev.getLocEdited();
                }
            }
            if (maxLocEdited < dev.getLocEdited()) {
                maxLocEdited = dev.getLocEdited();
            }
            if (dev.getIsOrcaDeveloper()) {
                if (maxOrcaLocEdited < dev.getLocEdited()) {
                    maxOrcaLocEdited = dev.getLocEdited();
                }
            }
        }

        for (File file : files) {
            if (fileExtMap.containsKey(file.getFileExt())) {
                fileExtMap.get(file.getFileExt()).add(file);
            } else {
                List<File> newList = new ArrayList<>();
                newList.add(file);
                fileExtMap.put(file.getFileExt(), newList);
            }
        }

        for (Commit commit : commitSuccess) {
            //private Set<Commit> commitsWithCooperation
            if (isCommitContainsCooperation(commit)) {
                commit.getDeveloper().getCommitsWithCooperation().add(commit);
            }
        }
        sortDevelopers();
    }
    //</editor-fold>

    //<editor-fold desc="Report Methods">
    public void produceReports() throws IOException {
        exportFileData();
        exportDeveloperData(false);
        exportDeveloperMatrixData(false);
        exportDeveloperNodeXLVertices(false);
        exportDeveloperNodeXLEdges(false);
        if (!isOnlyOrcaDeveloper()) {
            exportDeveloperData(true);
            exportDeveloperMatrixData(true);
            exportDeveloperNodeXLVertices(true);
            exportDeveloperNodeXLEdges(true);
        }
    }

    private String getFileDataHeadlines() {
        return "isim," +
                "ilk işlem tarihi," +
                "son işlem tarihi," +
                "orca geliştiricilerinin ilk işlem tarihi," +
                "orca geliştiricilerinin son işlem tarihi," +
                "yaş," +
                "orca geliştiricilerine göre yaş," +
                "kod veritabanı kaydı değişikliği sıklığı," +
                "orca geliştiricilerinin kod veritabanı kaydı değişikliği sıklığı," +
                "dosya dizini," +
                "dosya uzantısı," +
                "modül," +
                "en çok kod üzerinde çalışmış olan geliştirici," +
                "değişen satır sayısı toplamı," +
                "yapılan kod veritabanı kaydı sayısı," +
                "kod veritabanı kaydı başına değişen ortalama satır sayısı";
    }

    private String getDeveloperDataHeadlines() {
        return "isim," +
                "ilk işlem tarihi," +
                "son işlem tarihi," +
                "orca geliştiricilerinin ilk işlem tarihi," +
                "orca geliştiricilerinin son işlem tarihi," +
                "yaş," +
                "orca geliştiricilerine göre yaş," +
                "kod veritabanı kaydı değişikliği sıklığı," +
                "orca geliştiricilerinin kod veritabanı kaydı değişikliği sıklığı," +
                "iş birliği yapılmış kod veritabanı değişikliği," +
                "toplam kod veritabanı değişikliği," +
                "iş birliği yapılmış kod veritabanı değişikliğinin toplam kod veritabanı değişikliğine oranı," +
                "toplam yapılan iş birliği sayısı," +
                "iş birliği yapılmış dosya sayısı," +
                "toplam üzerinde çalışılmış dosya sayısı," +
                "iş birliği yapılmış dosya sayısının üzerinde çalışılmış toplam dosya sayısına oranı," +
                "iş birliği yapılmış özgün dosya sayısı," +
                "özgün dosya başına yapılmış iş birliği ortalaması," +
                "iş birliği yapılmış toplam modül sayısı," +
                "modül başına yapılmış iş birliği ortalaması," +
                "iş birliği yapılmış geliştirici sayısı," +
                "geliştirici başına yapılmış iş birliği ortalaması," +
                "satır sayısı değişikliği," +
                "kod veritabanı kaydı başına değiştirilmiş satır sayısı ortalaması";
    }


    public void exportDeveloperData(boolean isOrcaOnly) throws IOException {
        String fileString = isOrcaOnly ? "-orca" : "";
        fileString = fileString + "-developers.csv";
        FileWriter fw = new FileWriter(projects.get(0).getName() + fileString);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getDeveloperDataHeadlines());
        bw.newLine();
        for (Developer dev : developers) {
            if (!isDeveloperValid(dev) || (isOrcaOnly && !dev.getIsOrcaDeveloper())) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(buildEntityStaticFieldsString(dev));
            sb.append(dev.getCommitsWithCooperation().size());
            sb.append(",");
            sb.append(dev.getCommits().size());
            sb.append(",");
            Double oran = ((double) dev.getCommitsWithCooperation().size()) / ((double) dev.getCommits().size());
            sb.append(oran);
            sb.append(",");

            Double totalCoop = 0D;
            for (Map.Entry<Developer, Integer> entry : dev.getCooperationCount().entrySet()) {
                totalCoop = totalCoop + entry.getValue();
            }
            sb.append(totalCoop);
            sb.append(",");

            Double totalFileCooperated = 0D;
            for (Commit commit : dev.getCommits()) {
                for (File file : commit.getFiles()) {
                    if (dev.getCooperatedOnFiles().containsKey(file)) {
                        totalFileCooperated++;
                    }
                }
            }

            sb.append(totalFileCooperated);
            sb.append(",");

            Double totalFileCommited = 0D;
            for (Commit commit : dev.getCommits()) {
                totalFileCommited = totalFileCommited + commit.getFiles().size();
            }

            sb.append(totalFileCommited);
            sb.append(",");

            sb.append(totalFileCooperated / totalFileCommited);
            sb.append(",");

            sb.append(dev.getCooperatedOnFiles().keySet().size());
            sb.append(",");

            sb.append(totalCoop / dev.getCooperatedOnFiles().keySet().size());
            sb.append(",");

            sb.append(dev.getCooperatedOnModules().keySet().size());
            sb.append(",");

            sb.append(totalCoop / dev.getCooperatedOnModules().keySet().size());
            sb.append(",");

            sb.append(dev.getCooperationCount().keySet().size());
            sb.append(",");


            sb.append(totalCoop / dev.getCooperationCount().keySet().size());
            sb.append(",");

            Double totalEditedLoc = 0D;
            for (Commit commit : dev.getCommits()) {
                if (commit.getFiles() != null) {
                    totalEditedLoc = totalEditedLoc + commit.getFiles().size();
                }
            }

            sb.append(totalEditedLoc);
            sb.append(",");
            sb.append(totalEditedLoc / dev.getCommits().size());
            sb.append(",");

            bw.write(sb.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public void exportFileData() throws IOException {
        FileWriter fw = new FileWriter(projects.get(0).getName() + "-files.csv");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(getFileDataHeadlines());
        bw.newLine();
        for (File file : files) {
            StringBuilder sb = new StringBuilder();
            sb.append(buildEntityStaticFieldsString(file));
            sb.append(file.getFullPath());
            sb.append(",");
            sb.append(file.getFileExt());
            sb.append(",");
            sb.append(file.getModule());
            sb.append(",");

            Map<Developer, Integer> developerCommitCountMap = new HashMap<>();
            for (Commit commit : file.getCommits()) {
                if (developerCommitCountMap.containsKey(commit.getDeveloper())) {
                    Integer commitCount = developerCommitCountMap.get(commit.getDeveloper());
                    developerCommitCountMap.put(commit.getDeveloper(), commitCount + 1);
                } else {
                    developerCommitCountMap.put(commit.getDeveloper(), 1);
                }
            }
            Map.Entry<Developer, Integer> topDeveloper = null;
            for (Map.Entry<Developer, Integer> entry : developerCommitCountMap.entrySet()) {
                if (topDeveloper == null || topDeveloper.getValue() < entry.getValue()) {
                    topDeveloper = entry;
                }
            }
            if (topDeveloper != null) {
                sb.append(topDeveloper.getKey().getName());
                sb.append(",");
            }
            sb.append(file.getLocEdited());
            sb.append(",");
            sb.append(file.getCommits().size());
            sb.append(",");
            sb.append(((double) file.getLocEdited()) / ((double) file.getCommits().size()));
            sb.append(",");

            bw.write(sb.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public void exportDeveloperMatrixData(boolean isOrcaOnly) throws IOException {
        String fileString = isOrcaOnly ? "-orca" : "";
        fileString = fileString + "-developer-matrix.csv";
        FileWriter fw = new FileWriter(projects.get(0).getName() + fileString);
        BufferedWriter bw = new BufferedWriter(fw);
        StringBuilder sb = new StringBuilder();
        sb.append(",");
        for (Developer developer : developers) {
            if (!isDeveloperValid(developer) || (isOrcaOnly && !developer.getIsOrcaDeveloper())) {
                continue;
            }
            sb.append(developer.getName());
            sb.append(",");
        }
        bw.write(sb.toString());
        bw.newLine();
        for (Developer developer : developers) {
            if (!isDeveloperValid(developer) || (isOrcaOnly && !developer.getIsOrcaDeveloper())) {
                continue;
            }
            sb = new StringBuilder();
            sb.append(developer.getName());
            sb.append(",");
            for (Developer iter : developers) {
                if (!isDeveloperValid(iter) || (isOrcaOnly && !iter.getIsOrcaDeveloper())) {
                    continue;
                }
                Integer cooperationCount = developer.getCooperationCount().get(iter);
                if (cooperationCount == null) {
                    sb.append("");
                } else {
                    sb.append(cooperationCount);
                }
                sb.append(",");
            }
            bw.write(sb.toString());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public void exportDeveloperNodeXLVertices(boolean isOrcaOnly) throws IOException {
        String fileString = isOrcaOnly ? "-orca" : "";
        fileString = fileString + "-developer-vertices-xl.csv";
        FileWriter fw = new FileWriter(projects.get(0).getName() + fileString);
        BufferedWriter bw = new BufferedWriter(fw);
        for (Developer developer : developers) {
            if (!isDeveloperValid(developer) || (isOrcaOnly && !developer.getIsOrcaDeveloper())) {
                continue;
            }
            //Vertex	Color	Shape	Size	Opacity	Image File	Visibility	Label
            bw.write(developer.getName() + "," + (developer.getIsOrcaDeveloper() ? "blue" : "red") + "," + "," + (getVertexSize(developer.getLocEdited(), isOrcaOnly)) + "," + "," + "," + "," + developer.getName());
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    public void exportDeveloperNodeXLEdges(boolean isOrcaOnly) throws IOException {
        String fileString = isOrcaOnly ? "-orca" : "";
        fileString = fileString + "-developer-edges-xl.csv";
        FileWriter fw = new FileWriter(projects.get(0).getName() + fileString);
        BufferedWriter bw = new BufferedWriter(fw);
        for (Developer developer : developers) {
            if (!isDeveloperValid(developer) || (isOrcaOnly && !developer.getIsOrcaDeveloper())) {
                continue;
            }
            //Vertex 1	Vertex 2	Color	Width	Style	Opacity	Visibility	Label	Label Text Color	Label Font Size
            for (Map.Entry<Developer, Integer> entry : developer.getCooperationCount().entrySet()) {
                if (!isDeveloperValid(entry.getKey()) || (isOrcaOnly && !entry.getKey().getIsOrcaDeveloper())) {
                    continue;
                }
                boolean isBothOrca = isBothOrca(entry.getKey(), developer);
                bw.write(developer.getName() + "," + entry.getKey().getName() + "," + (isBothOrca ? "blue" : "red") + "," + getVertexWidth(entry.getValue(), isOrcaOnly));
                bw.newLine();
            }
        }
        bw.close();
        fw.close();
    }

    private String getVertexSize(Integer locEdited, boolean isOrcaOnly) {
        if (isOrcaOnly) {
            if (minOrcaLocEdited.equals(maxOrcaLocEdited)) {
                return "500";
            }
            Double calculation = Double.valueOf(locEdited);
            return String.valueOf((((calculation - minOrcaLocEdited) * 999) / (maxOrcaLocEdited - minOrcaLocEdited)) + 1);
        } else {
            if (minLocEdited.equals(maxLocEdited)) {
                return "500";
            }
            Double calculation = Double.valueOf(locEdited);
            return String.valueOf((((calculation - minLocEdited) * 999) / (maxLocEdited - minLocEdited)) + 1);
        }
    }

    private String getVertexWidth(Integer cooperationCount, boolean isOrcaOnly) {
        if (isOrcaOnly) {
            if (minOrcaCooperationCount.equals(maxOrcaCooperationCount)) {
                return "5";
            }
            Double calculation = Double.valueOf(cooperationCount);
            return String.valueOf((((calculation - minOrcaCooperationCount) * 9) / (maxOrcaCooperationCount - minOrcaCooperationCount)) + 1);
        } else {
            if (minCooperationCount.equals(maxCooperationCount)) {
                return "5";
            }
            Double calculation = Double.valueOf(cooperationCount);
            return String.valueOf((((calculation - minCooperationCount) * 9) / (maxCooperationCount - minCooperationCount)) + 1);
        }
    }

    private boolean isBothOrca(Developer dev1, Developer dev2) {
        return dev1.getIsOrcaDeveloper() && dev2.getIsOrcaDeveloper();
    }

    private String buildEntityStaticFieldsString(BaseEntity entity) {
        return entity.getName() + "," + entity.getCreatedDate() + "," + entity.getUpdatedDate() + "," + entity.getRelCreatedDate() + "," + entity.getRelUpdatedDate() + "," + entity.getAge() + "," + entity.getRelAge() + "," + entity.getChangeFrequencyPerDay() + "," + entity.getRelChangeFrequencyPerDay() + ",";
    }
    //</editor-fold>
}
