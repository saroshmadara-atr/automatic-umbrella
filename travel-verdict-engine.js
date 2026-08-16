// Travel Verdict Scoring Engine
// Categories: Clear (0-30 points), Caution (31-70 points), No-Go (71+ points)

export const CITIES_BY_COUNTRY = {
  France: ["Paris", "Lyon", "Marseille", "Nice", "Toulouse"],
  Germany: ["Berlin", "Munich", "Frankfurt", "Hamburg", "Cologne"],
  Italy: ["Rome", "Milan", "Venice", "Florence", "Naples"],
  Spain: ["Madrid", "Barcelona", "Seville", "Valencia", "Malaga"],
  UK: ["London", "Manchester", "Liverpool", "Glasgow", "Edinburgh"],
  Netherlands: ["Amsterdam", "Rotterdam", "The Hague", "Utrecht", "Eindhoven"],
  Belgium: ["Brussels", "Antwerp", "Ghent", "Bruges", "Charleroi"],
  Poland: ["Warsaw", "Krakow", "Wroclaw", "Poznan", "Gdansk"],
  Czech: ["Prague", "Brno", "Ostrava", "Plzen", "Liberec"],
  Austria: ["Vienna", "Salzburg", "Innsbruck", "Graz", "Linz"],
  Portugal: ["Lisbon", "Porto", "Faro", "Covilha", "Braga"],
  Greece: ["Athens", "Thessaloniki", "Crete", "Rhodes", "Mykonos"],
  Hungary: ["Budapest", "Debrecen", "Szeged", "Miskolc", "Pecs"],
  Romania: ["Bucharest", "Constanta", "Brasov", "Timisoara", "Craiova"],
  Bulgaria: ["Sofia", "Plovdiv", "Varna", "Burgas", "Ruse"],
  Sweden: ["Stockholm", "Gothenburg", "Malmo", "Uppsala", "Vasteras"],
  Denmark: ["Copenhagen", "Aarhus", "Odense", "Aalborg", "Esbjerg"],
  Norway: ["Oslo", "Bergen", "Stavanger", "Trondheim", "Tromsø"]
};

export const COUNTRY_ADVISORIES = {
  France: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() },
  Germany: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() },
  Italy: { status: "OPEN", riskLevel: 2, lastUpdated: new Date().toISOString() },
  Spain: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() },
  UK: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() },
  Netherlands: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() },
  Belgium: { status: "OPEN", riskLevel: 2, lastUpdated: new Date().toISOString() },
  Poland: { status: "OPEN", riskLevel: 2, lastUpdated: new Date().toISOString() },
  Czech: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() },
  Austria: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() },
  Portugal: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() },
  Greece: { status: "OPEN", riskLevel: 2, lastUpdated: new Date().toISOString() },
  Hungary: { status: "OPEN", riskLevel: 2, lastUpdated: new Date().toISOString() },
  Romania: { status: "OPEN", riskLevel: 2, lastUpdated: new Date().toISOString() },
  Bulgaria: { status: "OPEN", riskLevel: 2, lastUpdated: new Date().toISOString() },
  Sweden: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() },
  Denmark: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() },
  Norway: { status: "OPEN", riskLevel: 1, lastUpdated: new Date().toISOString() }
};

// Airport Conflict News (indexed by city for quick lookup)
export const AIRPORT_CONFLICT_DATA = {
  Kiev: { hasConflicts: true, severity: "HIGH" },
  Moscow: { hasConflicts: true, severity: "HIGH" },
  Donetsk: { hasConflicts: true, severity: "CRITICAL" },
  Luhansk: { hasConflicts: true, severity: "CRITICAL" },
  Istanbul: { hasConflicts: false, severity: "LOW" },
  Athens: { hasConflicts: false, severity: "LOW" },
  Warsaw: { hasConflicts: false, severity: "LOW" }
};

export class TravelVerdictEngine {
  constructor() {
    this.pointsPerRiskLevel = {
      1: 5,    // Low risk: 5 points
      2: 15,   // Medium risk: 15 points
      3: 35    // High risk: 35 points
    };
  }

  calculateCityScore(city, country) {
    let score = 0;
    const advisory = COUNTRY_ADVISORIES[country];

    // Base score from country risk level
    if (advisory && advisory.status === "OPEN") {
      score += this.pointsPerRiskLevel[advisory.riskLevel] || 0;
    } else {
      // Airspace closed = automatic No-Go
      return 100;
    }

    // Check for airport-specific conflict news
    if (AIRPORT_CONFLICT_DATA[city]) {
      const conflict = AIRPORT_CONFLICT_DATA[city];
      if (conflict.hasConflicts) {
        if (conflict.severity === "CRITICAL") {
          score += 40;
        } else if (conflict.severity === "HIGH") {
          score += 30;
        } else if (conflict.severity === "MEDIUM") {
          score += 15;
        }
      }
    }

    return Math.min(score, 100);
  }

  getVerdict(score) {
    if (score <= 30) return "Clear";
    if (score <= 70) return "Caution";
    return "No-Go";
  }

  getVerdictDetails(score) {
    const verdict = this.getVerdict(score);
    return {
      verdict,
      score,
      risk: {
        low: verdict === "Clear",
        medium: verdict === "Caution",
        high: verdict === "No-Go"
      },
      recommendations: this.getRecommendations(verdict)
    };
  }

  getRecommendations(verdict) {
    switch (verdict) {
      case "Clear":
        return ["Safe to travel", "No major advisories", "Standard travel precautions apply"];
      case "Caution":
        return [
          "Exercise increased caution",
          "Monitor local conditions",
          "Register with your embassy",
          "Avoid certain areas",
          "Follow local news updates"
        ];
      case "No-Go":
        return [
          "Do not travel",
          "Airspace may be closed",
          "Conflict or security concerns",
          "Contact your embassy for updates"
        ];
      default:
        return [];
    }
  }

  analyzeCityTravel(city, country) {
    const score = this.calculateCityScore(city, country);
    const details = this.getVerdictDetails(score);

    return {
      city,
      country,
      ...details,
      lastUpdated: new Date().toISOString(),
      dataSource: "Government Travel Advisories + Airport Conflict News"
    };
  }

  analyzeCountryTravel(country) {
    const cities = CITIES_BY_COUNTRY[country] || [];
    const cityAnalyses = cities.map(city => this.analyzeCityTravel(city, country));

    // Country verdict = worst city verdict + highest score
    const worstScore = Math.max(...cityAnalyses.map(c => c.score));
    const verdict = this.getVerdict(worstScore);

    return {
      country,
      verdict,
      overallScore: worstScore,
      citiesCount: cities.length,
      cities: cityAnalyses,
      lastUpdated: new Date().toISOString()
    };
  }

  analyzeAllCountries() {
    const countries = Object.keys(CITIES_BY_COUNTRY);
    const analysis = countries.map(country => this.analyzeCountryTravel(country));

    // Group by verdict
    const grouped = {
      Clear: analysis.filter(c => c.verdict === "Clear"),
      Caution: analysis.filter(c => c.verdict === "Caution"),
      "No-Go": analysis.filter(c => c.verdict === "No-Go")
    };

    return {
      allCountries: analysis,
      grouped,
      summary: {
        totalCountries: countries.length,
        clearCount: grouped.Clear.length,
        cautionCount: grouped.Caution.length,
        noGoCount: grouped["No-Go"].length
      },
      timestamp: new Date().toISOString()
    };
  }
}

export default new TravelVerdictEngine();
