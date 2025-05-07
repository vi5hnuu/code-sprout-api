package com.vi5hnu.codesprout.commons;

import com.vi5hnu.codesprout.enums.FileExtension;

import java.util.List;
import java.util.Map;

public class Constants {
    public static final Map<String,String> allowedExtensions=Map.ofEntries(Map.entry(FileExtension.txt.getValue(),"text/plain"),
            Map.entry(FileExtension.md.getValue(), "text/markdown"));
    public static final String EMAIL_PATTERN="^[a-zA-Z0-9._%+-]+@gmail\\.com$";
    public static final String USER_ID_PREFIX="UID";
    public static final String USER_AUTH_PROVIDER_ID_PREFIX="UAP";
    public static final String OTP_ID_PREFIX="OTP";
    public static final String VERIFICATION_TOKEN_ID_PREFIX="VTN";
}
