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

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {

    private List<Note> noteList;
    private final NoteCustomListener listener;

    public NoteAdapter(List<Note> list, NoteCustomListener listener) {
        this.noteList=list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View noteView =
                LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_note_item, parent, false);
        return new NoteHolder(noteView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteHolder holder, int position) {
        Note currentNote = noteList.get(position);
        if(currentNote!=null) {
            holder.noteTitle.setText(currentNote.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public UUID getIdOfNote(int index) {
        return noteList.get(index).getId();
    }

    public interface NoteCustomListener {
        void onClickCustom(NoteHolder holder);
    }

    public class NoteHolder extends RecyclerView.ViewHolder {

        private TextView noteTitle;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.layout_note_title);

            itemView.setOnClickListener(
                    (View view) -> {
                        listener.onClickCustom(NoteHolder.this);
                    });
        }
    }
}
