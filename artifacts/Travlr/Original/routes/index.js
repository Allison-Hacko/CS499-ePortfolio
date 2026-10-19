var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Travlr Getaways' });
});

/* GET travel page. */
router.get('/travel', function(req, res, next) {
  res.render('travel', { title: 'Travel' });
});

module.exports = router;
