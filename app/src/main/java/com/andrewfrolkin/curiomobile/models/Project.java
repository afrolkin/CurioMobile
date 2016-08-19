package com.andrewfrolkin.curiomobile.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by andrewfrolkin on 2016-07-13.
 */

public class Project implements Parcelable{
    public final int id;
    public final String owner;
    public final String name;
    public final String avatar;
    public final String short_description;
    public final String description;
    public final ArrayList<Team> team;

    public Project(int id, String owner, String name, String avatar, String short_description, String description, ArrayList<Team> team) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.avatar = avatar;
        this.short_description = short_description;
        this.description = description;
        this.team = team;
    }

    protected Project(Parcel in) {
        id = in.readInt();
        owner = in.readString();
        name = in.readString();
        avatar = in.readString();
        short_description = in.readString();
        description = in.readString();
        team = new ArrayList<>();
        in.readTypedList(team, Team.CREATOR);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(owner);
        dest.writeString(name);
        dest.writeString(avatar);
        dest.writeString(short_description);
        dest.writeString(description);
        dest.writeTypedList(team);
    }
}

/*

 {
        "id": 1,
        "slug": "thoreaus-field-notes",
        "owner": "newuser@gmail.com",
        "name": "Thoreau's Field Notes",
        "avatar": "https://curio-media.s3.amazonaws.com/thoreaus-field-notes/avatar.jpg",
        "short_description": "How does climate change affect the timing of when buds, flowers and fruits come out?",
        "description": "Compared to a century ago, we now have earlier springs and longer summers across New England. As a result, plants have adjusted the timing of when they leaf out, flower and bear fruit.  Species respond to climate change in different ways, and these differences can lead to \"missed encounters\" between species that depend on each other.\n\nFor example, a plant that flowers too late might miss its primary pollinator and fail to reproduce. Understanding how different species respond to climate change is crucial to predicting how they will thrive in our current environment.",
        "data_type": "I",
        "is_active": true,
        "is_featured": true,
        "is_external": false,
        "redirect_url": "",
        "curios": [
            1
        ],
        "team": [
            {
                "id": 2,
                "owner": "shaishav1"
            }
        ]
    },

 */