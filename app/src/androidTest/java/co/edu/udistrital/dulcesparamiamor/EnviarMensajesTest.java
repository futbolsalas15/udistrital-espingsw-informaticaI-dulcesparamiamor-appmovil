package co.edu.udistrital.dulcesparamiamor;

import android.os.Handler;
import android.os.Looper;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import co.edu.udistrital.dulcesparamiamor.presenter.messages.FacebookMessage;
import co.edu.udistrital.dulcesparamiamor.presenter.messages.IMessageSender;
import co.edu.udistrital.dulcesparamiamor.presenter.messages.MessageContent;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by futbo on 15/05/2016.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class EnviarMensajesTest {

    @Test
    public void testSendFacebookMsg(){
        MessageContent msgContent = new MessageContent();
        msgContent.setEmail("futbolsalas15@gmail.com");
        msgContent.setTextOfMsg("Unit test from DulcesParaMiAmor");
        Handler handler = new Handler(Looper.getMainLooper());
        IMessageSender msgSender = new FacebookMessage(handler);
        msgSender.sendMessage(msgContent);
        assertThat(msgSender.getSendStatus(), is(IMessageSender.STATUS.ON_PROCESS));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertThat(msgSender.getSendStatus(), is(IMessageSender.STATUS.OK));
    }


}
