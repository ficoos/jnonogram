package org.bs.jnonogram.wui.api;

import com.google.gson.annotations.SerializedName;

@ApiEntityName(singular = "error", plural = "errors")
public class ApiError {
    public ApiError(int code, String message)
    {
        this.code = code;
        this.message = message;
    }

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;
}
