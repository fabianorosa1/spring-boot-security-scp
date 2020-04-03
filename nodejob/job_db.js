/*eslint no-console: 0*/
"use strict";

// SAP HANA Client Interface Programming Reference
// https://help.sap.com/viewer/0eec0d68141541d1b07893a39944924e/2.0.02/en-US/58c18548dab04a438a0f9c44be82b6cd.html
const hana = require("@sap/hana-client");
// https://github.com/SAP/cf-nodejs-logging-support
const log = require('cf-nodejs-logging-support'); //kibana
const xsenv = require('@sap/xsenv');

// Set the minimum logging level (Levels: error, warn, info, verbose, debug, silly)
log.setLoggingLevel("info");

log.info("----- BEGIN TASK -----");

var now = new Date();
log.info(">>> Current Month UTC: " + (now.getUTCMonth() +1));
log.info(">>> Current Year UTC: " + now.getUTCFullYear());

// Get HANA connection details from HDI Container
try {
	var hanaOptions = xsenv.getServices({
		hana: {tag: "hana"}
	});
} catch (err) {
	log.error("[WARN] %s", err.message);
}

// parameters for the DB connection
var conn = hana.createConnection();
var conn_params = {
  HOST: hanaOptions.hana.host,
  PORT: hanaOptions.hana.port,
  UID: hanaOptions.hana.user,
  PWD: hanaOptions.hana.password,
  CURRENTSCHEMA: hanaOptions.hana.schema,
  ENCRYPT: true,
  sslValidateCertificate: false
};

log.info(">>> CONNECT TO DB");
// connect to HANA DB
conn.connect(conn_params);

log.info(">>> CALL STORED PROCEDURE");
// call the stored procedure
conn.prepare('call ADVERTISEMENT_PROC (?, ?)', function(err, statement){
  if (err) {
    log.error("Prepare error: %s", err);
    throw err;
  }
  statement.exec({
    INVAL: 123
  }, function(err, outVal) {
    if (err) {
      log.error("Exec statement error: %s", err);
      throw err;
    }
    
    // Log the response from the store procedure execution
    log.info(">>> Result of execution: outVal: %s", outVal);

    log.info(">>> DISCONNECT");
    // close the connection
    conn.disconnect();

    log.info("----- END TASK -----");  
  });
});




