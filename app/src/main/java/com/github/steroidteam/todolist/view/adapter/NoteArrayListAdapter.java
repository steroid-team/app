package com.github.steroidteam.todolist.view.adapter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.notes.Note;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.steroidteam.todolist.util.Utils.dip2px;

public class NoteArrayListAdapter extends RecyclerView.Adapter<NoteArrayListAdapter.NoteHolder> {

    private List<Note> noteList;
    private final NoteHolder.NoteCustomListener listener;

    private Map<UUID, File> noteHeaderPathMap;

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
        return new NoteHolder(noteView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteArrayListAdapter.NoteHolder holder, int position) {
        Note currentNote = noteList.get(position);
        if (currentNote != null) {
            holder.noteTitle.setText(currentNote.getTitle());
            holder.note = currentNote;

            if(noteHeaderPathMap.containsKey(currentNote.getId())) {
                Bitmap bitmap = BitmapFactory.decodeFile(noteHeaderPathMap.get(currentNote.getId()).getAbsolutePath());

                Bitmap scaled =
                        Bitmap.createScaledBitmap(
                                bitmap, 1000, 500, false);
                holder.headerView.setImageBitmap(getRoundedBitmap(bitmap));
            } else {
                holder.headerView.setImageDrawable(null);
            }
        }
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 50;
        paint.setAntiAlias(true);
        Path path = new Path();
        float[] corners =
                new float[] {
                        roundPx,
                        roundPx, // Top left radius in px
                        roundPx,
                        roundPx, // Top right radius in px
                        roundPx,
                        roundPx, // Bottom right radius in px
                        roundPx,
                        roundPx // Bottom left radius in px
                };
        path.addRoundRect(rectF, corners, Path.Direction.CW);
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public int getItemCount() {
        return (noteList == null) ? 0 : noteList.size();
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }

    public void putHeaderPath(UUID noteID, File file) {this.noteHeaderPathMap.put(noteID, file);}

    public static class NoteHolder extends RecyclerView.ViewHolder {

        private TextView noteTitle;
        private ImageView headerView;
        private Note note;
        private String headerPath;

        public NoteHolder(@NonNull View itemView, final NoteCustomListener listener) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.layout_note_title);
            headerView = itemView.findViewById(R.id.layout_note_picture);

            itemView.setOnClickListener(
                    (View view) -> {
                        listener.onClickCustom(NoteHolder.this);
                    });
        }

        public Note getNote() {
            return note;
        }

        public interface NoteCustomListener {
            void onClickCustom(final NoteHolder holder);
        }
    }
}
