const passport = require('passport');
const LocalStrategy = require('passport-local').Strategy;
const User = require('../models/user');

passport.use(new LocalStrategy(
    {
        usernameField: 'email',
        passwordField: 'password'
    },
    async (email, password, done) => {
        try {
            const user = await User.findOne({
                email: email.toLowerCase().trim()
            }).exec();

            if (!user || !user.validPassword(password)) {
                return done(null, false, {
                    message: 'Invalid email or password'
                });
            }

            return done(null, user);
        } catch (err) {
            return done(err);
        }
    }
));

module.exports = passport;
