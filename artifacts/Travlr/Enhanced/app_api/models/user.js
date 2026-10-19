const mongoose = require('mongoose');
const crypto = require('crypto');
const jwt = require('jsonwebtoken');

const userSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true,
        trim: true
    },
    email: {
        type: String,
        required: true,
        unique: true,
        lowercase: true,
        trim: true
    },
    role: {
        type: String,
        enum: ['user', 'admin'],
        default: 'user',
        required: true
    },
    hash: {
        type: String,
        required: true
    },
    salt: {
        type: String,
        required: true
    }
});

userSchema.methods.setPassword = function (password) {
    this.salt = crypto.randomBytes(16).toString('hex');
    this.hash = crypto.pbkdf2Sync(password, this.salt, 100000, 64, 'sha512').toString('hex');
};

userSchema.methods.validPassword = function (password) {
    const hash = crypto.pbkdf2Sync(password, this.salt, 100000, 64, 'sha512');
    const storedHash = Buffer.from(this.hash, 'hex');

    return storedHash.length === hash.length &&
        crypto.timingSafeEqual(storedHash, hash);
};

userSchema.methods.generateJWT = function () {
    if (!process.env.JWT_SECRET) {
        throw new Error('JWT_SECRET is not configured');
    }

    return jwt.sign(
        {
            _id: this._id.toString(),
            email: this.email,
            name: this.name,
            role: this.role
        },
        process.env.JWT_SECRET,
        { expiresIn: '1h' }
    );
};

module.exports = mongoose.model('User', userSchema);
