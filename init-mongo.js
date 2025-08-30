// Only run rs.initiate() if not yet initiated
// try {
//     rs.status();
// } catch (e) {
//     rs.initiate({
//         _id: "rs0",
//         members: [{ _id: 0, host: "home-calendar-db:27017" }]
//     });
// }

db = db.getSiblingDB('home-calendar-db');

db.createUser({
  user: "app_user",
  pwd: "admin",
  roles: [{role: "readWrite", db: "home-calendar-db"}]
});