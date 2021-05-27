package com.github.steroidteam.todolist.view.adapter;

import static com.github.steroidteam.todolist.util.Utils.getRoundedBitmap;
import static com.github.steroidteam.todolist.util.Utils.scaleBitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.notes.Note;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NoteArrayListAdapter extends RecyclerView.Adapter<NoteArrayListAdapter.NoteHolder> {

    private List<Note> noteList;
    private final NoteHolder.NoteCustomListener listener;

    private Map<UUID, File> noteHeaderPathMap;

    // CONFIGURATION ASPECT IMAGE:
    private int WIDTH_HEADER;
    private final int HEIGHT_HEADER = 400;
    private final int RADIUS_TOP = 25;
    private final int RADIUS_BOTTOM = 25;

    public NoteArrayListAdapter(NoteHolder.NoteCustomListener listener) {
        this.listener = listener;
        this.noteHeaderPathMap = new HashMap<>();
    }

    @NonNull
    @Override
    public NoteArrayListAdapter.NoteHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View noteView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_note_item, parent, false);

        noteView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        WIDTH_HEADER = parent.getWidth();
        return new NoteHolder(noteView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteArrayListAdapter.NoteHolder holder, int position) {
        Note currentNote = noteList.get(position);
        if (currentNote != null) {
            holder.noteTitle.setText(currentNote.getTitle());
            holder.note = currentNote;

            if (noteHeaderPathMap.containsKey(currentNote.getId()) && WIDTH_HEADER > 0) {
                Bitmap bitmap =
                        BitmapFactory.decodeFile(
                                noteHeaderPathMap.get(currentNote.getId()).getAbsolutePath());

                holder.headerView.setImageBitmap(
                        getRoundedBitmap(
                                scaleBitmap(bitmap, WIDTH_HEADER, HEIGHT_HEADER),
                                RADIUS_TOP,
                                RADIUS_BOTTOM));
            } else {
                holder.headerView.setImageDrawable(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (noteList == null) ? 0 : noteList.size();
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public void putHeaderPath(UUID noteID, File file) {
        this.noteHeaderPathMap.put(noteID, file);
    }

    public static class NoteHolder extends RecyclerView.ViewHolder {

        private TextView noteTitle;
        private ImageView headerView;
        private Note note;

        private int headerViewWidth;

        public NoteHolder(@NonNull View itemView, final NoteCustomListener listener) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.layout_note_title);
            headerView = itemView.findViewById(R.id.layout_note_picture);

            headerViewWidth = itemView.findViewById(R.id.layout_note).getWidth();

            itemView.setOnClickListener(
                    (View view) -> {
                        listener.onClickCustom(NoteHolder.this);
                    });
        }

        public int getHeaderViewWidth() {
            return this.headerViewWidth;
        }

        public Note getNote() {
            return note;
        }

        public interface NoteCustomListener {
            void onClickCustom(final NoteHolder holder);
        }
    }
}
