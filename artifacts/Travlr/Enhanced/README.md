# CS-465 Travlr Getaways

## Prerequisites
- Node.js and npm
- MongoDB running locally, or an accessible MongoDB server

## Configure the environment
1. Copy `.env.example` to `.env` in the project root.
2. Replace the placeholder `JWT_SECRET` with a long, random secret.
3. Set `DB_HOST` to your MongoDB host. For a local database, use `127.0.0.1`.
4. Keep `.env` private. Never commit or share your real secret.

The application uses the `travlr` MongoDB database.

## Start the backend
From the project root, run:

```powershell
npm ci
npm start
```

The backend normally runs at `http://localhost:3000`.

## Start the Angular admin application
Open a second terminal and run:

```powershell
Set-Location app_admin
npm ci
npm start
```

The Angular development server normally runs at `http://localhost:4200`.

## Notes
- Configure the environment before starting the backend. The API requires `JWT_SECRET` for JWT authentication.
- Ensure MongoDB is running before using database-backed features.
- The `.env.example` file contains placeholders only, not production credentials.