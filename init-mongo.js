db = db.getSiblingDB('home-calendar-db');

db.createUser({
    user: "app_user",
    pwd: "admin",
    roles: [{role: "readWrite", db: "home-calendar-db"}]
});
