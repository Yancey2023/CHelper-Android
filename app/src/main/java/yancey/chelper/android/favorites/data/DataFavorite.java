/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package yancey.chelper.android.favorites.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏界面列表中每个视图的数据
 */
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
