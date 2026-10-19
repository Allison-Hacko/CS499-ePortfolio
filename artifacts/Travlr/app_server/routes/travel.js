var express = require("express");
var router = express.Router();

var ctrlTravlr = require("../controllers/travlr");

/* Keep the specific route before the parameter route. */
router.get("/", ctrlTravlr.travel);
router.get("/:tripCode", ctrlTravlr.tripDetails);

module.exports = router;
