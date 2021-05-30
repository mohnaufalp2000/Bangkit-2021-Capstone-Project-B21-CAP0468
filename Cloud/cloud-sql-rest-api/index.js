const express = require("express");
const mysql = require("mysql");
const app = express();

app.use(express.json());
const port = process.env.PORT || 8080;
app.listen(port, () => {
  console.log(`Fruitinformation Rest API listening on port ${port}`);
});

app.get("/", async (req, res) => {
  res.json({ status: "We ready to serve!" });
});

app.get("/:finfo", async (req, res) => {
  const query = "SELECT * FROM fruitinfo WHERE no_hp = ?";
  pool.query(query, [ req.params.finfo ], (error, results) => {
    if (!results) {
      res.json({ status: "Not found!" });
    } else {
      res.json(results);
    }
  });
});

const pool = mysql.createPool({
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME,
  socketPath: `/cloudsql/${process.env.INSTANCE_CONNECTION_NAME}`,
});

app.post("/", async (req, res) => {
  const data = {
    scandate: req.body.scandate,
    fruitname: req.body.fruitname,
    fruitstatus: req.body.fruitstatus,
    prohib: req.body.prohib,
    negative: req.body.negative,
    no_hp: req.body.no_hp
  }
  const query = "INSERT INTO fruitinfo VALUES (?, ?, ?, ?, ?, ?)";
  pool.query(query, Object.values(data), (error) => {
    if (error) {
      res.json({ status: "failure", reason: error.code  });
    } else {
      res.json({ status: "success", data: data});
    }
  });
});