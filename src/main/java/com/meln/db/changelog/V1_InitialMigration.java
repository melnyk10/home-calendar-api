package com.meln.db.changelog;

import com.mongodb.client.MongoDatabase;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;

@ChangeUnit(id = "init", order = "001", author = "system", systemVersion = "1")
public class V1_InitialMigration {

    @Execution
    public void init(MongoDatabase db) {
        Document indexKeys = new Document()
                .append("email", 1)
                .append("firstName", 1)
                .append("lastName", 1);

        db.getCollection("users").createIndex(indexKeys);
    }

    @RollbackExecution
    public void rollbackInit(MongoDatabase db) {
        Document indexKeys = new Document()
                .append("email", 1)
                .append("firstName", 1)
                .append("lastName", 1);

        db.getCollection("users").dropIndex(indexKeys);
    }

}
