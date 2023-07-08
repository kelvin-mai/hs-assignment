const { defineConfig } = require("cypress");
const { Client } = require("pg");
const fs = require("fs");

const client = new Client({
  connectionString:
    process.env.DB_CONNECTION_STRING ||
    "postgres://postgres:postgres@localhost:5432/samurai_db",
});
client.connect();

module.exports = defineConfig({
  e2e: {
    setupNodeEvents(on, config) {
      // implement node event listeners here

      on("task", {
        initTestData: function (filename) {
          return client.query(fs.readFileSync(filename, "utf8"));
        },
      });
    },
  },
});
