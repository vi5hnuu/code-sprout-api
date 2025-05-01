package com.vi5hnu.codesprout.commons;

import com.vi5hnu.codesprout.enums.FileExtension;

import java.util.List;

public class Constants {
    public static final List<FileExtension> allowedExtensions=List.of(FileExtension.TXT,FileExtension.MD);
    public static final String EMAIL_PATTERN="^[a-zA-Z0-9._%+-]+@gmail\\.com$";
    public static final String USER_ID_PREFIX="UID";
    public static final String OTP_ID_PREFIX="OTP";
    public static final String VERIFICATION_TOKEN_ID_PREFIX="VTN";
}
