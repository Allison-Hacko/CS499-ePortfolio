const apiBase = "http://localhost:3000/api";

function priceNumber(value) {
    const cleaned = String(value ?? "").replace(/[^0-9.]/g, "");
    const number = Number(cleaned);
    return Number.isFinite(number) ? number : null;
}

function prepareTrip(trip) {
    const image = String(trip.image || "").replace(/^\/+/, "");
    trip.imageUrl = image.startsWith("images/")
        ? "/" + image
        : "/images/" + image;

    const start = new Date(trip.start);
    trip.startDate = Number.isNaN(start.getTime())
        ? "Date to be announced"
        : start.toLocaleDateString("en-US", {
            month: "long",
            day: "numeric",
            year: "numeric",
            timeZone: "UTC"
        });

    trip.priceNumber = priceNumber(trip.perPerson);
    return trip;
}

const travel = async (req, res) => {
    try {
        const searchTerm = String(req.query.search || "").trim();
        const minRaw = String(req.query.minPrice || "").trim();
        const maxRaw = String(req.query.maxPrice || "").trim();
        const minPrice = minRaw === "" ? null : Number(minRaw);
        const maxPrice = maxRaw === "" ? null : Number(maxRaw);

        if (
            (minPrice !== null && (!Number.isFinite(minPrice) || minPrice < 0)) ||
            (maxPrice !== null && (!Number.isFinite(maxPrice) || maxPrice < 0)) ||
            (minPrice !== null && maxPrice !== null && minPrice > maxPrice)
        ) {
            return res.status(400).send(
                "Please enter valid prices. The minimum price cannot exceed the maximum."
            );
        }

        const response = await fetch(`${apiBase}/trips`);
        if (!response.ok) throw new Error("Trip API request failed");

        let trips = await response.json();

        if (searchTerm) {
            const escaped = searchTerm.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
            const pattern = new RegExp(escaped, "i");
            trips = trips.filter(trip =>
                pattern.test(trip.name || "") ||
                pattern.test(trip.resort || "") ||
                pattern.test(trip.description || "")
            );
        }

        if (minPrice !== null || maxPrice !== null) {
            trips = trips.filter(trip => {
                const price = priceNumber(trip.perPerson);
                if (price === null) return false;
                if (minPrice !== null && price < minPrice) return false;
                if (maxPrice !== null && price > maxPrice) return false;
                return true;
            });
        }

        trips.sort((a, b) => (a.name || "").localeCompare(b.name || ""));
        trips = trips.map(prepareTrip);

        res.render("travel", {
            title: "Explore Our Getaways | Travlr Getaways",
            trips,
            searchTerm,
            minPrice: minRaw,
            maxPrice: maxRaw,
            hasFilters: Boolean(searchTerm || minRaw || maxRaw),
            noResults: trips.length === 0
        });
    } catch (err) {
        console.error("Error retrieving trips:", err.message);
        res.status(500).send(
            "Unable to retrieve travel information. Please try again later."
        );
    }
};

const tripDetails = async (req, res) => {
    try {
        const code = encodeURIComponent(req.params.tripCode);
        const response = await fetch(`${apiBase}/trips/${code}`);

        if (response.status === 404) {
            return res.status(404).send("Sorry, that trip could not be found.");
        }
        if (!response.ok) throw new Error("Trip API request failed");

        const trip = prepareTrip(await response.json());

        res.render("trip-details", {
            title: `${trip.name || "Trip Details"} | Travlr Getaways`,
            trip
        });
    } catch (err) {
        console.error("Error retrieving trip details:", err.message);
        res.status(500).send(
            "Unable to retrieve this trip. Please try again later."
        );
    }
};

module.exports = { travel, tripDetails };
