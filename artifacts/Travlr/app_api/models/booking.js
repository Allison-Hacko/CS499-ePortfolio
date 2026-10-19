const mongoose = require('mongoose');

const bookingSchema = new mongoose.Schema({
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true,
        index: true
    },
    tripCode: {
        type: String,
        required: true,
        trim: true
    },
    tripName: {
        type: String,
        required: true
    },
    tripStart: {
        type: Date,
        required: true
    },
    travelers: {
        type: Number,
        required: true,
        min: 1,
        max: 10
    },
    pricePerPerson: {
        type: Number,
        required: true,
        min: 0
    },
    totalPrice: {
        type: Number,
        required: true,
        min: 0
    },
    status: {
        type: String,
        enum: ['pending', 'confirmed', 'cancelled'],
        default: 'pending',
        required: true
    }
}, {
    timestamps: true
});

module.exports = mongoose.model('Booking', bookingSchema);
