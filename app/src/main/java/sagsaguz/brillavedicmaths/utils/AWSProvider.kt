package sagsaguz.brillavedicmaths.utils

import android.content.Context

import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions

class AWSProvider {

    fun getCredentialsProvider(context: Context): CognitoCachingCredentialsProvider {
        return CognitoCachingCredentialsProvider(
                context,
                "identity pool id",
                Regions.AP_SOUTH_1
        )
    }

}
