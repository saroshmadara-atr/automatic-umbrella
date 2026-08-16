import express from "express";
import cors from "cors";
import engine from "./travel-verdict-engine.js";

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Health check endpoint
app.get("/health", (req, res) => {
  res.json({ status: "ok", timestamp: new Date().toISOString() });
});

// Single city verdict
app.get("/api/verdict/city/:city/:country", (req, res) => {
  try {
    const { city, country } = req.params;
    const result = engine.analyzeCityTravel(city, country);
    res.json(result);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Country verdict (all cities in country)
app.get("/api/verdict/country/:country", (req, res) => {
  try {
    const { country } = req.params;
    const result = engine.analyzeCountryTravel(country);
    res.json(result);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// All countries verdict
app.get("/api/verdict/all", (req, res) => {
  try {
    const result = engine.analyzeAllCountries();
    res.json(result);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Quick verdict endpoint (minimal response)
app.get("/api/quick-verdict/:city/:country", (req, res) => {
  try {
    const { city, country } = req.params;
    const analysis = engine.analyzeCityTravel(city, country);
    res.json({
      city: analysis.city,
      country: analysis.country,
      verdict: analysis.verdict,
      score: analysis.score
    });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Batch verdict for multiple cities
app.post("/api/verdict/batch", express.json(), (req, res) => {
  try {
    const { destinations } = req.body;
    if (!Array.isArray(destinations)) {
      return res.status(400).json({ error: "destinations must be an array" });
    }

    const results = destinations.map(({ city, country }) =>
      engine.analyzeCityTravel(city, country)
    );

    res.json({
      count: results.length,
      results,
      timestamp: new Date().toISOString()
    });
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Root endpoint with API documentation
app.get("/", (req, res) => {
  res.json({
    name: "Travel Verdict API",
    version: "1.0.0",
    description: "EU Travel Advisory System with Scoring",
    endpoints: {
      health: {
        method: "GET",
        path: "/health",
        description: "Health check"
      },
      singleCity: {
        method: "GET",
        path: "/api/verdict/city/:city/:country",
        description: "Get verdict for a specific city",
        example: "/api/verdict/city/Paris/France"
      },
      quickVerdict: {
        method: "GET",
        path: "/api/quick-verdict/:city/:country",
        description: "Get minimal verdict response",
        example: "/api/quick-verdict/Paris/France"
      },
      country: {
        method: "GET",
        path: "/api/verdict/country/:country",
        description: "Get verdict for all cities in a country",
        example: "/api/verdict/country/France"
      },
      allCountries: {
        method: "GET",
        path: "/api/verdict/all",
        description: "Get verdict for all countries and cities"
      },
      batch: {
        method: "POST",
        path: "/api/verdict/batch",
        description: "Get verdicts for multiple cities",
        body: {
          destinations: [
            { city: "Paris", country: "France" },
            { city: "Rome", country: "Italy" }
          ]
        }
      }
    },
    scoring: {
      Clear: "0-30 points (Safe to travel)",
      Caution: "31-70 points (Exercise increased caution)",
      "No-Go": "71+ points (Do not travel)"
    }
  });
});

app.listen(PORT, () => {
  console.log(`Travel Verdict API running on port ${PORT}`);
  console.log(`Health check: http://localhost:${PORT}/health`);
  console.log(`API docs: http://localhost:${PORT}/`);
});
