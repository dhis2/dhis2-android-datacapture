/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.ehealthMobile.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public final class TextFileUtils {
    private static final String TAG = TextFileUtils.class.getSimpleName();

    public enum FileNames {
        ORG_UNITS_WITH_DATASETS, ACCOUNT_INFO
    }

    public enum Directory {
        ROOT(""),
        DATASETS("datasets"),
        OFFLINE_DATASETS("offlineDatasets"),
        OPTION_SETS("optionSets");

        private String directory;

        Directory(String directory) {
            this.directory = directory;
        }

        @Override
        public String toString() {
            return directory;
        }
    }

    public static String readTextFile(Context context, Directory dir, FileNames name) {
        return readTextFile(context, dir, name.toString());
    }

    /**
     * Reads text file and returns string.
     * <p/>
     *
     * @param context Context from environment.
     * @param dir     Directory where specified file exists
     * @param name    String which represents filename
     * @return Returns contents of text file as String.
     */
    public static String readTextFile(Context context, Directory dir, String name) {
        String path = getDirectoryPath(context, dir);
        File directory = new File(path);
        if (!directory.exists()) {
            throw new IllegalArgumentException("Specified diretory doesn't exist");
        }

        File file = new File(path, name);
        if (!file.exists()) {
            throw new IllegalArgumentException(name + " File not found");
        }

        return readTextFile(file);
    }

    public static String readTextFile(File file) {
        try {
            BufferedReader bufferedStream = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            try {
                String line;
                while ((line = bufferedStream.readLine()) != null) {
                    builder.append(line);
                    builder.append('\n');
                }
                return builder.toString();
            } finally {
                bufferedStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(file.getName() + " IOException");
        }
    }

    public static void writeTextFile(Context context, Directory dir, FileNames name, String data) {
        writeTextFile(context, dir, name.toString(), data);
    }

    /**
     * Writes data to text file with given name.
     * <p/>
     *
     * @param context Context from environment.
     * @param dir     Directory to which file will be written
     * @param name    String which represents filename
     * @param data    String which will be written to text file.
     */
    public static void writeTextFile(Context context, Directory dir, String name, String data) {
        String path = getDirectoryPath(context, dir);
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }

        File file = new File(path, name);
        try {
            file.createNewFile();
            FileOutputStream output = new FileOutputStream(file);
            try {
                output.write(data.getBytes());
            } finally {
                output.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(name + " IOException");
        }
    }

    public static String getDirectoryPath(Context context, Directory dir) {
        return context.getFilesDir().getPath() + '/' + dir.toString();
    }

    /**
     * Removes file with given name
     * <p/>
     *
     * @param context  Context from environment.
     * @param fileName Enumerable of type FileNames which represents file name.
     */
    public static void removeFile(Context context, Directory dir, FileNames fileName) {
        String path = getDirectoryPath(context, dir);
        File file = new File(path, fileName.toString());
        removeFile(file);
    }

    public static void removeFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public static void removeDirectory(Context context, Directory dir) {
        if (Directory.ROOT == dir) {
            Log.e(TAG, "Can't remove application's diretory");
            return;
        }
        String path = getDirectoryPath(context, dir);
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            for (File innerFile : file.listFiles()) {
                removeFile(innerFile);
            }
            file.delete();
        }
    }

    /**
     * Erases all files from FileNames list
     * <p/>
     *
     * @param context
     */
    public static void eraseData(Context context) {
        for (Directory dir : Directory.values()) {
            removeDirectory(context, dir);
        }
        for (FileNames name : FileNames.values()) {
            removeFile(context, Directory.ROOT, name);
        }
    }

    public static boolean doesFileExist(Context context, Directory dir, FileNames name) {
        return doesFileExist(context, dir, name.toString());
    }

    public static boolean doesFileExist(Context context, Directory dir, String name) {
        String path = getDirectoryPath(context, dir);
        File file = new File(path, name);
        return file.exists();
    }
}
