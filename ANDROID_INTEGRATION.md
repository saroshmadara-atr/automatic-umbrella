# Android Integration Guide

How to integrate the Travel Verdict API with your Android Compose app.

## Backend Setup

1. Deploy the backend using Vercel or Hostinger (see `BACKEND_README.md`)
2. Get your API URL:
   - **Vercel**: `https://your-project.vercel.app`
   - **Hostinger**: `https://yourdomain.com` or `http://ip:port`

## Android Implementation

### Step 1: Add Dependencies

In `build.gradle.kts`:

```kotlin
dependencies {
    // Retrofit for HTTP requests
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // OkHttp for HTTP client
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    
    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
}
```

### Step 2: Create Data Classes

Create `TravelVerdictModels.kt`:

```kotlin
package com.entry.exit.system.eu.europe.models

import com.google.gson.annotations.SerializedName

data class CityVerdict(
    val city: String,
    val country: String,
    val verdict: String,  // "Clear", "Caution", "No-Go"
    val score: Int,
    val risk: RiskLevel,
    val recommendations: List<String>,
    val lastUpdated: String
)

data class RiskLevel(
    val low: Boolean,
    val medium: Boolean,
    val high: Boolean
)

data class CountryVerdict(
    val country: String,
    val verdict: String,
    val overallScore: Int,
    val citiesCount: Int,
    val cities: List<CityVerdict>,
    val lastUpdated: String
)

data class AllCountriesVerdict(
    val summary: Summary,
    val grouped: GroupedVerdicts,
    val allCountries: List<CountryVerdict>,
    val timestamp: String
)

data class Summary(
    val totalCountries: Int,
    val clearCount: Int,
    val cautionCount: Int,
    val noGoCount: Int
)

data class GroupedVerdicts(
    @SerializedName("Clear")
    val clear: List<CountryVerdict>,
    @SerializedName("Caution")
    val caution: List<CountryVerdict>,
    @SerializedName("No-Go")
    val noGo: List<CountryVerdict>
)

data class BatchRequest(
    val destinations: List<Destination>
)

data class Destination(
    val city: String,
    val country: String
)

data class BatchResponse(
    val count: Int,
    val results: List<CityVerdict>,
    val timestamp: String
)
```

### Step 3: Create Retrofit Service

Create `TravelVerdictService.kt`:

```kotlin
package com.entry.exit.system.eu.europe.api

import com.entry.exit.system.eu.europe.models.*
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path

interface TravelVerdictService {
    
    @GET("api/verdict/city/{city}/{country}")
    suspend fun getCityVerdict(
        @Path("city") city: String,
        @Path("country") country: String
    ): CityVerdict

    @GET("api/quick-verdict/{city}/{country}")
    suspend fun getQuickVerdict(
        @Path("city") city: String,
        @Path("country") country: String
    ): CityVerdict

    @GET("api/verdict/country/{country}")
    suspend fun getCountryVerdict(
        @Path("country") country: String
    ): CountryVerdict

    @GET("api/verdict/all")
    suspend fun getAllVerdicts(): AllCountriesVerdict

    @POST("api/verdict/batch")
    suspend fun getBatchVerdicts(
        @Body request: BatchRequest
    ): BatchResponse

    @GET("health")
    suspend fun healthCheck(): Map<String, String>
}
```

### Step 4: Create Retrofit Client

Create `RetrofitClient.kt`:

```kotlin
package com.entry.exit.system.eu.europe.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://your-project.vercel.app/"  // Change to your URL
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    val service: TravelVerdictService = retrofit.create(TravelVerdictService::class.java)
}
```

### Step 5: Create ViewModel

Create `TravelVerdictViewModel.kt`:

```kotlin
package com.entry.exit.system.eu.europe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entry.exit.system.eu.europe.api.RetrofitClient
import com.entry.exit.system.eu.europe.models.CityVerdict
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TravelVerdictViewModel : ViewModel() {
    
    private val _cityVerdict = MutableStateFlow<CityVerdict?>(null)
    val cityVerdict: StateFlow<CityVerdict?> = _cityVerdict

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getCityVerdict(city: String, country: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val verdict = RetrofitClient.service.getCityVerdict(city, country)
                _cityVerdict.value = verdict
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllCountries() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                RetrofitClient.service.getAllVerdicts()
                // Handle the response as needed
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
```

### Step 6: Update Compose UI

Update your `BookingScreen.kt` to use the API:

```kotlin
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState

@Composable
fun BookingScreen(modifier: Modifier = Modifier) {
    val viewModel: TravelVerdictViewModel = viewModel()
    val verdict by viewModel.cityVerdict.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.size(48.dp))
    } else if (error != null) {
        Text("Error: $error", color = Color.Red)
    } else if (verdict != null) {
        // Display verdict details
        VerdictCard(verdict = verdict!!)
    } else {
        // Original UI with buttons
        Column(/* ... */) {
            SelectionButton(
                text = "YES",
                onClick = {
                    // Fetch verdict for selected city/country
                    viewModel.getCityVerdict("Paris", "France")
                }
            )
            SelectionButton(
                text = "NO",
                onClick = {
                    // Handle NO action
                }
            )
        }
    }
}

@Composable
fun VerdictCard(verdict: CityVerdict) {
    val verdictColor = when (verdict.verdict) {
        "Clear" -> Color.Green
        "Caution" -> Color.Yellow
        "No-Go" -> Color.Red
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = verdictColor.copy(alpha = 0.1f),
        border = BorderStroke(2.dp, verdictColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${verdict.city}, ${verdict.country}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Verdict: ${verdict.verdict}",
                fontSize = 18.sp,
                color = verdictColor
            )
            Text(
                text = "Risk Score: ${verdict.score}/100",
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Recommendations:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            verdict.recommendations.forEach { rec ->
                Text("• $rec", fontSize = 12.sp)
            }
        }
    }
}
```

### Step 7: Add Permissions

In `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
</uses-permission>
```

## Testing

Test your integration:

```kotlin
// In your MainActivity or test
val viewModel = TravelVerdictViewModel()

// Test single city
viewModel.getCityVerdict("Paris", "France")

// Listen for results
lifecycleScope.launch {
    viewModel.cityVerdict.collect { verdict ->
        verdict?.let {
            println("Verdict: ${it.verdict}, Score: ${it.score}")
        }
    }
}
```

## Configuration

Update `RetrofitClient.kt` with your deployed backend URL:

```kotlin
// For Vercel
private const val BASE_URL = "https://your-project.vercel.app/"

// For Hostinger
private const val BASE_URL = "https://yourdomain.com/"

// For local development
private const val BASE_URL = "http://localhost:3000/"
```

## Error Handling

Add proper error handling in your Composables:

```kotlin
when {
    isLoading -> { /* Show loading */ }
    error != null -> { /* Show error message */ }
    verdict != null -> { /* Show verdict */ }
    else -> { /* Show default UI */ }
}
```

## Performance Tips

1. **Cache verdicts** locally using Room database
2. **Use quick-verdict endpoint** for minimal responses
3. **Batch requests** when checking multiple cities
4. **Implement retry logic** for failed requests

## Example: Batch Request

```kotlin
fun getBatchVerdicts(destinations: List<Pair<String, String>>) {
    viewModelScope.launch {
        try {
            val request = BatchRequest(
                destinations = destinations.map { (city, country) ->
                    Destination(city, country)
                }
            )
            val response = RetrofitClient.service.getBatchVerdicts(request)
            // Handle batch response
        } catch (e: Exception) {
            _error.value = e.message
        }
    }
}
```

## Troubleshooting

### "Unable to resolve host" Error
- Check internet permission in manifest
- Verify API URL is correct
- Check if backend is running

### "HTTP 404" Error
- Verify endpoint paths match
- Check city/country spelling
- Ensure backend is deployed

### Timeout Issues
- Increase timeout in `OkHttpClient` (already set to 30s)
- Check backend response time
- Try quick-verdict endpoint instead
