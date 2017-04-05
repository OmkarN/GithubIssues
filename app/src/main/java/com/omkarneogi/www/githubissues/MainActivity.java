package com.omkarneogi.www.githubissues;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        The URL to download from has been hardcoded.
        A MalformedURL Exception will be caught while downloading the JSON, (in the downloadJSON() method) if the URL isn't right.
        We only want "open" issues, so the condition state=open
         */
        String downloadCondition = "state=open";
        String downloadUrl = "https://api.github.com/repos/rails/rails/issues?"+downloadCondition;

        JSONDownloaderTask jdt = new JSONDownloaderTask();
        jdt.execute(downloadUrl);


    }

    private class JSONDownloaderTask extends AsyncTask<String, Void, ArrayList <Issue>> {

        @Override
        protected ArrayList<Issue> doInBackground(String... params) {
            /*
            1. Downloads JSON
            2. Parses the JSON into an object of the "Issue" class.
            3. Stores these Issue objects into an arraylist called "issueArrayList".
            4. Sorts this arraylist by most recently updated first values.
            5. Returns this arraylist to onPostExecute() for display on screen.
            */
            String downloadUrl = params[0];
            ArrayList <Issue> issueArrayList = new ArrayList<>();

            try {

                String jsonAsString = downloadJson(downloadUrl);
                Log.d(TAG, "JSON downloaded successfully");

                JSONArray jsonArray = new JSONArray(jsonAsString);
                /*
                Converted to JSONArray because we want individual JSONObjects to display on screen.
                These individual objects are added to the "issueArrayList" arraylist
                 */

                for(int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Issue issue = returnPopulatedIssue(jsonObject);

                    issueArrayList.add(issue);
                }

                Collections.sort(issueArrayList);
                /*
                Sorted the arraylist of Issues so as to display in "most recently updated first" format as was requested
                 */

            } catch (MalformedURLException m) {
                Log.e(TAG, "Malformed URL Exception in JSONDownloaderTask: ", m);
                m.printStackTrace();

            } catch (IOException i){
                Log.e(TAG, "IOException in JSONDownloaderTask: ", i);
                i.printStackTrace();

            } catch (JSONException j) {
                Log.e(TAG, "JSONException in JSONDownloaderTask: ", j);
                j.printStackTrace();

            } catch (Exception e) {
                Log.d(TAG, "Generic Exception");
                e.printStackTrace();
            }

            return issueArrayList;
        }


        @Override
        protected void onPostExecute(ArrayList<Issue> issueArrayList) {
            /*
            1. Receives a sorted arraylist of issues from the doInBackground() method.
            2. Sets a custom adapter on the listview inside the MainActivity "R.id.listview", to display the arraylist of issues.
             */
            super.onPostExecute(issueArrayList);

            CustomAdapter adapter = new CustomAdapter(MainActivity.this, issueArrayList.toArray(new Issue[issueArrayList.size()]));
            ListView listView = (ListView) findViewById(R.id.listview);
            listView.setAdapter(adapter);

        }
    }

    public static String downloadJson(String downloadUrl) throws MalformedURLException, IOException {
        /*
        1. Downloads JSON and returns it *as a String*
         */
        URL u = new URL(downloadUrl);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();

        conn.setRequestMethod("GET");
        conn.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuffer sb = new StringBuffer();
        String line = "";
        while ((line = br.readLine())!= null) {
            sb.append(line);
        }
        br.close();
        conn.disconnect();
        Log.d(TAG, sb.toString());
        return sb.toString();
    }

    public static Issue returnPopulatedIssue(JSONObject jsonObject) {
        /*
        1. Parses JSONObjects and returns objects of type Issue populated with these JSONObjects

        This dataset was very clean and I did not yet experience missing data issues while parsing JSON.
        */

        /*
        What fields does an object of class Issue contain?

        1. id: the ID of the Issue as given in the JSON downloaded (Double)
        2. title: the text title of the issue (String)
        3. body: the text body of the issue (String)
        4. shortBody: the first 140 characters of the body of the issue (Required for displaying on screen (String)
        5. comment_url: the url from which to download the comments related to a particular issue. (String)
        6. updated_at: the time at which this Issue was updated. (Long)
         */
        try {
            String updated_at = jsonObject.getString("updated_at");
            updated_at = updated_at.replace("Z", ".000-0000");
            /*
            To conform to the following date format:
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

            the updated_at string which came as:
            2017-03-30T16:54:35Z

            was converted to:
            2017-03-30T16:54:35.000-0000
             */
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Date date = dateFormat.parse(updated_at);
            Long updated_at_LONG = date.getTime();
            /*
            For sorting Issue objects based on "most recently updated first" format,
            the time of the updates has been coverted to Long values.
            These Long values will then be stored as updated_At fields in every Issue.
             */

            Issue issue = new Issue();

            issue.setId(Double.parseDouble(jsonObject.getString("id")));
            issue.setTitle(jsonObject.getString("title"));
            issue.setBody(jsonObject.getString("body"));
            issue.setShortBody(jsonObject.getString("body"));
            issue.setComment_url(jsonObject.getString("comments_url"));
            issue.setUpdated_at(updated_at_LONG);

            return issue;

        } catch(JSONException j) {
            Log.d(TAG, "JSONException caught in returnPopulatedIssue()");
            j.printStackTrace();

        } catch(ParseException p) {
            Log.d(TAG, "ParseException caught in returnPopulatedIssue()");
            p.printStackTrace();

        } catch(Exception e) {
            Log.d(TAG, "Generic exception caught in returnPopulatedIssue()");
            e.printStackTrace();
        }

        return null;
    }
}
