package com.andrewfrolkin.curiomobile;

import android.util.JsonReader;
import android.util.Log;

import com.andrewfrolkin.curiomobile.models.Project;
import com.andrewfrolkin.curiomobile.models.Question;
import com.andrewfrolkin.curiomobile.models.Team;
import com.andrewfrolkin.curiomobile.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrewfrolkin on 2016-07-14.
 */

public class CurioApi {

    public static List<Project> projectsUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readJsonStream(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static Project projectUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readJsonStreamProject(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static Project readJsonStreamProject(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readProject(reader);
        } finally {
            reader.close();
        }
    }

    private static List<Project> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readProjectsArray(reader);
        } finally {
            reader.close();
        }
    }

    private static List<Project> readProjectsArray(JsonReader reader) throws IOException {
        List<Project> messages = new ArrayList<Project>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readProject(reader));
        }
        reader.endArray();
        return messages;
    }

    private static Project readProject(JsonReader reader) throws IOException {
        int id = -1;
        String description = null;
        String owner = null;
        String name2 = null;
        String avatar = null;
        String short_description = null;
        ArrayList<Team> team = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("description")) {
                description = reader.nextString();
            } else if (name.equals("short_description")) {
                short_description = reader.nextString();
            } else if (name.equals("avatar")) {
                avatar = reader.nextString();
            } else if (name.equals("name")) {
                name2 = reader.nextString();
            } else if (name.equals("owner")) {
                owner = reader.nextString();
            } else if (name.equals("team")) {
                team = readTeamArray(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Project(id, owner, name2, avatar, short_description, description, team);
    }

    private static ArrayList<Team> readTeamArray(JsonReader reader) throws IOException {
        ArrayList<Team> messages = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readTeam(reader));
        }
        reader.endArray();
        return messages;
    }

    private static Team readTeam(JsonReader reader) throws IOException {
        int id = -1;
        String owner = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("slug")) {
                owner = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Team(id, owner);
    }

    public static List<Question> questionUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readJsonStreamQuestions(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static List<Question> questionsForIdUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readJsonStreamQuestions(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static List<Question> readJsonStreamQuestions(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readQuestionsArray(reader);
        } finally {
            reader.close();
        }
    }

    private static List<Question> readQuestionsArray(JsonReader reader) throws IOException {
        List<Question> messages = new ArrayList<Question>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readQuestion(reader));
        }
        reader.endArray();
        return messages;
    }



    private static Question readQuestion(JsonReader reader) throws IOException {
        int id = -1;
        String slug = null;
        String title = null;
        String question = null;
        String motivaton = null;
        int project = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("slug")) {
                slug = reader.nextString();
            } else if (name.equals("title")) {
                title = reader.nextString();
            } else if (name.equals("question")) {
                question = reader.nextString();
            } else if (name.equals("motivation")) {
                motivaton = reader.nextString();
            } else if (name.equals("project")) {
                project = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Question(id, slug, title, question, motivaton, project);
    }

    public static User userUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("", "The USER response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readUserJsonStream(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static User readUserJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readUser(reader);
        } finally {
            reader.close();
        }
    }

    private static User readUser(JsonReader reader) throws IOException {
        int id = -1;
        String owner = null;
        String email = null;
        String avatar = null;
        String bio = null;
        String title = null;
        String nick_name = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("email")) {
                email = reader.nextString();
            } else if (name.equals("bio")) {
                bio = reader.nextString();
            } else if (name.equals("avatar")) {
                avatar = reader.nextString();
            } else if (name.equals("title")) {
                title = reader.nextString();
            } else if (name.equals("owner")) {
                owner = reader.nextString();
            } else if (name.equals("nickname")) {
                nick_name = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new User(id, owner, nick_name, email, avatar, bio, title);
    }

}
