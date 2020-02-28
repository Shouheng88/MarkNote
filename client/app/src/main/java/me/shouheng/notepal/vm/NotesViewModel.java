package me.shouheng.notepal.vm;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.PopupMenu;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.shouheng.commons.model.data.Resource;
import me.shouheng.commons.utils.ColorUtils;
import me.shouheng.commons.utils.PalmUtils;
import me.shouheng.data.entity.Category;
import me.shouheng.data.entity.Note;
import me.shouheng.data.entity.Notebook;
import me.shouheng.data.helper.ArchiveHelper;
import me.shouheng.data.helper.NotebookHelper;
import me.shouheng.data.helper.TrashHelper;
import me.shouheng.data.model.enums.Status;
import me.shouheng.data.store.NotebookStore;
import me.shouheng.data.store.NotesStore;
import me.shouheng.notepal.R;
import me.shouheng.notepal.activity.MainActivity;
import me.shouheng.notepal.adapter.NotesAdapter;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: NotesViewModel, v 0.1 2018/12/2 16:03 shouh Exp$
 */
public class NotesViewModel extends ViewModel {

    /**
     * The notes status
     */
    private Status status;

    /**
     * The notebook of notes
     */
    private Notebook notebook;

    /**
     * The category of notes
     */
    private Category category;

    /**
     * Is notes list the top stack of notes tree. This field is used for the MainActivity when
     * the back event is invoked. To decide should navigate to the parent notebook of just exit app.
     *
     * @see MainActivity#onBackPressed()
     */
    private boolean isTopStack = true;

    /**
     * Notebook update position
     */
    private int notebookUpdatePosition;

    private MutableLiveData<Resource<List<NotesAdapter.MultiItem>>> mutableLiveData;

    private MutableLiveData<Resource<Note>> noteUpdateLiveData;

    private MutableLiveData<Resource<Notebook>> notebookUpdateLiveData;

    public MutableLiveData<Resource<List<NotesAdapter.MultiItem>>> getMutableLiveData() {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
        }
        return mutableLiveData;
    }

    public MutableLiveData<Resource<Note>> getNoteUpdateLiveData() {
        if (noteUpdateLiveData == null) {
            noteUpdateLiveData = new MutableLiveData<>();
        }
        return noteUpdateLiveData;
    }

    public MutableLiveData<Resource<Notebook>> getNotebookUpdateLiveData() {
        if (notebookUpdateLiveData == null) {
            notebookUpdateLiveData = new MutableLiveData<>();
        }
        return notebookUpdateLiveData;
    }

    /**
     * Fetch the multi items.
     */
    public Disposable fetchMultiItems() {
        if (mutableLiveData != null) {
            mutableLiveData.setValue(Resource.loading(null));
        }
        return Observable.create((ObservableOnSubscribe<List<NotesAdapter.MultiItem>>) emitter -> {
            List<NotesAdapter.MultiItem> multiItems = new LinkedList<>();
            List list;
            if (category != null) {
                switch (status) {
                    case ARCHIVED: list = ArchiveHelper.getNotebooksAndNotes(category);break;
                    case TRASHED: list = TrashHelper.getNotebooksAndNotes(category);break;
                    default: list = NotebookHelper.getNotesAndNotebooks(category);
                }
            } else {
                switch (status) {
                    case ARCHIVED: list = ArchiveHelper.getNotebooksAndNotes(notebook);break;
                    case TRASHED: list = TrashHelper.getNotebooksAndNotes(notebook);break;
                    default: list = NotebookHelper.getNotesAndNotebooks(notebook);
                }
            }
            for (Object obj : list) {
                if (obj instanceof Note) {
                    multiItems.add(new NotesAdapter.MultiItem((Note) obj));
                } else if (obj instanceof Notebook) {
                    multiItems.add(new NotesAdapter.MultiItem((Notebook) obj));
                }
            }
            emitter.onNext(multiItems);
        }).observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(multiItems -> {
            if (mutableLiveData != null) {
                mutableLiveData.setValue(Resource.success(multiItems));
            }
        });
    }

    /**
     * Update note to given status.
     *
     * @param note the note to update
     * @param statusTo the status to update to
     */
    public Disposable updateNoteStatus(Note note, Status statusTo) {
        return Observable
                .create((ObservableOnSubscribe<Note>) emitter -> {
                    NotesStore.getInstance().update(note, statusTo);
                    emitter.onNext(note);
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(note1 -> {
                    if (noteUpdateLiveData != null) {
                        noteUpdateLiveData.setValue(Resource.success(note1));
                    }
                });
    }

    /**
     * Update given note.
     *
     * @param note the note to update
     * @return hte disposable
     */
    public Disposable updateNote(Note note) {
        return Observable
                .create((ObservableOnSubscribe<Note>) emitter -> {
                    NotesStore.getInstance().update(note);
                    emitter.onNext(note);
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(note1 -> {
                    if (noteUpdateLiveData != null) {
                        noteUpdateLiveData.setValue(Resource.success(note1));
                    }
                });
    }

    /**
     * Update notebook statsu to given status.
     *
     * @param notebook the notebook to update.
     * @param statusTo the status to update to.
     * @return the disposable
     */
    public Disposable updateNotebookStatus(Notebook notebook, Status statusTo) {
        return Observable
                .create((ObservableOnSubscribe<Notebook>) emitter -> {
                    NotebookStore.getInstance().update(notebook, status, statusTo);
                    emitter.onNext(notebook);
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(notebook1 -> {
                    if (notebookUpdateLiveData != null) {
                        notebookUpdateLiveData.setValue(Resource.success(notebook1));
                    }
                });
    }

    /**
     * Update notebook
     *
     * @param notebook the notebook to update
     * @return the disposable
     */
    public Disposable updateNotebook(Notebook notebook) {
        return Observable
                .create((ObservableOnSubscribe<Notebook>) emitter -> {
                    NotebookStore.getInstance().update(notebook);
                    emitter.onNext(notebook);
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(notebook1 -> {
                    if (notebookUpdateLiveData != null) {
                        notebookUpdateLiveData.setValue(Resource.success(notebook1));
                    }
                });
    }

    public Disposable moveNotebook(Notebook notebook, Notebook notebookTo) {
        return Observable
                .create((ObservableOnSubscribe<Notebook>) emitter -> {
                    NotebookStore.getInstance().move(notebook, notebookTo);
                    emitter.onNext(notebook);
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(notebook1 -> {
                    if (notebookUpdateLiveData != null) {
                        notebookUpdateLiveData.setValue(Resource.success(notebook1));
                    }
                });
    }

    public int getNotebookUpdatePosition() {
        return notebookUpdatePosition;
    }

    public void setNotebookUpdatePosition(int notebookUpdatePosition) {
        this.notebookUpdatePosition = notebookUpdatePosition;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Notebook getNotebook() {
        return notebook;
    }

    public void setNotebook(Notebook notebook) {
        this.notebook = notebook;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isTopStack() {
        return isTopStack;
    }

    public void setTopStack(boolean topStack) {
        isTopStack = topStack;
    }

    /**
     * Get the title of toolbar in notes list fragment
     *
     * @return toolbar title
     */
    public int getTitle() {
        return category == null ? R.string.notes_list_notebook_toolbar_title : R.string.notes_list_category_toolbar_title;
    }

    /**
     * Get the sub title for the action bar.
     *
     * @return the sub title
     */
    public String getSubTitle() {
        return notebook != null ? notebook.getTitle() :
                category != null ? category.getName() : null;
    }

    /**
     * Get the navigation bar icon.
     *
     * @return the icon drawable
     */
    public Drawable getHomeAsUpIndicator() {
        return ColorUtils.tintDrawable(isTopStack ?
                R.drawable.ic_menu_white : R.drawable.ic_arrow_back_white_24dp,
                ColorUtils.isDarkTheme() ? Color.WHITE : Color.BLACK);
    }

    /**
     * Get the sub title for the notes list empty view.
     *
     * @return the sub title
     */
    public String getEmptySubTitle() {
        return PalmUtils.getStringCompact(
                status == Status.NORMAL ? R.string.notes_list_empty_sub_normal :
                        status == Status.TRASHED ? R.string.notes_list_empty_sub_trashed :
                                status == Status.ARCHIVED ? R.string.notes_list_empty_sub_archived :
                                        R.string.notes_list_empty_sub_normal);
    }

    /**
     * Config the popup menu according to the fragment circumstance.
     *
     * @param popupMenu the popup menu to config
     */
    public void configPopMenu(PopupMenu popupMenu) {
        popupMenu.getMenu().findItem(R.id.action_move_out).setVisible(status == Status.ARCHIVED || status == Status.TRASHED);
        popupMenu.getMenu().findItem(R.id.action_edit).setVisible(status == Status.ARCHIVED || status == Status.NORMAL);
        popupMenu.getMenu().findItem(R.id.action_move).setVisible(status == Status.NORMAL);
        popupMenu.getMenu().findItem(R.id.action_trash).setVisible(status == Status.NORMAL || status == Status.ARCHIVED);
        popupMenu.getMenu().findItem(R.id.action_archive).setVisible(status == Status.NORMAL);
        popupMenu.getMenu().findItem(R.id.action_delete).setVisible(status == Status.TRASHED);
    }
}
