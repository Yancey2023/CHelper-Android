package yancey.chelper.android.favorite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataFavorite {

    public boolean isChoose;
    public List<DataFavorite> dataFavoriteList;
    public String title;
    public String description;

    public DataFavorite(String title, String description, List<DataFavorite> dataFavoriteList) {
        isChoose = false;
        this.title = title;
        this.description = description;
        this.dataFavoriteList = dataFavoriteList;
    }

    public DataFavorite(DataInputStream dataInputStream) throws IOException {
        isChoose = false;
        title = dataInputStream.readUTF();
        description = dataInputStream.readUTF();
        if (dataInputStream.readBoolean()) {
            int length = dataInputStream.readInt();
            dataFavoriteList = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                dataFavoriteList.add(new DataFavorite(dataInputStream));
            }
        } else {
            dataFavoriteList = null;
        }
    }

    public void writeToFile(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(title);
        dataOutputStream.writeUTF(description);
        if (dataFavoriteList == null) {
            dataOutputStream.writeBoolean(false);
        } else {
            dataOutputStream.writeBoolean(true);
            dataOutputStream.writeInt(dataFavoriteList.size());
            for (DataFavorite dataFavorite : dataFavoriteList) {
                dataFavorite.writeToFile(dataOutputStream);
            }
        }
    }
}
