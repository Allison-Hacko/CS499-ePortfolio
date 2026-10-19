const mongoose = require('mongoose');
const Booking = require('../models/booking');
const Trip = require('../models/travlr');

function getPrice(value) {
    const cleaned = String(value ?? '').replace(/[$,\s]/g, '');
    if (!/^\d+(\.\d{1,2})?$/.test(cleaned)) return null;
    const price = Number(cleaned);
    return Number.isFinite(price) && price >= 0 ? price : null;
}

function getUserId(req) {
    const id = req.auth && req.auth._id;
    return id && mongoose.isValidObjectId(id) ? id : null;
}

const createBooking = async (req, res) => {
    try {
        const userId = getUserId(req);
        if (!userId) {
            return res.status(401).json({ message: 'Please log in again.' });
        }

        const tripCode = typeof req.body.tripCode === 'string'
            ? req.body.tripCode.trim()
            : '';
        const travelers = Number(req.body.travelers);

        if (!tripCode || !Number.isInteger(travelers) ||
            travelers < 1 || travelers > 10) {
            return res.status(400).json({
                message: 'Choose a valid trip and between 1 and 10 travelers.'
            });
        }

        const trip = await Trip.findOne({ code: tripCode }).exec();
        if (!trip) {
            return res.status(404).json({ message: 'That trip could not be found.' });
        }

        const price = getPrice(trip.perPerson);
        if (price === null) {
            return res.status(400).json({
                message: 'This trip does not have a valid price and cannot be booked.'
            });
        }

        const booking = await Booking.create({
            userId,
            tripCode: trip.code,
            tripName: trip.name,
            tripStart: trip.start,
            travelers,
            pricePerPerson: price,
            totalPrice: Math.round(price * travelers * 100) / 100,
            status: 'pending'
        });

        return res.status(201).json({
            message: 'Reservation created.',
            booking
        });
    } catch (err) {
        console.error('Booking creation error:', err.message);
        return res.status(500).json({ message: 'Unable to create reservation.' });
    }
};

const listBookings = async (req, res) => {
    try {
        const userId = getUserId(req);
        if (!userId) {
            return res.status(401).json({ message: 'Please log in again.' });
        }

        const bookings = await Booking.find({ userId })
            .sort({ createdAt: -1 })
            .lean()
            .exec();

        return res.status(200).json(bookings);
    } catch (err) {
        console.error('Booking retrieval error:', err.message);
        return res.status(500).json({ message: 'Unable to retrieve reservations.' });
    }
};

const cancelBooking = async (req, res) => {
    try {
        const userId = getUserId(req);
        if (!userId) {
            return res.status(401).json({ message: 'Please log in again.' });
        }

        if (!mongoose.isValidObjectId(req.params.bookingId)) {
            return res.status(404).json({ message: 'Reservation not found.' });
        }

        const booking = await Booking.findOne({
            _id: req.params.bookingId,
            userId
        }).exec();

        if (!booking) {
            return res.status(404).json({ message: 'Reservation not found.' });
        }

        if (booking.status === 'cancelled') {
            return res.status(400).json({ message: 'This reservation is already cancelled.' });
        }

        booking.status = 'cancelled';
        await booking.save();

        return res.status(200).json({
            message: 'Reservation cancelled.',
            booking
        });
    } catch (err) {
        console.error('Booking cancellation error:', err.message);
        return res.status(500).json({ message: 'Unable to cancel reservation.' });
    }
};

module.exports = {
    createBooking,
    listBookings,
    cancelBooking
};
