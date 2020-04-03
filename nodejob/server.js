/*eslint no-console: 0*/
"use strict";

const express = require("express");
const app = express();

app.get("/healthcheck", (req, res) => {
 res.json({"status": "UP"});
});

const port = process.env.PORT || 3000;
app.listen(port, function () {
  console.log('NodeJob listening on port ' + port);
});
