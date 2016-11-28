package com.jobbs.jobsapp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Inzimam on 8/10/2016.
 */
public class utils {

    public static final int LOLLIPOP = 21;

    public static void ShowPickerDialog(AlertDialog.Builder dialogBuilder, String title, String[] items, int selected, DialogInterface.OnClickListener listener)
    {
        dialogBuilder.setTitle(title);
        dialogBuilder.setSingleChoiceItems(items, selected, listener);
        AlertDialog alert = dialogBuilder.create();
        alert.show();
    }

    public static void ShowMessageBox(AlertDialog.Builder dialogBuilder, String title, String message)
    {
        utils.ShowMessageBox(dialogBuilder, title, message, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
    }

    private static void ShowMessageBox(AlertDialog.Builder dialogBuilder, String title, String message, DialogInterface.OnClickListener listener)
    {
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Close", listener);

        if(!alertDialog.isShowing())
            alertDialog.show();
    }

    public static boolean IsNetworkConnected(Context ct)
    {
        ConnectivityManager cm = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    public static void TrustInvalidSslCertificates()
    {
        try
        {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
            {
                public boolean verify(String hostname, SSLSession session)
                {
                    return true;
                }});

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager()
            {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers()
                {
                    return new X509Certificate[0];
                }}}, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isCompatible(int apiLevel)
    {
        return android.os.Build.VERSION.SDK_INT >= apiLevel;
    }




    public static String getDateCurrentTimeZone(long timestamp) {
        SimpleDateFormat day = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        Long now = System.currentTimeMillis()/1000;


        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long today = c.getTimeInMillis();


        if ((today-timestamp)>24*60*60*1000) {
            return day.format(new Date(timestamp));
        }else if((today-timestamp)<24*60*60*1000 && (today-timestamp)>0){
            return "Yesterday";
        }else if((today-timestamp)<0){
            return time.format(new Date(timestamp));
        }
        return null;
    }
}
