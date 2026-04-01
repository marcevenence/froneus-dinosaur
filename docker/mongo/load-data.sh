#!/bin/bash

# Ejecuta un insertMany en la base dinosaurs
docker exec -i mongo mongosh -u mongoADMIN -p mongoPASS --authenticationDatabase admin <<EOF

db = db.getSiblingDB('dinosaurs_db');

db.createUser({
  user: 'froneus',
  pwd: 'secret123',
  roles: [
    {
      role: 'readWrite',
      db: 'dinosaurs_db',
    },
  ],
});

db.createCollection('dinosaurs');

// indexes
db.dinosaurs.createIndex({ name: 1 }, { unique: true });
db.dinosaurs.createIndex({ status: 1 });

db.dinosaurs.insertMany([
  {
      _id: ObjectId('69cc0a3b3ad0e659fad805db'),
      name: "Tyrannosaurus Rex",
      species: "Theropod",
      discoveryDate: ISODate("1902-01-01T23:59:59Z"),
      extinctionDate: ISODate("2023-12-31T23:59:59Z"),
      status: "ALIVE"
    },
    {
      name: "Triceratops",
      species: "Ceratopsid",
      discoveryDate: ISODate("1889-01-01T00:00:00Z"),
      extinctionDate: ISODate("2023-12-31T23:59:59Z"),
      status: "ALIVE"
    },
    {
      name: "Velociraptor",
      species: "Dromaeosaurid",
      discoveryDate: ISODate("1924-01-01T00:00:00Z"),
      extinctionDate: ISODate("2023-12-31T23:59:59Z"),
      status: "ALIVE"
    }
])
EOF
