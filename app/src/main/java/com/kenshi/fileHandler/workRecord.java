package com.kenshi.fileHandler;

import android.content.Intent;
import android.view.View;

/**
 * Created by kenshi on 03/03/2018.
 *
 * The idea is taken from a google developer
 *
 * Descrition: This function will bind the View object with the functions then submit the result
 * back the activity using an intent or run on UI thread call. The activity can then call the update
 * function with the new information or the view isn't there then just drop the work altogether and
 * the activity that issue the work with destroy, then the new activity won't have the references
 * to any of this and it will just drop the work too -> no crashes nor memory leaks
 *
 * Crashes by main thread doing too much work are:
 *  + Heavy resource files
 *  + SharedPreference access
 *  + Bad thread coding
 */

public class workRecord {
    //TODO: make threads and pair it up with UI object
    public Intent startWork(View object) {
        Intent result = new Intent();
        return result;
    }
}
