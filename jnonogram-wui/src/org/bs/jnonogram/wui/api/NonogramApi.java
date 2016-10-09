package org.bs.jnonogram.wui.api;

import com.google.gson.annotations.SerializedName;

@ApiEntityName(singular = "nonogram", plural = "nonograms")
public class NonogramApi {
    @SerializedName("row_constraints")
    public int[] rowConstraints;

    @SerializedName("column_constraints")
    public int[] columnConstraints;

    @SerializedName("cells")
    public String[] cells;

    @SerializedName("row_count")
    public int rowCount;

    @SerializedName("column_count")
    public int columnCount;
}
