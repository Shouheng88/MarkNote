package me.shouheng.notepal.util.tools;

/**
 * Created by WngShhng on 2017/12/11.*/
public class SearchConditions {

    private boolean includeNote = true;

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
        conditions.setIncludeTags(false);
        conditions.setIncludeNote(true);
        return conditions;
    }

    public boolean isIncludeNote() {
        return includeNote;
    }

    public void setIncludeNote(boolean includeNote) {
        this.includeNote = includeNote;
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

    @Override
    public String toString() {
        return "SearchConditions{" +
                "includeNote=" + includeNote +
                ", includeTags=" + includeTags +
                ", includeArchived=" + includeArchived +
                ", includeTrashed=" + includeTrashed +
                '}';
    }
}
