package yancey.chelper.util;

import androidx.annotation.NonNull;

public class SelectedString {
    public final String string;
    public final int start;
    public final int end;

    public SelectedString(String string, int position) {
        this.string = string;
        this.start = position;
        this.end = position;
    }

    public SelectedString(String string, int start, int end) {
        this.string = string;
        this.start = start;
        this.end = end;
    }

    @NonNull
    @Override
    public String toString() {
        return "SelectedString{" +
                "string='" + string + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
