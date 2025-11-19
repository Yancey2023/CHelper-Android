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

package yancey.chelper.android.library.util;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.data.LibraryFunction;

public class LocalLibraryManager {

    public static LocalLibraryManager INSTANCE;
    private final File file;
    private List<LibraryFunction> libraryFunctions;

    private LocalLibraryManager(File file) {
        this.file = file;
    }

    public static void init(File file) {
        INSTANCE = new LocalLibraryManager(file);
    }

    public Observable<List<LibraryFunction>> getFunctions() {
        return Observable.create(emitter -> {
            if (libraryFunctions == null) {
                libraryFunctions = new ArrayList<>();
                if (file.exists()) {
                    try {
                        libraryFunctions = ServiceManager.GSON.fromJson(FileUtil.readString(file), new TypeToken<List<LibraryFunction>>() {
                        }.getType());
                    } catch (Throwable ignored) {

                    }
                }
            }
            emitter.onNext(libraryFunctions);
            emitter.onComplete();
        });
    }

    public void save() {
        FileUtil.writeString(file, ServiceManager.GSON.toJson(libraryFunctions));
    }

}
