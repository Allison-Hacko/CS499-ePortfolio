require('dotenv').config();

const mongoose = require('mongoose');
const User = require('../app_api/models/user');

async function main() {
    const email = String(process.argv[2] || '').trim().toLowerCase();

    if (!email || !email.includes('@')) {
        console.error('Usage: node scripts/promote-admin.js existing-account@example.com');
        process.exitCode = 1;
        return;
    }

    const host = process.env.DB_HOST || '127.0.0.1';
    await mongoose.connect(`mongodb://${host}/travlr`);

    try {
        const user = await User.findOne({ email }).exec();

        if (!user) {
            console.error('No account found for that email. Register the account first.');
            process.exitCode = 1;
            return;
        }

        user.role = 'admin';
        await user.save();

        console.log(`Administrator role assigned to ${user.email}.`);
        console.log('Sign in again to receive a token with the updated role.');
    } finally {
        await mongoose.disconnect();
    }
}

main().catch(error => {
    console.error('Admin promotion failed:', error.message);
    process.exitCode = 1;
});
