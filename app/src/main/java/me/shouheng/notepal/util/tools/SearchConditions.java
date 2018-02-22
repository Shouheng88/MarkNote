package me.shouheng.notepal.util.tools;

/**
 * Created by WngShhng on 2017/12/11.*/
public class SearchConditions {

    private boolean includeAssignment;

    private boolean includeClass;

    private boolean includeNote;

    private boolean includeMindSnagging;

    private boolean includePurpose;

    /**
     * For current version (1.0), don't include the filter option of tag.
     * So we let this filed be false and the menu item invisible. */
    private boolean includeTags = false;

    private boolean includeArchived;

    private boolean includeTrashed;

    public static SearchConditions getDefaultConditions() {
        SearchConditions conditions = new SearchConditions();
        conditions.setIncludeArchived(true);
        conditions.setIncludeTrashed(false);
        conditions.setIncludeTags(true);
        conditions.setIncludeAssignment(true);
        conditions.setIncludeClass(true);
        conditions.setIncludeNote(true);
        conditions.setIncludeMindSnagging(true);
        conditions.setIncludePurpose(true);
        return conditions;
    }

    public boolean isIncludeAssignment() {
        return includeAssignment;
    }

    public void setIncludeAssignment(boolean includeAssignment) {
        this.includeAssignment = includeAssignment;
    }

    public boolean isIncludeClass() {
        return includeClass;
    }

    public void setIncludeClass(boolean includeClass) {
        this.includeClass = includeClass;
    }

    public boolean isIncludeNote() {
        return includeNote;
    }

    public void setIncludeNote(boolean includeNote) {
        this.includeNote = includeNote;
    }

    public boolean isIncludeMindSnagging() {
        return includeMindSnagging;
    }

    public void setIncludeMindSnagging(boolean includeMindSnagging) {
        this.includeMindSnagging = includeMindSnagging;
    }

    public boolean isIncludePurpose() {
        return includePurpose;
    }

    public void setIncludePurpose(boolean includePurpose) {
        this.includePurpose = includePurpose;
    }

    public boolean isIncludeTags() {
        return includeTags;
    }

    public void setIncludeTags(boolean includeTags) {
        this.includeTags = includeTags;
    }

    public boolean isIncludeArchived() {
        return includeArchived;
    }

    public void setIncludeArchived(boolean includeArchived) {
        this.includeArchived = includeArchived;
    }

    public boolean isIncludeTrashed() {
        return includeTrashed;
    }

    public void setIncludeTrashed(boolean includeTrashed) {
        this.includeTrashed = includeTrashed;
    }
}
