package me.shouheng.data.model;

import java.util.List;

import me.shouheng.data.entity.Location;

/**
 * Created by wang shouheng on 2018/1/19.*/
public class Stats {

    private int totalNotes;

    private int archivedNotes;

    private int trashedNotes;

    private int totalCategories;

    /**
     * Distinct locations
     */
    private List<Location> locations;

    /**
     * Distinct locations
     */
    private int locCnt;

    /**
     * Total locations
     */
    private int totalLocations;

    private int totalNotebooks;

    private int totalAttachments;

    private int images;

    private int videos;

    private int audioRecordings;

    private int sketches;

    private int files;

    private int totalAlarms;

    /**
     * Added notes last seven days
     */
    private List<Integer> notesStats;

    public int getTotalNotes() {
        return totalNotes;
    }

    public void setTotalNotes(int totalNotes) {
        this.totalNotes = totalNotes;
    }

    public int getArchivedNotes() {
        return archivedNotes;
    }

    public void setArchivedNotes(int archivedNotes) {
        this.archivedNotes = archivedNotes;
    }

    public int getTrashedNotes() {
        return trashedNotes;
    }

    public void setTrashedNotes(int trashedNotes) {
        this.trashedNotes = trashedNotes;
    }

    public int getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(int totalCategories) {
        this.totalCategories = totalCategories;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public int getLocCnt() {
        return locCnt;
    }

    public void setLocCnt(int locCnt) {
        this.locCnt = locCnt;
    }

    public int getTotalLocations() {
        return totalLocations;
    }

    public void setTotalLocations(int totalLocations) {
        this.totalLocations = totalLocations;
    }

    public int getTotalNotebooks() {
        return totalNotebooks;
    }

    public void setTotalNotebooks(int totalNotebooks) {
        this.totalNotebooks = totalNotebooks;
    }

    public int getTotalAttachments() {
        return totalAttachments;
    }

    public void setTotalAttachments(int totalAttachments) {
        this.totalAttachments = totalAttachments;
    }

    public int getImages() {
        return images;
    }

    public void setImages(int images) {
        this.images = images;
    }

    public int getVideos() {
        return videos;
    }

    public void setVideos(int videos) {
        this.videos = videos;
    }

    public int getAudioRecordings() {
        return audioRecordings;
    }

    public void setAudioRecordings(int audioRecordings) {
        this.audioRecordings = audioRecordings;
    }

    public int getSketches() {
        return sketches;
    }

    public void setSketches(int sketches) {
        this.sketches = sketches;
    }

    public int getFiles() {
        return files;
    }

    public void setFiles(int files) {
        this.files = files;
    }

    public int getTotalAlarms() {
        return totalAlarms;
    }

    public void setTotalAlarms(int totalAlarms) {
        this.totalAlarms = totalAlarms;
    }

    public List<Integer> getNotesStats() {
        return notesStats;
    }

    public void setNotesStats(List<Integer> notesStats) {
        this.notesStats = notesStats;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "totalNotes=" + totalNotes +
                ", archivedNotes=" + archivedNotes +
                ", trashedNotes=" + trashedNotes +
                ", totalCategories=" + totalCategories +
                ", locations=" + locations +
                ", locCnt=" + locCnt +
                ", totalLocations=" + totalLocations +
                ", totalNotebooks=" + totalNotebooks +
                ", totalAttachments=" + totalAttachments +
                ", images=" + images +
                ", videos=" + videos +
                ", audioRecordings=" + audioRecordings +
                ", sketches=" + sketches +
                ", files=" + files +
                ", totalAlarms=" + totalAlarms +
                ", notesStats=" + notesStats +
                '}';
    }
}
