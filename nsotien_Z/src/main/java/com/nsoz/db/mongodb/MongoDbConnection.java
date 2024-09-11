package com.nsoz.db.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nsoz.server.Config;
import org.bson.Document;

public class MongoDbConnection {

    public static MongoClient mongoClient;
    public static MongoDatabase db;

    public static void connect() {
        Config config = Config.getInstance();
        mongoClient = MongoClients.create(config.getMongodbUrl());
        db = mongoClient.getDatabase(config.getMongodbName());
        deleteAllRecordsOlderThan7Day(db.getCollection("player"));
        deleteAllRecordsOlderThan7Day(db.getCollection("clone_player"));
    }

    public static MongoCollection getCollection(String collectionName) {
        return db.getCollection(collectionName);
    }

    public static void deleteAllRecordsOlderThan7Day(MongoCollection<Document> collection) {
        // Set the cutoff date for records to delete
        long cutoff = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        // Delete all records with an update_at field older than 7 days
        collection.deleteMany(Filters.lt("update_at", cutoff));
    }

    public static void close() {
        mongoClient.close();
    }

}
