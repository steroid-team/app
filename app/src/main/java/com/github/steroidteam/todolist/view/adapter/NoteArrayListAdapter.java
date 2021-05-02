package com.github.steroidteam.todolist.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.notes.Note;
import java.util.List;
import java.util.UUID;

public class NoteArrayListAdapter extends RecyclerView.Adapter<NoteArrayListAdapter.NoteHolder> {

    private List<Note> noteList;
    private final NoteHolder.NoteCustomListener listener;

    public NoteArrayListAdapter(NoteHolder.NoteCustomListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteArrayListAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View noteView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_note_item, parent, false);
        return new NoteHolder(noteView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteArrayListAdapter.NoteHolder holder, int position) {
        Note currentNote = noteList.get(position);
        if (currentNote != null) {
            holder.noteTitle.setText(currentNote.getTitle());
            holder.noteID=currentNote.getId();
        }
    }

    @Override
    public int getItemCount() {
        return (noteList==null) ? 0 : noteList.size();
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList=noteList;
    }

    public static class NoteHolder extends RecyclerView.ViewHolder {

        private TextView noteTitle;
        private UUID noteID;

        public NoteHolder(@NonNull View itemView, final NoteCustomListener listener) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.layout_note_title);

            itemView.setOnClickListener(
                    (View view) -> {
                        listener.onClickCustom(NoteHolder.this);
                    });
        }

        public UUID getId() {
            return noteID;
        }

        public interface NoteCustomListener {
            void onClickCustom(NoteHolder holder);
        }
    }
}
