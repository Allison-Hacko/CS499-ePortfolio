const Trip = require("../models/travlr");

// GET: Retrieve all trips
const tripsList = async (req, res) => {
    try {
        const trips = await Trip.find({}).exec();
        res.status(200).json(trips);
    } catch (err) {
        console.error("Error retrieving trips:", err.message);
        res.status(500).json({ message: "Unable to retrieve trips" });
    }
};

// GET: Retrieve one trip by code
const tripsFindByCode = async (req, res) => {
    try {
        const trip = await Trip.findOne({ code: req.params.tripCode }).exec();
        if (!trip) return res.status(404).json({ message: "Trip not found" });
        res.status(200).json(trip);
    } catch (err) {
        console.error("Error retrieving trip:", err.message);
        res.status(500).json({ message: "Unable to retrieve trip" });
    }
};

// POST: Create a new trip
const tripsAddTrip = async (req, res) => {
    try {
        const newTrip = new Trip(req.body);
        const savedTrip = await newTrip.save();
        res.status(201).json(savedTrip);
    } catch (err) {
        console.error("Error creating trip:", err.message);
        res.status(400).json({ message: "Unable to create trip", error: err.message });
    }
};

// PUT: Update an existing trip by code
const tripsUpdateTrip = async (req, res) => {
    try {
        const updatedTrip = await Trip.findOneAndUpdate(
            { code: req.params.tripCode },
            req.body,
            { new: true, runValidators: true }
        ).exec();

        if (!updatedTrip) return res.status(404).json({ message: "Trip not found" });
        res.status(200).json(updatedTrip);
    } catch (err) {
        console.error("Error updating trip:", err.message);
        res.status(400).json({ message: "Unable to update trip", error: err.message });
    }
};

// DELETE: Remove a trip by code
const tripsDeleteTrip = async (req, res) => {
    try {
        const deletedTrip = await Trip.findOneAndDelete({
            code: req.params.tripCode
        }).exec();

        if (!deletedTrip) return res.status(404).json({ message: "Trip not found" });
        res.status(200).json({ message: "Trip deleted successfully", deletedTrip });
    } catch (err) {
        console.error("Error deleting trip:", err.message);
        res.status(500).json({ message: "Unable to delete trip" });
    }
};

module.exports = {
    tripsList,
    tripsFindByCode,
    tripsAddTrip,
    tripsUpdateTrip,
    tripsDeleteTrip
};
