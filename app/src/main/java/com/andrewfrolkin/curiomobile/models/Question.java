package com.andrewfrolkin.curiomobile.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by andrewfrolkin on 2016-07-14.
 */
public class Question implements Parcelable {
    public final int id;
    public final String slug;
    public final String title;
    public final String question;
    public final String motivaton;
    public final int project;

    public Question(int id, String slug, String title, String question, String motivaton, int project) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.question = question;
        this.motivaton = motivaton;
        this.project = project;
    }

    protected Question(Parcel in) {
        id = in.readInt();
        slug = in.readString();
        title = in.readString();
        question = in.readString();
        motivaton = in.readString();
        project = in.readInt();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(slug);
        dest.writeString(title);
        dest.writeString(question);
        dest.writeString(motivaton);
        dest.writeInt(project);
    }

    /*
    {
        "id": 1,
        "slug": "0",
        "title": "Chelidonium versus Vaccinium",
        "question": "What are the differences in the climate change response of Chelidonium and Vaccinium?",
        "motivation": "Who doesn't like blueberries?",
        "tasks": [
            1
        ],
        "project": 1
    }
     */
}
