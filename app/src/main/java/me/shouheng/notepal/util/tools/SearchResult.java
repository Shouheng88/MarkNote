package me.shouheng.notepal.util.tools;

import java.util.LinkedList;
import java.util.List;

import me.shouheng.notepal.model.MindSnagging;
import me.shouheng.notepal.model.Note;

/**
 * Created by shouh on 2018/3/18.*/
public class SearchResult {

    private List<Note> notes = new LinkedList<>();

    private List<MindSnagging> minds = new LinkedList<>();

    public SearchResult(List<Note> notes, List<MindSnagging> minds) {
        this.notes = notes;
        this.minds = minds;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<MindSnagging> getMinds() {
        return minds;
    }

    public void setMinds(List<MindSnagging> minds) {
        this.minds = minds;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "notes=" + notes +
                ", minds=" + minds +
                '}';
    }
}
