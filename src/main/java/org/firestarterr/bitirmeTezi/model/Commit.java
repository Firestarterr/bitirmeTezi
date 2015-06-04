package org.firestarterr.bitirmeTezi.model;

import java.util.*;

public class Commit extends BitBaseEntity {

    private Developer developer;
    private Date commitDate;
    private Issue relatedIssue;
    private Integer locEdited;

    private List<File> files = new ArrayList<>(0);
    private List<Package> packages = new ArrayList<>(0);
    private Project project;

    private Map<File, Integer> locEditedPerFile = new HashMap<>(0);


    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public Issue getRelatedIssue() {
        return relatedIssue;
    }

    public void setRelatedIssue(Issue relatedIssue) {
        this.relatedIssue = relatedIssue;
    }

    public Integer getLocEdited() {
        return locEdited;
    }

    public void setLocEdited(Integer locEdited) {
        this.locEdited = locEdited;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Package> getPackages() {
        return packages;
    }

    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Map<File, Integer> getLocEditedPerFile() {
        return locEditedPerFile;
    }

    public void setLocEditedPerFile(Map<File, Integer> locEditedPerFile) {
        this.locEditedPerFile = locEditedPerFile;
    }
}
