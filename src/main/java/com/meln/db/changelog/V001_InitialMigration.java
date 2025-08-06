package com.meln.db.changelog;

import com.mongodb.client.MongoDatabase;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;

@ChangeUnit(id = "init", order = "001", author = "system", systemVersion = "1")
public class V001_InitialMigration {

    @Execution
    public void init(MongoDatabase db) {
        Document indexKeys = new Document().append("email", 1);
        db.getCollection("users").createIndex(indexKeys);

        Document user = new Document()
                .append("email", "o.melnyk10@gmail.com")
                .append("firstName", "Sam")
                .append("lastName", "Porter-Bridge");
        db.getCollection("users").insertOne(user);
    }

    @RollbackExecution
    public void rollback(MongoDatabase db) {
        Document indexKeys = new Document().append("email", 1);
        db.getCollection("users").dropIndex(indexKeys);
        db.getCollection("users").deleteOne(new Document("email", "o.melnyk10@gmail.com"));
    }

}
