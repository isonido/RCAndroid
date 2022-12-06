package ru.app.autocat.helpers.downloader.utility;

import java.util.List;

/**
 * Created by aspsine on 15-4-6.
 */
public class ListUtils {

    public static boolean isEmpty(List list) {
        return !(list != null && list.size() > 0);
    }
}
