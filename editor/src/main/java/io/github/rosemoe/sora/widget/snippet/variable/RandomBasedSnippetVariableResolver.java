/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2024  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.sora.widget.snippet.variable;

import androidx.annotation.NonNull;

import java.util.Random;
import java.util.UUID;

public class RandomBasedSnippetVariableResolver implements ISnippetVariableResolver {

    @NonNull
    @Override
    public String[] getResolvableNames() {
        return new String[]{
                "RANDOM", "RANDOM_HEX", "UUID"
        };
    }

    @NonNull
    @Override
    public String resolve(@NonNull String name) {
        switch (name) {
            case "RANDOM":
                return Integer.toString(new Random().nextInt());
            case "RANDOM_HEX":
                return Integer.toString(new Random().nextInt(), 16);
            case "UUID":
                return UUID.randomUUID().toString();
        }
        throw new IllegalArgumentException("Unsupported variable name:" + name);
    }
}