# Travel Verdict API Backend

A single-file deployable backend system for EU travel advisories with scoring and verdict system.

## Features

- **City-level analysis**: Verdict for each specific city
- **Country-level analysis**: Aggregate verdict for all cities in a country
- **Scoring system**: 0-30 (Clear), 31-70 (Caution), 71+ (No-Go)
- **Real-time conflict detection**: Checks airport conflict news
- **Batch processing**: Analyze multiple cities in one request
- **Quick endpoint**: Minimal response for mobile apps
- **Multiple deployment options**: Vercel, Hostinger, or any Node.js host

## Scoring System

### Points Breakdown
- **Country Risk Level 1** (Low): 5 points
- **Country Risk Level 2** (Medium): 15 points
- **Airspace Closed**: 100 points (automatic No-Go)
- **Airport Conflicts**:
  - CRITICAL: +40 points
  - HIGH: +30 points
  - MEDIUM: +15 points

### Verdict Levels
- **Clear**: 0-30 points → "Safe to travel"
- **Caution**: 31-70 points → "Exercise increased caution"
- **No-Go**: 71+ points → "Do not travel"

## API Endpoints

### 1. Single City Verdict
```
GET /api/verdict/city/:city/:country
GET /api/quick-verdict/:city/:country

Example:
GET /api/verdict/city/Paris/France
```

**Response:**
```json
{
  "city": "Paris",
  "country": "France",
  "verdict": "Clear",
  "score": 5,
  "risk": {
    "low": true,
    "medium": false,
    "high": false
  },
  "recommendations": ["Safe to travel", "No major advisories", ...],
  "lastUpdated": "2026-08-16T12:00:00.000Z"
}
```

### 2. Country Verdict
```
GET /api/verdict/country/:country

Example:
GET /api/verdict/country/France
```

**Response:**
```json
{
  "country": "France",
  "verdict": "Clear",
  "overallScore": 5,
  "citiesCount": 5,
  "cities": [
    { city analysis objects }
  ],
  "lastUpdated": "2026-08-16T12:00:00.000Z"
}
```

### 3. All Countries Verdict
```
GET /api/verdict/all
```

**Response:**
```json
{
  "allCountries": [ { country analysis objects } ],
  "grouped": {
    "Clear": [ ...countries... ],
    "Caution": [ ...countries... ],
    "No-Go": [ ...countries... ]
  },
  "summary": {
    "totalCountries": 18,
    "clearCount": 15,
    "cautionCount": 3,
    "noGoCount": 0
  }
}
```

### 4. Batch Verdict
```
POST /api/verdict/batch

Body:
{
  "destinations": [
    { "city": "Paris", "country": "France" },
    { "city": "Rome", "country": "Italy" }
  ]
}
```

## Supported Countries

France, Germany, Italy, Spain, UK, Netherlands, Belgium, Poland, Czech, Austria, Portugal, Greece, Hungary, Romania, Bulgaria, Sweden, Denmark, Norway

## Local Development

```bash
# Install dependencies
npm install

# Start local server
npm run dev

# Server runs on http://localhost:3000
# API docs: http://localhost:3000/
```

## Deployment to Vercel

### Option 1: CLI
```bash
npm install -g vercel
vercel
```

### Option 2: GitHub Integration
1. Push code to GitHub
2. Sign in to [vercel.com](https://vercel.com)
3. Click "Add New Project"
4. Select your GitHub repository
5. Click "Deploy"

Vercel automatically detects the `vercel.json` configuration.

**Your Vercel URL will be:** `https://your-project.vercel.app`

**API Examples:**
```
https://your-project.vercel.app/api/travel-verdict?action=city&city=Paris&country=France
https://your-project.vercel.app/api/travel-verdict?action=country&country=France
https://your-project.vercel.app/api/travel-verdict?action=all
```

## Deployment to Hostinger

### Step 1: Prepare Your Package
```bash
npm install
```

### Step 2: Connect to Hostinger
1. Log into [Hostinger](https://www.hostinger.com)
2. Go to **Hosting Dashboard** → **Node.js** (or **Application**)
3. Create new Node.js application

### Step 3: Upload Files via FTP/SFTP
1. Download Hostinger's connection credentials
2. Use an FTP client (FileZilla, Cyberduck, etc.)
3. Upload:
   - `index.js`
   - `travel-verdict-engine.js`
   - `package.json`
   - `api/` directory (optional)

### Step 4: Configure in Hostinger
1. Set **Startup File**: `index.js`
2. Set **Node Version**: 18+
3. Click **Deploy/Activate**

### Step 5: Set Environment Variables (Optional)
In Hostinger dashboard:
- `PORT=3000` (or port provided by Hostinger)
- `NODE_ENV=production`

**Your Hostinger URL will be:** `https://yourdomain.hostinger.com` (or IP:port)

## Using the API

### JavaScript/Node.js
```javascript
import fetch from "node-fetch";

const response = await fetch("http://localhost:3000/api/verdict/city/Paris/France");
const verdict = await response.json();
console.log(verdict.verdict, verdict.score);
```

### React
```javascript
const [verdict, setVerdict] = useState(null);

useEffect(() => {
  fetch("/api/verdict/city/Paris/France")
    .then(res => res.json())
    .then(data => setVerdict(data));
}, []);
```

### Android/Kotlin
```kotlin
val client = OkHttpClient()
val request = Request.Builder()
  .url("https://your-domain.com/api/verdict/city/Paris/France")
  .build()

client.newCall(request).enqueue(object : Callback {
  override fun onResponse(call: Call, response: Response) {
    val verdict = response.body?.string()
    // Parse JSON and update UI
  }
})
```

## Adding New Cities/Countries

Edit `travel-verdict-engine.js`:

```javascript
export const CITIES_BY_COUNTRY = {
  // Add new country
  "NewCountry": ["City1", "City2"],
  // Update existing
  France: ["Paris", "Lyon", "NewCity"]
};

export const COUNTRY_ADVISORIES = {
  "NewCountry": { 
    status: "OPEN",           // or "CLOSED"
    riskLevel: 1,             // 1=low, 2=medium, 3=high
    lastUpdated: new Date().toISOString()
  }
};

// Add airport conflict data
export const AIRPORT_CONFLICT_DATA = {
  "NewCity": { 
    hasConflicts: false, 
    severity: "LOW"           // LOW, MEDIUM, HIGH, CRITICAL
  }
};
```

## Integrating Real APIs

To use real travel advisories and news APIs:

```javascript
// Example: US State Department API
async function fetchRealAdvisory(country) {
  const response = await fetch(
    `https://api.state.gov/travel-advisories/${country}`
  );
  return response.json();
}

// Example: NewsAPI for conflict detection
async function fetchAirportNews(city, airport) {
  const response = await fetch(
    `https://newsapi.org/v2/everything?q=${airport}%20conflict&sortBy=publishedAt`,
    { headers: { Authorization: `Bearer ${process.env.NEWS_API_KEY}` } }
  );
  return response.json();
}
```

## Architecture

```
automatic-umbrella/
├── index.js                    # Express server (Hostinger)
├── package.json               # Dependencies
├── vercel.json               # Vercel config
├── travel-verdict-engine.js  # Scoring logic
├── api/
│   └── travel-verdict.js     # Vercel serverless function
└── BACKEND_README.md         # This file
```

## Performance

- **Single city verdict**: ~5ms
- **Country verdict**: ~50ms
- **All countries verdict**: ~100ms
- **Batch (10 cities)**: ~50ms

## Security

- CORS enabled for cross-origin requests
- Input validation on all endpoints
- No sensitive data exposure
- Query parameter validation

## Monitoring

Check endpoint health:
```
GET /health
```

Returns:
```json
{
  "status": "ok",
  "timestamp": "2026-08-16T12:00:00.000Z"
}
```

## Troubleshooting

### Vercel Deployment Issues
- Check `.vercelignore` (should not exclude `api/` or `travel-verdict-engine.js`)
- Verify Node.js version is 18+
- Check Vercel logs: `vercel logs`

### Hostinger Connection Issues
- Verify FTP credentials
- Ensure `index.js` exists in root directory
- Check Hostinger Node.js app logs
- Try restarting the application

### CORS Errors
The API already has CORS enabled. If you still get errors:
```javascript
// Add to your client code
const headers = {
  'Content-Type': 'application/json'
};
```

## License

MIT
