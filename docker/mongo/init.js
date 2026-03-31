
// Creamos un usuario específico para Mongo Express
db = db.getSiblingDB("admin");
db.createUser({
  user: "mongoexpress",
  pwd: "secret123",
  roles: [
    { role: "readWrite", db: "dinosaurs_db" },
    { role: "read", db: "admin" }
  ]
});
