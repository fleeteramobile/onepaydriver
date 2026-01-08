

import android.content.Context
import android.location.Geocoder
import android.util.Log
import java.io.IOException
import java.util.Locale

// In your Activity or a utility class
object AddressParser {

    private const val TAG = "AddressParser"

    /**
     * Extracts the city name from a given address string using Geocoder.
     * Requires INTERNET permission.
     *
     * @param context The application context.
     * @param addressString The full address string (e.g., "Saravanampatti, bus stop, Kalavai Thottam, Saravanampatti, Coimbatore, Tamil Nadu, India").
     * @return The city name as a String, or null if not found or an error occurs.
     */
    fun getCityFromAddress(context: Context, addressString: String): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            // maxResults = 1 because we usually only need the first, most relevant result
            val addresses = geocoder.getFromLocationName(addressString, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                // The getLocality() method often returns the city name.
                // Depending on the address, you might also check getAdminArea() or getSubAdminArea()
                // for larger regions or districts, but getLocality() is typically for the city.
                return address.locality
            } else {
                Log.w(TAG, "No address found for: $addressString")
                return null
            }
        } catch (e: IOException) {
            // This can happen if there's no internet connection or Geocoder service is unavailable
            Log.e(TAG, "Geocoder service error or no network: ${e.message}", e)
            return null
        } catch (e: IllegalArgumentException) {
            // Invalid address string format
            Log.e(TAG, "Invalid address string for Geocoder: ${e.message}", e)
            return null
        }
    }

    /**
     * A simpler, less robust method for specific address formats.
     * This might work for your given examples but is prone to breaking
     * if the address format changes.
     */
    fun getCityFromAddressSimple(addressString: String): String? {
        val parts = addressString.split(",")
        // Based on your example "..., Coimbatore, Tamil Nadu, India"
        // Coimbatore is usually the second-to-last non-empty part or similar.
        // A safer bet is to trim and check the parts.
        // For "Saravanampatti, bus stop, Kalavai Thottam, Saravanampatti, Coimbatore, Tamil Nadu, India"
        // "Coimbatore" is at index 4 (0-indexed) or just before "Tamil Nadu".
        if (parts.size > 2) {
            val potentialCity = parts[parts.size - 3].trim() // Assuming "Coimbatore" is 3rd from end
            // You might want to add more checks here, e.g., if it looks like a state.
            return potentialCity
        }
        return null
    }
}
