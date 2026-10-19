require('dotenv').config();
const mongoose = require('mongoose');
const Trip = require('../app_api/models/travlr');

async function main() {
    const host = process.env.DB_HOST || '127.0.0.1';
    await mongoose.connect(`mongodb://${host}/travlr`);

    try {
        const result = await Trip.deleteOne({ code: 'SECURITYTEST' });
        console.log(`Temporary SECURITYTEST trips removed: ${result.deletedCount}`);
    } finally {
        await mongoose.disconnect();
    }
}

main().catch(err => {
    console.error(err.message);
    process.exitCode = 1;
});
