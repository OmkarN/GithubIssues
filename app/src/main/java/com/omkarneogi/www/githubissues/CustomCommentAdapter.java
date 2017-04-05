package com.omkarneogi.www.githubissues;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by omkar on 4/2/17.
 */

public class CustomCommentAdapter extends ArrayAdapter<Comment> {
    /*
    Definer a custom Adapter for displaying objects of Comment class in te listview R.id.dialog_list_view
     */
    public static final String TAG = CustomCommentAdapter.class.getSimpleName();
    Context context;

    public CustomCommentAdapter(Context context, Comment[] comments) {
        super(context, R.layout.new_row_layout, comments);
    }

    private static class ViewHolder {
        TextView usernameTextView;
        TextView commentBodyTextView;
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        final Comment comment =getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());

            convertView = layoutInflater.inflate(R.layout.new_dialog_row_layout,
                    parent,
                    false);

            viewHolder.usernameTextView = (TextView) convertView.findViewById(R.id.username_drl);
            viewHolder.commentBodyTextView = (TextView) convertView.findViewById(R.id.comment_drl);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.usernameTextView.setText(comment.getUsername());
        viewHolder.commentBodyTextView.setText(comment.getCommentBody());

        return convertView;
    }
}
