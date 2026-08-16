// Vercel Serverless Function for Travel Verdict API
import engine, { CITIES_BY_COUNTRY } from "../travel-verdict-engine.js";

export default function handler(req, res) {
  // Enable CORS
  res.setHeader("Access-Control-Allow-Credentials", "true");
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET,OPTIONS,PATCH,DELETE,POST,PUT");
  res.setHeader(
    "Access-Control-Allow-Headers",
    "X-CSRF-Token, X-Requested-With, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version"
  );

  if (req.method === "OPTIONS") {
    res.status(200).end();
    return;
  }

  try {
    const { action, city, country, destinations } = req.query;

    // Route 1: Single city verdict
    if (action === "city" && city && country) {
      const result = engine.analyzeCityTravel(city, country);
      return res.status(200).json(result);
    }

    // Route 2: Quick verdict (minimal)
    if (action === "quick" && city && country) {
      const analysis = engine.analyzeCityTravel(city, country);
      return res.status(200).json({
        city: analysis.city,
        country: analysis.country,
        verdict: analysis.verdict,
        score: analysis.score
      });
    }

    // Route 3: Country verdict
    if (action === "country" && country) {
      const result = engine.analyzeCountryTravel(country);
      return res.status(200).json(result);
    }

    // Route 4: All countries
    if (action === "all") {
      const result = engine.analyzeAllCountries();
      return res.status(200).json(result);
    }

    // Route 5: Batch (POST)
    if (req.method === "POST" && action === "batch") {
      const batchDestinations = req.body?.destinations || [];
      if (!Array.isArray(batchDestinations)) {
        return res.status(400).json({ error: "destinations must be an array" });
      }

      const results = batchDestinations.map(({ city, country }) =>
        engine.analyzeCityTravel(city, country)
      );

      return res.status(200).json({
        count: results.length,
        results,
        timestamp: new Date().toISOString()
      });
    }

    // Default: API documentation
    return res.status(200).json({
      name: "Travel Verdict API",
      version: "1.0.0",
      description: "EU Travel Advisory System with Scoring",
      usage: {
        singleCity: "/api/travel-verdict?action=city&city=Paris&country=France",
        quickVerdict: "/api/travel-verdict?action=quick&city=Paris&country=France",
        country: "/api/travel-verdict?action=country&country=France",
        allCountries: "/api/travel-verdict?action=all",
        batch: "POST to /api/travel-verdict with action=batch and body: {destinations: [{city, country}]}"
      },
      verdictLevels: {
        Clear: "0-30 points (Safe to travel)",
        Caution: "31-70 points (Exercise increased caution)",
        "No-Go": "71+ points (Do not travel)"
      },
      supportedCountries: Object.keys(CITIES_BY_COUNTRY)
    });
  } catch (error) {
    return res.status(500).json({ error: error.message });
  }
}
