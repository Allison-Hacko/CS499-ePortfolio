const fs = require('fs');
const mongoose = require('mongoose');
const Trip = require('./travlr');

// Read the trips.json file
const trips = JSON.parse(
    fs.readFileSync('./data/trips.json', 'utf8')
);

// Connect to MongoDB
mongoose.connect('mongodb://127.0.0.1/travlr')
    .then(async () => {
        console.log('Connected to MongoDB');

        // Remove existing trips
        await Trip.deleteMany({});

        // Insert trips from trips.json
        await Trip.insertMany(trips);

        console.log('Database seeded successfully');

        // Close the connection
        await mongoose.connection.close();

        console.log('MongoDB connection closed');
    })
    .catch(err => {
        console.error('Error seeding database:', err);
        process.exit(1);
    });