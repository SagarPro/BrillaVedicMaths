package sagsaguz.brillavedicmaths.utils

import android.content.Context

import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions

class AWSProvider {

    fun getCredentialsProvider(context: Context): CognitoCachingCredentialsProvider {
        return CognitoCachingCredentialsProvider(
                context,
                "ap-south-1:7124d20c-97cf-430d-a43c-6b98dac26b02",
                Regions.AP_SOUTH_1
        )
    }

}
