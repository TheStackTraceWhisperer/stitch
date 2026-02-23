package com.example.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {
    record StitchFeature(String name, String description, boolean implemented) {}

    public static void main(String[] args) {
        var feature = new StitchFeature("Remote Dependency Resolution", "Resolves modules from Maven Central", true);
        var gson = new GsonBuilder().setPrettyPrinting().create();

        System.out.println("Invoking Gson to serialize a record:");
        System.out.println(gson.toJson(feature));
    }
}

