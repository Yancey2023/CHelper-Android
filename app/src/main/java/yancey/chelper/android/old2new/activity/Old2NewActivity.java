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

package yancey.chelper.android.old2new.activity;

import androidx.annotation.NonNull;

import yancey.chelper.android.old2new.view.Old2NewView;
import yancey.chelper.fws.activity.FWSActivity;
import yancey.chelper.fws.view.FWSView;

/**
 * 旧命令转新命令的界面
 */
public class Old2NewActivity extends FWSActivity<Old2NewView> {

    @Override
    protected Old2NewView createView(@NonNull FWSView.FWSContext fwsContext) {
        return new Old2NewView(fwsContext);
    }

}