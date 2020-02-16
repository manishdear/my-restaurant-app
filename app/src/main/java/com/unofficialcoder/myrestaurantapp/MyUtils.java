package com.unofficialcoder.myrestaurantapp;

import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.unofficialcoder.myrestaurantapp.network.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyUtils {
    private static final String TAG = "MyUtils";
    static AlertDialog dialog;

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    // validating email id
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd-MM-yyyy", cal).toString();
    }

    public static void showVolleyError(VolleyError error, String TAG, Context context) {
        error.printStackTrace();
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                Log.d(TAG, "simpleVolleyRequestError: " + body);
                if (statusCode == 400 || statusCode == 401) {
                    JSONObject obj = new JSONObject(body);
                    if (obj.has("errors_keys")) {
                        Object intervention;
                        intervention = obj.get("errors_keys");
                        if (intervention instanceof JSONArray) {
                            // It's an array
                            JSONArray errors = obj.getJSONArray("errors_keys");
                            String key = errors.get(0).toString();
                            JSONArray messageArray = obj.getJSONObject("errors").getJSONArray(key);
                            Toast.makeText(context, messageArray.get(0).toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            // It's an object
                            String key = obj.getString("errors_keys");
                            JSONArray messageArray = obj.getJSONObject("errors").getJSONArray(key);
                            Toast.makeText(context, messageArray.get(0).toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    String errorString = MyVolley.handleVolleyError(error);
                    Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                }

            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e + "", Toast.LENGTH_SHORT).show();
            }
        } else {
            MyVolley.handleVolleyError(error);
        }
    }

    public static void showVolleyErrorNoDataFound(VolleyError error, String TAG, Context context, TextView textView) {
        error.printStackTrace();
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                Log.d(TAG, "simpleVolleyRequestError: " + body);
                if (statusCode == 400 || statusCode == 401) {
                    JSONObject obj = new JSONObject(body);
                    if (obj.has("errors_keys")) {
                        Object intervention;
                        intervention = obj.get("errors_keys");
                        if (intervention instanceof JSONArray) {
                            // It's an array
                            JSONArray errors = obj.getJSONArray("errors_keys");
                            String key = errors.get(0).toString();
                            JSONArray messageArray = obj.getJSONObject("errors").getJSONArray(key);
                            Toast.makeText(context, messageArray.get(0).toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            // It's an object
                            String key = obj.getString("errors_keys");
                            JSONArray messageArray = obj.getJSONObject("errors").getJSONArray(key);
                            Toast.makeText(context, messageArray.get(0).toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    String errorString = MyVolley.handleVolleyError(error);
                    Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                }

            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, e + "", Toast.LENGTH_SHORT).show();
            }
        } else {
            MyVolley.handleVolleyError(error);
        }
    }

    public static void positionShifterOTP(final EditText et1, final EditText et2) {

        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et1.getText().toString().length() == 1) {     //size as per your requirement
                    et2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public static boolean isValidMobile(String mobile) {
        String MobilePattern = "[0-9]{10}";
        return mobile.matches(MobilePattern);
    }

    public static void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                //  handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {


                final String id = DocumentsContract.getDocumentId(uri);
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:", "");
                    }
                    try {
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUri, null, null);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

//                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//
//                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String fetchFileName(Intent data) {
        Uri uri = data.getData();
        String uriString = uri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();
        String displayName = null;

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = MyApplication.getAppContext().getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    // Log.i(TAG, "onActivityResult_new_file " + displayName);
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            displayName = myFile.getName();
            // Log.i(TAG, "onActivityResult_new_file " + displayName);
        }
        return displayName;
    }

    public static void showTheToastMessage(String message) {
        if (message != null) {
            Toast.makeText(MyApplication.getAppContext(), message, Toast.LENGTH_LONG).show();
        }
    }


    public static String colorDecToHex(int p_red, int p_green, int p_blue) {
        String red = Integer.toHexString(p_red);
        String green = Integer.toHexString(p_green);
        String blue = Integer.toHexString(p_blue);

        if (red.length() == 1) {
            red = "0" + red;
        }
        if (green.length() == 1) {
            green = "0" + green;
        }
        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        String colorHex = "#" + red + green + blue;
        return colorHex;
    }

    public static Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String generateRandomUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("[-+.^:,|@_]", "");
    }

    public static long getTimeInMilliSeconds(String timeToConvert) {
        String[] data = timeToConvert.split(":");
        int hours = Integer.parseInt(data[0]);
        int minutes = Integer.parseInt(data[1]);
        int seconds = Integer.parseInt(data[2]);
        int time = seconds + 60 * minutes + 3600 * hours;
        return TimeUnit.MILLISECONDS.convert(time, TimeUnit.SECONDS);
    }

//    public static void showCustomBottomSheet(Context mContext, View parentView) {
//        if (parentView.getParent() != null) {
//            ((ViewGroup) parentView.getParent()).removeView(parentView);
//        } else {
//            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext, R.style.CustomBottomSheetDialogTheme);
//            bottomSheetDialog.setContentView(parentView);
//            bottomSheetDialog.show();
//        }
//    }

    public static void showProgressDialog(Context ctx, boolean cancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        View v = LayoutInflater.from(ctx).inflate(R.layout.progresslay, null, false);
        builder.setView(v);
        builder.setCancelable(cancelable);
        dialog = builder.create();
        dialog.show();
    }

    public static void dismisProgressDialog() {
        dialog.dismiss();
    }

    public static List<String> getFullAddressAPI(double latitude, double longitude, Context context) {
        List<String> addressList = new ArrayList<>();
        Address locationAddress = getAddress(latitude, longitude, context);

        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();
            String SubLocality = locationAddress.getSubLocality();
            String house_number = locationAddress.getFeatureName();

            addressList.add(country);
            addressList.add(state);
            addressList.add(city);
        }
        return addressList;
    }

    // get adddress
    public static Address getAddress(double latitude, double longitude, Context context) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long convertDateTimeToTimeStamp(String onlyDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        Date date = null;
        try {
            date = sdf.parse(onlyDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime() / 1000;
        return millis;
    }

    public static boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    public static boolean isFirstItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (firstVisibleItemPosition != RecyclerView.NO_POSITION && firstVisibleItemPosition == 0)
                return true;
        }
        return false;
    }

    public static String convertDateTime(String timestamp) {
        long unixSeconds = Long.parseLong(timestamp);
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("+5:30"));
        return sdf.format(date);
    }


    public static String convertDate(String timestamp) {
        long unixSeconds = Long.parseLong(timestamp);
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("+5:30"));
        return sdf.format(date);
    }

    public static String convertTime(String timestamp) {
        long unixSeconds = Long.parseLong(timestamp);
        Date date = new Date(unixSeconds * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("+5:30"));
        return sdf.format(date);
    }

    public static void downloadFile(String file, Context context, String file_type) {
        String DownloadUrl = file;
        DownloadManager.Request request1 = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request1.setDescription("Tripster");
        request1.setTitle("Tripster File");
        request1.setVisibleInDownloadsUi(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request1.allowScanningByMediaScanner();
            request1.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        request1.setDestinationInExternalFilesDir(context, "/File", System.currentTimeMillis() + "." + file_type);

        DownloadManager manager1 = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Objects.requireNonNull(manager1).enqueue(request1);
        if (DownloadManager.STATUS_SUCCESSFUL == 8) {
            Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    public static List<String> genderList(){
        List<String> list = new ArrayList<>();
        list.add("gender");
        list.add("male");
        list.add("female");
        list.add("other");
        return list;
    }

}