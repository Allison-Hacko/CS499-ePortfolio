const express = require('express');
const router = express.Router();
const tripsController = require('../controllers/trips');
const authentication = require('../controllers/authentication');
const bookingsController = require('../controllers/bookings');
const passport = require('../config/passport');
const { expressjwt: jwt } = require('express-jwt');

const requireAuth = jwt({
    secret: process.env.JWT_SECRET,
    algorithms: ['HS256']
});

// A valid login is not enough to administer trips.
const requireAdmin = (req, res, next) => {
    if (req.auth && req.auth.role === 'admin') {
        return next();
    }

    return res.status(403).json({
        message: 'Administrator access is required for trip management.'
    });
};

// Authentication endpoints
router.post('/register', authentication.register);
router.post(
    '/login',
    passport.authenticate('local', { session: false }),
    authentication.login
);

// Public trip browsing
router.get('/trips', tripsController.tripsList);
router.get('/trips/:tripCode', tripsController.tripsFindByCode);

// Customer bookings: every route requires a valid JWT.
router.post('/bookings', requireAuth, bookingsController.createBooking);
router.get('/bookings', requireAuth, bookingsController.listBookings);
router.patch('/bookings/:bookingId/cancel', requireAuth, bookingsController.cancelBooking);

// Protected trip management
router.post('/trips', requireAuth, requireAdmin, tripsController.tripsAddTrip);
router.put('/trips/:tripCode', requireAuth, requireAdmin, tripsController.tripsUpdateTrip);
router.delete('/trips/:tripCode', requireAuth, requireAdmin, tripsController.tripsDeleteTrip);

module.exports = router;
