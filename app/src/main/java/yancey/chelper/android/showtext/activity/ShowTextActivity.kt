/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package yancey.chelper.android.showtext.activity

import android.os.Bundle
import yancey.chelper.android.common.activity.BaseComposeActivity
import yancey.chelper.ui.showtext.ShowTextScreen

/**
 * 文本展示界面
 */
class ShowTextActivity : BaseComposeActivity() {

    companion object {
        const val TITLE: String = "title"
        const val CONTENT: String = "content"
    }

    override val pageName = "ShowText"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras!!
        setContent {
            ShowTextScreen(
                title = extras.getString(TITLE)!!,
                content = extras.getString(CONTENT)!!
            )
        }
    }

}