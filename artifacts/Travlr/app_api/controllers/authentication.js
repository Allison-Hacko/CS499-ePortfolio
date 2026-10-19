const User = require('../models/user');

const register = async (req, res) => {
    try {
        const name = req.body.name;
        const email = req.body.email;
        const password = req.body.password;

        if (
            typeof name !== 'string' || !name.trim() ||
            typeof email !== 'string' || !email.trim() ||
            typeof password !== 'string' || password.length < 8
        ) {
            return res.status(400).json({
                message: 'Name and email are required. Password must be at least 8 characters.'
            });
        }

        const normalizedEmail = email.trim().toLowerCase();
        const existingUser = await User.findOne({
            email: normalizedEmail
        }).exec();

        if (existingUser) {
            return res.status(409).json({
                message: 'An account with that email already exists.'
            });
        }

        const user = new User({
            name: name.trim(),
            email: normalizedEmail
        });

        user.setPassword(password);
        await user.save();

        return res.status(201).json({
            token: user.generateJWT(),
            user: {
                name: user.name,
                email: user.email
            }
        });
    } catch (err) {
        if (err.code === 11000) {
            return res.status(409).json({
                message: 'An account with that email already exists.'
            });
        }

        console.error('Registration error:', err.message);
        return res.status(500).json({
            message: 'Unable to register account.'
        });
    }
};

const login = (req, res) => {
    if (!req.user) {
        return res.status(401).json({
            message: 'Invalid email or password.'
        });
    }

    return res.status(200).json({
        token: req.user.generateJWT(),
        user: {
            name: req.user.name,
            email: req.user.email
        }
    });
};

module.exports = {
    register,
    login
};
