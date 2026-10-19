var express = require("express");
var router = express.Router();

var ctrlMain = require("../controllers/main");

/* GET home page. */
router.get("/", ctrlMain.index);

/* Redirect legacy navigation URLs to the existing static pages. */
router.get("/rooms", function(req, res) { res.redirect("/rooms.html"); });
router.get("/meals", function(req, res) { res.redirect("/meals.html"); });
router.get("/news", function(req, res) { res.redirect("/news.html"); });
router.get("/about", function(req, res) { res.redirect("/about.html"); });
router.get("/contact", function(req, res) { res.redirect("/contact.html"); });

module.exports = router;