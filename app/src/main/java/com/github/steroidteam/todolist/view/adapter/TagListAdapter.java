package com.github.steroidteam.todolist.view.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.todo.Tag;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.TagHolder> {
    private List<Tag> tags;

    public TagListAdapter(List<Tag> tags) {
        this.tags = tags;
    }

    @NonNull
    @NotNull
    @Override
    public TagHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new TagHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TagHolder holder, int position) {
        Tag tag = tags.get(position);
        if (tag != null) {
            holder.setView(tag);
        }
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class TagHolder extends RecyclerView.ViewHolder {
        private Button tagView;

        public TagHolder(@NonNull View itemView) {
            super(itemView);
            tagView = itemView.findViewById(R.id.tag_button);
        }

        public void setView(Tag tag) {
            tagView.setText(tag.getBody());
            tagView.setBackgroundTintList(ColorStateList.valueOf(tag.getColor()));
        }
    }
}
