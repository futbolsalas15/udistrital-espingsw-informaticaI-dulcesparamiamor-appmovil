package co.edu.udistrital.dulcesparamiamor.mensajeria;


import android.app.Activity;
import android.net.Uri;
import android.os.Environment;

import com.facebook.FacebookSdk;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.ShareToMessengerParams;

import co.edu.udistrital.dulcesparamiamor.R;

/**
 * Created by Oscar on 10/04/2016.
 */
public class SevicioMensajeria{


    public static void sendFacebookMessage(Activity activity){
        // The URI can reference a file://, content://, or android.resource. Here we use
        // android.resource for sample purposes.
        Uri uri =
                Uri.parse("android.resource://co.edu.udistrital.dulcesparamiamor.mensajeria/" + R.drawable.tree);

        // Create the parameters for what we want to send to Messenger.
        ShareToMessengerParams shareToMessengerParams =
                ShareToMessengerParams.newBuilder(uri, "image/jpeg")
                        .setMetaData("{ \"image\" : \"tree\" }")
                        .build();

        MessengerUtils.shareToMessenger(
                activity,
                1,
                shareToMessengerParams);
            MessengerUtils.finishShareToMessenger(activity, shareToMessengerParams);

    }
}
