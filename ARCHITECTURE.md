# Architecture Overview

## System Design

```
┌─────────────────────────────────────────────────────────────────┐
│                      Travel Verdict System                       │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────┐
│                    Android Application                        │
│  ┌────────────────────────────────────────────────────────┐  │
│  │         Compose UI (BookingScreen)                    │  │
│  │  - City Selection                                     │  │
│  │  - Verdict Display (Color-coded)                      │  │
│  │  - Risk Score Visualization                           │  │
│  └────────────────────────────────────────────────────────┘  │
│                           ↓                                    │
│  ┌────────────────────────────────────────────────────────┐  │
│  │    TravelVerdictViewModel (Coroutines)                │  │
│  │  - State management                                   │  │
│  │  - Loading/Error handling                             │  │
│  └────────────────────────────────────────────────────────┘  │
│                           ↓                                    │
│  ┌────────────────────────────────────────────────────────┐  │
│  │  Retrofit HTTP Client                                 │  │
│  │  - TravelVerdictService interface                     │  │
│  │  - Auto JSON serialization (Gson)                     │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
                           ↓ HTTP
                    ┌──────────────┐
                    │   Internet   │
                    └──────────────┘
                           ↓
┌──────────────────────────────────────────────────────────────┐
│                   Backend API (Travel Verdict)                │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Express.js Server (Node.js)                  │   │
│  │         OR                                           │   │
│  │    Vercel Serverless Functions                      │   │
│  └──────────────────────────────────────────────────────┘   │
│                           ↓                                   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  API Routes (index.js / api/travel-verdict.js)       │   │
│  │                                                      │   │
│  │  GET /api/verdict/city/:city/:country               │   │
│  │  GET /api/verdict/country/:country                  │   │
│  │  GET /api/verdict/all                               │   │
│  │  POST /api/verdict/batch                            │   │
│  │  GET /api/quick-verdict/:city/:country              │   │
│  │  GET /health                                        │   │
│  └──────────────────────────────────────────────────────┘   │
│                           ↓                                   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │   TravelVerdictEngine (travel-verdict-engine.js)    │   │
│  │                                                      │   │
│  │  ┌──────────────────────────────────────────────┐  │   │
│  │  │  Data Structure                             │  │   │
│  │  │  - CITIES_BY_COUNTRY                        │  │   │
│  │  │  - COUNTRY_ADVISORIES                       │  │   │
│  │  │  - AIRPORT_CONFLICT_DATA                    │  │   │
│  │  └──────────────────────────────────────────────┘  │   │
│  │                      ↓                              │   │
│  │  ┌──────────────────────────────────────────────┐  │   │
│  │  │  Scoring Engine                             │  │   │
│  │  │                                              │  │   │
│  │  │  Score = Country Risk + Airport Conflicts   │  │   │
│  │  │                                              │  │   │
│  │  │  Country Risk:                              │  │   │
│  │  │    - Low (1): 5 points                      │  │   │
│  │  │    - Medium (2): 15 points                  │  │   │
│  │  │    - High (3): 35 points                    │  │   │
│  │  │                                              │  │   │
│  │  │  Airport Conflicts:                         │  │   │
│  │  │    - CRITICAL: +40 points                   │  │   │
│  │  │    - HIGH: +30 points                       │  │   │
│  │  │    - MEDIUM: +15 points                     │  │   │
│  │  │    - Airspace Closed: 100 points (No-Go)   │  │   │
│  │  └──────────────────────────────────────────────┘  │   │
│  │                      ↓                              │   │
│  │  ┌──────────────────────────────────────────────┐  │   │
│  │  │  Verdict Decision Tree                      │  │   │
│  │  │                                              │  │   │
│  │  │  Score: 0-30   → "Clear"                    │  │   │
│  │  │  Score: 31-70  → "Caution"                  │  │   │
│  │  │  Score: 71+    → "No-Go"                    │  │   │
│  │  │                                              │  │   │
│  │  │  + Risk Level Details                       │  │   │
│  │  │  + Recommendations                          │  │   │
│  │  │  + Last Updated Timestamp                   │  │   │
│  │  └──────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
```

## Data Flow Example

### User Action: Check Paris Verdict

```
1. User opens app
   └─> BookingScreen displays
       └─> User selects destination: Paris, France

2. Android clicks "CHECK VERDICT"
   └─> ViewModel.getCityVerdict("Paris", "France")
       └─> Retrofit HTTP GET request
           └─> https://api.example.com/api/verdict/city/Paris/France

3. Backend Express Server receives request
   └─> Route handler for /api/verdict/city/:city/:country
       └─> Calls engine.analyzeCityTravel("Paris", "France")
           ├─> Looks up Country: France
           │   └─> Gets advisory: OPEN, riskLevel: 1
           │       └─> Score += 5 points
           │
           ├─> Looks up City: Paris
           │   └─> Checks AIRPORT_CONFLICT_DATA["Paris"]
           │       └─> Not found (no conflicts)
           │           └─> Score += 0 points
           │
           └─> Total Score: 5
               └─> Verdict: "Clear" (0-30 range)
                   └─> Recommendations: ["Safe to travel", ...]

4. Backend returns JSON response
   ├─> {
   │   "city": "Paris",
   │   "country": "France",
   │   "verdict": "Clear",
   │   "score": 5,
   │   "risk": { "low": true, "medium": false, "high": false },
   │   "recommendations": [...],
   │   "lastUpdated": "2026-08-16T12:00:00Z"
   │  }
   └─> HTTP 200 OK

5. Android receives response
   └─> Retrofit deserializes to CityVerdict object
       └─> ViewModel updates StateFlow
           └─> Compose UI recomposes
               └─> VerdictCard displays:
                   ├─> City: Paris, France
                   ├─> Verdict: Clear (Green)
                   ├─> Score: 5/100
                   └─> Recommendations: [...]
```

## Deployment Architectures

### Architecture A: Vercel Serverless

```
GitHub Repository
       ↓
   Vercel CLI or Web
       ↓
  Vercel Deployment
       ↓
  ┌─────────────────────────────┐
  │  Vercel Serverless Edge     │
  │  - Automatic scaling        │
  │  - Global distribution      │
  │  - api/travel-verdict.js    │
  └─────────────────────────────┘
       ↓
   https://your-project.vercel.app
```

### Architecture B: Hostinger Standard Node.js

```
Local Repository
       ↓
   FTP Upload
       ↓
  ┌─────────────────────────────┐
  │  Hostinger Node.js App      │
  │  - Standard process         │
  │  - index.js                 │
  │  - Always-on deployment     │
  └─────────────────────────────┘
       ↓
   https://yourdomain.com
```

### Architecture C: Local Development

```
Local Machine
       ↓
  npm install
       ↓
  npm run dev
       ↓
  ┌─────────────────────────────┐
  │  Express Server (Port 3000) │
  │  - index.js                 │
  │  - Hot reload capable       │
  │  - Full debugging           │
  └─────────────────────────────┘
       ↓
   http://localhost:3000
```

## File Structure

```
automatic-umbrella/
│
├── Backend Core
│   ├── index.js                    (Express server)
│   ├── travel-verdict-engine.js    (Scoring logic)
│   └── package.json                (Dependencies)
│
├── Serverless (Vercel)
│   └── api/
│       └── travel-verdict.js       (Serverless function)
│
├── Configuration
│   ├── vercel.json                 (Vercel config)
│   └── .env.example                (Environment template)
│
├── Documentation
│   ├── BACKEND_README.md           (Complete guide)
│   ├── ANDROID_INTEGRATION.md      (Android setup)
│   ├── QUICKSTART.md               (5-minute start)
│   └── ARCHITECTURE.md             (This file)
│
└── Android App
    ├── MainActivity.kt             (Existing)
    ├── api/TravelVerdictService.kt (New)
    ├── models/*.kt                 (New)
    └── viewmodel/*.kt              (New)
```

## Performance Characteristics

| Operation | Time | Notes |
|-----------|------|-------|
| Single city verdict | ~5ms | In-memory calculation |
| Country verdict | ~50ms | 5 cities × ~10ms each |
| All countries | ~100ms | 18 countries × ~5ms each |
| Batch (10 cities) | ~50ms | Parallel processing |
| Network latency | ~50-200ms | Varies by location |

## Scaling Strategy

### Current (Phase 1)
- In-memory data
- Suitable for: <1,000 requests/day
- All endpoints on single instance

### Phase 2: Add Caching
```javascript
// Redis cache for verdicts
const cachedVerdict = await redis.get(`verdict:Paris:France`);
if (cachedVerdict) return JSON.parse(cachedVerdict);
```

### Phase 3: Add Database
```javascript
// PostgreSQL for historical tracking
const history = await db.query(
  `SELECT * FROM verdicts WHERE city=$1 AND date >= NOW()-7d`,
  [city]
);
```

### Phase 4: Real Data Integration
```javascript
// Fetch from government APIs
const advisory = await fetchStateDepAvisory(country);
const news = await fetchAirportNews(city);
const score = calculateScore(advisory, news);
```

## API Response Times by Region

```
┌─────────────────────────────────────────┐
│  Vercel Global Distribution             │
│  ┌──────────────────────────────────┐   │
│  │  US East:      ~20ms to user    │   │
│  │  Europe:       ~15ms to user    │   │
│  │  Asia:         ~80ms to user    │   │
│  │  Backend:      <5ms calculation │   │
│  └──────────────────────────────────┘   │
│  Total: 20-85ms end-to-end              │
└─────────────────────────────────────────┘
```

## Security Considerations

```
┌──────────────────────────────────────┐
│  Input Validation                    │
├──────────────────────────────────────┤
│  ✓ City name validation              │
│  ✓ Country name validation           │
│  ✓ Query parameter limits            │
│  ✓ Request size limits               │
└──────────────────────────────────────┘
         ↓
┌──────────────────────────────────────┐
│  Processing                          │
├──────────────────────────────────────┤
│  ✓ Sandboxed execution               │
│  ✓ No SQL injection risk             │
│  ✓ No external API keys exposed      │
│  ✓ No sensitive data in logs         │
└──────────────────────────────────────┘
         ↓
┌──────────────────────────────────────┐
│  Response                            │
├──────────────────────────────────────┤
│  ✓ CORS headers properly set         │
│  ✓ Rate limiting ready (implement)   │
│  ✓ Error messages don't leak data    │
│  ✓ HTTPS only in production          │
└──────────────────────────────────────┘
```

## Future Enhancements

1. **Real-time Data**: Connect to live travel advisory APIs
2. **Conflict Detection**: Integrate conflict tracking APIs
3. **Historical Trends**: Database to track verdict changes
4. **Push Notifications**: Alert users to verdict changes
5. **Personalization**: Save user preferences and favorites
6. **Analytics**: Track which cities are queried
7. **Webhooks**: Notify apps when advisories change
8. **GraphQL API**: Alternative to REST endpoints
