# Quick Start Guide

Get your Travel Verdict API running in 5 minutes.

## Option 1: Deploy to Vercel (Fastest)

```bash
# 1. Install Vercel CLI
npm install -g vercel

# 2. Deploy
vercel

# 3. Open your URL
https://your-project.vercel.app/
```

**That's it!** API is live.

## Option 2: Deploy to Hostinger

1. **Upload to Hostinger via FTP:**
   - Upload: `index.js`, `travel-verdict-engine.js`, `package.json`

2. **Configure in Dashboard:**
   - Startup File: `index.js`
   - Node Version: 18+

3. **Start the app** → API is live

## Option 3: Local Testing

```bash
# Install
npm install

# Run
npm run dev

# Test in browser
http://localhost:3000/

# Try API
http://localhost:3000/api/verdict/city/Paris/France
```

## Test the API

### Browser (Quick Test)
```
http://localhost:3000/api/verdict/city/Paris/France
http://localhost:3000/api/verdict/country/France
http://localhost:3000/api/verdict/all
```

### curl
```bash
curl http://localhost:3000/api/verdict/city/Paris/France
curl http://localhost:3000/api/verdict/country/Italy
curl http://localhost:3000/api/verdict/all
```

### JavaScript
```javascript
const response = await fetch("/api/verdict/city/Paris/France");
const verdict = await response.json();
console.log(verdict.verdict); // "Clear" or "Caution" or "No-Go"
```

## Response Example

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
  "recommendations": [
    "Safe to travel",
    "No major advisories",
    "Standard travel precautions apply"
  ],
  "lastUpdated": "2026-08-16T12:00:00.000Z"
}
```

## Verdict Meanings

| Verdict | Score | Meaning |
|---------|-------|---------|
| **Clear** | 0-30 | Safe to travel |
| **Caution** | 31-70 | Exercise increased caution |
| **No-Go** | 71+ | Do not travel |

## All Endpoints

```
GET  /api/verdict/city/:city/:country
GET  /api/verdict/country/:country
GET  /api/verdict/all
GET  /api/quick-verdict/:city/:country
POST /api/verdict/batch (body: {destinations: [{city, country}]})
GET  /health
```

## Customize

### Add Cities
Edit `travel-verdict-engine.js`:
```javascript
export const CITIES_BY_COUNTRY = {
  "MyCountry": ["City1", "City2"]
};
```

### Change Scoring
Edit `travel-verdict-engine.js`:
```javascript
this.pointsPerRiskLevel = {
  1: 5,    // Change these values
  2: 15,
  3: 35
};
```

### Use Real APIs
Replace mock data with:
- Government travel advisories API
- NewsAPI for airport conflicts
- Airport status APIs

See `BACKEND_README.md` for integration examples.

## Android Integration

See `ANDROID_INTEGRATION.md` for:
- Retrofit setup
- ViewModel integration
- Compose UI examples
- Error handling

## Troubleshooting

### Vercel Deploy Failed
```bash
# Check logs
vercel logs

# Redeploy
vercel --prod
```

### Local Server Won't Start
```bash
# Check Node version
node --version  # Should be 18+

# Clear cache
rm -rf node_modules package-lock.json
npm install

# Run with debug
NODE_DEBUG=* npm run dev
```

### CORS Errors
API already has CORS enabled. If errors persist:
- Check your request headers
- Verify URL is correct
- Check browser console

## Next Steps

1. ✅ Deploy backend
2. ✅ Test API endpoints
3. → Integrate with Android app (`ANDROID_INTEGRATION.md`)
4. → Connect booking screen to verdicts
5. → Add real travel advisory data

## Support

- **Full docs**: See `BACKEND_README.md`
- **Android setup**: See `ANDROID_INTEGRATION.md`
- **Endpoint reference**: Visit `/` on your deployed API
- **Health check**: Visit `/health`

---

**Deployed URL Examples:**
- Vercel: `https://your-project.vercel.app`
- Hostinger: `https://yourdomain.com` or `http://ip:port`
- Local: `http://localhost:3000`
