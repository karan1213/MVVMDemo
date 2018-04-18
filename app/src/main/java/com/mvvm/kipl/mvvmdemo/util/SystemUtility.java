package com.mvvm.kipl.mvvmdemo.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by Android on 4/1/2015.
 */

/*
*  How to use:
*
*  <receiver android:name=".utils.SystemUtility" >
            <intent-select_fav>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-select_fav>
        </receiver>
        */
public class SystemUtility extends BroadcastReceiver{

    private static SystemUtility systemUtility;
    private static final String PREF_IS_USER_LOGIN = "pref_login";
    private static final String PREF_LOGIN_USER_DATA = "user_pref";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "On Boot Completed");
        //TODO
    }


    public static SystemUtility getInstance(FragmentActivity activity){
        if(systemUtility == null)
            systemUtility = new SystemUtility();

        return systemUtility;
    }


    public static boolean appInstalledOrNot(Context context, String packageName) {
        if(context == null)
            throw new NullPointerException("Context must be provided");
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String toTitleCase(String str) {
        if (str == null) {
            return null;
        }
        boolean space = true;
        StringBuilder builder = new StringBuilder(str);
        final int len = builder.length();

        for (int i = 0; i < len; ++i) {
            char c = builder.charAt(i);
            if (space) {
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and switch out of whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    space = false;
                }
            } else if (Character.isWhitespace(c)) {
                space = true;
            } else {
                builder.setCharAt(i, Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

    public Intent generateShareIntent(String text){
        return new Intent().setAction(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, StringUtility.validateString(text) ? text : "null").setType("text/plain");
    }

    public Intent generateShareIntent(Uri uriToImage){
        return new Intent().setAction(Intent.ACTION_SEND).putExtra(Intent.EXTRA_STREAM, uriToImage).setType("image/*");
    }

    public Intent generateShareIntent(ArrayList<Uri> imageUris){
        return new Intent().setAction(Intent.ACTION_SEND_MULTIPLE).putExtra(Intent.EXTRA_STREAM,imageUris).setType("image/*");
    }

    public Intent generateNavigationIntent(String sLatitude,String sLongitude,String dLatitude,String dLongitude,String label){
            String format = "geo:" + sLatitude + "," + sLongitude + "?q=" + dLatitude + "," + dLongitude + (StringUtility.validateString(label) ? "(" + label + ")" : "");
            Uri uri = Uri.parse(format);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return intent;
    }

    public Intent generateSendEmailIntent(String strEmail, String subject, String body, Uri attachment){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/image");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{strEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.putExtra(Intent.EXTRA_STREAM, attachment/*Uri.parse("file:///mnt/sdcard/Myimage.jpeg")*/);
        return emailIntent;
    }

    public boolean validateFilePath(String path){
        return StringUtility.validateString(path) && validateFile(new File(path));
    }

    public boolean validateFile(File file){
        return file.exists();
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        if (validateFilePath(contentUri.getPath())){
            return contentUri.getPath();
        }
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            if (cursor == null)
                return null;
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        try {
            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        int smallestSize = bitmap.getWidth() < bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                smallestSize / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public String generateFBHashKey(Context context){
        // Add code to print out the key hash
        try {
            PackageInfo info =context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String key = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("FB KeyHash: ", key);
                return key;
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        return null;
    }

    public String getMediaDirectory(Context context){
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + context.getApplicationInfo().name);
        if (dir!=null && !dir.exists()){
            dir.mkdirs();
        }
        if (dir.exists() && dir.isDirectory()){
            return dir.getAbsolutePath();
        }
        return null;
    }

    public static String getTempMediaDirectory(Context context){
        String state = Environment.getExternalStorageState();
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            dir = context.getExternalCacheDir();
        }else{
            dir = context.getCacheDir();
        }

        if (dir!=null && !dir.exists()){
            dir.mkdirs();
        }
        if (dir.exists() && dir.isDirectory()){
            return dir.getAbsolutePath();
        }
        return null;
    }

    public static void clearTempDirectory(Context context){
        try {
            String dir = getTempMediaDirectory(context);
            if (StringUtility.validateString(dir)) {
                File file = new File(dir);
                if (file != null && file.exists()) {
                    deleteRecursive(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

//        fileOrDirectory.delete();

    }


    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
