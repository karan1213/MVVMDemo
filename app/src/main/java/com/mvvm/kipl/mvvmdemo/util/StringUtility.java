package com.mvvm.kipl.mvvmdemo.util;

import android.widget.EditText;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class StringUtility {


    public static boolean stringNotNull(String str){
        return str != null;
    }

    public static boolean stringNotEmpty(String str){
        return !str.isEmpty();
    }

    public static boolean validateString(String str){
        return stringNotNull(str) && stringNotEmpty(str);
    }

    public static boolean validateEditText(EditText editText){
        return stringNotEmpty(editText.getText().toString().trim());
    }

    public static boolean validateMobileNumber(EditText mobileEditText){
        final Pattern regexPattern = Pattern.compile("[789]\\d{9}");
        return regexPattern.matcher(mobileEditText.getText().toString().trim()).matches();
    }


    public static boolean validateEmail(EditText emailEditText){
        final Pattern emailPattern = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+(aero|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|ax|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|er|es|et|eu|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|rs|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw)\\b",2);
        return emailPattern.matcher(emailEditText.getText().toString().trim()).matches();
    }


    public static String getDoubleAsString(Float value) {
        if(value==null){
            return "";
        }
        DecimalFormat df2 = new DecimalFormat("0.00");
        return df2.format(value);
    }

    public static boolean validateExpiryDate(EditText edtExpirationDate) {
        return true;
    }
}
