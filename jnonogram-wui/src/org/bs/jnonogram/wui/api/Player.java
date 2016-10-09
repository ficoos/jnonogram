package org.bs.jnonogram.wui.api;

import com.google.gson.annotations.SerializedName;

@ApiEntityName(singular = "player", plural = "players")
public class Player {
    @SerializedName("username")
    public String username;
}
