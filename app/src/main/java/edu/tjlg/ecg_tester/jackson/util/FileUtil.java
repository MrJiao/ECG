package edu.tjlg.ecg_tester.jackson.util;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jackson on 2017/5/14.
 */

public class FileUtil {

    /**
     * 将一个bitmap切割成两个，保存
     * @param bitmap
     * @param fileName
     */
    public static void saveBitmap(final Bitmap bitmap,final String fileName,final int width,final int height){
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Bitmap> e) throws Exception {
                Bitmap b1 = Bitmap.createBitmap(bitmap, 0, 0,width , height/ 2);
                e.onNext(b1);
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, height/ 2,width ,height );
                e.onNext(b2);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(new Consumer<Bitmap>() {
            int i =0;
            @Override
            public void accept(@NonNull Bitmap bitmap) throws Exception {
                i++;
                FileOutputStream fOut = null;
                File f = new File("/sdcard/DCIM/print/"+fileName+"("+i+")"+".png");
                try {
                    fOut = new FileOutputStream(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, fOut);
                fOut.flush();
                fOut.close();
                bitmap.recycle();
            }
        });


    }

}
