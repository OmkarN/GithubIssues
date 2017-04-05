package com.omkarneogi.www.githubissues;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by omkar on 4/2/17.
 */

public class CustomAdapter extends ArrayAdapter<Issue>  {
    /*
    Summary: Creates a RecyclerView to display Issue objects inside the listview "R.id.listview"

    In this case, the number of Issues to display was less and the RecyclerView was not needed, however,
    with an increase in the number of Issues to display, the UI would get sluggish.
     */
    public static final String TAG = CustomAdapter.class.getSimpleName();
    Context context;

    CustomAdapter(Context context, Issue[] issueArr) {
        super(context, R.layout.new_row_layout, issueArr);
        this.context = context;
    }

    private static class ViewHolder {
        TextView titleTextView;
        TextView bodyTextView;
    }

    @Override
    public View getView(final int position,
                        View convertView,
                        ViewGroup parent) {
        // Storing the issue to display
        final Issue singleIssue = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            // Create convertView for the first time
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());

            convertView = layoutInflater.inflate(R.layout.new_row_layout, parent, false);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            viewHolder.bodyTextView = (TextView) convertView.findViewById(R.id.bodyTextView);

            convertView.setTag(viewHolder);
        } else {
            //Recycling convertView
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.titleTextView.setText(singleIssue.getTitle());
        viewHolder.bodyTextView.setText(singleIssue.getShortBody());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentWorkerTask commentWorkerTask = new CommentWorkerTask();
                commentWorkerTask.execute(singleIssue.getComment_url());
            }
        });
        return convertView;
    }

    private class CommentWorkerTask extends AsyncTask<String, Void, ArrayList<Comment>> {

        @Override
        protected ArrayList<Comment> doInBackground(String... params) {
            /*
            1. Downloads JSON
            2. Parses the JSON into an object of the "Comment" class.
            3. Stores these Comment objects into an arraylist called "commentArrayList".
               No need to sort the comments arraylist, because it has not been asked
            4. Returns this arraylist to onPostExecute() for display inside a Dialog Box.
            */
            Log.d(TAG, "inside doinbackground");
            String comment_url = params[0];
            ArrayList<Comment> commentArrayList = new ArrayList<>();
            Log.d(TAG, "inside doinbackground 2");

            try {
                String jsonAsString = MainActivity.downloadJson(comment_url);
                //Downloads JSON using the same method as in the MainActivity
                JSONArray jsonArray = new JSONArray(jsonAsString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Comment comment = returnPopulatedComment(jsonObject);
                    // Parses the JSON to an object of Comment class

                    commentArrayList.add(comment);
                }

            } catch (MalformedURLException m) {
                Log.e(TAG, "Malformed URL Exception in JSONDownloaderTask: ", m);
            } catch (IOException i) {
                Log.e(TAG, "IOException in JSONDownloaderTask: ", i);
            } catch (JSONException j) {
                Log.e(TAG, "JSONException in JSONDownloaderTask: ", j);
            } catch (Exception e) {
                Log.d(TAG, "Generic Exception");
                e.printStackTrace();
            }
            return commentArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Comment> comments) {
            super.onPostExecute(comments);
            Toast.makeText(context, "Toast", Toast.LENGTH_SHORT).show();
            try {
                // Dialog to display the comment's username and body
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.new_dialog_layout);

                ListView listView = (ListView) dialog.findViewById(R.id.dialog_list_view);

                CustomCommentAdapter customCommentAdapter = new CustomCommentAdapter(context,
                        comments.toArray(new Comment[comments.size()]));

                listView.setAdapter(customCommentAdapter);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
    }



    public static Comment returnPopulatedComment(JSONObject jsonObject) {
        /*
        1. Parses JSONObjects and returns objects of type Comment populated with these JSONObjects

        This dataset was very clean and I did not yet experience missing data issues while parsing JSON.

        What fields does and object of class Comment contain?
        1. commentBody: the text body of the comment (String)
        2. username: the user who wrote that comment (String)
         */
        try {
            Comment comment = new Comment();

            comment.setCommentBody(jsonObject.getString("body"));
            comment.setUsername(jsonObject.getJSONObject("user").getString("login"));

            return comment;
        } catch (JSONException e) {
            Log.d(TAG, "JSONException caught in returnPopulatedComment()");
        }
        return null;
    }
}