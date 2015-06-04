package org.firestarterr.bitirmeTezi.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class File extends BitBaseEntity {

    private String fileExt;
    private String fullPath;

    private Integer locEdited;

    private Package module;
    private Project project;

    private List<Commit> commits = new ArrayList<>(0);

    //bu map file bazında developer coop count için kullanılabilinir.
    private Map<Developer, Integer> developerChangeCountMap = new HashMap<>(0);

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public Integer getLocEdited() {
        return locEdited;
    }

    public void setLocEdited(Integer locEdited) {
        this.locEdited = locEdited;
    }

    public Package getModule() {
        return module;
    }

    public void setModule(Package module) {
        this.module = module;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    public Map<Developer, Integer> getDeveloperChangeCountMap() {
        return developerChangeCountMap;
    }

    public void setDeveloperChangeCountMap(Map<Developer, Integer> developerChangeCountMap) {
        this.developerChangeCountMap = developerChangeCountMap;
    }
}
